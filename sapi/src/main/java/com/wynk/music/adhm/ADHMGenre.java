package com.wynk.music.adhm;

import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anurag on 10/30/15.
 */
public enum ADHMGenre {

	DEFAULT(100,"default","default"),
	HINDI_ROCK(101,"hi","rock"),
	ENGLISH_ROCK(102,"en","rock"),
	HINDI_POP(103,"hi","pop"),
	ENGLISH_POP(104,"en","pop"),
	HINDI_EDM(105,"hi","edm"),
	ENGLISH_EDM(106,"en","edm"),
	HINDI_HIPHOP(107,"hi","hip hop"),
	ENGLISH_HIPHOP(108,"en","hip hop"),
	ENGLISH_METAL(109,"en","metal"),
	ENGLISH_REGGAE(110,"en","reggae"),
	ENGLISH_INDIE(111,"en","indie"),
	ENGLISH_COUNTRY(112,"en","country"),
	HINDI_BOLLYWOOD(113,"hi","bollywood"),
	HINDI_FUSION(114,"hi","fusion"),
	ENGLISH_FUSION(115,"en","fusion"),
	PUNJABI_BEATS(116,"hi","punjabi beats"),
	HINDI_INSPIRATIONAL(117,"hi","inspirational"),
	ENGLISH_INSPIRATIONAL(118,"en","inspirational"),
	HINDI_CHARTS(119,"hi","charts"),
	ENGLISH_CHARTS(120,"en","charts"),
	HINDI_INSTRUMENTAL(121,"hi","instrumental"),
	ENGLISH_INSTRUMENTAL(122,"en","instrumental"),
	HINDI_WINDDOWN(123,"hi","wind down"),
	ENGLISH_WINDDOWN(124,"en","wind down"),
	HINDI_METAL(125,"hi","metal"),
	HINDI_REGGAE(126,"hi","reggae"),
	HINDI_INDIE(127,"hi","indie"),
	HINDI_COUNTRY(128,"hi","country");

	private final int                       opcode;
	private final String lang;
	private final String name;

	private static Map<String,List<ADHMGenre>> genreNameMap=new HashMap<String,List<ADHMGenre>>();
	private static Map<Integer, ADHMGenre> genreIdMapping = new HashMap<>();

	static {
		for (ADHMGenre genre : ADHMGenre.values()) {
			String name = genre.getName().replace(" ","");
			List<ADHMGenre> currentList = genreNameMap.get(name.toLowerCase());
			if(currentList == null)
				currentList = new ArrayList<>();

			currentList.add(genre);
			genreNameMap.put(name.toLowerCase(), currentList);
			genreIdMapping.put(genre.getOpcode(),genre);
		}
	}

	ADHMGenre(int opcode, String lang, String name) {
		this.opcode = opcode;
		this.lang= lang;
		this.name=name;
	}

	public int getOpcode() {
		return opcode;
	}

	public String getName() {
		return name;
	}

	public String getLang() {
		return lang;
	}

	public static ADHMGenre getGenreByNameAndLang(String name,String lang) {

		if(name == null)
			return null;

		name = name.replace(" ","");
		List<ADHMGenre> adhmGenreList=genreNameMap.get(name.toLowerCase());

		if(CollectionUtils.isEmpty(adhmGenreList)) {
			return null;
		}

		for(ADHMGenre genre : adhmGenreList)
		{
			if(genre.getLang().equalsIgnoreCase(lang))
				return genre;
		}

		return null;
	}

	public static ADHMGenre getGenreById(Integer code) {

		ADHMGenre adhmGenre=genreIdMapping.get(code);
		if(adhmGenre ==null) {
			return null;
		}
		return adhmGenre;
	}

}
