/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package core;

import java.util.Properties;

public class AppProperties extends Properties {
	
	public static final String[] propKeys = {"tweet_text","user","published_date","sentiment","url",
			"Twitter_Followers","Twitter_Following","Twitter_Reply_Count",
			"Twitter_Retweets","Twitter_Tweets"};

	public AppProperties() {
		// TODO Auto-generated constructor stub
	}

	public AppProperties(Properties defaults) {
		super(defaults);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public String getModel(){
		String modelPath = this.getProperty("model", "");
		if(modelPath.equalsIgnoreCase("")){
			return null;
		}
		return modelPath;
	}
	
	public String getLexicon(){
		String lexicon = this.getProperty("lexicon", "");
		if(lexicon.equalsIgnoreCase("")){
			return null;
		}
		return lexicon;
	}
	
	public String getQueryList(){
		String query_list = this.getProperty("query_list", "");
		if(query_list.equalsIgnoreCase("")){
			return null;
		}
		return query_list;
	}
	
	public String getClassIndex(){
		String class_index = this.getProperty("class_index", "");
		if(class_index.equalsIgnoreCase("")){
			return null;
		}
		return class_index;
	}
	
	public String getStringList(){
		String string_list = this.getProperty("string_list", "");
		if(string_list.equalsIgnoreCase("")){
			return null;
		}
		return string_list;
	}
	
	public String getRemoveList(){
		String remove_list = this.getProperty("remove_list", "");
		if(remove_list.equalsIgnoreCase("")){
			return null;
		}
		return remove_list;
	}
	
	
	public String getHeader(String key){
		if(super.getProperty(key).equalsIgnoreCase("")){
			return null;
		}
		return super.getProperty(key);
	}
}
