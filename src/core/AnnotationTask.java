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
package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.collections.ObservableList;

import org.apache.commons.io.FilenameUtils;

import sentinets.Prediction;
import sentinets.Prediction.MODELTYPE;
import sentinets.Prediction.outDirIndex;
import sentinets.ReadTweetCorpus;
import sentinets.ReadTweetCorpus.COL_INDECIES;
import sentinets.Utils;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.sun.net.httpserver.HttpServer;

import frontend.Tweet;

/**
 * @author Shubhanshu
 *
 */
public class AnnotationTask {

	/**
	 * This is a workflow class which is used by the GUI for tying together
	 * the differents parts like data parsing, feature extraction, prediction, model update
	 * and finally sending the data in required format to the table and running the visualization.
	 */
	
	
	public List<String> files = new ArrayList<String>();
    public String inputDir, outputDir;
    private static HttpServer server;
    private String configFile;
    public AppProperties defaultProperties;
    private String lexiconPath,queryTerms;
    private String[] headerRow;
	private Prediction obj;
	private String customModelFile = "";
	private int class_index = 19;
	private String string_list = "";
	private String remove_list = "";
	private boolean customProps;
	public AnnotationTask() {
		// TODO Auto-generated constructor stub
		defaultProperties = new AppProperties();
		if(server != null){
			server.stop(0);
		}
		server = null;
		setConfigFile("./data/config.properties");
		customProps = false;
		
	}
	
	public boolean usingCustomConfig(){
		return customProps;
	}
	
	/**
	 * @return the lexiconPath
	 */
	public String getLexiconPath() {
		return lexiconPath;
	}

	/**
	 * @return the queryTerms
	 */
	public String getQueryTerms() {
		return queryTerms;
	}

	/**
	 * @param lexiconPath the lexiconPath to set
	 */
	public void setLexiconPath(String lexiconPath) {
		this.lexiconPath = lexiconPath;
	}

	/**
	 * @param queryTerms the queryTerms to set
	 */
	public void setQueryTerms(String queryTerms) {
		this.queryTerms = queryTerms;
	}

	public void setInputDir(String inputDir){
		this.inputDir = inputDir;
		/*this.setQueryTerms("data/lexicons/QueryTerms.txt");
		this.setLexiconPath("data/lexicons/FILTERED_LEXICON.txt");*/
		final File folder = new File(inputDir);
        listFilesForFolder(folder);
	}
	
	public void setCustomModelFile(String path){
		customModelFile = path;
	}
	
	public void setClassIndex(String class_index){
		this.class_index = Integer.parseInt(class_index);
	}
	
	public void setStringList(String string_list){
		this.string_list = string_list;
	}
	
	public void setRemoveList(String remove_list){
		this.remove_list = remove_list;
	}
	
