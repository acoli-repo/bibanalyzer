package de.acoli.informatik.uni.frankfurt.featureengineering;

import java.util.ArrayList;

/**
 * Class for representation of each token in a reference.
 * It represents the actual word(token), with its tag or category and
 * an array of features used for better classification. Also the getters
 * and setters for them.
 * 
 * @author Suman Kumar Chalavadi <Suman.Chalavadi@crest.in>
 *
 */
public class ReferenceToken {
	String token;
	String tag;
	ArrayList<String> features = new ArrayList<String>();
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public ArrayList<String> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<String> features) {
		this.features = features;
	}
	
	/**
	 * Just returns the token and the tag.
	 * @return
	 */
	public String toPrintString() {
		//return token + " " + features  + " " + tag;
		return token + " " + tag;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		//return token + " " + features  + " " + tag;
		String featureString = "";
		for(String feature : features) {
			featureString = featureString + " " + feature ;
		}
		String output = "";
		if(featureString.isEmpty()) {
			output = token + " " + tag;
		}
		else {
			output = token + " " + featureString.trim() + " " + tag;
		}
		
		return output;
	}

}
