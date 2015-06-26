/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package sentinets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.Remove;

public class TrainModel {
	Instances ins;
	String inputFile, outputFile;
	FilteredClassifier cls;
	
	public TrainModel(String inputFile, String outputFile) {
		// TODO Auto-generated constructor stub
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.setInstances(this.inputFile);
	}
	
	public void setInstances(String inputFile) {
		String[] nominalVals =  {"42:positive,negative,neutral"};
		ins = null;
		try {
			CSVLoader csvSource = new CSVLoader();
			csvSource.setSource(new File(inputFile));
			csvSource.setFieldSeparator("\t");
			csvSource.setNominalAttributes("15-16");
			csvSource.setStringAttributes("3,4,6,8,10-12,14");
			csvSource.setNominalLabelSpecs(nominalVals);
			ins = csvSource.getDataSet();
			Remove r = new Remove();
			r.setAttributeIndices("3-4,6,8,10-12,14,40-41");
			r.setInputFormat(ins);
			ins = Remove.useFilter(ins, r);
			//System.out.println(unlabled.toSummaryString());
			r = new Remove();
			System.out.println(ins.toSummaryString());
			
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
		int cIdx = ins.numAttributes() - 1;
		ins.setClassIndex(cIdx);
	}
	
	public void runExps(){
		Classifier c1 = new SMO();
		Classifier c2 = new J48();
		Classifier c3 = new NaiveBayes();
		trainModel(c1, "SVM");
		trainModel(c2, "J48");
		trainModel(c3, "Naive Bayes");
		
	}
	
	public void trainModel(Classifier c, String name){
		Evaluation e;
		try {
			e = new Evaluation(ins);
			e.crossValidateModel(c, ins, 10, new Random(1));
			System.out.println("****Results of "+name+"****");
			System.out.println(e.toSummaryString());
			System.out.println(e.toClassDetailsString());
			System.out.println(e.toCumulativeMarginDistributionString());
			System.out.println(e.toMatrixString());
			System.out.println("*********************");
			TrainModel.saveModel(c, name);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
	private static void saveModel(Classifier c, String name) throws IOException{


	    ObjectOutputStream oos = null;
	    try {
	        oos = new ObjectOutputStream(
	                new FileOutputStream("./models/" + name + ".model"));

	    } catch (FileNotFoundException e1) {
	        e1.printStackTrace();
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }
	    oos.writeObject(c);
	    oos.flush();
	    oos.close();

	}
	
	private static Classifier loadModel(String name) throws Exception {

	    Classifier classifier;

	    FileInputStream fis = new FileInputStream("./models/" + name + ".model");
	    ObjectInputStream ois = new ObjectInputStream(fis);

	    classifier = (Classifier) ois.readObject();
	    ois.close();

	    return classifier;
	}
	
	public static void main(String[] args) {
		String dataFile = "TEST_POS.tsv";
		System.out.println("args.length="+args.length);
		for(int i = 0; i < args.length; i++){
			System.out.printf("args[%s]=%s\n",i,args[i]);
		}
		if(args.length == 1){
			dataFile = args[0];
		}
		else if(args.length > 1){
			System.out.println("Format for calling is:\n");
			System.out.println("ant TrainModel <filepath>");
			System.exit(-1);
		}
		TrainModel t = new TrainModel(dataFile,"");
		t.runExps();
	}
}
