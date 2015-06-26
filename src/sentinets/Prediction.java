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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SGD;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Debug.Random;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * This class is for handling all routines related to prediction and updating of the model.
 * @author Shubhanshu
 *
 */
public class Prediction {
	
	
	Instances original, labled, unlabled, ep_instances, sns_instances;
	String inputFile, outputDir;
	MODELTYPE classifierType;
	int clsIndex;
	String filePrefix;
	Classifier cls;
	
	private static int CLASSINDEX = 44;
	private static String stringAttr = "3,4,6,8,10-12,14,42,43,45";
	private static String removeAttr = "3,4,6,10-12,14,42,43,45-last";
	
	/*private static int CLASSINDEX = 19;
	private static String stringAttr = "3,4,6,8,10-12,14,17,18,20";
	private static String removeAttr = "3,4,6,10-12,14,17,18,20-last";*/
	//FilteredClassifier cls;
	public static enum outDirIndex {ORIGINAL, LABELED, UPDATES, MODELS};
	public static final String[] outDirs = {"original", "labled", "updates", "models"};
	private static final String SentimentModelFile = "./data/models/All_filtered_pos_j48.model"; // This file no longer exists.
	private static String SentimentModelFile_word = "./data/models/All_filtered_pos_SGD_10000_3.model";
	private String customModelFile = "";
	private static String FILEPREFIX = "Sentiment";
	private boolean showProbability  =true;
	public static enum MODELTYPE {
		SENTIMENT("Meta Model"),
		SENTIMENT_WORD("Word Model"),
		CUSTOM("Custom...");
		private String text;
		private MODELTYPE(String text) {
			// TODO Auto-generated constructor stub
			this.text = text;
		}
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.text;
		}
		
	};
	private ArrayList<Double[][]> classDist;
	private String[] classNames = {"positive", "negative"};
	private int classProbIndex = -1;
	
	public static void setModelParams(int class_index, String string_list, String remove_list, String model){
		CLASSINDEX = class_index;
		stringAttr = string_list;
		removeAttr = remove_list;
		SentimentModelFile_word = model;
	}
	
	
	public Prediction(String inputFile, String outputDir, MODELTYPE classifierType) {
		// TODO Auto-generated constructor stub
		this.inputFile = inputFile;
		this.outputDir = outputDir;
		this.classifierType = classifierType;
		this.setInstances(this.inputFile);
	}
	
	public Prediction(String inputFile, String outputDir, MODELTYPE classifierType, String prefix) {
		// TODO Auto-generated constructor stub
		this(inputFile, outputDir, classifierType);
		this.filePrefix = prefix;
	}

	/**
	 * @return the customModelFile
	 */
	public String getCustomModelFile() {
		return customModelFile;
	}

	/**
	 * @param customModelFile the customModelFile to set
	 */
	public void setCustomModelFile(String customModelFile) {
		this.customModelFile = customModelFile;
		System.out.println("Using custom model file for predictions: "+customModelFile);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String inputFile = "./output/Supplementary_POS.tsv";
		String outputDir = "./output";
		Prediction obj = new Prediction(inputFile, outputDir, MODELTYPE.SENTIMENT);
		obj.doPredictions(false);
	}

	private void prepareOutputFolder() {
		for (String dirName : outDirs) {
			dirName = outputDir + "/" + dirName;
			Utils.createFolder(dirName);
		}
	}
	
	/**
	 * 
	 * 
	 * @param predicted - if false then do the predictions else do not do the predictions
	 */
	public int doPredictions(boolean predicted) {
		//classDist = new ArrayList<Double[][]>();
		int status = 0;
		this.prepareOutputFolder();
		for(int i = 0; i < unlabled.numInstances(); i++){
			/*
			 * Distribution stored as:
			 * {{E, P}, {S, NS}}
			 */
			//classDist.add(TweetCorpusStatistics.getNullDist()); 
		}
		status = setClassifier(classifierType);
		if(status > 0){
			System.err.println("Error in setting classifier.");
			return 1;
		}
		labled = new Instances(unlabled);
		labled.setClassIndex(labled.numAttributes() - 1);
		if(!predicted){
			status = performClassification();
			if(status > 0){
				System.err.println("Error in performing classification");
				return 2;
			}
		}
		
		status = writePredictions(labled, "/"+Utils.getOutDir(Utils.OutDirIndex.LABELED)+"/"+filePrefix);
		if(status < 0){
			System.err.println("Writing output file failed: "+Utils.getOutDir(Utils.OutDirIndex.LABELED));
			return 3;
		}
		try {
			Remove r = new Remove();
			status = writePredictions(original, "/"+Utils.getOutDir(Utils.OutDirIndex.ORIGINAL)+"/"+filePrefix);
			if(status < 0){
				System.err.println("Writing output file failed: "+Utils.getOutDir(Utils.OutDirIndex.ORIGINAL));
				return 4;
			}
			//writeStats(original);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 5;
		}
		return 0;
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
	public int performClassification() {
		for (int i = 0; i < unlabled.numInstances(); i++) {
			double clsLabel = 0;
			//Double[][] instanceDist = classDist.get(i);
			double[] dist = {-1., -1.};
			//int distIndex = 0;
			if(classifierType == MODELTYPE.SENTIMENT || classifierType == MODELTYPE.SENTIMENT_WORD
					|| classifierType == MODELTYPE.CUSTOM){
				//distIndex = 0;
			}
			//System.out.println(unlabled.instance(i).toString(4));
			try {
				dist = cls.distributionForInstance(unlabled.instance(i));
				//System.out.println("Distributions for "+filePrefix+" :\t"+dist[0]+","+dist[1]+","+dist[2]);
				//instanceDist[distIndex][0] = dist[0];
				//instanceDist[distIndex][1] = dist[1];
				clsLabel = cls.classifyInstance(unlabled.instance(i));
				//System.out.println("CLSLABEL: " + clsLabel);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Classification task failed.");
				return 1;
			}
			labled.instance(i).setClassValue(clsLabel);
			original.instance(i).setValue(clsIndex, clsLabel);
			if(showProbability){
				original.instance(i).setValue(classProbIndex, Double.max(dist[0], dist[1]));
			}
			//classDist.set(i, instanceDist);
		}
		return 0;
	}
	
	public String updateModel(String inputFile, ArrayList<Double[]> metrics){
		String output = "";
		this.setInstances(inputFile);
		FilteredClassifier fcls = (FilteredClassifier)this.cls;
		SGD cls = (SGD) fcls.getClassifier();
		Filter filter = fcls.getFilter();
		Instances insAll;
		try {
			insAll = Filter.useFilter(this.unlabled, filter);
			if(insAll.size() > 0){
				Random rand = new Random(10);
				int folds = 10 > insAll.size() ? 2: 10;
				Instances randData = new Instances(insAll);
				randData.randomize(rand);
				if (randData.classAttribute().isNominal()){
			        randData.stratify(folds);
				}
				Evaluation eval = new Evaluation(randData);
				eval.evaluateModel(cls, insAll);
				System.out.println("Initial Evaluation");
				System.out.println(eval.toSummaryString());
				System.out.println(eval.toClassDetailsString());
				metrics.add(new Double[]{eval.fMeasure(0), eval.fMeasure(1), eval.weightedFMeasure()});
				output += "\n===="+"Initial Evaluation"+"====\n";
				output += "\n"+eval.toSummaryString();
				output += "\n"+eval.toClassDetailsString();
				System.out.println("Cross Validated Evaluation");
				output += "\n===="+"Cross Validated Evaluation"+"====\n";
				for (int n = 0; n < folds; n++) {
					Instances train = randData.trainCV(folds, n);
			        Instances test = randData.testCV(folds, n);

		            for (int i = 0; i < train.numInstances(); i++) {
		              cls.updateClassifier(train.instance(i));
		            }

			        eval.evaluateModel(cls, test);
			        System.out.println("Cross Validated Evaluation fold: " + n);
					output += "\n===="+"Cross Validated Evaluation fold ("+n+")====\n";
			        System.out.println(eval.toSummaryString());
					System.out.println(eval.toClassDetailsString());
					output += "\n"+eval.toSummaryString();
					output += "\n"+eval.toClassDetailsString();
					metrics.add(new Double[]{eval.fMeasure(0), eval.fMeasure(1), eval.weightedFMeasure()});
				}
				for (int i = 0; i < insAll.numInstances(); i++) {
	              cls.updateClassifier(insAll.instance(i));
	            }
		        eval.evaluateModel(cls, insAll);
		        System.out.println("Final Evaluation");
		        System.out.println(eval.toSummaryString());
				System.out.println(eval.toClassDetailsString());
				output += "\n===="+"Final Evaluation"+"====\n";
				output += "\n"+eval.toSummaryString();
				output += "\n"+eval.toClassDetailsString();
				metrics.add(new Double[]{eval.fMeasure(0), eval.fMeasure(1), eval.weightedFMeasure()});
				fcls.setClassifier(cls);
				String modelFilePath = outputDir+"/"+Utils.getOutDir(Utils.OutDirIndex.MODELS)+
						"/updatedClassifier.model";
				weka.core.SerializationHelper.write(modelFilePath, fcls);
				output += "\n" + "Updated Model saved at: "+modelFilePath;
			} else {
				output += "No new instances for training the model.";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	public int setClassifier(MODELTYPE classifierType) {
		String modelFile = "";
		cls = null;
		try {
			if (classifierType == MODELTYPE.SENTIMENT) {
				modelFile = SentimentModelFile;
				clsIndex = CLASSINDEX-1;
				
					cls = (J48) weka.core.SerializationHelper
							.read(modelFile);
				
				//filePrefix = FILEPREFIX;
			} else if (classifierType == MODELTYPE.SENTIMENT_WORD) {
				modelFile = SentimentModelFile_word;
				clsIndex = CLASSINDEX-1;
				cls = (FilteredClassifier) weka.core.SerializationHelper
						.read(modelFile);
				//filePrefix = FILEPREFIX;
			} else if (classifierType == MODELTYPE.CUSTOM){
				modelFile = getCustomModelFile();
				//this.classifierType = MODELTYPE.SENTIMENT_WORD; 
				clsIndex = CLASSINDEX-1;
				cls = (FilteredClassifier) weka.core.SerializationHelper
						.read(modelFile);
			} else {
				System.out.println("Wrong Classifier type");
				return 1;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		}
		return 0;
	}

	public int setInstances(String inputFile) {
		//String[] nominalVals =  {"42:positive,neutral,negative"};
		String[] nominalVals =  {CLASSINDEX+":"+StringUtils.join(classNames, ",")};
		original = null;
		try {
			System.out.println("[In Prediction] Loading instances. ");
			CSVLoader csvSource = new CSVLoader();
			csvSource.setSource(new File(inputFile));
			csvSource.setFieldSeparator("\t");
			csvSource.setNominalAttributes(CLASSINDEX+"");
			csvSource.setStringAttributes(stringAttr);
			csvSource.setNominalLabelSpecs(nominalVals);
			original = csvSource.getDataSet();
			unlabled = original;
			classProbIndex = original.numAttributes()-1;
			//System.out.println(unlabled.toSummaryString());
			Remove r = new Remove();
			//r.setAttributeIndices("3-4,6,10-12,14");
			if(classifierType == MODELTYPE.SENTIMENT || classifierType == MODELTYPE.SENTIMENT_WORD
					|| classifierType == MODELTYPE.CUSTOM){
				if(showProbability){
					/*
					Add afilter;
					afilter = new Add();
					afilter.setAttributeName("last");
					afilter.setAttributeName("prediction_prob");
					afilter.setInputFormat(original);
					original = Filter.useFilter(original, afilter);
					classProbIndex = original.numAttributes()-1;*/
				}
				if(classifierType == MODELTYPE.SENTIMENT){
					r.setAttributeIndices("3,4,6,8,10-12,14,42,43,45-last");
					System.out.println("Filtering instances for SENTIMENT");
				}
				else if(classifierType == MODELTYPE.SENTIMENT_WORD || classifierType == MODELTYPE.CUSTOM){
					r.setAttributeIndices(removeAttr);
					System.out.println("Filtering instances for SENTIMENT WORD");
				}
			}
			//r.setAttributeIndices("3-4,6,10-12,14,40-41,43-last");
			r.setInputFormat(unlabled);
			unlabled = Remove.useFilter(unlabled, r);
			//System.out.println(unlabled.toSummaryString());
			r = new Remove();
			//System.out.println(unlabled.toSummaryString());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 3;
		}
		int cIdx = unlabled.numAttributes() - 1;
		unlabled.setClassIndex(cIdx);
		System.out.println("Class Attribute is: "+unlabled.classAttribute()+" at index: "+unlabled.classIndex());
		return 0;
	}

	public int writePredictions(Instances ins, String filePrefix) {
		try {
			System.out.println("Trying to create the following files:");
			System.out.println(outputDir+ "/" + filePrefix + ".arff");
			System.out.println(outputDir+ "/" + filePrefix + ".tsv");
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
			return 1;
		}
		return 0;
	}

}
