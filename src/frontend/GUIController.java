/*******************************************************************************
 * Copyright (c) 2015 University of Illinois Board of Trustees, All rights reserved.
 * Developed at GSLIS/ the iSchool, by Dr. Jana Diesner, Shubhanshu Mishra, Liang Tao, and Chieh-Li Chin.    
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses>.
 *******************************************************************************/
package frontend;

//import com.sun.javafx.scene.control.skin.SkinBase;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;



import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;



import org.apache.commons.io.FilenameUtils;



import sentinets.Prediction.MODELTYPE;
import core.AnnotationTask;
import core.AppProperties;
import frontend.Tweet.LABEL;

/**
 * This is the GUI Controller class.
 * @author ltao3, Shubhanshu
 */
@SuppressWarnings("deprecation")
public class GUIController implements Initializable {
    
    private Label label;
    @FXML
    private Button next1;
    @FXML
    private Button next2;
    @FXML
    private Button next13;
    @FXML
    private Button save2;
    @FXML
    private Button next4;
    @FXML // fx:id="retrain"
    private Button retrain; // Value injected by FXMLLoader
    @FXML
    private TableView<Tweet> featureTable;
    @FXML
    private WebView webview;
    @FXML // fx:id="predicted"
    private CheckBox predicted; // Value injected by FXMLLoader
    @FXML // fx:id="visualize"
    private CheckBox visualize; // Value injected by FXMLLoader
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressMessage;
    @FXML
    private ComboBox<String> combo1;
    @FXML
    private ComboBox<String> combo2;
    @FXML
    private ComboBox<String> combo3;
    @FXML
    private ComboBox<String> combo4;
    @FXML
    private ComboBox<String> combo5;
    @FXML
    private ComboBox<String> combo6;
    @FXML
    private ComboBox<String> combo7;
    @FXML
    private ComboBox<String> combo8;
    @FXML
    private ComboBox<String> combo9;
    @FXML
    private ComboBox<String> combo10;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tab1;
    @FXML
    private Tab tab2;
    @FXML
    private AnchorPane webpage;
    @FXML
    private Tab tab3;
    @FXML
    private Tab tab4;  
    @FXML
    private Tab tab5;
    @FXML
    private AnchorPane anchorPane2;
    @FXML
    private TableColumn<Tweet, String> tweetText;
    @FXML
    private TableColumn<Tweet, LABEL> tweetLabel;
    @FXML
    private TableColumn<Tweet, String> tweetFeature;
    
    @FXML
    private TableColumn<Tweet, Double> tweetProb;
    
    @FXML // fx:id="predModel"
    private ComboBox<MODELTYPE> predModel; // Value injected by FXMLLoader
    
    @FXML // fx:id="retrainOutput"
    private TextArea retrainOutput; // Value injected by FXMLLoader

    @FXML // fx:id="outFolder"
    private TextField outFolder; // Value injected by FXMLLoader

    @FXML // fx:id="inFolder"
    private TextField inFolder; // Value injected by FXMLLoader
    @FXML
    private ComboBox<String> outFiles;
    @FXML
    private LineChart<Integer, Double> metricsUpdate;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    
    private File currentDir = new File(System.getProperty("user.dir"));
    
    private ObservableList<Tweet> personData = FXCollections.observableArrayList();    
   
    private Stage stage;
    
    private AnnotationTask at;
    
    private String customModelFile = "";
    
    private ArrayList<Double[]> metrics = new ArrayList<Double[]>(); 

   
    final DirectoryChooser dirChooser = new DirectoryChooser();
    final FileChooser fileChooser = new FileChooser();
    
    ArrayList<ComboBox<String>> comboBoxes;
    
    public void setStage(final Stage stage){
    	this.stage = stage;
    }
    
    public void exit(){
    	at.stopServer();
    }
    
    public File selectFileDir(boolean isFile){
    	File file = null;
    	if(isFile){
    		fileChooser.setInitialDirectory(currentDir);
    		file = fileChooser.showOpenDialog(stage);
    		currentDir = file;
    	} else {
    		dirChooser.setInitialDirectory(currentDir);
    		 file = dirChooser.showDialog(stage);
    		 currentDir = file;
    	}
    	return file;
    }
    
