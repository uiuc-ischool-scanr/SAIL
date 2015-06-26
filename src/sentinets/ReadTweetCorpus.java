/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package sentinets;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;


/**
 * @author Shubhanshu
 *
 */
public class ReadTweetCorpus {

	/**
	 * @param args
	 */
	
	public static enum COL_INDECIES{MESSAGE,AUTHOR,TIME,SENTIMENT,URL,
		T_FOLLOWERS,T_FOLLOWING,T_REPLY_COUNT,T_RETWEETS,T_TWEETS};
	
	private String[] colNames; // Names of columns mapping to COL_INDECIES
	private String inFile;
	private String outFile;
	private static String[] POS_KEYSET = {"D", "#", "E", "G", "!", "&", "@", "A", "$",
	                                      "L", "M", "N", "O", ",", "U", "T", "V", "P", 
	                                      "S", "R", "~", "^", "Y", "X", "Z"};
	private HashMap<String, String> sentimentLexicon;
	private HashSet<String> queryTerms;
	
	public ReadTweetCorpus(){
		this.colNames = new String[3];
	}
	
	public ReadTweetCorpus(String inFile,String outFile, String[] colNames){
		this();
		this.inFile = inFile;
		this.outFile = outFile;
		this.colNames = colNames;
	}
	
	public ReadTweetCorpus(String inFile,String outFile, String[] colNames, String lexiconPath, String queryTermsPath){
		this(inFile,outFile,colNames);
		updateSentimentLexicon(lexiconPath);
		updateQueryTermsSet(queryTermsPath);
	}
	
