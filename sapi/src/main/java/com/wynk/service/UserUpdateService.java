package com.wynk.service;

import com.mongodb.DBObject;
import com.netflix.hystrix.HystrixCommand;
import com.wynk.common.Circle;
import com.wynk.common.ExceptionTypeEnum;
import com.wynk.common.Gender;
import com.wynk.db.MongoDBManager;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.dto.NdsUserInfo;
import com.wynk.server.ChannelContext;
import com.wynk.service.api.NdsUserInfoApiService;
import com.wynk.service.hystrix.HystrixUtils;
import com.wynk.service.hystrix.NDSUserInfoCommand;
import com.wynk.service.hystrix.WCFCommandSetterFactory;
import com.wynk.user.dto.User;
import com.wynk.user.dto.UserDevice;
import com.wynk.user.dto.UserDob;
import com.wynk.user.dto.UserEntityKey;
import com.wynk.utils.LogstashLoggerUtils;
import com.wynk.utils.OperatorUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wynk.constants.MusicConstants.USER_COLLECTION;

@Service
public class UserUpdateService {

	@Autowired
	private NdsUserInfoApiService ndsUserInfoApiService;

	@Autowired
	private ShardedRedisServiceManager musicUserShardRedisServiceManager;

	@Autowired
	private MongoDBManager mongoUserDBManager;

	@Autowired
	private AccountService accountService;

	@Value("${hystrix.wcf.nds.timeout}")
	public int NDS_EXECUTION_TIMEOUT = 800;

	@Value("${hystrix.wcf.threadpool.size}")
	public int threadPoolSize = 200;
	@Value("${hystrix.wcf.threadpool.queue}")
	public int threadPoolQueue = 20;


	private static final Logger logger = LoggerFactory.getLogger(UserUpdateService.class.getCanonicalName());

	private int numOfThreads = 10;
	private ExecutorService userOperatorUpdateExecutor = Executors.newFixedThreadPool(numOfThreads);

	public void checkAndUpdateOperator() {
		User user = ChannelContext.getUser();
		if (StringUtils.isEmpty(user.getOperator()) || user.getOperator().equalsIgnoreCase("other")) {
		    logger.info("Checking Operator and updating for uid : " + user.getUid());
			User appUser = fetchUserNDSDataAndUpdate(user.getUid(), user.getMsisdn(), user.getOperator(),true);
			ChannelContext.setUser(appUser);
			return;
		}
        logger.info("Checking Operator and updating in backgroud for uid : " + user.getUid());
        userOperatorUpdateExecutor.submit(new UpdateUserTask(user.getMsisdn(), user.getUid(), user.getOperator()));
	}

	class UpdateUserTask implements Runnable {

		private String msisdn;
		private String uid;
		private String currentOp;

		UpdateUserTask(String msisdn, String uid, String currentOp) {
			this.msisdn = msisdn;
			this.uid = uid;
			this.currentOp = currentOp;
		}

		@Override
		public void run() {
			fetchUserNDSDataAndUpdate(uid,msisdn,currentOp, false);
		}

	}

	private User fetchUserNDSDataAndUpdate(String uid, String msisdn, String currentOp, boolean useHystrix) {
		if (msisdn == null || uid == null)
			return null;

		String operatorName = AccountService.OTHER;
		NdsUserInfo ndsUser;
		boolean updateNdsTimestamp = true;
		if (useHystrix){
			HystrixCommand.Setter setter = WCFCommandSetterFactory.INSTANCE.getSetter(HystrixUtils.WCF_COMMAND_GROUP_KEY, NDS_EXECUTION_TIMEOUT, threadPoolSize, threadPoolQueue);
			NDSUserInfoCommand ndsUserInfoCommand = new NDSUserInfoCommand(setter, msisdn, uid, ndsUserInfoApiService);
			ndsUser = ndsUserInfoCommand.execute();
			if (ndsUser==null || ndsUser== NdsUserInfoApiService.fallbackNDSUserInfo){
				updateNdsTimestamp = false;
			}
		}else{
			ndsUser = ndsUserInfoApiService.getNdsUserInfoFromWCFCache(msisdn);
			if(ndsUser == NdsUserInfoApiService.emptyNDSUserInfo ){
				logger.info("Wcf nds Api failed for user :  " + uid);
				return ChannelContext.getUser();
			}
		}

		String ndsCircle = ndsUser.getCircle();
		String circleShortName = null;
		if (!StringUtils.isEmpty(ndsCircle)) {
			circleShortName = Circle.getCircleShortName(ndsCircle);
			operatorName = AccountService.AIRTEL;
		}
		boolean updateOperator = OperatorUtils.isOperatorUpdateRequired(operatorName, currentOp);
		String name = ndsUser.getFirstName() + " " +ndsUser.getLastName();
		String gender = ndsUser.getGender() == null ? Gender.UNAVAILABLE.toString() : ndsUser.getGender().toString();
		return updateUserDetails(uid,operatorName, updateOperator, ndsUser.getDateOfBirth(),gender,ndsUser.getCustomerCategory(),ndsUser.getDevice4gCapable(),name, ndsUser.getEmailID(), ndsUser.getVasDND(), circleShortName, updateNdsTimestamp);
	}