    @FXML
    private void chooseInput(ActionEvent event) throws Exception{
        File file = selectFileDir(false);
        if (file != null) {
            System.out.println(file.getAbsolutePath());
            String path = file.getAbsolutePath();
            inFolder.setText(path);
            at.setInputDir(inFolder.getText());
            List<String> headers = at.readHeader();
            for(int i = 0; i < comboBoxes.size(); i++){
            	comboBoxes.get(i).getSelectionModel().clearSelection();
        		comboBoxes.get(i).getItems().clear();
        		comboBoxes.get(i).getItems().addAll(headers);
        		comboBoxes.get(i).setPromptText("Choose one");
        		if(headers.contains(at.defaultProperties.getHeader(AppProperties.propKeys[i]))){
        			comboBoxes.get(i).setValue(at.defaultProperties.getHeader(AppProperties.propKeys[i]));
        		}
        	}
            if(at.usingCustomConfig() && at.defaultProperties.getModel()!= null){
            	predModel.setValue(MODELTYPE.CUSTOM);
            	customModelFile = at.defaultProperties.getModel();
            	System.out.println(customModelFile);
            	at.setCustomModelFile(customModelFile);
            }
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        }
    }
    
    
    @FXML
    private void chooseOutput(ActionEvent event) throws Exception{
        File file = selectFileDir(false);
        if (file != null) {
            file.getAbsolutePath();
            System.out.println(file.getAbsolutePath());
            String path = file.getAbsolutePath();
            outFolder.setText(path);
            at.outputDir = outFolder.getText();
            //Save file here
        }
    }
    
    private void selectModelFile(ActionEvent event) throws Exception{
    	File file = selectFileDir(true);
        if (file != null) {
            customModelFile = file.getAbsolutePath();
            System.out.println(customModelFile);
            at.setCustomModelFile(customModelFile);
            //Save file here
        }
    }
    