	private void updateSentimentLexicon(String lexiconPath){
		sentimentLexicon = new HashMap<String, String>();
		CSVReader cr = null;
		String[] csvLine;
		String key, value;
		try {
			cr = new CSVReader(new FileReader(lexiconPath), '\t');
			while((csvLine=cr.readNext())!=null){
				key = csvLine[0].toLowerCase();
				value = csvLine[1].toLowerCase();
				if(!sentimentLexicon.containsKey(key)){
					// Only first instance of semtiment is considered.
					sentimentLexicon.put(key, value);
				}
			}
			cr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateQueryTermsSet(String queryListPath){
		queryTerms = new HashSet<String>();
		CSVReader cr = null;
		String[] csvLine;
		try {
			cr = new CSVReader(new FileReader(queryListPath), '\t');
			while((csvLine=cr.readNext())!=null){
				queryTerms.add(csvLine[0].toLowerCase());
			}
			cr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int writeData(){
		CSVWriter cw = null;
		CSVReader cr = null;
		int lines_read = 0;
		String[] tweetList = null;
	    try {
	    	System.out.println("[In ReadTweetCorpus Task] reading Data: "+this.inFile);
	    	System.out.println("[In ReadTweetCorpus Task] writing Data: "+this.outFile);
	    	cr = new CSVReader(new FileReader(this.inFile), '\t');
	    	int[] ids = this.getColIds(cr.readNext());
	    	lines_read += 1;
			cw = new CSVWriter(new FileWriter(this.outFile), '\t', '\"','\\');
			String header = "c_emo\tc_hash\tm_mention\turl\tc_url\ttweet_text\tc_mention"
					+ "\tparsed_text\tlength\tm_emo\tuser\tpublished_date\tc_quote"
					+ "\tm_hash";
			POSFeatures.init();		
			for(String key:POS_KEYSET){
				header += "\t"+key;
			}
			if(sentimentLexicon.size() > 0 || queryTerms.size() > 0){
				DictionaryFeatures.init(sentimentLexicon, queryTerms);
				header+="\t"+StringUtils.join(DictionaryFeatures.getKeys(),"\t");
			}
			/*
			 * Twitter Followers	Twitter Following	Twitter Reply Count	Twitter Reply to	Twitter Retweet of	Twitter Retweets	Twitter Tweets
			 * */
			header += "\te/p/i\ts/ns/na\tsentiment";
			header += "\tt_url\tTwitter Followers\tTwitter Following\tTwitter Reply Count\tTwitter Retweets\tTwitter Tweets\tprediction_prob"; 
			//header += "\tt_url\tt_followers\tt_following\tt_replies\tt_retweets\tt_tweets";
			cw.writeNext(header.split("\t"));
			System.out.println("[In ReadTweetCorpus Task] starting tweet parsing.");
            ParseTweet pt;
            while ((tweetList = cr.readNext())!= null) {
            	lines_read += 1;
                //System.out.println("Length of columns: "+tweetList.length);
                //System.out.println("Length of ids: "+ids.length);
            	String author = "", pub_time = "";
            	if(ids[COL_INDECIES.AUTHOR.ordinal()] > -1){
            		author = tweetList[ids[COL_INDECIES.AUTHOR.ordinal()]];
            	}
            	if(ids[COL_INDECIES.TIME.ordinal()] > -1){
            		pub_time = tweetList[ids[COL_INDECIES.TIME.ordinal()]];
            	}
                pt = new ParseTweet(tweetList[ids[COL_INDECIES.MESSAGE.ordinal()]],
                		author,pub_time);
                //stats.updateStatistics(pt);
                //pt.showFeatures();
                String sentiment = "?";
                String URL = "";
                String t_followers = "0";
                String t_following = "0";
                String rt_count = "0";
                String reply_count = "0";
                String status_count = "0";
                if(ids[COL_INDECIES.SENTIMENT.ordinal()] != -1){
                	sentiment = tweetList[ids[COL_INDECIES.SENTIMENT.ordinal()]];
                }
                if(ids[COL_INDECIES.URL.ordinal()] != -1){
                	URL = tweetList[ids[COL_INDECIES.URL.ordinal()]];
                }
                if(ids[COL_INDECIES.T_FOLLOWERS.ordinal()] != -1){
                	t_followers = tweetList[ids[COL_INDECIES.T_FOLLOWERS.ordinal()]];
                }
                if(ids[COL_INDECIES.T_FOLLOWING.ordinal()] != -1){
                	t_following = tweetList[ids[COL_INDECIES.T_FOLLOWING.ordinal()]];
                }
                if(ids[COL_INDECIES.T_REPLY_COUNT.ordinal()] != -1){
                	reply_count = tweetList[ids[COL_INDECIES.T_REPLY_COUNT.ordinal()]];
                }
                if(ids[COL_INDECIES.T_RETWEETS.ordinal()] != -1){
                	rt_count = tweetList[ids[COL_INDECIES.T_RETWEETS.ordinal()]];
                }
                if(ids[COL_INDECIES.T_TWEETS.ordinal()] != -1){
                	status_count = tweetList[ids[COL_INDECIES.T_TWEETS.ordinal()]];
                }
                cw.writeNext(this.getRowAsList(pt,sentiment,
                		new String[]{URL,t_followers,t_following,reply_count,rt_count,status_count}));
                
            }
            cr.close();
            cw.close();
            //stats.printStats(new PrintStream(new File(fileName+".stats.tsv")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Error occured while reading line: "+lines_read);
			System.err.println(Arrays.toString(tweetList));
			return 1;
		}
	    return 0;
	}
	
	private int[] getColIds(String[] header){
		int[] ids = new int[this.colNames.length];
		for(int i = 0; i < this.colNames.length; i++){
			int index = -1;
			if(this.colNames[i] != null && !this.colNames[i].equals("<Empty>")){
				index = Arrays.asList(header).indexOf(this.colNames[i]);
				if( index < 0){
					System.err.println("Didn't find column in search: "+this.colNames[i]);
					return null;
				}
				System.err.println("Index of "+this.colNames[i]+" at: "+index);
			}
			ids[i] = index;
		}
		return ids;
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
		
		for(String key:POS_KEYSET){
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
		ArrayList<String> row = new ArrayList<String>();
		
		String[] row1 = {
				Integer.toString(t.c_emo),Integer.toString(t.c_hash),StringUtils.join(t.m_mention,",")
				,t.url,Integer.toString(t.c_url),t.tweet_text
				,Integer.toString(t.c_mention),t.parsed_text,Integer.toString(t.length)
				,StringUtils.join(t.m_emo, ","),t.user,t.published_date
				,Integer.toString(t.c_quote),StringUtils.join(t.m_hash, ",")
				
			};
		
		row.addAll(Arrays.asList(row1));
		for(String key:t.pf.posCounts.keySet()){
			row.add(t.pf.posCounts.get(key).toString());
		}
		for(String key: DictionaryFeatures.getSentimentLabels()){
			row.add(t.to.getLexicalScores().get(key).toString());
		}
		row.addAll(Arrays.asList(new String[]{"?","?"}));

		return row.toArray(new String[row.size()]);
	}
	
	public String[] getRowAsList(ParseTweet t, String sentiment, String[] extras){
		/*
		 * Each row of format:
		 * c_emo	c_hash	m_mention	url	c_url	tweet_text	c_mention	parsed_text	length	m_emo	user	published_date	c_quote	m_hash	e/p/i	s/ns/na
		 */
		String[] row = this.getRowAsList(t);
		int arr_size = row.length+1+extras.length+1; // Last one added for adding probability
		String[] newRow = new String[arr_size];
		for (int i = 0; i < row.length; i++) {
			newRow[i] = row[i];
		}
		newRow[row.length] = sentiment;
		for (int i = 0; i < extras.length; i++) {
			newRow[row.length+1+i] = extras[i];
		}
		newRow[row.length+1+extras.length] = "-1.0"; // Default probability

		return newRow;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		ReadTweetCorpus rtc = new ReadTweetCorpus("input/SupplementaryData.txt","output/Supplementary_POS.tsv",
				new String[]{"tweet_text","user_screenname","created_date",null,"tweet_url",
				"user_followers","user_following",null,"retweet_count","statuses_count"});
		**/
		
		/**
		 * "c_emo"	"c_hash"	"m_mention"	"url"	"c_url"	"tweet_text"	"c_mention"	"parsed_text"	"length"	"m_emo"	"user"	"published_date"	"c_quote"	"m_hash"	"e/p/i"	"s/ns/na"	"sentiment"

		 */
		/*ReadTweetCorpus rtc = new ReadTweetCorpus("temp/All.tsv","temp/All_POS.tsv",
				new String[]{"tweet_text","user","published_date","sentiment","url",
				null,null,null,null,null});
		rtc.writeData();*/
		/*ReadTweetCorpus rtc = new ReadTweetCorpus("E:\\Box\\Box Sync\\Research\\Jana\\InBev\\Sentiment\\data\\Full\\Sampled_3Way_Experiment\\input\\Deduplicated.txt",
				"E:\\Box\\Box Sync\\Research\\Jana\\InBev\\Sentiment\\data\\Full\\Sampled_3Way_Experiment\\output\\Deduplicated_LEX_QUE_POS.txt",
				new String[]{"Snippet","Author","Date","Sentiment","Url",
				"Twitter Followers","Twitter Following","Twitter Reply Count","Twitter Retweets","Twitter Tweets"},
		"E:\\Box\\Box Sync\\Research\\Jana\\InBev\\Sentiment\\data\\Full\\All_data+superbowl\\output\\FinalLexicon\\FILTERED_LEXICON.txt",
		"E:\\Box\\Box Sync\\Research\\Jana\\InBev\\Sentiment\\data\\Full\\All_data+superbowl\\output\\QueryTerms.txt");
		rtc.writeData();*/
		
		ReadTweetCorpus rtc = new ReadTweetCorpus("E:\\Box\\Box Sync\\Research\\Jana\\InBev\\Sentiment\\data\\Full\\All_data+superbowl\\input\\Deduplicated.txt",
				"E:\\Box\\Box Sync\\Research\\Jana\\InBev\\Sentiment\\data\\Full\\All_data+superbowl\\output\\NOPOS_LATEST\\Deduplicated_LEX_QUE.txt",
				new String[]{"Snippet","Author","Date","Sentiment","Url",
				"Twitter Followers","Twitter Following","Twitter Reply Count","Twitter Retweets","Twitter Tweets"},
		"E:\\Box\\Box Sync\\Research\\Jana\\InBev\\Sentiment\\data\\Full\\All_data+superbowl\\output\\FinalLexicon\\FILTERED_LEXICON.txt",
		"E:\\Box\\Box Sync\\Research\\Jana\\InBev\\Sentiment\\data\\Full\\All_data+superbowl\\output\\QueryTerms.txt");
		rtc.writeData();
		

	}

}