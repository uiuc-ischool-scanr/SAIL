/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package sentinets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import sentinets.DictionaryFeatures.TweetObject;
import twitter4j.Status;

import com.twitter.Extractor;
import com.twitter.Extractor.Entity;

public class ParseTweet {
	Status tweet;
	String parsed_text, tweet_text, url, user, published_date;
	int length,c_emo,c_hash,c_url,c_mention,c_quote;
	ArrayList<String> m_emo, m_hash, m_url, m_mention;
	POSFeatures pf;
	TweetObject to;
	
	public ParseTweet(){
		c_emo = c_hash = c_url = c_mention= c_quote = 0;
		m_emo = new ArrayList<String>();
		m_hash = new ArrayList<String>();
		m_url = new ArrayList<String>();
		m_mention = new ArrayList<String>();
		this.parsed_text = "";
	}
	
	public ParseTweet(String tweet){
		this();
		this.tweet_text = tweet.replaceAll("\n", " ");
		this.length = tweet.length();
		extractFeatures();
	}
	
	public ParseTweet(String tweet, String user, String published_date){
		this(tweet);
		this.user = user;
		this.published_date = published_date;
	}
	
	public ParseTweet(Status tweet){
		this(tweet.getText());
		this.tweet = tweet;
		this.url =  "http://twitter.com/" + tweet.getUser().getScreenName() + 
				"/status/" + tweet.getId();
		this.user = tweet.getUser().getScreenName();
		this.published_date = tweet.getCreatedAt().toString();		
	}
	
	public void extractFeatures(){
		int last = 0;
		pf = new POSFeatures(this.tweet_text);
		to = DictionaryFeatures.applyDictionaryFeatures(this.tweet_text);
		//System.out.println("Tweet Object: "+to.getLexicalScores());
		Extractor ex = new Extractor();
		String processText = to.getText();
		List<Entity> entities = ex.extractEntitiesWithIndices(processText);
		for(Entity e:entities){
			//System.out.println(e);
			if(e.getType() == Entity.Type.HASHTAG){
				c_hash++;
				m_hash.add(e.getValue());
				parsed_text += processText.substring(last, e.getStart());
				parsed_text += " _HASH ";
				last = e.getEnd();
				
			}
			else if(e.getType() == Entity.Type.MENTION){
				c_mention++;
				m_mention.add(e.getValue());
				parsed_text += processText.substring(last, e.getStart());
				parsed_text += " _MENTION ";
				last = e.getEnd();
			}
			
			else if(e.getType() == Entity.Type.URL){
				c_url++;
				m_url.add(e.getValue());
				parsed_text += processText.substring(last, e.getStart());
				parsed_text += " _URL ";
				last = e.getEnd();
			}

		}
		parsed_text += processText.substring(last, processText.length());
		Matcher m = TweerRegularExpressions.EMOTICON_REGEX_PATTERN.matcher(parsed_text);
		while(m.find()){
			c_emo++;
			m_emo.add(m.group(1));
		}
		parsed_text = m.replaceAll(" _EMO ");
		m = TweerRegularExpressions.DQ.matcher(parsed_text);
		while(m.find()){
			c_quote++;
		}
		parsed_text = m.replaceAll(" _DQ ");
	}
	
	public void showFeatures(){
		System.out.println("c_emo: "+c_emo);
		System.out.println("c_hash: "+c_hash);
		System.out.println("m_mention: "+m_mention);
		System.out.println("url: "+url);
		System.out.println("c_url: "+c_url);
		System.out.println("tweet_text: "+tweet_text);
		System.out.println("c_mention: "+c_mention);
		System.out.println("parsed_text: "+parsed_text);
		System.out.println("length: "+length);
		System.out.println("m_emo: "+m_emo);
		System.out.println("user: "+user);
		System.out.println("published_date: "+published_date);
		System.out.println("c_quote: "+c_quote);
		System.out.println("m_hash: "+m_hash);
		pf.printInfo();

	}
	
	public static void main(String args[]){
		ParseTweet pt = new ParseTweet("@Dolb this is very bad #way \"http://bitly.com/44\" :)");
		pt.showFeatures();
	}
}
