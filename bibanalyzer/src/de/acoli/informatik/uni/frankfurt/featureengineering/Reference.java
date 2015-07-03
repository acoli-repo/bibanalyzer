package de.acoli.informatik.uni.frankfurt.featureengineering;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Reference {
	List<ReferenceToken> referenceTokens = new ArrayList<ReferenceToken>();
	
	void addToken(String token, String tag) {
		ReferenceToken rToken = new ReferenceToken();
		rToken.setToken(token);
		rToken.setTag(tag);
		referenceTokens.add(rToken);
	}
	
	public void addToken(String[] tokens) {
		ReferenceToken rToken = new ReferenceToken();
		for(int i=0; i<tokens.length; i++) {
			if(i == 0) rToken.setToken(tokens[i]);
			else if(i == tokens.length-1) rToken.setTag(tokens[i]);
			else rToken.getFeatures().add(tokens[i]);
		}
		referenceTokens.add(rToken);
	}

	
	public String toString() {
		String output = "";
		for(ReferenceToken rToken : referenceTokens) {
			output = output + System.lineSeparator() + rToken;
		}
		return output.trim();
		
	}
	
	public String toPrintString() {
		String output = "";
		for(ReferenceToken rToken : referenceTokens) {
			output = output + "\"" + rToken + "\"  " ;
		}
		return output;
		
	}
	
	String toTokenString() {
		String output = "";
		for(ReferenceToken rToken : referenceTokens) {
			output = output + rToken.getToken() ;
		}
		return output.trim().replace("&nbsp;", " ").replace("BOR ", "").replace(" EOR", "");
	}
	
	public void addLowerCaseFeatures() {
		for(ReferenceToken rToken : referenceTokens) {
			rToken.getFeatures().add(rToken.getToken().toLowerCase());
		}
		
	}
	
	public void addNumericFeatures() {
		for(ReferenceToken rToken : referenceTokens) {
			String token = rToken.getToken();
			String feature = "<NotNumber>";
			if(isNumeric(token)) {
				feature = "<Number>";
				if(isYearPattern(token)) {
					feature = "<PossiblyYear>";
				}
			}
			rToken.getFeatures().add(feature);
		}
		
	}
	
	public void addTokenPositionFeatures() {
		double bucketSize = ((double)referenceTokens.size())/12;
		int i = 0;
		//for(ReferenceToken rToken : referenceTokens) {
		//	int position = (int) (((double)i) / bucketSize);
		//	rToken.getFeatures().add("<" + (position + 1) + ">");
		//	i++;
		//}
		
		//i = 0;
		for(ReferenceToken rToken : referenceTokens) {
			int position = (int)((double)i * 12)/referenceTokens.size();
			rToken.getFeatures().add("<" + (position + 1) + ">");
			i++;
		}
	}
	
	public void addOrthographicFeatures() {
		for(ReferenceToken rToken : referenceTokens) {
			String token = rToken.getToken();
			String feature = "<noCaseInfo>";
			if(StringUtils.isAllUpperCase(token)) {
				feature = "<allCaps>";
			} else if(StringUtils.isAllLowerCase(token)) {
				feature = "<allLowerCase>";
			} else {
				//String[] temp = StringUtils.splitByCharacterTypeCamelCase(token);
				//if(temp.length > 1) {
				if(token.matches("[A-Z]+[a-z]+[A-Z]+")) {
					feature = "<CamelCase>";
				} else if(token.matches("^[A-Z]{1}[a-z]+")) {
					feature = "<TitleCase>";
				}
				
			}
			//System.out.println(rToken.getFeatures());
			rToken.getFeatures().add(feature);
			//System.out.println(rToken.getFeatures());
			//System.out.println(rToken);
		}
		
	}
	

	
	private boolean isYearPattern(String token) {
		if(token.length() != 4) {
			return false;
		}
		if(token.startsWith("19") || token.startsWith("20")) {
			return true;
		}
		return false;
	}
	public static boolean isNumeric(String str)
	{
	  return str.matches("^\\d+$");  //match a number.
	}



}
