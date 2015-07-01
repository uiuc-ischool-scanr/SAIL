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
package frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import core.AppProperties;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * @author Shubhanshu
 *
 */
public class EditPreferences extends Dialog<AppProperties> {
	
	private AppProperties props;
	
	private class BrowseEventHandler implements EventHandler<ActionEvent> {
		
		FileChooser fc;
		TextField tf;
		String propKey;
		Stage stage;
		
		public BrowseEventHandler(Stage stage, FileChooser fc, TextField tf, String propKey) {
			// TODO Auto-generated constructor stub
			this.stage = stage;
			this.fc = fc;
			this.tf = tf;
			this.propKey = propKey;
		}
		
        @Override
        public void handle(final ActionEvent e) {
            File file = fc.showOpenDialog(stage);
            if (file != null) {
                String filePath = file.getAbsolutePath();
                tf.setText(filePath);
                props.setProperty(propKey, filePath);
            }
        }
    };

	public EditPreferences(Stage stage){
		
		this.loadProperties();
		
		this.setTitle("Edit Preferences ...");
		// Set the button types.
		ButtonType save = new ButtonType("Save", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(save, ButtonType.CANCEL);
		
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		final FileChooser fileChooser = new FileChooser();
		
		TextField modelFile = new TextField();		
		modelFile.setText(props.getModel());
		Button browseModel = new Button("Browse...");
		browseModel.setOnAction(new BrowseEventHandler(stage, fileChooser, modelFile, "model"));
		
		TextField lexiconFile = new TextField();
		lexiconFile.setText(props.getLexicon());
		Button browseLexicon = new Button("Browse...");
		browseLexicon.setOnAction(new BrowseEventHandler(stage, fileChooser, lexiconFile, "lexicon"));
		
		TextField queryFile = new TextField();
		queryFile.setText(props.getQueryList());
		Button browseQuery = new Button("Browse...");
		browseQuery.setOnAction(new BrowseEventHandler(stage, fileChooser, queryFile, "query_list"));
		
		Button restoreDefaults = new Button("Restore Defaults ...");
		restoreDefaults.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				try {
					props.load(new FileInputStream(GUI.configDefaultsFile));
					modelFile.setText(props.getModel());
					lexiconFile.setText(props.getLexicon());
					queryFile.setText(props.getQueryList());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		});
		
		grid.add(new Label("Model File"), 0, 0);
		grid.add(modelFile, 1, 0);
		grid.add(browseModel, 2, 0);
		
		grid.add(new Label("Lexicon File"), 0, 1);
		grid.add(lexiconFile, 1, 1);
		grid.add(browseLexicon, 2, 1);
		
		grid.add(new Label("Query File"), 0, 2);
		grid.add(queryFile, 1, 2);
		grid.add(browseQuery, 2, 2);
		
		grid.add(restoreDefaults, 2, 3);
		grid.add(new Label("Restart the application after saving config."), 0, 4, 3, 1);
		
		this.getDialogPane().setContent(grid);
		
		// Convert the result to a username-password-pair when the login button is clicked.
		this.setResultConverter(dialogButton -> {
		    if (dialogButton == save) {
		        return props;
		    }
		    return null;
		});
		
	}
	
	private void loadProperties(){
		props = new AppProperties();
		try {
			props.load(new FileInputStream(GUI.configFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
