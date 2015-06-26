/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentinets;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.filters.unsupervised.attribute.Remove;

/**
 * @author Shubhanshu
 * 
 */
public class SentiNets {

	/**
	 * @param args
	 */
	Instances original, labled, unlabled, ep_instances, sns_instances;
	String inputFile, outputDir;
	int classifierType;
	int clsIndex;
	String filePrefix;
	FilteredClassifier cls;
	static int E_P = 0;
	static int S_NS = 1;
	static int BOTH = 2;
	private static String EPFilePrefix = "E_P_ALL";
	private static String SNSFilePrefix = "S_NS_ALL";
	private static String BOTHFilePrefix = "ALL";
	private static String EPModelFile = "./data/models/E_P_ALL.model";
	private static String SNSModelFile = "./data/models/S_NS_ALL.model";
	ArrayList<Double[][]> classDist;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFile = "./data/output/cyber_bull.tsv";
		String outputDir = "./data/output";
		SentiNets obj = new SentiNets(inputFile, outputDir, SentiNets.BOTH);
		obj.doPredictions();
	}

	public SentiNets(String inputFile, String outputDir, int classifierType) {
		this.inputFile = inputFile;
		this.outputDir = outputDir;
		this.classifierType = classifierType;
		this.setInstances(this.inputFile);
	}

	public void doPredictions() {
		classDist = new ArrayList<Double[][]>();
		for(int i = 0; i < unlabled.numInstances(); i++){
			/*
			 * Distribution stored as:
			 * {{E, P}, {S, NS}}
			 */
			//classDist.add(TweetCorpusStatistics.getNullDist()); 
		}
		if (classifierType == BOTH) {
			Remove r = new Remove();
			try {
				r.setAttributeIndices("9");
				r.setInputFormat(unlabled);
				ep_instances = Remove.useFilter(unlabled, r);
		
				r.setAttributeIndices("8");
				r.setInputFormat(unlabled);
				sns_instances = Remove.useFilter(unlabled, r);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Classifying E_P classes");
			setClassifier(E_P);
			unlabled = ep_instances;
			unlabled.setClassIndex(unlabled.numAttributes()-1);
			//System.out.println(unlabled.toSummaryString());
			performClassification();
			labled.renameAttribute(labled.numAttributes()-1, "e/p");
			writePredictions(labled, EPFilePrefix);
			
			System.out.println("Classifying S_NS classes");
			setClassifier(S_NS);
			unlabled = sns_instances;
			//System.out.println(unlabled.toSummaryString());
			performClassification();
			writePredictions(labled, SNSFilePrefix);
			
			try {
				original.renameAttribute(original.numAttributes()-2, "e/p");
				original.renameAttribute(original.numAttributes()-1, "s/ns");
				original.renameAttributeValue(original.numAttributes()-1, 1, "ns");
				r = new Remove();
				r.setAttributeIndices("1-3,5,7-10,13-14");
				//r.setAttributeIndices("1-2,5,7-10,13");
				r.setInputFormat(original);
				writePredictions(Remove.useFilter(original, r), BOTHFilePrefix);
				writeStats(original);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else {
			setClassifier(classifierType);
			performClassification();
			writePredictions(labled, filePrefix);
			try {
				original.renameAttribute(original.numAttributes()-2, "e/p");
				original.renameAttribute(original.numAttributes()-1, "s/ns");
				original.renameAttributeValue(original.numAttributes()-1, 1, "ns");
				Remove r = new Remove();
				r.setAttributeIndices("1-3,5,7-10,13-14");
				r.setInputFormat(original);
				writePredictions(Remove.useFilter(original, r), BOTHFilePrefix);
				writeStats(original);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String printDist(Double[][] dist){
		String distStr = "";
		if(dist != null)
			distStr = dist[0][0]+"\t"+dist[0][1]+"\t"+dist[1][0]+"\t"+dist[1][1];
		return distStr;
	}
	public void writeStats(Instances tweetInstances){
		//TweetCorpusStatistics stats = new TweetCorpusStatistics();
		System.out.println("Stats Instances: \n"+tweetInstances.toSummaryString());
		for(int i =0; i < tweetInstances.size(); i++){
			String user = tweetInstances.get(i).stringValue(11-1);
			String mentions = tweetInstances.get(i).stringValue(3-1);
			String hashtags = tweetInstances.get(i).stringValue(14-1);
			String epClass = tweetInstances.get(i).stringValue(15-1);
			String snsClass = tweetInstances.get(i).stringValue(16-1);
			System.out.println("Tweet Details:\t"+user+"\t"+mentions+"\t"+hashtags+"\t"+printDist(classDist.get(i)));
			//stats.updateStatistics(user, mentions, hashtags, epClass+","+snsClass, classDist.get(i));
		}
	}
	public void performClassification() {
		labled = new Instances(unlabled);
		labled.setClassIndex(labled.numAttributes() - 1);
		for (int i = 0; i < unlabled.numInstances(); i++) {
			double clsLabel = 0;
			Double[][] instanceDist = classDist.get(i);
			double[] dist = {0.0, 0.0};
			int distIndex = 0;
			if(classifierType == S_NS)
				distIndex = 1;
			//System.out.println(unlabled.instance(i).toString(4));
			try {
				dist = cls.distributionForInstance(unlabled.instance(i));
				//System.out.println("Distributions for "+filePrefix+" :\t"+dist[0]+","+dist[1]);
				instanceDist[distIndex][0] = dist[0];
				instanceDist[distIndex][1] = dist[1];
				clsLabel = cls.classifyInstance(unlabled.instance(i));
				//System.out.println("CLSLABEL: " + clsLabel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			labled.instance(i).setClassValue(clsLabel);
			original.instance(i).setValue(clsIndex, clsLabel);
			classDist.set(i, instanceDist);
		}
	}

	public void setClassifier(int classifierType) {
		String modelFile = "";
		if (classifierType == E_P) {
			modelFile = EPModelFile;
			clsIndex = 15-1;
			filePrefix = EPFilePrefix;
		} else if (classifierType == S_NS) {
			modelFile = SNSModelFile;
			clsIndex = 16-1;
			filePrefix = SNSFilePrefix;
		} else {
			System.out.println("Wrong Classifier type");
			return;
		}
		cls = null;
		try {
			cls = (FilteredClassifier) weka.core.SerializationHelper
					.read(modelFile);
			//System.out.println(cls.toString());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void setInstances(String inputFile) {
		String[] nominalVals =  {"15:e,p","16:s,na_ns"};
		original = null;
		try {
			CSVLoader csvSource = new CSVLoader();
			csvSource.setSource(new File(inputFile));
			csvSource.setFieldSeparator("\t");
			csvSource.setNominalAttributes("15-16");
			csvSource.setStringAttributes("3,4,6,8,10-12,14");
			csvSource.setNominalLabelSpecs(nominalVals);
			original = csvSource.getDataSet();
			unlabled = original;
			//System.out.println(unlabled.toSummaryString());
			Remove r = new Remove();
			r.setAttributeIndices("3-4,6,10-12,14");
			r.setInputFormat(unlabled);
			unlabled = Remove.useFilter(unlabled, r);
			//System.out.println(unlabled.toSummaryString());
			r = new Remove();
			if(classifierType == E_P){
				System.out.println("Filtering instances for E_P");
				r.setAttributeIndices("9");
				r.setInputFormat(unlabled);
				unlabled = Remove.useFilter(unlabled, r);
			}
			else if(classifierType == S_NS){
				System.out.println("Filtering instances for S_NS");
				r.setAttributeIndices("8");
				r.setInputFormat(unlabled);
				unlabled = Remove.useFilter(unlabled, r);
			}
			//System.out.println(unlabled.toSummaryString());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int cIdx = unlabled.numAttributes() - 1;
		unlabled.setClassIndex(cIdx);
	}

	public void writePredictions(Instances ins, String filePrefix) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputDir
					+ "/" + filePrefix + ".arff"));
			writer.write(ins.toString());
			writer.newLine();
			writer.flush();
			writer.close();
			CSVSaver s = new CSVSaver();

			s.setFile(new File(outputDir + "/" + filePrefix + ".tsv"));
			s.setInstances(ins);
			s.setFieldSeparator("\t");
			s.writeBatch();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
