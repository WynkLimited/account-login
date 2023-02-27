package com.wynk.notification;

import com.wynk.common.*;
import com.wynk.constants.JsonKeyNames;
import com.wynk.constants.NotificationSubType;
import com.wynk.dto.BaseObject;
import com.wynk.music.constants.MusicContentLanguage;
import com.wynk.music.constants.MusicContentType;
import com.wynk.music.constants.WorkflowState;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;

/**
 * Created by bhuvangupta on 11/05/14.
 */
public class MusicAdminNotification extends BaseObject {

	/*
	 * { text : { en: hi: ta: te: } screen itemId: itemType: time:
	 *
	 * regionalLang: }
	 */
	private String notificationId;
	private String title;
	private String text;
	private String nonRichText;
	private int deviceType; // 0 means all, 1 android, 2 iOS
	private ScreenCode targetScreen;
	private String aok = "Go";
	private NotificationSubType notificationSubType = NotificationSubType.CONTENT;
	private int updatedCount;
	private String targetContentId;
	private MusicContentType targetContentType;
	private MusicContentLanguage targetContentLanguage;
	private List<MusicContentLanguage> targetContentLanguages;
	private List<MusicContentLanguage> exclusionLangList;
	private Language targetAppLanguage;
	private long sentTime; // -1 for immediate
	private NotificationTimeSlot sentTimeSlot;
	private boolean save = false; // whether save the notification in userdb
	private boolean processed; // status whether the notification has been
								// processed
	private long processedTime; // time when the notification was processed
	private WorkflowState workflowState;
	private Set<Circle> targetCircles;
	private NotificationReceiver notificationReceiver;
	private String imgUrl;
	private String ngtUrl;
	private String customFileUrl;

	private String queryString;

	private String unrDays;

	private List<NotificationAction> action;
	private ActionOpen actionOpen;
	private String browserUrl;
	private Boolean showPacks;
	private Boolean showImage;
	private String iconUrl;

	public static ArrayList<Integer> androidDeviceTypes = new ArrayList<Integer>();
	{
		androidDeviceTypes.add(0);
		androidDeviceTypes.add(1);
	}

