package com.wynk.common;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum Circle {

    ALL("all", "All",CircleType.THREE_G), ANDHRAPRADESH("ap", "Andhra Pradesh",CircleType.THREE_G), ASSAM("as", "Assam",CircleType.THREE_G), BIHAR("bh", "Bihar and Jharkhand",CircleType.THREE_G),
    CHENNAI("ch", "Chennai",CircleType.THREE_G), DELHI("dl", "Delhi",CircleType.THREE_G), GUJRAT("gj", "Gujarat",CircleType.ICR), HIMACHAL("hp", "Himachal Pradesh",CircleType.THREE_G),
    HARYANA("hr", "Haryana",CircleType.ICR), JAMMUKASHMIR("jk", "Jammu and Kashmir",CircleType.THREE_G), KARNATAKA("ka", "Karnataka",CircleType.THREE_G), KERALA("kl", "Kerala",CircleType.ICR),
    KOLKATA("ko", "Kolkata",CircleType.ICR), MUMBAI("mb", "Mumbai",CircleType.THREE_G), MAHARASHTRA("mh", "Maharashtra and Goa",CircleType.ICR), MADHYAPRADESH("mp", "MP and Chhattisgarh", "MP and Chattisgarh",CircleType.ICR),
    NORTHEAST("ne", "North East",CircleType.THREE_G), ORISSA("or", "Orissa",CircleType.ICR), PUNJAB("pb", "Punjab",CircleType.ICR), RAJSTHAN("rj", "Rajsthan", "Rajasthan",CircleType.THREE_G),
    TAMILNADU("tn", "Tamilnadu", "Tamil Nadu",CircleType.THREE_G), UPEAST("ue", "UP East",CircleType.ICR), UPWEST("uw", "UP West", "UP West and Uttaranchal",CircleType.THREE_G), WESTBENGAL("wb", "West Bengal",CircleType.THREE_G),
    //These circles are only present in telemedia
    NORTHUPWEST("nuw", "North - UP West",CircleType.THREE_G),NORTHHARYANA("nhr", "North Haryana",CircleType.THREE_G),
    SRILANKA("sl","Sri Lanka",CircleType.THREE_G),
    NORTHRAJASTHAN("nrj", "North Rajasthan",CircleType.THREE_G),NORTHPUNJAB("npb", "North Punjab",CircleType.THREE_G),
    SINGAPORE("sg","Singa Pore",CircleType.THREE_G);



    private static Map<String, String> circleShortNameMap = new HashMap<String, String>();
    private static Map<String, Circle> circleMap = new HashMap<String, Circle>();

    static {
        for(Circle circle : Circle.values()) {
            circleShortNameMap.put(circle.getCircleName().toLowerCase(),circle.getCircleId());
            circleShortNameMap.put(circle.getNdsCircleName().toLowerCase(),circle.getCircleId());
            circleMap.put(circle.getCircleId(),circle);
        }
    }

    private final String circleId;
    private final String circleName;
    private final String ndsCircleName;
    private CircleType circleType;

    private Circle(String circleId, String circleName, CircleType circleType) {
        this.circleId = circleId;
        this.circleName = circleName;
        this.ndsCircleName = circleName;
        this.circleType = circleType;
    }


    private Circle(String circleId, String circleName, String ndsCircleName) {
        this.circleId = circleId;
        this.circleName = circleName;
        this.ndsCircleName = ndsCircleName;
        this.circleType = CircleType.TWO_G;
    }
    
    private Circle(String circleId, String circleName, String ndsCircleName, CircleType circleType) {
		this.circleId = circleId;
		this.circleName = circleName;
		this.ndsCircleName = ndsCircleName;
		this.circleType = circleType;
	}

	public static CircleType getCircleTypeById(String circleId) {
		Circle circle = getCircleById(circleId);
		return circle.getCircleType();
	}
    
	public static Circle getCircleById(String circleId) {
        if(StringUtils.isBlank(circleId))
            return ALL;

        circleId = circleId.toLowerCase();
        Circle circle = circleMap.get(circleId);

        if (circle != null) {
            return circle;
        }
        return ALL;
    }

    public static String getCircleShortName(String circleName) {
        String circleId = circleShortNameMap.get(circleName.toLowerCase());

        if (circleId != null) {
            return circleId;
        }
        return ALL.getCircleId();
    }




    public String getCircleId() {
        return circleId;
    }


    public String getCircleName() {
        return circleName;
    }

    public String getNdsCircleName() {
        return ndsCircleName;
    }

    public CircleType getCircleType() {
		return circleType;
	}

	public void setCircleType(CircleType circleType) {
		this.circleType = circleType;
	}


	@Override
    public String toString() {
        return getCircleName();
    }

    private static Map<String, Circle> cityCircleMap = new HashMap<String, Circle>();
    private static Map<String, Circle> circleMapByNameAsKey = new HashMap<String, Circle>();
    
    

    public static Map<String, Circle> getCircleMapByNameAsKey() {
		return circleMapByNameAsKey;
	}


	static {
        cityCircleMap.put("bangalore", Circle.KARNATAKA);
        cityCircleMap.put("pune", Circle.MAHARASHTRA);
        cityCircleMap.put("chennai", Circle.CHENNAI);
        cityCircleMap.put("hyderabad", Circle.ANDHRAPRADESH);
        cityCircleMap.put("trivandrum", Circle.KERALA);
        cityCircleMap.put("mumbai", Circle.MUMBAI);
        cityCircleMap.put("kolkata", Circle.KOLKATA);
        cityCircleMap.put("delhi", Circle.DELHI);
        cityCircleMap.put("gurgaon", Circle.DELHI);
        cityCircleMap.put("noida", Circle.DELHI);
        cityCircleMap.put("faridabad", Circle.DELHI);
        cityCircleMap.put("gaziabad", Circle.DELHI);
        
        for(Circle circle:Circle.values()){
        	circleMapByNameAsKey.put(circle.getCircleName(),circle);
        }
    }

    public static Circle getCircleForCity(String city) {
        Circle circle = cityCircleMap.get(city.toLowerCase());
        if (circle == null) {
            circle = Circle.DELHI;
        }
        return circle;

    }
    
    /*Get ICR Circles*/
    private static Map<String, Circle> icrCircle = new HashMap<String, Circle>();
    
    static{
        icrCircle.put("hr", Circle.HARYANA);
        icrCircle.put("ue", Circle.UPEAST);
        icrCircle.put("ko", Circle.KOLKATA);
        icrCircle.put("gj", Circle.GUJRAT);
        icrCircle.put("mp", Circle.MADHYAPRADESH);
        icrCircle.put("mh", Circle.MAHARASHTRA);
        icrCircle.put("kl", Circle.KERALA);
        icrCircle.put("pb", Circle.PUNJAB);
        icrCircle.put("or", Circle.ORISSA);
    }
    
    public static Circle getICRCircle(String circleId){
        if(!StringUtils.isBlank(circleId))
            circleId = circleId.toLowerCase();
        Circle circle = icrCircle.get(circleId);
        if(null != circle){
            return circle;
        }
        return null;
    }

}
