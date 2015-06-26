/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package sentinets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cmu.arktweetnlp.Tagger;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class POSFeatures {
	public static void init(){
		if(t == null){
			
			t = new Tagger();
			try {
				t.loadModel("./data/models/ARK_NLP_POS/model.20120919");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Invalid model file.");
				
			}
			
			posCountTmpl = new HashMap<String, Integer>();
			// Removing ARK_TWEET_NLP
			for(int i = 0; i < t.model.labelVocab.size(); i++){
				String l = t.model.labelVocab.name(i);
				posCountTmpl.put(l, 0);
			}
		}
	}
	
	public static void resetPOSCounts(){
		// Reset the static POS value before any new initialization
		for(String l: posCountTmpl.keySet()){
			posCountTmpl.put(l, 0);
		}
	}
	public static void main(String[] args) {
		Tagger t = new Tagger();
		try {
			t.loadModel("model.20120919");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Invalid model file.");
			
		}
		int[] labels = new int[t.model.labelVocab.size()];
		for(int i = 0; i < labels.length; i++){
			String l = t.model.labelVocab.name(i);
			
			System.out.printf("%s\t%s\n", i, l);
		}
		
		String tweet = "@Dolb this is very bad #way \"http://bitly.com/44\" :)";
		// Removing ARK_TWEET_NLP
		List<TaggedToken> tt = t.tokenizeAndTag(tweet);
		for(TaggedToken a: tt){
			System.out.println(a.token+":\t"+a.tag);
		}
		
	}
	private static Tagger t;
	public static HashMap<String, Integer> posCountTmpl;
	private List<TaggedToken> taggedTokens;
	
	private String tweet;
	
	public HashMap<String, Integer> posCounts;
		
	public POSFeatures(String tweet) {
		// TODO Auto-generated constructor stub
		if(t == null){
			POSFeatures.init();
		}
		this.tweet = tweet;
		this.genPOSCounts();
		
	}
	
	public void genPOSCounts() {
		resetPOSCounts();
		posCounts = POSFeatures.posCountTmpl;
		
		this.taggedTokens = t.tokenizeAndTag(tweet);
		for(TaggedToken tt: taggedTokens){
			//System.out.printf("%s\t%s,\t",tt.token,tt.tag);
			posCounts.put(tt.tag, posCounts.get(tt.tag)+1);
			
		}
		
	}
	
	public void printInfo(){
		if(taggedTokens == null){
			System.out.println("Please call getPOSCounts first");
		}
		for(TaggedToken tt: taggedTokens){
			System.out.printf("%s\t%s,\t",tt.token,tt.tag);
			
		}
		System.out.println("\nCounts");
		for(String key: posCounts.keySet()){
			System.out.printf("%s\t%s\n",key, posCounts.get(key));
		}
		System.out.println();
		
		
	}
	/**
	 * @return the posCounts
	 */
	public HashMap<String, Integer> getPosCounts() {
		return posCounts;
	}
	
}
