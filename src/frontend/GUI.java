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
package frontend;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for launching the tool.
 * @author ltao3
 */
public class GUI extends Application {
	
	public static final String configFile = "./data/config.properties";
	public static final String configDefaultsFile = "./data/config.defaults.properties";
	public static final String licenseFile = "./data/license.txt";
	public GUIController controller;
    @Override
    public void start(Stage stage) throws Exception {
    	stage.setTitle("SAIL (Sentiment Analysis and Incremental Learning)");
    	final FXMLLoader loader = new FXMLLoader(
    		      getClass().getResource(
    		        "GUI.fxml"
    		      )
    		    );
    	
        Parent root = (Parent) loader.load();
        controller = loader.getController();
        controller.setStage(stage);
        Scene scene = new Scene(root);
        //
        /*scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
            }
        });*/
        //
        //Scene scene = new Scene(root, 790, 600);
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();
        
        
    }
    
    @Override
    public void stop() throws Exception {
    	// TODO Auto-generated method stub
    	controller.exit();
    	super.stop();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	try {
			System.setOut(new PrintStream("log.stdout.txt"));
			System.setErr(new PrintStream("log.stderr.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("This is the first output to the log.stdout.txt file");
    	System.err.println("This is the first output to the log.stderr.txt file");
        launch(args);
    }
    
}
