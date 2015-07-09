package de.acoli.informatik.uni.frankfurt.featureengineering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Class for representing the reference (in bibliography) as a list of
 * tokens (another class ReferenceToken to represent token with token, tag and features).
 * The class also contains all possible operations on a reference. Like adding features.
 * 
 * @author Suman Kumar Chalavadi <Suman.Chalavadi@crest.in>
 *
 */
public class Reference {
	//static HashMap<String,String> ordinalTokens = createOrdinalTokens();
	
	List<ReferenceToken> referenceTokens = new ArrayList<ReferenceToken>();
	
	/**
	 * Add a reference token, used while creating reference object.
	 * In case there are no features, this method is called with just token and tag.
	 * 
	 * @param token
	 * @param tag
	 */
	void addToken(String token, String tag) {
		ReferenceToken rToken = new ReferenceToken();
		rToken.setToken(token);
		rToken.setTag(tag);
		referenceTokens.add(rToken);
	}

	/**
	 * Add a reference token. It expects the first string in array as token, last string
	 * as tag and the rest string in the middle of the array as features.
	 * 
	 * @param tokens
	 */
	public void addToken(String[] tokens) {
		ReferenceToken rToken = new ReferenceToken();
		for(int i=0; i<tokens.length; i++) {
			if(i == 0) rToken.setToken(tokens[i]);
			else if(i == tokens.length-1) rToken.setTag(tokens[i]);
			else rToken.getFeatures().add(tokens[i]);
		}
		referenceTokens.add(rToken);
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String output = "";
		for(ReferenceToken rToken : referenceTokens) {
			output = output + System.lineSeparator() + rToken;
		}
		return output.trim();
		
	}
	
	/**
	 * Used to check/debugging. To print entire reference in a single line. Useful for validation purposes.
	 * @return
	 */
	public String toPrintString() {
		String output = "";
		for(ReferenceToken rToken : referenceTokens) {
			output = output + "\"" + rToken.toPrintString() + "\"  " ;
		}
		return output;
		
	}
	
	/**
	 * Returns only the reference string without any tags. Also removes the BOR, EOR.
	 * @return
	 */
	String toTokenString() {
		String output = "";
		for(ReferenceToken rToken : referenceTokens) {
			output = output + rToken.getToken() ;
		}
		return output.trim().replace("&nbsp;", " ").replace("BOR ", "").replace(" EOR", "");
	}
	
	/**
	 * Adds lowercased token as feature to the feature list.
	 */
	public void addLowerCaseFeatures() {
		for(ReferenceToken rToken : referenceTokens) {
			rToken.getFeatures().add(rToken.getToken().toLowerCase());
		}
		
	}
	
	/**
	 * Adds the numeric feature to the feature list.
	 */
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
			if(isOrdinalNumber(token)) {
				feature = "<OrdinalNumber>";
			}
			rToken.getFeatures().add(feature);
		}
		
	}

	/**
	 * Adds the token position feature to the feature list.
	 */
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
	
	/**
	 * Adds the orthographic feature to the feature list.
	 */
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
	
	/**
	 * Adds the token size feature to the feature list.
	 */
	public void addTokenSizeFeatures() {
		for(ReferenceToken rToken : referenceTokens) {
			int tokenSize = rToken.getToken().length();
			rToken.getFeatures().add("" + tokenSize);
		}	
	}


	
	/**
	 * Checks whether the given string is year or not.
	 * @param token
	 * @return
	 */
	private boolean isYearPattern(String token) {
		if(token.length() != 4) {
			return false;
		}
		if(token.startsWith("19") || token.startsWith("20")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks whether the given string is a number or not.
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str)
	{
	  return str.matches("^\\d+$");  //match a number.
	}
	
	private boolean isOrdinalNumber(String str) {
		if (str.toLowerCase().matches("^\\d+(st|nd|rd|th)$")) return true;
		
		return false;
	}

	/*private static HashMap<String, String> createOrdinalTokens() {
	HashMap<String,String> ordinalTokens = new HashMap<String,String>();
	ordinalTokens.put("first", "");
	ordinalTokens.put("second", "");
	ordinalTokens.put("third", "");
	ordinalTokens.put("fourth", "");
	ordinalTokens.put("fifth", "");
	ordinalTokens.put("sixth", "");
	ordinalTokens.put("seventh", "");
	ordinalTokens.put("eighth", "");
	ordinalTokens.put("ninth", "");
	ordinalTokens.put("tenth", "");
	ordinalTokens.put("eleventh", "");
	ordinalTokens.put("twelth", "");
	ordinalTokens.put("thirteenth", "");
	ordinalTokens.put("fourteenth", "");
	ordinalTokens.put("fifteenth", "");
	ordinalTokens.put("sixteenth", "");
	ordinalTokens.put("seventeenth", "");
	ordinalTokens.put("eighteenth", "");
	ordinalTokens.put("twenty", "");
	ordinalTokens.put("thirty", "");
	ordinalTokens.put("fourty", "");
	ordinalTokens.put("fifty", "");
	ordinalTokens.put("sixty", "");
	ordinalTokens.put("seventy", "");
	ordinalTokens.put("eighty", "");
	ordinalTokens.put("ninety", "");
	ordinalTokens.put("hundred", "");
	ordinalTokens.put("thousand", "");
	return null;
}*/

}
