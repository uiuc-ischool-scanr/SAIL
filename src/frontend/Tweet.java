/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package frontend;

import java.util.HashMap;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.cell.ComboBoxTableCell;
//import online_learning_abv.ComboBoxCellSample.Day;

/**
 *
 * @author ltao3
 */
public class Tweet {	
    
    public static enum LABEL { positive, negative;
    public static LABEL getEnum(String s){
        if(positive.name().equals(s)){
            return positive;
        }else if(negative.name().equals(s)){
            return negative;
        }
        throw new IllegalArgumentException("No Enum specified for this string");
    	}
    }
    public static int SentimentIndex = 44;
    private StringProperty tweet;
    private StringProperty feature;
    private DoubleProperty probability;
    private int index;
    private String[] tweetList;
    private String originalLabel;
    private boolean changed;
    
    private double probability_val;
    
//    private StringProperty label;
    private StringProperty label;
    private HashMap<String, Integer> features = new HashMap<>();
    private ComboBoxTableCell priorityComboBox = new ComboBoxTableCell();
    
    public Tweet(String tweet, String label, int i, String[] tweetList) {
        this.tweet = new SimpleStringProperty(tweet);
        this.label = new SimpleStringProperty(label);
        this.feature = new SimpleStringProperty(getFeature());
        this.index = i;
        this.tweetList = tweetList;
        this.originalLabel = label;
        this.changed = false;
        this.probability_val = -1.0;
    }
    
    public Tweet(String tweet, String label, int i, String[] tweetList, double probability_val) {
    	this(tweet, label, i, tweetList);
    	this.probability_val = probability_val;
    	this.probability = new SimpleDoubleProperty(this.probability_val);
    }
    
    public String getFeature(){
        String t = "";
        for (String s : features.keySet()){
            t += s + ": " + features.get(s) + "\n";
        }
        return t;
    }
    
    public int getIndex(){
    	return this.index;
    }
    
    public String getLabel(){
    	return this.label.get();
    }
    
    public String[] getMeta(){
    	return this.tweetList;
    }
    
    public Double getProbability(){
    	return this.probability.get();
    }
    
    
    public String getTweet(){
        return this.tweet.get();
    }
    
    public boolean isChanged(){
    	return this.changed;
    }
    
    public void set(String newTweet){
        this.tweet = new SimpleStringProperty(newTweet);
    }
    
    public void setFeature(String key, Integer val){
    	features.put(key, val);
    }
    
    public void setLabel(LABEL lbl){
    	this.label.set(lbl.toString());
    	this.setSentiment(lbl.toString());
    }
//    
//    public void setLabel(String string){
//    	this.label.set(string);
//    }
    
    public void setSentiment(String sentiment){
    	this.tweetList[this.tweetList.length -8 ] = sentiment;
    	if(!sentiment.equals(originalLabel)){
    		this.changed = true;
    		System.out.println("Updated Sentiment to: "+sentiment);
    	} else {
    		this.changed = false;
    	}
    	
	}
    
    public String toString(){
    	return "orginal index" + this.getIndex() + " " + this.label.get();
    }
    
}
