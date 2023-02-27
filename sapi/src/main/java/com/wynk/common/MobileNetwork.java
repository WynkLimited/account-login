package com.wynk.common;

/**
 * reference : https://github.com/android/platform_frameworks_base/blob/master/telephony/java/android/telephony/TelephonyManager.java
 */
public class MobileNetwork {

    /** Network type is unknown */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    public static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    public static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    public static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B*/
    public static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0*/
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A*/
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT*/
    public static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
    public static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
    public static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B*/
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
    public static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
    public static final int NETWORK_TYPE_HSPAP = 15;
    /** Current network is GSM {@hide} */
    public static final int NETWORK_TYPE_GSM = 16;

    public static final int CONNECTION_TYPE_MOBILE      = 1;
    public static final int CONNECTION_TYPE_WIFI        = 2;

    /**
     * Check if the connection is fast
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType){
        if(type==CONNECTION_TYPE_WIFI){
            return true;
        }else if(type==CONNECTION_TYPE_MOBILE){
            switch(subType){
                case NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
			/*
			 * Above API level 7, make sure to set android:targetSdkVersion
			 * to appropriate level to use these
			 */
                case NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }else{
            return false;
        }
    }

}