	private User updateUserDetails(String uid,String operator, boolean updateOperator, String dob, String gender, String customerCategory, Boolean is4g, String name, String email, String vasDnd, String circle, boolean updateNdsTimestamp) {
		User appUser = null;
		try {
			DBObject userObject = accountService.getUserObjectByUid(uid);
			if (userObject != null) {
				if(!updateNdsTimestamp && ChannelContext.getUser().getNdsTS() != null ){
					//return previous state of old users in case of hystrix fallback
					return ChannelContext.getUser();
				}
				if (musicUserShardRedisServiceManager != null) {
					musicUserShardRedisServiceManager.delete(accountService.getRedisUidKey(uid));
				}
				String userJson = userObject.toString();
				appUser = new User();
				appUser.fromJson(userJson);
				appUser.setLastActivityDate(System.currentTimeMillis());
				if (StringUtils.isNotEmpty(circle)) {
					appUser.setCircle(circle);
				}
				if (StringUtils.isNotEmpty(dob)) {
					UserDob dateOfBirth = new UserDob();
					try {
						dateOfBirth.fromString(dob);
						appUser.setDob(dateOfBirth);
					} catch (Exception e) {
						logger.error("Error parsing date of birth of user from nds : " + uid + ". Error : " + e);
					}
				}
				if (StringUtils.isNotEmpty(gender) && !(gender.equalsIgnoreCase("UNAVAILABLE")) && StringUtils.isNotEmpty(getParsedGender(gender))) {
					appUser.setGender(getParsedGender(gender));
				}
				if (StringUtils.isNotEmpty(vasDnd)) {
					appUser.setVasDND(vasDnd);
				}
				if (StringUtils.isEmpty(appUser.getName())) {
					appUser.setName(name);
				}
				appUser.setEmail(email);
				List<UserDevice> devices = appUser.getDevices();
				for (UserDevice device : devices) {
					if (device.isActive()) {
						device.setIs4GEnabled(is4g!=null ? is4g:false);
						device.setCustomerCategory(customerCategory);
						break;
					}
				}
				appUser.setDevices(devices);
				Map<String, Object> queryParams = new HashMap<>();
				queryParams.put(UserEntityKey.uid, appUser.getUid());
				Map<String, Object> queryValues = new HashMap<>();
				queryValues.put(UserEntityKey.name, appUser.getName().toLowerCase());
				if (appUser.getDob() != null) {
					queryValues.put(UserEntityKey.Dob.dob, appUser.getDob().toJsonObject());
				}
				if (appUser.getCircle() != null) {
					queryValues.put(UserEntityKey.circle, appUser.getCircle());
				}
				if (StringUtils.isNotEmpty(gender) && !(gender.equalsIgnoreCase("UNAVAILABLE")) && StringUtils.isNotEmpty(getParsedGender(gender))) {
					queryValues.put(UserEntityKey.gender, appUser.getGender());
				}
				queryValues.put(UserEntityKey.preferredLang, appUser.getPreferredLanguage());
				queryValues.put(UserEntityKey.email, appUser.getEmail());
				queryValues.put(UserEntityKey.lastActivityDate, appUser.getLastActivityDate());
				if (updateNdsTimestamp){
					appUser.setNdsTS(System.currentTimeMillis());
					queryValues.put(UserEntityKey.ndsTS, appUser.getNdsTS());
				}
				if (updateOperator){
					appUser.setOperator(operator);
					queryValues.put(UserEntityKey.operator, operator);
				}
				if (StringUtils.isNotEmpty(vasDnd)) {
					queryValues.put(UserEntityKey.vasDND, appUser.getVasDND());
				}
				JSONArray deviceArray = new JSONArray();
				if (appUser.getDevices() != null) {
					for (int i = 0; i < appUser.getDevices().size(); i++) {
						UserDevice userDevice = appUser.getDevices().get(i);
						deviceArray.add(userDevice.toJsonObject());
					}
				}
				queryValues.put(UserEntityKey.devices, deviceArray);
				try {
					mongoUserDBManager.setField(USER_COLLECTION, queryParams, queryValues, false);
				} catch (Exception e) {
					LogstashLoggerUtils.createFatalExceptionLogWithMessage(e, ExceptionTypeEnum.INFRA.MONGO.name(),
							appUser.getUid(), "UserUpdateService.updateUserDetails", "Error updating user in mongo");
				}
			}
			accountService.createUpdateUserInCache(appUser);
		} catch (Exception e) {
			LogstashLoggerUtils.createCriticalExceptionLogWithMessage(e, ExceptionTypeEnum.CODE.name(), uid,
					"UserUpdateService.updateUserDetails", "Error updating user in db object");
			logger.error("Error updating User details in Redis for uid : " + uid + ". Error : " + e);
		}
		return appUser;
	}

	//todo convert to enum for gender
	private String getParsedGender(String gender) {
		if (gender.equalsIgnoreCase("MALE")) {
			return "m";
		} else if (gender.equalsIgnoreCase("FEMALE")) {
			return "f";
		}
		return null;
	}

}