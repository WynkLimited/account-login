package com.wynk.music.constants;

import java.util.*;

public enum MusicPackType {

	FUPPACK(4, "FUPPack");

    private int    priority;
    private String type;

    private MusicPackType(int packPriority, String packType) {
        this.priority = packPriority;
        this.type = packType;
    }

    private static Map<String, Integer> packTypeToPriorityMapping = new HashMap<String, Integer>();
    private static Map<Integer, MusicPackType> packPriorityToTypeMapping = new HashMap<>();

    private static List<Integer> priorities                = new ArrayList<Integer>();

    static {
        for(MusicPackType packType : MusicPackType.values()) {
            packTypeToPriorityMapping.put(packType.type, packType.priority);
            packPriorityToTypeMapping.put(packType.priority, packType);
            priorities.add(packType.priority);
        }
        Collections.sort(priorities);
    }

    
    public int getPriority() {
        return priority;
    }

    
    public String getType() {
        return type;
    }

    
    public static Map<String, Integer> getPackTypeToPriorityMapping() {
        return packTypeToPriorityMapping;
    }

    
    public static Map<Integer, MusicPackType> getPackPriorityToTypeMapping() {
        return packPriorityToTypeMapping;
    }

    
    public static List<Integer> getPriorities() {
        return priorities;
    }
    
    public static List<Integer> getReversedPriorities() {
        List invertedList = new ArrayList();
        for (int i = priorities.size() - 1; i >= 0; i--) {
            invertedList.add(priorities.get(i));
        }
        return invertedList;
    }
    
    

    
}