    @FXML
    private void save(ActionEvent event) throws Exception{
        //ADD the existing file to the output folder
    	SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab5);
    }
    
    @FXML
    private void retrain(ActionEvent event) throws Exception{
        //ADD retrain the model
    	System.out.println("Retraining the model");
    	SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab2);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) throws IllegalArgumentException{
    	at = new AnnotationTask();
    	comboBoxes = new ArrayList<ComboBox<String>>();
        comboBoxes.add(combo1);
        comboBoxes.add(combo3);
        comboBoxes.add(combo5);
        comboBoxes.add(combo7);
        comboBoxes.add(combo9);
        comboBoxes.add(combo2);
        comboBoxes.add(combo4);
        comboBoxes.add(combo6);
        comboBoxes.add(combo8);
        comboBoxes.add(combo10);
        
        for(int i = 0; i < comboBoxes.size(); i++){
    		comboBoxes.get(i).setPromptText("Choose one");
    	}
        
        predModel.getItems().add(MODELTYPE.SENTIMENT_WORD);
        predModel.getItems().add(MODELTYPE.CUSTOM);
     // Handle ComboBox event.
        predModel.setOnAction((event) -> {
            if(predModel.getSelectionModel().getSelectedItem().equals(MODELTYPE.CUSTOM)){
            	System.out.println("ComboBox Action (selected: " + MODELTYPE.CUSTOM + ")");
            	try {
					this.selectModelFile(event);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    	//predModel.getItems().add(MODELTYPE.SENTIMENT);
    	predModel.getSelectionModel().selectFirst();
        //progressBar = new ProgressIndicator();
        
//        tabPane.prefWidthProperty().bind(scene.widthProperty());
//        tabPane.prefHeightProperty().bind(scene.heightProperty());
        
        tweetText.setCellValueFactory(new PropertyValueFactory<Tweet, String>("tweet"));
        tweetText.setCellFactory(new Callback<TableColumn<Tweet,String>, TableCell<Tweet,String>>() {
            @Override
            public TableCell<Tweet, String> call(TableColumn<Tweet, String> param) {
                 final TableCell<Tweet, String> cell = new TableCell<Tweet, String>() {
                      private Text text;
                      @Override
                      public void updateItem(String item, boolean empty) {
                           super.updateItem(item, empty);
                           if (!isEmpty()) {
                                text = new Text(item.toString());
                                text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
                                setGraphic(text);
                           }
                      }
                 };
                 return cell;
            }
       });
        tweetLabel.setCellValueFactory(new PropertyValueFactory<Tweet, LABEL>("label"));
        tweetLabel.setCellFactory(ComboBoxTableCell.<Tweet, LABEL>forTableColumn(LABEL.values()));
        tweetLabel.setEditable(true);
        // add here
//        
//        tweetLabel.setOnEditCommit(
//                (CellEditEvent<Tweet, LABEL> t) -> {
//                    ((Tweet) t.getTableView().getItems().get(
//                            t.getTablePosition().getRow())
//                            ).setLabel(t.getNewValue());
//            });
        
        tweetLabel.setOnEditCommit(event -> {
            final TableColumn.CellEditEvent _evn = (TableColumn.CellEditEvent) event;
            int rowId = _evn.getTablePosition().getRow();
            String newVal = _evn.getNewValue().toString();
            Tweet t = ((Tweet) _evn.getTableView().getItems().get(rowId));
            t.setLabel(LABEL.getEnum(newVal));
            System.out.println("Getting new value at row : " + rowId +
            		" with label " + newVal);
            System.out.println("Tweet information : " + t.toString());
            String[] meta = t.getMeta();
            System.out.println(t.getTweet()+"\t"+meta[meta.length - 8]);
            System.out.println(personData.get(rowId).getTweet()+"\t"+personData.get(rowId).getMeta()[meta.length - 8]);
            
        });
        
        tweetFeature.setCellValueFactory(new PropertyValueFactory<Tweet, String>("feature"));
        tweetProb.setCellValueFactory(new PropertyValueFactory<Tweet, Double>("probability"));
        dirChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        /*tweetText.prefWidthProperty().bind(featureTable.widthProperty().divide(4)); // w * 2/4
        tweetLabel.prefWidthProperty().bind(featureTable.widthProperty().divide(2)); // w * 1/4
        tweetFeature.prefWidthProperty().bind(featureTable.widthProperty().divide(4)); // w * 1/4        
         */
        
        }
    
    @FXML
    private void processLogin(ActionEvent event) {
        System.out.println("Steping to next step...");
        progressBar.progressProperty().unbind();
        progressMessage.textProperty().unbind();
        next1.setDisable(true);
        final ArrayList<String> comboBoxValues = new ArrayList<String>();
        for(ComboBox<String> c: comboBoxes){
        	comboBoxValues.add(c.getValue());
        }
        Task<Boolean> t = new Task<Boolean>(){
        	@Override
    		protected Boolean call() throws Exception {
        		updateMessage("Doing Prediction");
                updateProgress(3, 10);
    			int status = at.processAfterLoadFile(comboBoxValues, predModel.getValue(), 
    					visualize.isSelected(), predicted.isSelected());
    			if(status > 0){
    				System.err.println("ProcessAfterLoadFile Error occured.");
    				return false;
    			}
    			updateMessage("Finished Prediction");
    	        if(visualize.isSelected()){
    	        	updateProgress(5, 10);
    	        	at.showVisualization();
    	        	updateMessage("Finished Visualization");
    	        	updateProgress(8, 10);
    	        }
    	        updateProgress(10, 10);
    	        updateMessage("Done");
    	        
    	        //webpage.getChildren().add(new Browser(url));
    	        //at.addTweets(at.outputDir+"/visualization/Sentiment.tsv", personData);
				return true;
    		};
    		
    		
    		
    		@Override protected void succeeded() {
                super.succeeded();
                updateMessage("Done!");
            }

            @Override protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelled!");
            }

            @Override protected void failed() {
                super.failed();
                updateMessage("Failed!");
            }
    		
    		@Override
    		protected void done() {
    			// TODO Auto-generated method stub
    			super.done();
    		}
    	};
    	progressBar.progressProperty().bind(t.progressProperty());
    	progressMessage.textProperty().bind(t.messageProperty());
    	
    	t.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			
			@Override
			public void handle(WorkerStateEvent event) {
				// TODO Auto-generated method stub
				if(t.getValue()){
					Platform.runLater(new Runnable() {
	                    @Override public void run() {
	                    	SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
	            	        selectionModel.select(tab3);
	            	        outFiles.getItems().clear();
	            			outFiles.getItems().addAll(at.files);
	            			System.out.println("Added files to drop down: "+at.files);
	            	        outFiles.getSelectionModel().selectFirst();
	            	        
	            	        next1.setDisable(false);
	            	        featureTable.setEditable(true);
	            	        tweetLabel.setEditable(true);
	            	        tweetText.setEditable(true);
	            	        tweetFeature.setVisible(true);
	            	        tweetProb.setVisible(true);
	                    }
	                });
				} else {
					System.err.println("Task Failed");
				}
			}
		});
    	
    	new Thread(t).start();
        
    }
    
    @FXML
    void updateTable(ActionEvent event) {
    	if(outFiles.getItems().size() < 1){
    		return;
    	}
    	fillTable(outFiles.getSelectionModel().getSelectedItem());
    }
    
    public void fillTable(String fileName){
    	System.out.println("Filling table using: "+fileName);
    	String prefixName;
    	prefixName = FilenameUtils.getBaseName(fileName);
    	personData = FXCollections.observableArrayList();
    	at.addTweets(at.outputDir+"/original/"+prefixName+".tsv", personData);
    	featureTable.setItems(personData);
    }
    
    @FXML
    private void processSave(ActionEvent event) {
        System.out.println("Steping to next step saving file...");
        String path = outFolder.textProperty().getValue().toString();
    	at.saveChanges(path, personData, predicted.isSelected());
    	
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(tab5);
    }
    
    @FXML
    private void processSaveChange(ActionEvent event) {
        String path = outFolder.textProperty().getValue().toString();
        System.out.println("Saving changes to folder " + path);
    	at.saveChanges(path, personData, predicted.isSelected());
    }
    
    @FXML
    void retrainModel(ActionEvent event) {
    	retrainOutput.setText(at.updateModel(metrics));
    	showChart();
    }

    @SuppressWarnings("deprecation")
	@FXML
    void showAbout(ActionEvent event) {
    	/*Dialogs.create()
    	.title("About")
    	.message(getLicense())
    	.showInformation();*/
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.initOwner(stage);
    	alert.initModality(Modality.APPLICATION_MODAL);
    	alert.setTitle("SAIL 1.1: About");
    	alert.setHeaderText("SAIL 1.1 details");
    	alert.setContentText("SAIL 1.1");
    	Label label = new Label("SAIL License details:");

    	TextArea textArea = new TextArea(getLicense());
    	textArea.setEditable(false);
    	textArea.setWrapText(true);

    	textArea.setMaxWidth(Double.MAX_VALUE);
    	textArea.setMaxHeight(Double.MAX_VALUE);
    	GridPane.setVgrow(textArea, Priority.ALWAYS);
    	GridPane.setHgrow(textArea, Priority.ALWAYS);

    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	expContent.add(label, 0, 0);
    	expContent.add(textArea, 0, 1);

    	// Set expandable Exception into the dialog pane.
    	alert.getDialogPane().setExpandableContent(expContent);

    	alert.showAndWait();
    	
    }

    private String getLicense(){
    	String file = GUI.licenseFile;
    	StringBuilder output = new StringBuilder();
    	try (BufferedReader br = new BufferedReader(new FileReader(file))) {
    	    String line;
    	    while ((line = br.readLine()) != null) {
    	       // process the line.
    	    	output.append(line + "\n");
    	    	
    	    }
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	return output.toString();
    	
    }
    
    @FXML
    void editPreferences(ActionEvent event) {
    	EditPreferences dialog = new EditPreferences(stage);
    	Optional<AppProperties> result = dialog.showAndWait();
    	result.ifPresent(props -> {
    		System.out.println("Updating preferences to: ");
    		System.out.println(props.toString());
    		System.out.println("Saving in file: "+GUI.configFile);
    		FileOutputStream saveFile;
    		try {
    			saveFile = new FileOutputStream(GUI.configFile);
				props.store(saveFile, "Saving file");
				saveFile.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	});
    	
    }
    
    private void showChart(){
    	
    	/*metricsUpdate.getXAxis().setAutoRanging(true);
    	metricsUpdate.getYAxis().setAutoRanging(true);
    	*/
    	ArrayList<XYChart.Series<Integer, Double>> metricSeries = new ArrayList<XYChart.Series<Integer, Double>>();
    	metricSeries.add(new XYChart.Series<Integer, Double>()); // POSITIVE
    	metricSeries.add(new XYChart.Series<Integer, Double>()); // NEGATIVE
    	metricSeries.add(new XYChart.Series<Integer, Double>()); // WEIGHTED AVERAGE
    	    	
    	metricSeries.get(0).setName("Positive");
    	metricSeries.get(1).setName("negative");
    	metricSeries.get(2).setName("Weighted Average");
    	
    	
    	int i = 0;
    	double min = 100.0, max = 0.0;
    	double score = 0.0;
    	for(Double[] fScores: metrics){
	    	for(int j = 0; j < 3; j++){
	    		score = fScores[j]*100;
	    		if(score < min){
	    			min = score;
	    		}
	    		if(score > max){
	    			max = score;
	    		}
	    		metricSeries.get(j).getData().add(new XYChart.Data<Integer, Double>(i, score));
	    	}
	    	i++;
    	}
    	
    	yAxis.setLowerBound(min);
    	yAxis.setUpperBound(max);
    	xAxis.setTickUnit(1);
    	
    	metricsUpdate.getData().add(metricSeries.get(0)); // POSITIVE
    	metricsUpdate.getData().add(metricSeries.get(1)); // NEGATIVE 
    	metricsUpdate.getData().add(metricSeries.get(2)); // WEIGHTED AVERAGE
    	
    	System.err.println("Y min: "+min+", Y max: "+max);
    	
    }
}