	private void setConfigFile(String path){
		this.configFile = path;
		FileInputStream in;
		try {
			in = new FileInputStream(path);
			defaultProperties.load(in);
			System.err.println(defaultProperties.entrySet());
			in.close();
			if(defaultProperties.getLexicon() != null){
				this.setLexiconPath(defaultProperties.getLexicon());
			}
			if(defaultProperties.getQueryList() != null){
				this.setQueryTerms(defaultProperties.getQueryList());
			}
			if(defaultProperties.getModel() != null){
				this.setCustomModelFile(defaultProperties.getModel());
			}
			if(defaultProperties.getClassIndex() != null){
				this.setClassIndex(defaultProperties.getClassIndex());
			}
			if(defaultProperties.getStringList() != null){
				this.setStringList(defaultProperties.getStringList());
			}
			if(defaultProperties.getRemoveList() != null){
				this.setRemoveList(defaultProperties.getRemoveList());
			}
			Prediction.setModelParams(this.class_index, this.string_list, this.remove_list, this.customModelFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private List<String[]> originalData = new ArrayList<>();
	
	public void addTweets(String fileName, ObservableList<Tweet> personData){
   	 CSVReader cr;
		try {
			cr = new CSVReader(new FileReader(fileName), '\t');
			headerRow = cr.readNext();
			HashMap<String, Integer> headers = new HashMap<String, Integer>();
			for(int i = 0; i < headerRow.length; i++){
				headers.put(headerRow[i], i);
				System.err.println("["+i+"]="+headerRow[i]);
			}
			HashMap<String, Integer> f_list = new HashMap<String, Integer>();
			f_list.put("c_emo", headers.get("c_emo"));
			f_list.put("c_hash", headers.get("c_hash"));
			f_list.put("c_url", headers.get("c_url"));
			f_list.put("c_mention", headers.get("c_mention"));
			f_list.put("length", headers.get("length"));
			int i = 0;
			String[] tweetList;
			while ((tweetList = cr.readNext())!= null){
				Tweet t = new Tweet(tweetList[headers.get("tweet_text")], tweetList[headers.get("sentiment")],
						i, tweetList, Double.parseDouble(tweetList[headers.get("prediction_prob")]));
				originalData.add(tweetList);
				for(String key: f_list.keySet()){
					t.setFeature(key, Integer.parseInt(tweetList[f_list.get(key)]));
				}
				personData.add(t);
				i++;
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	
   }
	
	public void saveChanges(String outFolder, ObservableList<Tweet> personData, boolean predicted) {
		CSVWriter writer;
		try {
			String updateFile = outFolder +"/"+Utils.getOutDir(Utils.OutDirIndex.UPDATES)+"/updates.txt";
			System.out.println("Path: " + updateFile);
			writer = new CSVWriter(new FileWriter(updateFile), '\t', '\"','\\');
			writer.writeNext(headerRow);
			for (Tweet t : personData) {
				if(t.isChanged() && !predicted){
					writer.writeNext(t.getMeta());
				} else if (predicted){
					writer.writeNext(t.getMeta());
				}
			}
			writer.close();
		} catch (Exception e){
			e.printStackTrace();
		}		
	}
	
	public String updateModel(ArrayList<Double[]> metrics){
		String updateFile = this.outputDir+"/"+Utils.getOutDir(Utils.OutDirIndex.UPDATES)+"/updates.txt";
		String output = "";
		output = obj.updateModel(updateFile, metrics);
		return output;
	}
	
	public void listFilesForFolder(final File folder) {
		files.clear();
        for (final File fileEntry : folder.listFiles()) {
        	if(FilenameUtils.getExtension(fileEntry.getAbsolutePath()).equals("properties")){
        		System.out.println("Updating properties file.");
        		setConfigFile(fileEntry.getAbsolutePath());
        		customProps = true;
        	}
        	else if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                files.add(fileEntry.getPath());
                System.out.println(fileEntry.getName());
            }
        }
    }
	
	public List<String> readHeader() throws Exception{
        List<String> combos = new ArrayList<String>();        
        CSVReader cr = new CSVReader(new FileReader(files.get(0)), '\t');
        String[] headers = cr.readNext();
        for (String s : headers){
            combos.add(s);
        }
        String[] carr = combos.toArray(new String[combos.size()]);
        Arrays.sort(carr);
        combos = new ArrayList<String>();
        combos.addAll(Arrays.asList(carr));
        combos.add("<Empty>");
        cr.close();
        return combos;
    }
	
	/**
	 * 
	 * TODO - Move Features folder name to Utils along with all the other list of output folders and use it using the enum index.
	 * @param comboBoxes
	 * @param mt
	 * @param visualize
	 * @param predicted
	 */
	public int processAfterLoadFile(ArrayList<String> comboBoxes, MODELTYPE mt, boolean visualize, boolean predicted){
    	String[] colMapping = new String[COL_INDECIES.values().length];
    	for(int i = 0; i < comboBoxes.size(); i++){
    		colMapping[COL_INDECIES.values()[i].ordinal()] = comboBoxes.get(i);
    	}
    	
    	System.err.println(colMapping);
    	System.err.print("[");
    	for(int i = 0; i < colMapping.length; i++){
    		System.err.print(colMapping[i]+",");
    	}
    	System.err.println("]");
    	String prefixName, parseFile;
    	ReadTweetCorpus rtc;
    	Utils.createFolder(outputDir+"/"+Utils.getOutDir(Utils.OutDirIndex.FEATURES)+"/");
    	for(String fileName: files){
    		System.out.println("[In Annotation Task] Reading file: "+fileName);
    		prefixName = FilenameUtils.getBaseName(fileName);
    		parseFile  = outputDir+"/"+Utils.getOutDir(Utils.OutDirIndex.FEATURES)+"/"+prefixName+".tsv";
    		rtc = new ReadTweetCorpus(fileName,parseFile,colMapping, this.getLexiconPath(), this.getQueryTerms());
    		int status = rtc.writeData();
    		if(status > 0){
    			System.err.println("ReadTweetCorpus writeData Error occured.");
				return 1;
    		}
    		obj = new Prediction(parseFile, outputDir, mt, prefixName);
    		if(mt.equals(MODELTYPE.CUSTOM)){
    			obj.setCustomModelFile(this.customModelFile);
    		}
   		
    		obj.doPredictions(predicted);
    	}
    	if(visualize){
    		setupVisualization();
    	}
    	return 0;
    }
	
	public void setupVisualization(){
		copyFolder("./data/visualization", outputDir);		

		File file = new File(outputDir+"/file_list.js");

		try {
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("var files=[\n");
			String prefixName = "";
			for(String fileName: files){
	    		prefixName = FilenameUtils.getBaseName(fileName);
				bw.write("\"/"+Utils.getOutDir(Utils.OutDirIndex.ORIGINAL)+"/"+prefixName+".tsv\",\n");
			}
			bw.write("];");
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Done creating filelist");
	}
	
	public void showVisualization(){
		String url = "http://localhost:8000/index.html";
		try {
			stopServer();
			server = HttpServer.create(new InetSocketAddress(8000), 0);
    	    String root = outputDir;
    	    server.createContext("/", new VisServerHandler(root));
    	    server.setExecutor(null); // creates a default executor
    	    server.start();
    	    System.out.println("Serving files at "+url);
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
       } catch (java.io.IOException e) {
    	   System.out.println("Caught exception in running the server.");
            System.out.println(e.getMessage());
       }
	}
	
	public void stopServer(){
		if(server!=null){
			System.out.println("Stopping the server.");
			server.stop(0);
			server = null;
		}
	}
	
	public static void copyFolder(String src, String dest){
		File srcFolder = new File(src);
    	File destFolder = new File(dest);
 
    	//make sure source exists
    	if(!srcFolder.exists()){
 
           System.out.println("Directory does not exist.");
           //just exit
           System.exit(0);
 
        }else{
 
           try{
        	copyFolder(srcFolder,destFolder);
           }catch(IOException e){
        	e.printStackTrace();
        	//error, just exit
                System.exit(0);
           }
        }
 
    	System.out.println("Done");
	}
	
	public static void copyFolder(File src, File dest)
	    	throws IOException{
	 
	    	if(src.isDirectory()){
	 
	    		//if directory not exists, create it
	    		if(!dest.exists()){
	    		   dest.mkdir();
	    		   System.out.println("Directory copied from " 
	                              + src + "  to " + dest);
	    		}
	 
	    		//list all the directory contents
	    		String files[] = src.list();
	 
	    		for (String file : files) {
	    		   //construct the src and dest file structure
	    		   File srcFile = new File(src, file);
	    		   File destFile = new File(dest, file);
	    		   //recursive copy
	    		   copyFolder(srcFile,destFile);
	    		}
	 
	    	}else{
	    		//if file, then copy it
	    		//Use bytes stream to support all file types
	    		InputStream in = new FileInputStream(src);
    	        OutputStream out = new FileOutputStream(dest); 
 
    	        byte[] buffer = new byte[1024];
 
    	        int length;
    	        //copy the file content in bytes 
    	        while ((length = in.read(buffer)) > 0){
    	    	   out.write(buffer, 0, length);
    	        }
 
    	        in.close();
    	        out.close();
    	        //System.out.println("File copied from " + src + " to " + dest);
	    	}
	    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new AnnotationTask();
	}

}
