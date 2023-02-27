package com.wynk.constants;

/**
 * Created with IntelliJ IDEA. User: bhuvangupta Date: 20/09/12 Time: 11:23 PM To change this
 * template use File | Settings | File Templates.
 */
public class Constants {

    public static final int MD_NEWS = 0;
    public static final int MD_IMAGE = 1;
    public static final int MD_VIDEO = 2;
    public static final int MD_MUSIC = 3;
    public static final int MD_HT = 4;
    public static final int MD_GAME = 5;

    public static final String HT_ONLY = "ht_only";

    public static final String DBC_IMAGES = "imagescol";
    public static final String DBC_VIDEOS = "videoscol";
    public static final String DBC_NEWS = "newscol";
    public static final String DBC_MUSIC = "musiccol";
    public static final String DBC_GAMES = "gamescol";
    public static final String DBC_MOVIES = "micrositescol";
    public static final String DBC_CINEMAS = "cinemascol";
    public static final String DBC_ELECTION = "electioncol";
    public static final String DBC_CONTESTS = "contests_col";
    public static final String DBC_TWITTER = "twitter_col";
    
    
    public static final String DBC_CONTEST_PARTICIPANTS = "contest_participants_col";
    public static final String DBC_CONTEST_WINNERS = "contest_winners_col";

    public static final String SITE_ID = "site_id";
    public static final String CONTEST_ID = "contest_id";
    public static final String WINNER_LIST = "winnerList";
    public static final String CONTEST = "contest";
    public static final String ANSMAP = "ansMap";
    public static final String MSISDN = "msisdn";
    public static final String PROCESSED = "processed";
    public static final String INITIATED = "initiated";
    public static final String UNPROCESSED = "unprocessed";


    // Common URL param names
    public static final String URL_PARAM_LANG = "lang";
    public static final String URL_PARAM_PARTNER_ID = "partner_id";
    public static final String URL_PARAM_POS = "pos";
    public static final String URL_PARAM_COUNT = "n";
    public static final String URL_PARAM_QUERY = "s";
    public static final String URL_PARAM_UA = "ua";
    public static final String URL_PARAM_CATEGORY = "cat";
    public static final String URL_PARAM_ID = "id";
    public static final String URL_PARAM_CONTEXT = "context";
    public static final String URL_PARAM_TYPE = "type";
    public static final String URL_PARAM_SORT_DESC = "sort_desc";
    public static final String URL_PARAM_SORT_ASC = "sort_asc";
    
    //FTP server user/password voyatone
    public static final String FTP_USERNAME = "ubuntu";
    public static final String FTP_PASSWORD = "yStBkRJA4vau";
    
    //FTP server user/password assure
    public static final String FTP_USERNAME_ASSURE = "common";
    public static final String FTP_PASSWORD_ASSURE = "BfRFZs4CvT";
    
    //Site Constants
    public static final String ELECTION_SITE_ID = "4-E-1";
	public static final String FIFA_SITE_ID = "4-F-1";
	public static final String TOP_TEN_SITE_ID = "4-TT-1";
	
	//top ten constants
	public static final String DBC_TOPTEN = "toptenlistcol";

	
    //todo: load from DB
    public static final String STAR_NAMES_STR = "Katrina Kaif,Shah rukh Khan,Aamir Khan,Amitabh Bachchan,Anushka Sharma,Kareena,Preity Zinta,Sonakshi Sinha,Aishwarya Rai,Abhishek Bachchan,Kangana Ranaut,Hrithik Roshan,Mallika Sherawat,Ajay Devgn,Sanjay Dutt,Asin,Deepika Padukone,Akshay Kumar,Saif Ali Khan,Bipasha Basu,John Abraham,Emraan,R Madhavan,Shahid Kapoor,Salman Khan,Sonam Kapoor,Vidya Balan,Priyanka Chopra,Ranbir Kapoor,Imran Khan,Jacqueline,Parineeti Chopra,Ranveer Singh,Dia,Malaika Arora Khan,Trisha,Nayantara,Tamannaah,Joseph Vijay,Surya,Ileana,Vikram,Anushka Shetty,Puneet,Rajinikanth,Allu Arjun,Kamal Haasan,Upendra,Jiiva,Shriya Saran,Divya Spandana,Priyamani,Bhavana,Chiranjeevi,Prithviraj,Mammootty,Sudeep,Mohanlal,Samantha,Junior NTR,Mahesh Babu,Ram Charan Teja,Ravi Teja,Akkineni Nagarjuna,Prosenjit Chatterjee,Dev,Jeet,Koyel Mullick,Swastika,Anubhav Mohanty,Anu Choudhury,Archita Sahu,Ajith Kumar,Namitha,Richa Chadda,Manoj,Shweta Tiwari,Uttam Mohanty,Ragini Dwivedi,Diganth,Mahendra,Sachin Tendulkar,Gautam Gambhir,Irfan Pathan,Virat Kohli,Virender Sehwag,Yusuf Pathan,Yuvraj Singh,Zaheer Khan,Harbhajan Singh,Suresh Raina,Sourav Ganguly,Ravindra Jadeja,Rohit Sharma";
    
    public static final String X_CLIENT = "x-client";

    public static final String CRICKET_KEYWORDS = "cricket";
    public static final String CRICKET_CATEGORY_ID = "70";
    public static final String SPORTS_CATEGORY_ID = "40";
    public static final String CONTENT_PARTNER_IANS = "ians";
    /* TextToImage conversion constants */
    public static enum TEXT_IMAGE_TYPE {
        PNG, JPEG
    }

    ;

    /* S3 Buckets */
    public static enum S3_BUCKETS {
        IMAGE, VIDEO, TEXTIMAGE, AVATARS, RDLOGS, MYAIRTELAPP
    }

    public static final int[] TEXT_IMAGE_WIDTHS = {120, 176};
    
    /* Postpaid Interstitial Constants */
    public static final String KEY_BOOSTER_ITEM_CODE = "SC";
    public static final String KEY_BOOSTER_ITEM_PRICE = "PP";
    public static final String KEY_BOOSTER_ITEM_DESC = "DESC";
    public static final String KEY_BOOSTER_ITEM_ID = "ITMID";
    public static final String DATA_GB = "GB";
    public static final String DATA_MB = "MB";
    public static final String DATA_KB = "KB";
    /* Postpaid Interstitial Constants */

    public static final String AIRTEL = "airtel";

    public static final String EMPTY_STRING = "";
    public static final String LOGIN = "login";
    public static final String DELETE = "delete";

    public interface RequestHeaders {
        String X_BSY_UTKN = "x-bsy-utkn";
        String X_BSY_DID = "x-bsy-did";
        String X_BSY_CID = "x-bsy-cid";

        String X_BSY_DATE = "x-bsy-date";
        String X_BSY_ATKN = "x-bsy-atkn";
        String UID = "uid";
        String X_BSY_COO = "x-bsy-coo";
        String X_BSY_COA = "x-bsy-coa";
    }

    public interface ResponseKeys
    {
        String SUCCESS = "success";
    }

    public interface ACTION_TYPE
    {
        String ACCOUNT_DELETE= "ACCOUNT_DELETE";
    }

    public interface GEO_LOCATION_HEADER
    {
        String CloudFront_Viewer_Country = "CloudFront-Viewer-Country";
        String CloudFront_Viewer_Country_Region = "CloudFront-Viewer-Country-Region";
        String CloudFront_Viewer_Address = "CloudFront-Viewer-Address";
    }

}
