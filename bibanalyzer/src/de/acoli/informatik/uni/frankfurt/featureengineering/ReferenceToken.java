package de.acoli.informatik.uni.frankfurt.featureengineering;

import java.util.ArrayList;

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
	
	public String toPrintString() {
		//return token + " " + features  + " " + tag;
		return token + " " + tag;
	}
	
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
