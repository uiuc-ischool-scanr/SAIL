/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package sentinets;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * 
 * @author Shubhanshu Mishra
 * Import Tweets
 * 1 - Based on Query
 * 2 - Based on Number
 */

public class ImportTweets {
	
	Twitter twitter;	
	
	private static String consumerKey="cDAfHiwgErg38su2vyzSA";
	private static String consumerSecret="LAd7KhSSMURnhipF5kDOv3Twwl7xr7gaQT5yDu1Z3Q";
	private static String accessToken="1897937414-culw4xaViBwnlTXNNUGzP3sgLgW6JZlA0V2lqW9";
	private static String accessTokenSecret="j9tYIDcV0exngVxYjx6ymk3394YKGYjED9l8dArk";
	
	//public TweetCorpusStatistics stats;
	
	public ImportTweets(){
		twitter = new TwitterFactory().getInstance();
	    AccessToken accessToken = new AccessToken(ImportTweets.accessToken, ImportTweets.accessTokenSecret);

	    twitter.setOAuthConsumer(ImportTweets.consumerKey, ImportTweets.consumerSecret);
	    twitter.setOAuthAccessToken(accessToken);
	    //stats = new TweetCorpusStatistics();
	}
	
	public ImportTweets(String queryStr, int countOfTweets, String fileName){
		this();
	    BufferedWriter bw = null;
	    try {
			bw = new BufferedWriter(new FileWriter(fileName));
			bw.write("c_emo\tc_hash\tm_mention\turl\tc_url\ttweet_text\tc_mention"
					+ "\tparsed_text\tlength\tm_emo\tuser\tpublished_date\tc_quote"
					+ "\tm_hash\te/p/i\ts/ns/na");
			bw.newLine();
			bw.flush();
			
            ParseTweet pt;
            for (Status tweet : this.getTweets(queryStr, countOfTweets)) {
                //System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                pt = new ParseTweet(tweet);
                //stats.updateStatistics(pt);
                //pt.showFeatures();
                bw.write(this.getRow(pt));
                bw.newLine();
                bw.flush();
            }
            bw.close();
            //stats.printStats(new PrintStream(new File(fileName+".stats.tsv")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
	}
	
	public void importTweetsAsCSV(String queryStr, int countOfTweets, String fileName){
		/*Twitter twitter = new TwitterFactory().getInstance();
	    AccessToken accessToken = new AccessToken(ImportTweets.accessToken, ImportTweets.accessTokenSecret);

	    twitter.setOAuthConsumer(ImportTweets.consumerKey, ImportTweets.consumerSecret);
	    twitter.setOAuthAccessToken(accessToken);*/
	    CSVWriter cw = null;
	    try {
			cw = new CSVWriter(new FileWriter(fileName), '\t', '\"','\\');
			String header = "c_emo\tc_hash\tm_mention\turl\tc_url\ttweet_text\tc_mention"
					+ "\tparsed_text\tlength\tm_emo\tuser\tpublished_date\tc_quote"
					+ "\tm_hash\te/p/i\ts/ns/na";
			cw.writeNext(header.split("\t"));

            ParseTweet pt;
            for (Status tweet : this.getTweets(queryStr, countOfTweets)) {
                System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                pt = new ParseTweet(tweet);
                //stats.updateStatistics(pt);
                //pt.showFeatures();
                cw.writeNext(this.getRowAsList(pt));
                
            }
            
            cw.close();
            //stats.printStats(new PrintStream(new File(fileName+".stats.tsv")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private List<Status> getTweets(String queryStr, int countOfTweets){
		Query query = new Query(queryStr);
        query.setCount(countOfTweets);
        query.setLang("en");
        QueryResult result = null;
        List<Status> tweets = new ArrayList<Status>();
        do {
            try {
				result = twitter.search(query);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e.printStackTrace();
	            System.out.println("Failed to search tweets: " + e.getMessage());

			}
            tweets.addAll(result.getTweets());
        } while ((query = result.nextQuery()) != null && tweets.size() <= countOfTweets);
        return tweets;
	}
	
	public String getRow(ParseTweet t){
		/*
		 * Each row of format:
		 * c_emo	c_hash	m_mention	url	c_url	tweet_text	c_mention	parsed_text	length	m_emo	user	published_date	c_quote	m_hash	e/p/i	s/ns/na
		 */
		String row = "";
		
		row += t.c_emo+"\t"+t.c_hash+"\t"+t.m_mention
				+"\t"+t.url+"\t"+t.c_url+"\t"+StringEscapeUtils.unescapeCsv(t.tweet_text)
				+"\t"+t.c_mention+"\t"+t.parsed_text+"\t"+t.length
				+"\t"+t.m_emo+"\t"+t.user+"\t"+t.published_date
				+"\t"+t.c_quote+"\t"+t.m_hash;
		
		for(String key:t.pf.posCounts.keySet()){
			row += "\t"+t.pf.posCounts.get(key);
		}
		
		row += "\t?\t?";

		return row;
	}
	
	public String[] getRowAsList(ParseTweet t){
		/*
		 * Each row of format:
		 * c_emo	c_hash	m_mention	url	c_url	tweet_text	c_mention	parsed_text	length	m_emo	user	published_date	c_quote	m_hash	e/p/i	s/ns/na
		 */
		/*String[] row = {
			Integer.toString(t.c_emo),Integer.toString(t.c_hash),t.m_mention.toString()
			,t.url,Integer.toString(t.c_url),t.tweet_text
			,Integer.toString(t.c_mention),t.parsed_text,Integer.toString(t.length)
			,t.m_emo.toString(),t.user,t.published_date
			,Integer.toString(t.c_quote),t.m_hash.toString(),"?","?"
			
		};*/
		ArrayList<String> row = new ArrayList<String>();
		
		String[] row1 = {
				Integer.toString(t.c_emo),Integer.toString(t.c_hash),StringUtils.join(t.m_mention,",")
				,t.url,Integer.toString(t.c_url),t.tweet_text
				,Integer.toString(t.c_mention),t.parsed_text,Integer.toString(t.length)
				,StringUtils.join(t.m_emo, ","),t.user,t.published_date
				,Integer.toString(t.c_quote),StringUtils.join(t.m_hash, ","),"?","?"
				
			};
		
		row.addAll(Arrays.asList(row1));
		for(String key:t.pf.posCounts.keySet()){
			row.add(t.pf.posCounts.get(key).toString());
		}

		return (String[]) row.toArray();
	}
		
	
	
	public static void main(String[] args) {
		ImportTweets it = new ImportTweets();
		it.importTweetsAsCSV("cyber bullying", 10, "./data/output/cyber_bull.tsv");
		
		
	}

}