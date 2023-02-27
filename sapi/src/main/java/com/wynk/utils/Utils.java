 package com.wynk.utils;

 import com.bsb.portal.core.common.CircleInf;
 import com.fasterxml.jackson.annotation.JsonInclude.Include;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.DeserializationFeature;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.SerializationFeature;
 import com.google.i18n.phonenumbers.NumberParseException;
 import com.google.i18n.phonenumbers.PhoneNumberUtil;
 import com.google.i18n.phonenumbers.Phonenumber;
 import com.wynk.common.Country;
 import com.wynk.common.PackProviderAuthRequest;
 import com.wynk.common.PortalException;
 import com.wynk.common.Version;
 import com.wynk.constants.JsonKeyNames;
 import com.wynk.constants.MusicConstants;
 import com.wynk.dto.BaseObject;
 import com.wynk.dto.IdNameType;
 import com.wynk.music.constants.MusicContentLanguage;
 import com.wynk.music.constants.ShufflePriority;
 import com.wynk.music.dto.*;
 import com.wynk.server.ChannelContext;
 import com.wynk.user.dto.User;
 import io.netty.handler.codec.http.HttpRequest;
 import org.apache.commons.collections.CollectionUtils;
 import org.apache.commons.collections.MapUtils;
 import org.apache.commons.lang.StringUtils;
 import org.apache.log4j.*;
 import java.util.Base64;
 import org.bouncycastle.jce.provider.BrokenPBE;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 import org.json.simple.JSONValue;
 import org.jsoup.Jsoup;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 import java.awt.*;
 import java.beans.BeanInfo;
 import java.beans.IntrospectionException;
 import java.beans.Introspector;
 import java.beans.PropertyDescriptor;
 import java.io.*;
 import java.lang.reflect.InvocationTargetException;
 import java.math.BigInteger;
 import java.net.URLDecoder;
 import java.net.URLEncoder;
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;
 import java.text.DateFormat;
 import java.text.ParseException;
 import java.text.SimpleDateFormat;
 import java.util.*;
 import java.util.List;
 import java.util.concurrent.ThreadLocalRandom;
 import java.util.concurrent.TimeUnit;
 import java.util.zip.GZIPOutputStream;

 import static com.wynk.constants.JsonKeyNames.*;
 import static com.wynk.utils.ObjectUtils.objectMapper;

 /**
  * Created with IntelliJ IDEA. User: bhuvangupta Date: 20/09/12 Time: 11:52 PM To change this
  * template use File | Settings | File Templates.
  */
 public class Utils {


	 public static final String X_BSY_ATKN = "x-bsy-atkn";
	 public static final String X_BSY_DATE = "x-bsy-date";
	 private static final Logger logger         = LoggerFactory.getLogger(Utils.class.getCanonicalName());

	 private static ObjectMapper objectMapper =
			 new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	 private static Map<String, String> contentTypeMap = new HashMap<String, String>();
	 static {
		 contentTypeMap.put("jpeg", "image/jpeg");
		 contentTypeMap.put("png", "image/png");
	 }

	 public static JSONArray convertToJSONArray(List<String> list) {
		 JSONArray result = new JSONArray();
		 if(list != null) {
			 for(String str : list) {
				 result.add(str);
			 }
		 }
		 return result;
	 }

	 public static JSONArray convertIntegerListToJSONArrayGeneric(List list) {
		 JSONArray result = new JSONArray();
		 if(list != null && !CollectionUtils.isEmpty(list)) {
			 Object obj = list.get(0);
			 for (Object object : list) {
				 result.add(object);
			 }
		 }
		 return result;
	 }

	 public static JSONArray convertToJSONArray(Set<String> set) {
		 JSONArray result = new JSONArray();
		 if(set != null) {
			 for(String str : set) {
				 result.add(str);
			 }
		 }
		 return result;
	 }

	 public static List<String> convertToStringList(JSONArray array) {
		 if(array != null) {
			 List<String> result = new ArrayList<String>(array);
			 return result;
		 }
		 return new ArrayList<String>();
	 }

	 public static List<Integer> convertToIntegerList(JSONArray array) {
		 if(array != null) {
			 List<Integer> result = new ArrayList<Integer>(array);
			 return result;
		 }
		 return new ArrayList<Integer>();
	 }

	 public static Set<String> convertToStringSet(JSONArray array) {
		 if(array != null) {
			 Set<String> result = new LinkedHashSet<String>(array);
			 return result;
		 }
		 return new HashSet<String>();
	 }

	 public static String toString(final List<?> list, char delimiter) {
		 final StringBuilder b = new StringBuilder();
		 if (list != null) {
			 for (int i = 0; i < list.size(); i++) {
				 b.append(list.get(i).toString());
				 if (i != list.size() - 1) {
					 b.append(delimiter);
				 }
			 }
		 }
		 return b.toString();
	 }

	 /**
	  * Returns the exception stack trace as a String.
	  *
	  * @param e
	  *            the exception to get the stack trace from.
	  * @return the exception stack trace
	  */
	 public static String getStackTrace(Throwable e) {
		 if(e == null) {
			 return "";
		 }
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
		 PrintWriter printWriter = new PrintWriter(baos);
		 e.printStackTrace(printWriter);
		 printWriter.flush();
		 String stackTrace = new String(baos.toByteArray());
		 printWriter.close();
		 return stackTrace;
	 }

	 /**
	  * Returns the error message associated with the given Throwable. The error message returned
	  * will try to be as precise as possible, handling cases where e.getMessage() is not meaningful,
	  * like 'NullPointerException' for instance.
	  *
	  * @param t
	  *            the throwable to get the error message from
	  * @return the error message of the given exception
	  */
	 public static String getErrorMessage(Throwable t) {
		 if(t == null) {
			 return "";
		 }
		 if(t instanceof InvocationTargetException) {
			 InvocationTargetException ex = (InvocationTargetException) t;
			 t = ex.getTargetException();
		 }
		 String errMsg = t instanceof RuntimeException ? t.getMessage() : t.toString();
		 if(errMsg == null || errMsg.length() == 0 || "null".equals(errMsg)) {
			 errMsg = t.getClass().getName() + " at " + t.getStackTrace()[0].toString();
		 }
		 return errMsg;
	 }

	 public static String getURLParamFromUrl(String url, String param) throws PortalException {

		 Map<String, List<String>> urlParameters = HTTPUtils.getUrlParameters(url);
		 return getURLParam(urlParameters, param);
	 }

	 public static String getURLParam(Map<String, List<String>> urlParameters, String param) {
		 String paramVal = "";
		 if((urlParameters.containsKey(param)) && (urlParameters.get(param).get(0) != null) && ((urlParameters.get(param).get(0).trim().length() != 0))) {
			 paramVal = urlParameters.get(param).get(0).trim();
		 }
		 return paramVal;
	 }

	 public static String getCircleShortName(String msisdn) {
		 try {
			 CircleInf circleObj = UserDeviceUtils.getCircleInfo(msisdn);
			 return circleObj.getCircleShortName();
		 }
		 catch (Exception e) {
			 logger.error("Could not get CSN for msisdn : " + msisdn + ". Log Message:" + e.getMessage());
			 return "pan";
		 }
	 }

	 /**
	  * return 10 digit msisdn. throws IllegalArgumentException if msisdn passed is less than 10
	  * characters. It does not validate msisdn for being numeric.
	  *
	  * @param msisdn
	  * @return 10 digit msisdn
	  */
	 public static String getTenDigitMsisdn(String msisdn) {
		 if(StringUtils.isEmpty(msisdn))
			 return msisdn;
		 msisdn = msisdn.trim();
		 int length = msisdn.length();
		 if(length == 10) {
			 return msisdn;
		 }
		 if(length > 10) {
			 return msisdn.substring(length - 10);
		 }
		 throw new IllegalArgumentException("Illegal value for msisdn : " + msisdn);
	 }



	 /**
	  * return 10 digit msisdn. throws IllegalArgumentException if msisdn passed is less than 10
	  * characters. It does not validate msisdn for being numeric.
	  *
	  * @param msisdn
	  * @return 10 digit msisdn
	  */
	 public static String getTenDigitMsisdnWithoutCountryCode(String msisdn) {
		 if(StringUtils.isEmpty(msisdn))
			 return msisdn;
		 msisdn = msisdn.trim();
		 int length = msisdn.length();
		 if(msisdn.contains("+91"))
		 {
			 return msisdn.replace("+91", "");
		 }
		 if(length == 10) {
			 return msisdn;
		 }
		 if(length > 10) {
			 return msisdn.substring(length - 10);
		 }
		 return msisdn;
	 }

	 /**
	  * Get 12 digit msisdn
	  *
	  * @param msisdn
	  * @return
	  */
	 public static String get12DigitMsisdn(String msisdn) {
		 if (org.apache.commons.lang.StringUtils.isNotEmpty(msisdn) && msisdn.length() == 10) {
			 return "91" + msisdn;
		 }
		 return msisdn;
	 }

	 /**
	  * Adds +91 prefix to 10 digit msisdn. It does not validate msisdn for being numeric.
	  *
	  * @param msisdn
	  * @return +91 prefixed 13 character long msisdn
	  */
	 public static String getPrefixedMsisdn(String msisdn) {
		 if(msisdn == null)
			 return msisdn;
		 msisdn = msisdn.trim();
		 int length = msisdn.length();
		 if(length == 10) {
			 return "+91" + msisdn;
		 }
		 if(length > 10) {
			 return "+91" + msisdn.substring(length - 10);
		 }
		 throw new IllegalArgumentException("Illegal value for msisdn : " + msisdn);
	 }

	 public static boolean containsAlpha(String str) {
		 if(str == null) {
			 return false;
		 }
		 for(int i = str.length() - 1; i >= 0; i--) {
			 char c = str.charAt(i);
			 if(Character.isLetter(c)) {
				 return true;
			 }
		 }
		 return false;
	 }

	 public static String normalizePhoneNumber(String ph) {
		 logger.info("Going to normalize this number {}",ph);
		 if(ph == null || ph.isEmpty() || ph.startsWith("+")) {
			 return ph;
		 }

		 try {
			 String phOther;
			 if(containsAlpha(ph)) {
				 return ph;
			 } else if ((phOther = parse("+94", 12, ph)) != null) {
				 return phOther;
			 } else if ((phOther = parse("+65", 11, ph)) != null) {
				 return phOther;
			 } else if (ph.length() == 9) {
				 return "+94" + ph;
			 } else if (ph.length() == 8) {
				 return "+65" + ph;
			 }
			 Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(ph, "IN");
			 return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
		 }
		 catch (NumberParseException e) {
			 // this can also be the case if we use number like "TD-HIKE"
			 // fallback to the raw parsing
			 if(ph.startsWith("91") && ph.length() == 12) {
				 return "+" + ph;
			 }
			 else if(ph.startsWith("94") && ph.length() == 11) {
				 // if SriLankan number
				 return "+" + ph;
			 }
			 else if(ph.length() == 10) {
				 return "+91" + ph;
			 }
			 else if(ph.length() == 11 && ph.startsWith("0")) // phone number starts with 0 e.g.
															  // 09811920234
			 {
				 return "+91" + ph.substring(1, ph.length());
			 }
			 return ph;
		 }

	 }
	 public static String parse(String countryCode, int lengthWithCountryCode, String ph) {

		 if (ph.startsWith("0")) {
			 ph = ph.substring(1);
		 }

		 if (ph.startsWith(countryCode) && ph.length() == lengthWithCountryCode) {
			 return ph;
		 }

		 if (("+" + ph).startsWith(countryCode) && ph.length() == (lengthWithCountryCode - 1)) {
			 return "+" + ph;
		 }

//        if (ph.length() == (lengthWithCountryCode - countryCode.length())) {
//            return ph;
//        }

		 return null;
	 }

	 public static String normalizePhoneNumber(String ph, Country country) {
		 logger.info("Mobile number give was : {} , with country : {}", ph, country);
	 	if(org.apache.commons.lang3.StringUtils.isBlank(ph))
	 		return ph;

		 if (ph.startsWith("+")) {
			 //already normalized
			 return ph;
		 }
		 //other country number new builds
		 if (country!=null && StringUtils.isNotEmpty(country.getCountryCode())) {
		 	if(country == Country.INDIA) {
		 		int length = ph.length();
		 		ph = ph.substring(length - 10, length);
			}
			 if (country == Country.SINGAPORE) {
				 int length = ph.length();
				 ph = ph.substring(length - 8, length);
			 }
			 return country.getCountryCode() + ph;
		 }
		 else if ((ph = parse("+94", 12, ph)) != null) {
			 return ph;
		 } else if ((ph = parse("+65", 11, ph)) != null) {
			 return ph;
		 } else if (ph.length() == 9) {
			 return "+94" + ph;
		 } else if (ph.length() == 8) {
			 return "+65" + ph;
		 }

		 //Old handling fot only India as default
		 if (ph.startsWith("91") && ph.length() == 12) {
			 return "+" + ph;
		 }
		 else if(ph.startsWith("94") && ph.length() == 11) {
			 // if SriLankan number
			 return "+" + ph;
		 }
		 else if (ph.length() == 10) {
			 return "+91" + ph;
		 } else if (ph.length() == 11 && ph.startsWith("0")) // phone number starts with 0 e.g.
		 // 09811920234
		 {
			 return "+91" + ph.substring(1, ph.length());
		 }
		 return ph;
	 }

	 public static void main(String[] args) {
		// String normalizePhoneNumber = getTrimmedMsisdn("+919650567451", 4);
		 System.out.println(getRandomNumber(10));
		 System.out.println(getRandomNumber(10));
		 System.out.println(getRandomNumber(10));
		 System.out.println(getRandomNumber(10));
		 System.out.println(getRandomNumber(10));
		 System.out.println(getRandomNumber(10));
		 System.out.println(getRandomNumber(10));
		 System.out.println(getRandomNumber(10));
		 System.out.println(getRandomNumber(10));

	 }

	 public static int getRandomNumber(int maxValue) {
		 int numb = 1 + (int) (maxValue * Math.random());
		 if(numb > 0 && numb <= maxValue) {
			 return numb;
		 }
		 return 0;
	 }

	 public static String getContentType(String type) {
		 return contentTypeMap.get(type.toLowerCase());
	 }


	 public static Date now() {
		 return Calendar.getInstance(TimeZone.getTimeZone("IST")).getTime();
	 }

	 public static <E extends BaseObject> JSONArray convertJsonArray(List<E> objectList) {
		 return convertJsonArray(objectList, false);
	 }

	 public static <E extends BaseObject> JSONArray convertJsonArray(List<E> objectList,boolean compressed) {

		 JSONArray arr = new JSONArray();
		 if(objectList != null) {
			 for(BaseObject baseObj : objectList) {
				 try {
					 arr.add(getJSONFromBaseObject(baseObj, compressed,false));
				 }
				 catch (Throwable e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return arr;
	 }
	 public static JSONObject getJSONFromBaseObject(BaseObject baseObj , boolean compressed, boolean userSeparateBuilds) {
		 JSONObject jsonObject = null;
		 if(compressed && (baseObj instanceof MusicMetadata))
		 {
			 if(userSeparateBuilds)
				 jsonObject = ((MusicMetadata) baseObj).getMinimalSetJSONObject();
			 else
				 jsonObject = ((MusicMetadata) baseObj).toCompressedJsonObject(MusicUtils.getKeysFromContext());
			 jsonObject.remove(JsonKeyNames.ITEMS);
			 jsonObject.put(JsonKeyNames.COUNT,0);

		 }
		 else
		 {
			 jsonObject = baseObj.toJsonObject();
		 }
		 return jsonObject;
	 }

	 public static JSONArray getJsonArrayFromStringList(List<String> list) {
		 JSONArray arr = new JSONArray();
		 if(list != null) {
			 for(String s : list) {
				 arr.add(s);
			 }
		 }
		 return arr;
	 }

	 public static <E> List<E> getListFromJsonArr(JSONArray jsonArr) {
		 List<E> list = new ArrayList<E>();
		 if(jsonArr != null) {
			 for(int i = 0; i < jsonArr.size(); i++) {
				 E e = (E) jsonArr.get(i);
				 list.add(e);
			 }
		 }
		 return list;
	 }

	 public static <E extends BaseObject> List<E> convertJsonAdaptableList(JSONArray arrList, Class<E> klass) {
		 List<E> objList = new ArrayList<E>();
		 if(arrList != null) {
			 for(Object obj : arrList) {
				 try {
					 JSONObject jsonObject = (JSONObject) obj;
					 E newInstance = klass.newInstance();
					 newInstance.fromJsonObject(jsonObject);
					 objList.add(newInstance);
				 }
				 catch (Exception e) {
					 e.printStackTrace();
				 }
			 }
		 }
		 return objList;
	 }

	 public static List<String> convertToStringList(String str) {
		 List<String> list = new ArrayList<String>();
		 if(str == null) {
			 return list;
		 }
		 str = str.replace("[", "");
		 str = str.replace("]", "");
		 String[] arr = str.split(",");
		 for(String s : arr) {
			 list.add(s.trim());
		 }
		 return list;
	 }

	 @SuppressWarnings("unchecked")
	 public static <T> T getFromJSONObj(Object obj, String tag) {
		 return (T) ((JSONObject) obj).get(tag);
	 }

	 public static boolean isValidImage(String url) {
		 if (url == null)
			 return false;

		 if (url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".gif") || url.endsWith(".JPG"))
			 return true;
		 return false;
	 }

	 public static JSONObject createErrorResponse(String errorcode,String errorMessage)
	 {
		 JSONObject response = new JSONObject();
		 response.put("errorCode",errorcode);
		 response.put("error",errorMessage);
		 return response;
	 }

	 public static String getUUIDFromMsisdnWithoutEx(String msisdn, String defaultValue) {
		 try {
			 return getUUIDFromMsisdn(msisdn);
		 } catch (Exception ignore) {
			 return defaultValue;
		 }
	 }

	 public static String getUUIDFromMsisdn(String msisdn) throws Exception {
		 return EncryptUtils.hmacSha1Enc("81BHyAUfMgCiu9I7XqArF1Bvy0o", get12DigitMsisdn(msisdn), 17) + "0";
	 }

	 public static String getRequestId() {
		 return String.format("%s-%s", ThreadLocalRandom.current().nextFloat(), System.currentTimeMillis());

	 }
	 public static JSONArray convertToIDNJSONArray(List<IdNameType> list) {
		 JSONArray result = new JSONArray();
		 if(list != null) {
			 for(IdNameType idNameType : list) {
				 result.add(idNameType.toJsonObject());
			 }
		 }
		 return result;
	 }

	public static String ConvertContentLangStringListToString(List<String> langList) {
		String languageString ="";
		if(langList!=null){
			boolean isFirst = true;
			for(String lang:langList){
				if(isFirst){
					languageString+= lang;
					isFirst=false;
				}
				else
					languageString+=MusicConstants.CONTENT_LANG_DELIMETER +lang;
			}
		}
		
		return languageString;
	}
	public static ArrayList<String> ConvertContentLangListToStringList(Collection<MusicContentLanguage> contentLanguages) {
		ArrayList<String> languagesList = new ArrayList<String>();
		if(contentLanguages!=null){
			for(MusicContentLanguage lang:contentLanguages){
				languagesList.add(lang.getId());
			}
		}
		return languagesList;
	}


	 public static String getSingleEmail(String email){
		 if(org.apache.commons.lang3.StringUtils.isNotBlank(email)){
			 String [] emails = email.split(",");
			 if(emails != null && emails.length >= 1){
				 return emails[0];
			 }
		 }
		 return null;
	 }

	 public static boolean equalLists(List<String> one, List<String> two){
		    if (one == null && two == null){
		        return true;
		    }
		    if((one == null && two != null) || (one != null && two == null) || (one.size() != two.size())){
		        return false;
		    }
		 	ArrayList<String> oneCopy = new ArrayList<String>(one);
		 	ArrayList<String> twoCopy = new ArrayList<String>(two);
		    Collections.sort(oneCopy);
		    Collections.sort(twoCopy);
		    return oneCopy.equals(twoCopy);
	}

	 public static String getSha1Hash(String key) {
		 MessageDigest mDigest;
		 try {
			 mDigest = MessageDigest.getInstance("SHA1");
			 byte[] result = mDigest.digest(key.getBytes());
			 StringBuffer sb = new StringBuffer();
			 for (int i = 0; i < result.length; i++) {
				 sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
			 }
			 return sb.toString();
		 } catch (NoSuchAlgorithmException e) {
			 logger.error("Error while retrieving SHA1 for key : " + key, e);
		 }
		 return null;
	 }
	 public static String getMCCs(JSONArray simInfoArray) {
		 List<String> mccs = new ArrayList<>();
		 for (Object sim : simInfoArray) {
			 JSONObject simJson = (JSONObject) sim;
			 if (simJson.containsKey("mcc")) {
				 String mccString = simJson.get("mcc").toString();
                 mccs.add(mccString);
			 }
		 }
		 return String.join(",", mccs);
	 }

	 public static Long getDobInMillis(User user) {
		 Long response = null;
		 String date = "";
		 if (!ObjectUtils.isEmpty(user)) {
			 try {
				 String day = user.getDob().getDate();
				 String month = user.getDob().getMonth();
				 String year = user.getDob().getYear();
				 //Date format yyyy-MM-dd
				 date = year.concat("-").concat(month).concat("-").concat(day);
				 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				 response = sdf.parse(date).getTime();
			 } catch (ParseException e) {
				 logger.info("Exception in parsing date : {} , Excep :{}", date, e.getMessage());
			 } catch (NullPointerException e) {
				 response = null;
			 }
		 }
		 return response;
	 }

	 public static boolean isIndentTypeValid(String intention) {
		 if (StringUtils.isNotBlank(intention) && "delete".equalsIgnoreCase(intention)) {
			 return true;
		 }
		 return false;
	 }

	 public static <T> T fromJackson(String json, Class<T> className) throws IOException {
		 long start = System.currentTimeMillis();
		 T readValue = objectMapper.readValue(json, className);
		 long diff = System.currentTimeMillis() - start;
		 logger.info("time taken in jackson deserialization (ms): " + diff);
		 return readValue;
	 }

	 public static ObjectMapper getObjectMapper() {
		 return objectMapper;
	 }

	 }