	public static ArrayList<Integer> iOSDeviceTypes = new ArrayList<Integer>();
	{
		iOSDeviceTypes.add(0);
		iOSDeviceTypes.add(2);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getNonRichText() {
		return nonRichText;
	}

	public void setNonRichText(String nonRichText) {
		this.nonRichText = nonRichText;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public ScreenCode getTargetScreen() {
		return targetScreen;
	}

	public void setTargetScreen(ScreenCode targetScreen) {
		this.targetScreen = targetScreen;
	}

	public String getTargetContentId() {
		return targetContentId;
	}

	public void setTargetContentId(String targetContentId) {
		this.targetContentId = targetContentId;
	}

	public MusicContentType getTargetContentType() {
		return targetContentType;
	}

	public void setTargetContentType(MusicContentType targetContentType) {
		this.targetContentType = targetContentType;
	}

	public MusicContentLanguage getTargetContentLanguage() {
		return targetContentLanguage;
	}

	public void setTargetContentLanguage(MusicContentLanguage targetContentLanguage) {
		this.targetContentLanguage = targetContentLanguage;
	}

	public long getSentTime() {
		return sentTime;
	}

	public void setSentTime(long sentTime) {
		this.sentTime = sentTime;
	}

	public boolean isProcessed() {
		return processed;
	}

	public void setProcessed(boolean processed) {
		this.processed = processed;
	}

	public long getProcessedTime() {
		return processedTime;
	}

	public void setProcessedTime(long processedTime) {
		this.processedTime = processedTime;
	}

	public Language getTargetAppLanguage() {
		return targetAppLanguage;
	}

	public void setTargetAppLanguage(Language targetAppLanguage) {
		this.targetAppLanguage = targetAppLanguage;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
	}

	public Set<Circle> getTargetCircles() {
		return targetCircles;
	}

	public void setTargetCircles(Set<Circle> targetCircles) {
		this.targetCircles = targetCircles;
	}

	public List<MusicContentLanguage> getTargetContentLanguages() {
		return targetContentLanguages;
	}

	public void setTargetContentLanguages(List<MusicContentLanguage> targetContentLanguages) {
		this.targetContentLanguages = targetContentLanguages;
	}

	public NotificationReceiver getNotificationReceiver() {
		return notificationReceiver;
	}

	public void setNotificationReceiver(NotificationReceiver notificationReceiver) {
		this.notificationReceiver = notificationReceiver;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public List<NotificationAction> getAction() {
		return action;
	}

	public void setAction(List<NotificationAction> action) {
		this.action = action;
	}

	public List<MusicContentLanguage> getExclusionLangList() {
		return exclusionLangList;
	}

	public void setExclusionLangList(List<MusicContentLanguage> exclusionLangList) {
		this.exclusionLangList = exclusionLangList;
	}

	public ActionOpen getActionOpen() {
		return actionOpen;
	}

	public void setActionOpen(ActionOpen actionOpen) {
		this.actionOpen = actionOpen;
	}

	public String getBrowserUrl() {
		return browserUrl;
	}

	public void setBrowserUrl(String browserUrl) {
		this.browserUrl = browserUrl;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public Boolean getShowPacks() {
		return showPacks;
	}

	public void setShowPacks(Boolean showPacks) {
		this.showPacks = showPacks;
	}

	public Boolean getShowImage() {
		return showImage;
	}

	public void setShowImage(Boolean showImage) {
		this.showImage = showImage;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	@Override
	public void fromJsonObject(JSONObject jsonObj) {
		super.fromJsonObject(jsonObj);

		if (jsonObj.get("workflowState") != null) {
			setWorkflowState(WorkflowState.valueOf((String) jsonObj.get("workflowState")));
		}
		if (jsonObj.get("text") != null) {
			text = (String) jsonObj.get("text");
		}
		if (jsonObj.get("nonRichText") != null) {
			nonRichText = (String) jsonObj.get("nonRichText");
		}
		if (jsonObj.get("notificationId") != null) {
			notificationId = (String) jsonObj.get("notificationId");
		}
		if (jsonObj.get("title") != null) {
			title = (String) jsonObj.get("title");
		}
		if (jsonObj.get("deviceType") != null) {
			deviceType = ((Number) jsonObj.get("deviceType")).intValue();
		}
		if (jsonObj.get("scr") != null) {
			targetScreen = ScreenCode.getScreenCodeByName((String) jsonObj.get("scr"));
		}
		if (jsonObj.get("contentId") != null) {
			targetContentId = (String) jsonObj.get("contentId");
		}
		if (jsonObj.get("contentType") != null) {
			targetContentType = MusicContentType.getContentTypeId((String) jsonObj.get("contentType"));
		}
		if (jsonObj.get(JsonKeyNames.CONTENT_LANGUAGE) != null) {
			targetContentLanguage = MusicContentLanguage
					.getContentLanguageById((String) jsonObj.get(JsonKeyNames.CONTENT_LANGUAGE));
		}
		if (jsonObj.get("sentTime") != null) {
			sentTime = ((Number) jsonObj.get("sentTime")).longValue();
		} else
			sentTime = System.currentTimeMillis();

		if (jsonObj.get("sentTimeSlot") != null) {
			sentTimeSlot = NotificationTimeSlot.getNotificationTimeSlot((String) jsonObj.get("sentTimeSlot"));
		} else
			sentTimeSlot = NotificationTimeSlot.MORNING;

		if (jsonObj.get("save") != null) {
			save = (Boolean) jsonObj.get("save");
		}

		if (jsonObj.get("processed") != null) {
			processed = (Boolean) jsonObj.get("processed");
		}

		if (jsonObj.get("processedTime") != null) {
			processedTime = ((Number) jsonObj.get("processedTime")).longValue();
		}

		if (jsonObj.get(JsonKeyNames.LANGUAGE) != null) {
			targetAppLanguage = Language.getLanguageById((String) jsonObj.get(JsonKeyNames.LANGUAGE));
		}

		if (jsonObj.get("imgUrl") != null) {
			imgUrl = (String) jsonObj.get("imgUrl");

		}
		if (jsonObj.get("ngtUrl") != null) {
			ngtUrl = (String) jsonObj.get("ngtUrl");
		}

		if (jsonObj.get("browserUrl") != null) {
			browserUrl = (String) jsonObj.get("browserUrl");
		}

		if (jsonObj.get("searchQuery") != null) {
			queryString = (String) jsonObj.get("searchQuery");
		}

		if (jsonObj.get("customFileUrl") != null) {
			customFileUrl = (String) jsonObj.get("customFileUrl");
		}

		if (jsonObj.get("notificationReceiver") != null) {
			setNotificationReceiver(
					NotificationReceiver.getNotificationReceiver((String) jsonObj.get("notificationReceiver")));
		} else
			setNotificationReceiver(NotificationReceiver.MAU);

		if (jsonObj.get("actionOpen") != null) {
			setActionOpen(ActionOpen.fromOpcode(((Number) jsonObj.get("actionOpen")).intValue()));
		} else
			setActionOpen(ActionOpen.ALERT);

		if (jsonObj.get(JsonKeyNames.CIRCLES) != null) {
			Set<Circle> circleList = new HashSet<>();
			JSONArray circlesArray = ((JSONArray) jsonObj.get(JsonKeyNames.CIRCLES));
			for (int i = 0; i < circlesArray.size(); i++) {
				String circleObj = (String) circlesArray.get(i);
				Circle circle = Circle.getCircleById(circleObj);
				circleList.add(circle);
				setTargetCircles(circleList);
			}
		}

		if (jsonObj.get(JsonKeyNames.CONTENT_LANGUAGES) != null) {
			List<MusicContentLanguage> clList = new ArrayList<>();
			JSONArray clArray = ((JSONArray) jsonObj.get(JsonKeyNames.CONTENT_LANGUAGES));
			for (int i = 0; i < clArray.size(); i++) {
				String clangObj = (String) clArray.get(i);
				MusicContentLanguage circle = MusicContentLanguage.getContentLanguageById(clangObj);
				clList.add(circle);
			}
			setTargetContentLanguages(clList);
		}

		if (jsonObj.get("exclusionLangList") != null) {
			List<MusicContentLanguage> elList = new ArrayList<>();
			JSONArray elArray = ((JSONArray) jsonObj.get("exclusionLangList"));
			for (int i = 0; i < elArray.size(); i++) {
				String elLangObj = (String) elArray.get(i);
				MusicContentLanguage circle = MusicContentLanguage.getContentLanguageById(elLangObj);
				elList.add(circle);
			}
			setExclusionLangList(elList);
		}

		if (jsonObj.get("action") != null) {
			List<NotificationAction> actionList = new ArrayList<>();
			JSONArray actionArr = ((JSONArray) jsonObj.get("action"));
			for (int i = 0; i < actionArr.size(); i++) {
				String action = (String) actionArr.get(i);
				NotificationAction notificationAction = NotificationAction.getNotificationAction(action);
				if (notificationAction != null) {
					actionList.add(notificationAction);
					setAction(actionList);
				}
			}
		}

		if (jsonObj.get("unrDays") != null) {
			unrDays = (String) jsonObj.get("unrDays");
		}

		if(jsonObj.get("showPacks") != null){
			showPacks = (Boolean)jsonObj.get("showPacks");
		}

		if(jsonObj.get("showImage")!= null){
			showImage = (Boolean) jsonObj.get("showImage");
		}

		if(jsonObj.get("iconUrl")!= null){
			iconUrl = (String) jsonObj.get("iconUrl");
		}
	}

	@Override
	public JSONObject toJsonObject() {
		JSONObject jsonObject = super.toJsonObject();
		if (workflowState != null)
			jsonObject.put("workflowState", workflowState.toString());
		jsonObject.put("text", getText());
		jsonObject.put("nonRichText", getNonRichText());
		jsonObject.put("title", getTitle());
		jsonObject.put("deviceType", getDeviceType());
		if (targetScreen != null)
			jsonObject.put("scr", targetScreen.name());
		jsonObject.put("contentId", getTargetContentId());
		if (getTargetContentType() != null)
			jsonObject.put("contentType", getTargetContentType().name());
		if (getTargetContentLanguage() != null)
			jsonObject.put(JsonKeyNames.CONTENT_LANGUAGE, getTargetContentLanguage().getId());
		jsonObject.put("sentTime", getSentTime());

		if (sentTimeSlot != null)
			jsonObject.put("sentTimeSlot", sentTimeSlot.name());

		jsonObject.put("save", isSave());
		jsonObject.put("processed", isProcessed());
		jsonObject.put("processedTime", getProcessedTime());
		jsonObject.put("notificationId", notificationId);
		jsonObject.put("imgUrl", getImgUrl());
		jsonObject.put("ngtUrl", getNgtUrl());
		jsonObject.put("customFileUrl", getCustomFileUrl());
		jsonObject.put("browserUrl", getBrowserUrl());

		jsonObject.put("searchQuery", getQueryString());

		jsonObject.put("unrDays", getUnrDays());
		if(getShowImage() != null){
			jsonObject.put("showImage",getShowImage());
		}
		if(getIconUrl() != null){
			jsonObject.put("iconUrl",getIconUrl());
		}
		if(getShowPacks() != null){
			jsonObject.put("showPacks", getShowPacks());
		}

		if (notificationReceiver != null)
			jsonObject.put("notificationReceiver", notificationReceiver.name());
		if (actionOpen != null)
			jsonObject.put("actionOpen", actionOpen.getOpcode());

		if (getTargetAppLanguage() != null) {
			jsonObject.put(JsonKeyNames.LANGUAGE, getTargetAppLanguage().getId());
		}

		if (getAction() != null && getAction().size() > 0) {
			JSONArray actionJson = new JSONArray();
			Iterator<NotificationAction> actionItr = action.iterator();
			while (actionItr.hasNext()) {
				NotificationAction notificationAction = actionItr.next();
				actionJson.add(notificationAction.name());
			}
			jsonObject.put("action", actionJson);
		}

		if (getTargetCircles() != null && getTargetCircles().size() > 0) {
			JSONArray circleJson = new JSONArray();
			Iterator<Circle> circleItr = targetCircles.iterator();
			while (circleItr.hasNext()) {
				Circle circle = circleItr.next();
				circleJson.add(circle.getCircleId());
			}
			jsonObject.put(JsonKeyNames.CIRCLES, circleJson);
		}

		if (getTargetContentLanguages() != null && getTargetContentLanguages().size() > 0) {
			JSONArray clangJson = new JSONArray();
			Iterator<MusicContentLanguage> clangItr = targetContentLanguages.iterator();
			while (clangItr.hasNext()) {
				MusicContentLanguage clang = clangItr.next();
				clangJson.add(clang.getId());
			}
			jsonObject.put(JsonKeyNames.CONTENT_LANGUAGES, clangJson);
		}

		if (getExclusionLangList() != null && getExclusionLangList().size() > 0) {
			JSONArray elLangJson = new JSONArray();
			Iterator<MusicContentLanguage> elLangItr = exclusionLangList.iterator();
			while (elLangItr.hasNext()) {
				MusicContentLanguage lang = elLangItr.next();
				elLangJson.add(lang.getId());
			}
			jsonObject.put("exclusionLangList", elLangJson);
		}
		return jsonObject;
	}

	@Override
	public void fromJson(String json) throws Exception {
		Object obj = JSONValue.parseWithException(json);
		JSONObject valueMap = (JSONObject) obj;
		fromJsonObject(valueMap);
	}

	@Override
	public String toJson() throws Exception {
		JSONObject obj = toJsonObject();

		return obj.toJSONString();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public WorkflowState getWorkflowState() {
		return workflowState;
	}

	public void setWorkflowState(WorkflowState workflowState) {
		this.workflowState = workflowState;
	}

	public NotificationTimeSlot getSentTimeSlot() {
		return sentTimeSlot;
	}

	public void setSentTimeSlot(NotificationTimeSlot sentTimeSlot) {
		this.sentTimeSlot = sentTimeSlot;
	}

	private String androidNougatMessage = null;
	private String androidRichMessage = null;
	private String androidNonRichMessage = null;

	public JSONObject getRichAndroidMessageJsonObject() {
		return getAndroidMessageJsonObject(text);
	}

	public JSONObject getNonRichAndroidMessageJsonObject() {
		return getAndroidMessageJsonObject(nonRichText);
	}

	public JSONObject getNougatAndroidMessageJsonObject() {
		return getAndroidMessageJsonObject(text);
	}

	private JSONObject getAndroidMessageJsonObject(String text) {
		// notification json
		JSONObject notificationJson = new JSONObject();

		String id = getNotificationId();
		if (StringUtils.isBlank(id))
			id = "" + System.currentTimeMillis();
		notificationJson.put("id", id);

		if (actionOpen != null)
			notificationJson.put("ao", actionOpen.getOpcode());
		else
			notificationJson.put("ao", ActionOpen.ALERT.getOpcode());

		notificationJson.put("ac", ActionClose.PUSH_NOTIFICATION.getOpcode());

		if (getImgUrl() != null) {
			notificationJson.put("img", getImgUrl());
		}
		notificationJson.put("notificationSubType", notificationSubType.getValue());

		if (getNgtUrl() != null)
			notificationJson.put("img_nougat", getNgtUrl());

		if (getAction() != null && getAction().size() > 0) {
			JSONArray actionJson = new JSONArray();
			Iterator<NotificationAction> actionItr = action.iterator();
			while (actionItr.hasNext()) {
				NotificationAction notificationAction = actionItr.next();
				actionJson.add(notificationAction.getOpcode());
			}
			notificationJson.put("action", actionJson);
		}

		JSONObject tgtObj = new JSONObject();
		if (getTargetScreen() != null) {
			tgtObj.put("scr", getTargetScreen().getOpcode());
		} else {
			tgtObj.put("scr", ScreenCode.HOME.getOpcode());
		}

		if (!org.apache.commons.lang3.StringUtils.isEmpty(getTargetContentId())) {
			JSONObject metaObj = new JSONObject();
			metaObj.put("id", getTargetContentId());
			if (getBrowserUrl() != null) {
				metaObj.put("url", getBrowserUrl());
				metaObj.put("title", getTitle());
			}
			if (getTargetContentType() != null)
				metaObj.put("type", getTargetContentType().name());
			if (getQueryString() != null)
				metaObj.put("qry", getQueryString());
			  metaObj.put("count", getUpdatedCount());

			tgtObj.put("meta", metaObj);
		} else {
			JSONObject metaObj = new JSONObject();
			if (getBrowserUrl() != null) {
				metaObj.put("title", getTitle());
				metaObj.put("url", getBrowserUrl());
			}
			if (getTargetContentType() != null)
				metaObj.put("type", getTargetContentType().name());
			if (getQueryString() != null)
				metaObj.put("qry", getQueryString());
			  metaObj.put("count", getUpdatedCount());

			tgtObj.put("meta", metaObj);
		}
		notificationJson.put("tgt", tgtObj);
		// todo: handle translieration
		notificationJson.put("msg", text);
		notificationJson.put("aok", aok);
		notificationJson.put("acncl", "Cancel");
		String notificationTitle = getTitle();
		if (StringUtils.isBlank(notificationTitle)) {
			if (getTargetScreen() != null && getTargetScreen().getOpcode() == ScreenCode.PLAYSTORE.getOpcode())
				notificationTitle = "Rate Wynk Music";
			else
				notificationTitle = "Wynk Music";
		}
		notificationJson.put("attl", notificationTitle);
		if(getShowImage() != null){
			notificationJson.put("showImage",getShowImage());
		}
		if(getShowPacks() != null){
			notificationJson.put("showPacks", getShowPacks());
		}
		if(getIconUrl() != null){
			notificationJson.put("iconUrl",getIconUrl());
		}
		return notificationJson;
	}

	public String getAndroidRichMessage() {
		if (!StringUtils.isBlank(androidRichMessage))
			return androidRichMessage;

		androidRichMessage = getRichAndroidMessageJsonObject().toJSONString();
		return androidRichMessage;
	}

	public String getAndroidNonRichMessage() {
		if (!StringUtils.isBlank(androidNonRichMessage))
			return androidNonRichMessage;

		androidNonRichMessage = getNonRichAndroidMessageJsonObject().toJSONString();
		return androidNonRichMessage;
	}

	public String getNougatAndroidMessage() {
		if (!StringUtils.isBlank(androidNougatMessage))
			return androidNougatMessage;

		androidNougatMessage = getNougatAndroidMessageJsonObject().toJSONString();
		return androidNougatMessage;
	}

	Map<String, Object> apnsMsgProps = null;

	public Map<String, Object> getSilentAppleMessage() {
		Map<String, Object> apnsMsgProperties = new HashMap<>();
		String id = getNotificationId();
		if (StringUtils.isBlank(id))
			id = "" + System.currentTimeMillis();
		apnsMsgProperties.put("id", id);
		return apnsMsgProperties;
	}

	public Map<String, Object> getAppleMessage() {
		if (apnsMsgProps != null)
			return apnsMsgProps;

		Map<String, Object> apnsMsgProperties = new HashMap<>();
		apnsMsgProperties.put("scr", getTargetScreen().getOpcode());
		String id = getNotificationId();
		if (StringUtils.isBlank(id))
			id = "" + System.currentTimeMillis();
		apnsMsgProperties.put("id", id);

		String notificationTitle = getTitle();
		if (StringUtils.isBlank(notificationTitle)) {
			if (getTargetScreen() != null && getTargetScreen().getOpcode() == ScreenCode.PLAYSTORE.getOpcode())
				notificationTitle = "Rate Wynk Music";
			else
				notificationTitle = "Wynk Music";
		}
		apnsMsgProperties.put("title", notificationTitle);
		apnsMsgProperties.put("sound", "gn.wav");
		// todo: pick from notification
		apnsMsgProperties.put("ao", ActionOpen.ALERT.getOpcode());

		if (!org.apache.commons.lang3.StringUtils.isBlank(getTargetContentId()))
			apnsMsgProperties.put("oid", getTargetContentId());

		if (getBrowserUrl() != null)
			apnsMsgProperties.put("url", getBrowserUrl());

		if (getQueryString() != null)
			apnsMsgProperties.put("qry", getQueryString());

		if (getTargetContentType() != null)
			apnsMsgProperties.put("type", getTargetContentType().name());

		// apnsMsgProperties.put("body",text);
		// apnsMsgProperties.put("action-loc-key","ok");
		// apnsMsgProperties.put("sound","gn.wav");

		apnsMsgProps = apnsMsgProperties;
		return apnsMsgProps;
	}

	public String getSNSMessage() {
		String text = getText();

		// notification json
		JSONObject notificationJson = new JSONObject();
		JSONObject appleAPSJson = new JSONObject();

		String id = "" + System.currentTimeMillis();
		notificationJson.put("id", id);
		appleAPSJson.put("id", id);
		notificationJson.put("ao", ActionOpen.ALERT.getOpcode());
		appleAPSJson.put("ao", ActionOpen.ALERT.getOpcode());
		notificationJson.put("ac", ActionClose.PUSH_NOTIFICATION.getOpcode());
		JSONObject tgtObj = new JSONObject();
		if (getTargetScreen() != null) {
			tgtObj.put("scr", getTargetScreen().getOpcode());
			appleAPSJson.put("scr", getTargetScreen().getOpcode());
		} else {
			tgtObj.put("scr", ScreenCode.HOME.getOpcode());
		}

		if (!org.apache.commons.lang3.StringUtils.isEmpty(getTargetContentId())) {
			JSONObject metaObj = new JSONObject();
			metaObj.put("id", getTargetContentId());
			tgtObj.put("meta", metaObj);
			appleAPSJson.put("oid", getTargetContentId());
		} else {
			JSONObject metaObj = new JSONObject();
			tgtObj.put("meta", metaObj);
		}
		notificationJson.put("tgt", tgtObj);
		// todo: handle translieration
		notificationJson.put("msg", text);
		notificationJson.put("aok", aok);
		notificationJson.put("acncl", "Cancel");
		notificationJson.put("attl", "Wynk Music"); // notification title

		JSONObject msgJson = new JSONObject();
		msgJson.put("msg", notificationJson.toJSONString());

		// SNS Object
		JSONObject snsJson = new JSONObject();
		snsJson.put("default", text);

		JSONObject gcmJson = new JSONObject();
		gcmJson.put("data", msgJson);

		snsJson.put("GCM", gcmJson.toJSONString());

		// apple notification
		if (org.apache.commons.lang3.StringUtils.isEmpty(getTitle()))
			appleAPSJson.put("title", "Wynk Music");
		else
			appleAPSJson.put("title", getTitle());
		JSONObject appleAlertJson = new JSONObject();
		JSONObject alertJson = new JSONObject();
		alertJson.put("body", text);
		alertJson.put("action-loc-key", "ok");

		appleAlertJson.put("alert", alertJson);
		appleAPSJson.put("aps", appleAlertJson);

		snsJson.put("APNS", appleAPSJson.toJSONString());

		return snsJson.toJSONString();
	}

	public boolean isValidContentLang(List<String> contentLangs) {
		boolean clangTargetting = (getTargetContentLanguages() != null && getTargetContentLanguages().size() > 0)
				&& !(getTargetContentLanguages().contains(MusicContentLanguage.ENGLISH)
						|| getTargetContentLanguages().contains(MusicContentLanguage.HINDI));

		if (!clangTargetting)
			return true;

		if (contentLangs == null || contentLangs.size() == 0)
			return false;

		for (int i = 0; i < contentLangs.size(); i++) {
			String clang = contentLangs.get(i);
			MusicContentLanguage contentLanguage = MusicContentLanguage.getContentLanguageById(clang);
			if ((contentLanguage == null))
				continue;
			if (getTargetContentLanguages().contains(contentLanguage))
				return true;
		}
		return false;
	}

	public boolean isExclusionLangListUser(List<String> contentLangs) {

		return false;
	}

	@Override
	public String toString() {
		final int maxLen = 3;
		return "MusicAdminNotification [" + (notificationId != null ? "notificationId=" + notificationId + ", " : "")
				+ (title != null ? "title=" + title + ", " : "") + (text != null ? "text=" + text + ", " : "")
				+ "deviceType=" + deviceType + ", "
				+ (targetScreen != null ? "targetScreen=" + targetScreen + ", " : "")
				+ (targetContentId != null ? "targetContentId=" + targetContentId + ", " : "")
				+ (targetContentType != null ? "targetContentType=" + targetContentType + ", " : "")
				+ (targetContentLanguage != null ? "targetContentLanguage=" + targetContentLanguage + ", " : "")
				+ (targetContentLanguages != null
						? "targetContentLanguages=" + toString(targetContentLanguages, maxLen) + ", " : "")
				+ (exclusionLangList != null ? "exclusionLangList=" + toString(exclusionLangList, maxLen) + ", " : "")
				+ (targetAppLanguage != null ? "targetAppLanguage=" + targetAppLanguage + ", " : "") + "sentTime="
				+ sentTime + ", sentTimeSlot=" + sentTimeSlot + ", save=" + save + ", processed=" + processed
				+ ", processedTime=" + processedTime + ", "
				+ (workflowState != null ? "workflowState=" + workflowState + ", " : "")
				+ (targetCircles != null ? "targetCircles=" + toString(targetCircles, maxLen) + ", " : "")
				+ (notificationReceiver != null ? "notificationReceiver=" + notificationReceiver + ", " : "")
				+ (imgUrl != null ? "imgUrl=" + imgUrl + ", " : "") + (ngtUrl != null ? "ngtUrl=" + ngtUrl + ", " : "")
				+ (customFileUrl != null ? "customFileUrl=" + customFileUrl + ", " : "")
				+ (action != null ? "action=" + toString(action, maxLen) + ", " : "")
				+ (androidRichMessage != null ? "androidNonMessage=" + androidRichMessage + ", " : "")
				+ (androidNonRichMessage != null ? "androidNonMessage=" + androidNonRichMessage + ", " : "")
				+ (apnsMsgProps != null ? "apnsMsgProps=" + toString(apnsMsgProps.entrySet(), maxLen) : "") + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0)
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	public static List<MusicAdminNotification> sortUsingPriority(List<MusicAdminNotification> notifications) {
		Collections.sort(notifications, new Comparator<MusicAdminNotification>() {

			public int compare(MusicAdminNotification p1, MusicAdminNotification p2) {
				return Integer.valueOf(p1.getNotificationReceiver().getPriority())
						.compareTo(p2.getNotificationReceiver().getPriority());
			}
		});
		return notifications;
	}

	public String getCustomFileUrl() {
		return customFileUrl;
	}

	public void setCustomFileUrl(String customFileUrl) {
		this.customFileUrl = customFileUrl;
	}

	public String getUnrDays() {
		return unrDays;
	}

	public void setUnrDays(String unrDays) {
		this.unrDays = unrDays;
	}

	public String getNgtUrl() {
		return ngtUrl;
	}

	public void setNgtUrl(String ngtUrl) {
		this.ngtUrl = ngtUrl;
	}

	public NotificationSubType getNotificationSubType() {
		return notificationSubType;
	}

	public void setNotificationSubType(NotificationSubType notificationSubType) {
		this.notificationSubType = notificationSubType;
	}

	public int getUpdatedCount() {
		return updatedCount;
	}

	public void setUpdatedCount(int updatedCount) {
		this.updatedCount = updatedCount;
	}

  public String getAok() {
    return aok;
  }

  public void setAok(String aok) {
    this.aok = aok;
  }
}
