/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package sentinets;

import java.util.Hashtable;

public class TweetEntityNode {
	enum EntityType { 
		USER ("U"), HASHTAG ("H") ;
		private String s;
		EntityType(String s){
			this.s = s;
		}
		public String getTypeString(){ return s;}
	};
	enum SentimentType {
		ENTHUSIASTIC ("e"), PASSIVE ("p"), SUPPORTIVE ("s"), NON_SUPPORTIVE ("ns");
		private String s;
		SentimentType(String s){
			this.s = s;
		}
		public String getTypeString(){ return s;}
		public static SentimentType getType(String s){
			if(s.equalsIgnoreCase("e")) return SentimentType.ENTHUSIASTIC;
			if(s.equalsIgnoreCase("p")) return SentimentType.PASSIVE;
			if(s.equalsIgnoreCase("s")) return SentimentType.SUPPORTIVE;
			if(s.equalsIgnoreCase("ns")) return SentimentType.NON_SUPPORTIVE;
			return null;
		}
	};
	EntityType et;
	String sid;
	int id;
	String name;
	Hashtable<SentimentType, Double> sentiment_score;
	Hashtable<SentimentType, Integer> sentiment_count;
	int count;
	int tweetCount;
	Double[] avgScores;
	Integer[] classCounts;
	String[] sentiment_class;
	
	public TweetEntityNode() {
		// TODO Auto-generated constructor stub
		sentiment_score = new Hashtable<SentimentType, Double>();
		sentiment_score.put(SentimentType.ENTHUSIASTIC, 0.0);
		sentiment_score.put(SentimentType.PASSIVE, 0.0);
		sentiment_score.put(SentimentType.SUPPORTIVE, 0.0);
		sentiment_score.put(SentimentType.NON_SUPPORTIVE, 0.0);
		
		sentiment_count = new Hashtable<SentimentType, Integer>();
		sentiment_count.put(SentimentType.ENTHUSIASTIC, 0);
		sentiment_count.put(SentimentType.PASSIVE, 0);
		sentiment_count.put(SentimentType.SUPPORTIVE, 0);
		sentiment_count.put(SentimentType.NON_SUPPORTIVE, 0);
		count = 0;
		tweetCount = 0;
	}
	
	public TweetEntityNode(String name, EntityType et){
		this();
		this.et = et;
		this.name = name;
	}
	
	public void setId(int id){
		this.id = id;
		sid = et.getTypeString()+id;
	}
	
	public void updateCount(){
		count ++;
	}
	
	public void updateTweetCount(){
		tweetCount ++;
	}
	
	public void updateSentimentScore(SentimentType s, double score){
		if(s == null)
			return;
		sentiment_score.put(s, sentiment_score.get(s)+score);
		sentiment_count.put(s, sentiment_count.get(s)+1);
	}
	
	public Double[] getAverageScores(){
		avgScores = new Double[2]; /*{EP, SNS}*/
		avgScores[0] = (sentiment_score.get(SentimentType.ENTHUSIASTIC)*sentiment_count.get(SentimentType.ENTHUSIASTIC)
				- sentiment_score.get(SentimentType.PASSIVE)*sentiment_count.get(SentimentType.PASSIVE))/count;
		avgScores[1] = (sentiment_score.get(SentimentType.SUPPORTIVE)*sentiment_count.get(SentimentType.SUPPORTIVE)
				- sentiment_score.get(SentimentType.NON_SUPPORTIVE)*sentiment_count.get(SentimentType.NON_SUPPORTIVE))/count;
		return avgScores;
	}
	
	public Integer[] getClassCounts(){
		classCounts = new Integer[2]; /*{EP, SNS}*/
		classCounts[0] = (sentiment_count.get(SentimentType.ENTHUSIASTIC)
				- sentiment_count.get(SentimentType.PASSIVE));
		classCounts[1] = (sentiment_count.get(SentimentType.SUPPORTIVE)
				- sentiment_count.get(SentimentType.NON_SUPPORTIVE));
		return classCounts;
	}
	
	public String[] getSentimentClasses(){
		this.getAverageScores();
		this.getClassCounts();
		sentiment_class = new String[2]; /*{EP, SNS}*/
		sentiment_class[0] = classCounts[0] > 0 ? SentimentType.ENTHUSIASTIC.toString() 
				: SentimentType.PASSIVE.toString();
		sentiment_class[1] = classCounts[0] > 0 ? SentimentType.SUPPORTIVE.toString() 
				: SentimentType.NON_SUPPORTIVE.toString();
		return sentiment_class;
	}
	
	public String[] getRow(){
		/*
		 * "Id	Type	Weight	Label	Tweet_Count	"
			+ "E_Count	P_Count	S_Count	NS_Count	"
			+ "E_Score	P_Score	S_Score	NS_Score	"
			+ "EP_Count	SNS_Count	EP_AvgScore	SNS_AvgScore	EP_Class	SNS_Class"
		 * */
		if(sentiment_class == null){
			this.getSentimentClasses();
		}
		String[] row = {Integer.toString(id), et.toString(), Integer.toString(count), name, Integer.toString(tweetCount),
				Integer.toString(sentiment_count.get(SentimentType.ENTHUSIASTIC)),Integer.toString(sentiment_count.get(SentimentType.PASSIVE)),
				Integer.toString(sentiment_count.get(SentimentType.SUPPORTIVE)),Integer.toString(sentiment_count.get(SentimentType.NON_SUPPORTIVE)),
				Double.toString(sentiment_score.get(SentimentType.ENTHUSIASTIC)),Double.toString(sentiment_score.get(SentimentType.PASSIVE)),
				Double.toString(sentiment_score.get(SentimentType.SUPPORTIVE)),Double.toString(sentiment_score.get(SentimentType.NON_SUPPORTIVE)),
				Integer.toString(classCounts[0]), Integer.toString(classCounts[1]), Double.toString(avgScores[0]), Double.toString(avgScores[1]),
				sentiment_class[0], sentiment_class[1]};
		return row;
	}
}
