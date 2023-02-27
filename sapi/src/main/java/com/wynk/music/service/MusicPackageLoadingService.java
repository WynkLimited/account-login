package com.wynk.music.service;

import com.wynk.common.*;
import com.wynk.constants.MusicConstants;
import com.wynk.music.MusicCMSDataFetcher;
import com.wynk.music.constants.*;
import com.wynk.music.dto.*;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wynk.constants.JsonKeyNames.*;

/**
 * Created by anurag on 3/17/16.
 */
public class MusicPackageLoadingService {

	private static final Logger logger   =  LoggerFactory.getLogger(MusicPackageLoadingService.class.getCanonicalName());
	private Map<Circle,HashMap<String,List>> moduleOrderCircleMap = new HashMap<>();

	MusicCMSDataFetcher musicCMSDataFetcher;
	private List<MusicContentLanguage> fullyCuratedLanguages = new ArrayList<MusicContentLanguage>();

	public boolean cacheHomeScreenCircleWiseModuleOrderingTask() {
		System.out.println("Caching home screen module order...");
		try {
			String jsonString = musicCMSDataFetcher.getJSONStringFromS3(MusicConstants.ALMUSICAPP, "moduleOrder/circleModuleOrder.json");
			String languageJsonString = musicCMSDataFetcher.getJSONStringFromS3(MusicConstants.ALMUSICAPP, "moduleOrder/circleLanguageOrder.json");
			String fullyCuratedLanguagesJsonString = musicCMSDataFetcher.getJSONStringFromS3(MusicConstants.ALMUSICAPP, "moduleOrder/fullyCreatedLanguages.json");

			if(StringUtils.isEmpty(jsonString)) {
				logger.info("Emtpy home screen module ordering JSON string...");
				return true;
			}

			JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(jsonString);
			JSONObject languageJsonObject = (JSONObject) JSONValue.parseWithException(languageJsonString);

			for(Circle circle : Circle.values()) {
				HashMap circleDetailsMap = getCircleDetailsMap(circle,jsonObject,languageJsonObject);
				if(!CollectionUtils.isEmpty(circleDetailsMap))
					moduleOrderCircleMap.put(circle, circleDetailsMap);
			}
			if(StringUtils.isNotBlank(fullyCuratedLanguagesJsonString)){
				JSONArray fullyCuratedLanguagesJsonObject = (JSONArray) JSONValue.parseWithException(fullyCuratedLanguagesJsonString);
				Iterator iterator = fullyCuratedLanguagesJsonObject.iterator();
				while(iterator.hasNext()){
					String language = (String) iterator.next();
					fullyCuratedLanguages.add(MusicContentLanguage.getContentLanguageById(language));
				}
			}
			logger.info("Caching home screen circle - wise module ordering/renaming complete ");
			return true;
		} catch (Exception e) {
			System.out.println("Error while caching home screen module order : " + e.getMessage() + e.toString());
			return false;
		}
	}

