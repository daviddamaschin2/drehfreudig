package org.example.baeume;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class HelloController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MenuButton menuButton;

    private Button flipButton;


    private TreeManager treeManager;

    private Stage stage;

    FileChooser chooser;

    final int PADDING = 50; // distance to the border of the window


    @FXML
    private void initialize() {
        //initial position of menu button
        menuButton.setLayoutX(PADDING);
        menuButton.setLayoutY(PADDING);

        //setting up tree manager
        treeManager = new TreeManager();

        //setting up file chooser
        chooser = new FileChooser();
        chooser.setTitle("Open Tree");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        chooser.setInitialDirectory(new File("src/main/resources/org/example/baeume/trees"));

        //setting up flip button
        flipButton = new Button();
        flipButton.setText("Flip Tree");
        flipButton.getStyleClass().setAll(getClass().getResource("styles/style.css").toExternalForm());
        flipButton.getStyleClass().add("glass-button");
        flipButton.setOnAction(actionEvent -> {
            if(treeManager.checkIfTreeCanBeFlipped()){
                System.out.println("Tree can be flipped");
            }
            else{
                System.out.println("Tree cannot be flipped");
            }
        });
    }


    @FXML
    private void handleNew(){
        handleClear();
        treeManager.newEmptyTree();
        treeManager.drawTree(rootPane, PADDING +200, PADDING, (int)stage.getWidth()-2*PADDING-200, (int)stage.getHeight()-4*PADDING);
    }

    @FXML
    private void handleOpen(){
        //letting user choose file
        File selectedFile = chooser.showOpenDialog(stage);

        //processing file
        try(BufferedReader br = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()))){
            //reading entire line and passing it to treeManager to construct tree from input file
            String line = br.readLine();
            treeManager.loadTreeFromFile(line);

            if(treeManager.isTreeLoaded()){
                //removing all existing boxes
                rootPane.getChildren().clear();
                //adding menu button again since it is also removed
                rootPane.getChildren().add(menuButton);
                //drawing boxes for current tree
                treeManager.drawTree(rootPane, PADDING +200, PADDING, (int)stage.getWidth()-2*PADDING-200, (int)stage.getHeight()-4*PADDING);

                flipButton.setLayoutX((stage.getWidth()-2*PADDING-200)/2 + 200);
                flipButton.setLayoutY(stage.getHeight()-2*PADDING);
                rootPane.getChildren().add(flipButton);
            }

        }catch(Exception e){
            System.out.print("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        System.out.println("Save clicked");
    }

    @FXML
    private void fullScreen(){
        //setting stage to fullscreen and back
        if(stage!= null){
            stage.setFullScreen(!stage.isFullScreen());
            if(treeManager.isTreeLoaded()){
                //removing all existing boxes
                rootPane.getChildren().clear();
                //adding menu button again since it is also removed
                rootPane.getChildren().add(menuButton);
                //drawing boxes for current tree
                treeManager.drawTree(rootPane, PADDING +200, PADDING, (int)stage.getWidth()-2*PADDING-200, (int)stage.getHeight()-4*PADDING);

                flipButton.setLayoutX((stage.getWidth()-2*PADDING-200)/2 + 200);
                flipButton.setLayoutY(stage.getHeight()-2*PADDING);
                rootPane.getChildren().add(flipButton);
            }
        }
    }

    @FXML
    private void handleClear(){
        //removing all boxes from the screen
        rootPane.getChildren().clear();
        //adding menu button again since it is also removed
        rootPane.getChildren().add(menuButton);
    }

    @FXML
    private void exit(){
        //exiting application
        javafx.application.Platform.exit();
    }

    @FXML
    private void handleTest(){
        //loading test tree from every test file
        for(int i = 1; i <=15; i++){
            //files with single digit numbers have a leading zero so they need to be handled differently
            if(i < 10){
                try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/org/example/baeume/trees/drehfreudig0" + i + ".txt"))){
                    //reading entire line and passing it to treeManager to construct tree from current test file
                    String line = br.readLine();
                    treeManager.loadTreeFromFile(line);
                }catch(Exception e){
                    System.out.println("Error: " + e.getMessage());
                }
            }
            else{
                try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/org/example/baeume/trees/drehfreudig" + i + ".txt"))){
                    //reading entire line and passing it to treeManager to construct tree from current test file
                    String line = br.readLine();
                    treeManager.loadTreeFromFile(line);
                }catch(Exception e){
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    public void setStage(Stage stage) {
        //this gets called from HelloApplication to set the stage for this controller
        this.stage = stage;
        stage.setFullScreenExitHint("");
    }

    public void processClick(MouseEvent event){
        //processing mouse click, checking if a box was clicked
        if(treeManager.isTreeLoaded()){
            int[] treeDimension = treeManager.getBoxDimensions();
            if(event.getX() >= treeDimension[1] && event.getX() <= treeDimension[1]+treeDimension[0] && event.getY() >= treeDimension[3] && event.getY() <= treeDimension[3]+treeDimension[2]){
                treeManager.addChildAt(event.getX(), event.getY());
                redraw();
            }
        }
    }

    public void redraw(){
        rootPane.getChildren().clear();
        rootPane.getChildren().add(menuButton);
        if(treeManager.isTreeLoaded()){
            treeManager.drawTree(rootPane, PADDING +200, PADDING, (int)stage.getWidth()-2*PADDING-200, (int)stage.getHeight()-4*PADDING);

            flipButton.setLayoutX((stage.getWidth()-2*PADDING-200)/2 + 200);
            flipButton.setLayoutY(stage.getHeight()-2*PADDING);
            rootPane.getChildren().add(flipButton);
        }
    }
}