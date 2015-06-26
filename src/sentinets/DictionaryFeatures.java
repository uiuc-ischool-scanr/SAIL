/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
/**
 * 
 */
package sentinets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cmu.arktweetnlp.Twokenize;
/**
 * @author Shubhanshu
 *
 */
public class DictionaryFeatures {

	private static HashMap<String, String> sentimentLexicon;
	private static HashSet<String> queryTerms;
	private static HashSet<String> sentimentLabels;
	
	
	/**
	 * 
	 */
	public DictionaryFeatures() {
		// TODO Auto-generated constructor stub
	}
	
	public static ArrayList<String> getKeys(){
		ArrayList<String> keys = new ArrayList<String>();
		for(String k: sentimentLabels){
			keys.add("c_"+k);
		}
		return keys;
	}
	
	public static void init(HashMap<String, String> sentimentLexicon, HashSet<String> queryTerms){
		DictionaryFeatures.sentimentLexicon = sentimentLexicon;
		DictionaryFeatures.queryTerms = queryTerms;
		DictionaryFeatures.sentimentLabels = new HashSet<String>(sentimentLexicon.values());
		List<String> temp = new ArrayList<String>(DictionaryFeatures.sentimentLabels); 
		Collections.sort(temp);
		DictionaryFeatures.sentimentLabels = new HashSet<String>(temp);
		
	}
	
	public static class TweetObject{
		private List<String> tokens;
		private HashMap<String, Integer> lexicalScores;
		public TweetObject(String text) {
			// TODO Auto-generated constructor stub
			text = text.replaceAll("[^ -~]", "");
			this.tokens = Twokenize.tokenizeRawTweetText(text);
			//this.tokens = Arrays.asList(text.split("\\s"));
		}
		public void setTokens(List<String> tokens){
			this.tokens = tokens;
		}
		
		public List<String> getTokens(){
			return tokens;
		}
		
		public String getText(){
			return StringUtils.join(this.tokens, " ");
		}
		/**
		 * @return the lexicalScores
		 */
		public HashMap<String, Integer> getLexicalScores() {
			return lexicalScores;
		}
		/**
		 * @param lexicalScores the lexicalScores to set
		 */
		public void setLexicalScores(HashMap<String, Integer> lexicalScores) {
			this.lexicalScores = lexicalScores;
		}
		
	}
	
	public static TweetObject applyDictionaryFeatures(String parsedText){
		TweetObject t = new TweetObject(parsedText);
		List<String> outTokens = new ArrayList<String>();
		HashMap<String, Integer> lexicalScores = new HashMap<String, Integer>();
		String sentiment = "";
		for(String k: sentimentLabels){
			lexicalScores.put(k,0); // Add a lexical count score for each sentiment class. 
		}
		for(String s: t.getTokens()){
			if(DictionaryFeatures.queryTerms.contains(s.toLowerCase())){
				outTokens.add("_QUERY");
			} else {
				outTokens.add(s);
			}
			if(DictionaryFeatures.sentimentLexicon.containsKey(s.toLowerCase())){
				sentiment = DictionaryFeatures.sentimentLexicon.get(s.toLowerCase());
				//System.out.println("Word: "+s+" Sentiment: "+sentiment);
				lexicalScores.put(sentiment, lexicalScores.get(sentiment)+1);
			}
			
		}
		t.setTokens(outTokens);
		t.setLexicalScores(lexicalScores);
		return t;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	/**
	 * @return the sentimentLabels
	 */
	public static HashSet<String> getSentimentLabels() {
		return sentimentLabels;
	}

	/**
	 * @param sentimentLabels the sentimentLabels to set
	 */
	public static void setSentimentLabels(HashSet<String> sentimentLabels) {
		DictionaryFeatures.sentimentLabels = sentimentLabels;
	}

}
