package com.wynk.music.service;

import com.wynk.common.*;
import com.wynk.config.MusicConfig;
import com.wynk.db.S3StorageService;
import com.wynk.db.ShardedRedisServiceManager;
import com.wynk.music.*;
import com.wynk.music.constants.*;
import com.wynk.music.dto.*;
import com.wynk.service.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by anurag on 3/31/16.
 */
@Service
public class MusicContentService extends BaseService {

	private static final Logger logger               = LoggerFactory
			.getLogger(MusicContentService.class.getCanonicalName());

	@Autowired
	private MusicConfig musicConfig;

	@Autowired
	private ShardedRedisServiceManager musicShardRedisServiceManager;

	@Autowired
	protected S3StorageService s3ServiceManager;
	

	private MusicPackageLoadingService      musicPackageLoadingService;

	private MusicCMSDataFetcher musicCMSDataFetcher;

	@PostConstruct
	public void init()
	{
		if(!musicConfig.isEnableMusic())
			return;
		initCMSDataFetcher();
	}

	@Scheduled(fixedDelay = 1000 * 60 * 30)
	public void runCacheHomeScreenCircleWiseModuleOrderingTask(){
		cacheHomeScreenCircleWiseModuleOrderingTask();
	}

	public void cacheHomeScreenCircleWiseModuleOrderingTask() {
		MusicPackageLoadingService tempPackageLoadingService = new MusicPackageLoadingService();
		tempPackageLoadingService.setMusicCMSDataFetcher(musicCMSDataFetcher);
		boolean status = tempPackageLoadingService.cacheHomeScreenCircleWiseModuleOrderingTask();
		if(status) {
			musicPackageLoadingService = tempPackageLoadingService;
			System.out.println("caching Home Screen Circle wise module ordering complete !");
		} else {
			logger.warn("Error caching Home Screen Circle wise module ordering");
			System.out.println("[WARN] Error caching Home Screen Circle wise module ordering");
		}
	}

	public void initCMSDataFetcher() {
		musicCMSDataFetcher = new MusicCMSDataFetcher();
		musicCMSDataFetcher.setMusicConfig(musicConfig);
		if(musicConfig.isEnableMusic()) {
			musicCMSDataFetcher.setMusicShardRedisServiceManager(musicShardRedisServiceManager);
			musicCMSDataFetcher.setS3ServiceManager(s3ServiceManager);
		}
	}

	//In case of EmptyCircle and invalid use All circle 
	public List<MusicContentLanguage> getLanguageOrderByCircle(String circle){
		if (musicPackageLoadingService != null) {
			if(StringUtils.isBlank(circle))
				return musicPackageLoadingService.getLanguageOrderByCircle(Circle.ALL);
			//This will retun All circle if circle is invalid one
			Circle cr = Circle.getCircleById(circle);
			return musicPackageLoadingService.getLanguageOrderByCircle(cr);
		}
		return null;
	}
	//In case of EmptyCircle and invalid use All circle 
	public List<MusicContentLanguage> getDefaultLanguageByCircle(String circle){
		logger.info("Getting default language by <circle> {} " ,circle);
		if (musicPackageLoadingService != null) {
			if(StringUtils.isBlank(circle))
			{
				return musicPackageLoadingService.getDefaultLanguagesByCircle(Circle.ALL);
			}
			//This will retun All circle if circle is invalid one
			Circle cr = Circle.getCircleById(circle);
			logger.info("Calculated circle is {}",cr.getCircleName());
			return musicPackageLoadingService.getDefaultLanguagesByCircle(cr);
		}
		return null;
	}
	//In case of EmptyCircle and invalid use All circle 
	public List<MusicContentLanguage> getBackUpLanguageByCircle(String circle){
		if (musicPackageLoadingService != null) {
			if(StringUtils.isBlank(circle))
				return musicPackageLoadingService.getBackUpLanguagesByCircle(Circle.ALL);
			//This will retun All circle if circle is invalid one
			Circle cr = Circle.getCircleById(circle);
			return musicPackageLoadingService.getBackUpLanguagesByCircle(cr);
		}
		return null;
	}

	
	public List<ModuleNameOrderType> getModuleOrderByCircle(String circle){
		if(StringUtils.isBlank(circle))
			return musicPackageLoadingService != null ? musicPackageLoadingService.getModuleOrderByCircle( Circle.ALL) : null;
		//This will retun All circle if circle is invalid one
		Circle cr = Circle.getCircleById(circle);
		List<ModuleNameOrderType> moduleOrderList = musicPackageLoadingService != null ?
				musicPackageLoadingService.getModuleOrderByCircle(cr) : null;
		if(CollectionUtils.isEmpty(moduleOrderList))
			moduleOrderList = musicPackageLoadingService != null ?
					musicPackageLoadingService.getModuleOrderByCircle(Circle.ALL) : null;
		return moduleOrderList;
	}
	
	public List<MusicContentLanguage> getFullyCuratedLanguagesList(){
		return musicPackageLoadingService != null ? musicPackageLoadingService.getFullyCuratedLanguagesList(): null;
	}
}