	private HashMap getCircleDetailsMap(Circle circle, JSONObject jsonObject,JSONObject languageJsonObject) {
		JSONArray moduleOrderArray = (JSONArray) jsonObject.get(circle.getCircleId().toLowerCase());
		JSONArray languageArray = (JSONArray) languageJsonObject.get(circle.getCircleId().toLowerCase());
		HashMap circleDetailsMap = new HashMap();
		
		if(!CollectionUtils.isEmpty(moduleOrderArray)) {
			List<ModuleNameOrderType> moduleOrderList = new ArrayList<>();
			for(int i=0; i< moduleOrderArray.size(); i++) {
				JSONObject moduleObject = (JSONObject) moduleOrderArray.get(i);
				ModuleNameOrderType nameOrderType = new ModuleNameOrderType();
				nameOrderType.fromJsonObject(moduleObject);
				moduleOrderList.add(nameOrderType);
			}
			
			logger.info("Caching DEFAULT home screen module ordering/renaming of size [{}] " + moduleOrderList.size());
			if(!CollectionUtils.isEmpty(moduleOrderList)){
				circleDetailsMap.put(MODULE_ORDER, moduleOrderList);
			}
		}
		
		if(!CollectionUtils.isEmpty(languageArray)) {
			List<MusicContentLanguage> languageOrder = new ArrayList<>();
			List<MusicContentLanguage> defaultLanguages = new ArrayList<>();
			List<MusicContentLanguage> backUpLanguages = new ArrayList<>();
			for(int i=0; i< languageArray.size(); i++) {
				JSONObject languageObject = (JSONObject) languageArray.get(i);
				if(languageObject.get(LANGUAGE)!=null){
					String languageId = languageObject.get(LANGUAGE).toString();
					MusicContentLanguage contentLanguage = MusicContentLanguage.getContentLanguageById(languageId);
					languageOrder.add(contentLanguage);
					if(languageObject.get(IS_DEFAULT_LANGUAGE)!=null){
						boolean isDefault = (boolean)languageObject.get(IS_DEFAULT_LANGUAGE);
						if(isDefault)
							defaultLanguages.add(contentLanguage);
					}
					if(languageObject.get(IS_BACKUP_LANGUAGE)!=null){
						boolean isBackUpLanguage = (boolean)languageObject.get(IS_BACKUP_LANGUAGE);
						if(isBackUpLanguage)
							backUpLanguages.add(contentLanguage);
					}
				}
				
			}
			
			if(!CollectionUtils.isEmpty(languageOrder))
				circleDetailsMap.put(LANGUAGE_ORDER, languageOrder);
			if(!CollectionUtils.isEmpty(defaultLanguages))
				circleDetailsMap.put(DEFAULT_LANGUAGES, defaultLanguages);
			if(!CollectionUtils.isEmpty(backUpLanguages))
				circleDetailsMap.put(BACK_UP_LANGUAGES, backUpLanguages);
		}
		return circleDetailsMap;
	}

	public void setMusicCMSDataFetcher(MusicCMSDataFetcher musicCMSDataFetcher) {
		this.musicCMSDataFetcher = musicCMSDataFetcher;
	}

	public List<MusicContentLanguage> getLanguageOrderByCircle(Circle circle) {
		HashMap circleDetailMap = moduleOrderCircleMap.get(circle);
		if(circleDetailMap!=null){
			if(circleDetailMap.get(LANGUAGE_ORDER)!=null)
				return (List<MusicContentLanguage>) circleDetailMap.get(LANGUAGE_ORDER);
		}
		return null;
	}
	
	public List<MusicContentLanguage> getDefaultLanguagesByCircle(Circle circle) {
		HashMap circleDetailMap = moduleOrderCircleMap.get(circle);
		logger.info("Fetched circle details with {}", circleDetailMap);
		if(circleDetailMap!=null){
			if(circleDetailMap.get(DEFAULT_LANGUAGES)!=null)
				return (List<MusicContentLanguage>) circleDetailMap.get(DEFAULT_LANGUAGES);
		}
		return null;
	}
	
	public List<MusicContentLanguage> getBackUpLanguagesByCircle(Circle circle) {
		HashMap circleDetailMap = moduleOrderCircleMap.get(circle);
		if(circleDetailMap!=null){
			if(circleDetailMap.get(BACK_UP_LANGUAGES)!=null)
				return (List<MusicContentLanguage>) circleDetailMap.get(BACK_UP_LANGUAGES);
		}
		return null;
	}
	
	public List<ModuleNameOrderType> getModuleOrderByCircle(Circle circle) {
		HashMap circleDetailMap = moduleOrderCircleMap.get(circle);
		if(circleDetailMap!=null){
			if(circleDetailMap.get(MODULE_ORDER)!=null)
				return (List<ModuleNameOrderType>) circleDetailMap.get(MODULE_ORDER);
		}
		return null;
	}

	public List<MusicContentLanguage> getFullyCuratedLanguagesList(){
		return fullyCuratedLanguages;
	}
}

