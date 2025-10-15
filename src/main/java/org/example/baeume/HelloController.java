package org.example.baeume;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class HelloController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MenuButton menuButton;

    private Button flipButton;

    private Button editTreeButton;

    private TreeManager treeManager;

    private Stage stage;

    FileChooser chooser;

    private Text flipText;

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
            handleFlip();
            if(treeManager.checkIfTreeCanBeFlipped()){
                System.out.println("Tree can be flipped");
            }
            else{
                System.out.println("Tree cannot be flipped");
            }
        });

        //setting up edit tree button
        editTreeButton = new Button();
        editTreeButton.setText("Edit Tree");
        editTreeButton.getStyleClass().setAll(getClass().getResource("styles/style.css").toExternalForm());
        editTreeButton.getStyleClass().add("glass-button");
        editTreeButton.setOnAction(actionEvent -> {
            if(treeManager.isTreeLoaded()) {
                handleClear();
                treeManager.makeEditable(true);
                treeManager.drawTree(rootPane, PADDING +200, PADDING, (int)stage.getWidth()-2*PADDING-200, (int)stage.getHeight()-4*PADDING);
                rootPane.getChildren().add(flipButton);
            }
        });

        //setting up flip text
        flipText = new Text();
        flipText.setFill(Color.WHITE);
        flipText.setY(40);
        flipText.getStyleClass().add("glass-text");
    }


    @FXML
    private void handleNew(){
        handleClear();
        treeManager.newEmptyTree();
        treeManager.drawTree(rootPane, PADDING +200, PADDING, (int)stage.getWidth()-2*PADDING-200, (int)stage.getHeight()-4*PADDING);
        treeManager.makeEditable(true);
        stage.setTitle("Tree Visualizer - New Tree");
    }

    @FXML
    private void handleOpen(){
        //letting user choose file
        File selectedFile = chooser.showOpenDialog(stage);

        //processing file
        try(BufferedReader br = new BufferedReader(new FileReader(selectedFile.getAbsolutePath()))){
            //reading entire line and passing it to treeManager to construct tree from input file
            String line = br.readLine();
            System.out.println(line);
            treeManager.loadTreeFromFile(line);
            stage.setTitle("Tree Visualizer - " + selectedFile.getName());

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
                treeManager.makeEditable(true);
            }

        }catch(Exception e){
            System.out.print("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        if (!treeManager.isTreeLoaded()) {
            System.out.println("No tree loaded to save!");
            return;
        }

        // create and initialize FileChooser
        FileChooser saveChooser = new FileChooser();
        saveChooser.setTitle("Save Tree");
        saveChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        saveChooser.setInitialDirectory(new File("src/main/resources/org/example/baeume/trees"));
        saveChooser.setInitialFileName("tree.txt");

        // show save dialog
        File fileToSave = saveChooser.showSaveDialog(stage);

        if (fileToSave != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
                String treeString = treeManager.getTreeString();
                bw.write(treeString);
                stage.setTitle("Tree Visualizer - " + fileToSave.getName());
            } catch (IOException e) {
                System.out.println("Error saving file: " + e.getMessage());
            }
        } else {
            System.out.println("Save cancelled by user.");
        }
    }

    @FXML
    private void fullScreen(){
        //setting stage to fullscreen and back
        if(stage!= null){
            stage.setFullScreen(!stage.isFullScreen());
            if(treeManager.isTreeLoaded()){
                redraw();
            }
        }
    }

    @FXML
    private void handleClearButton(){
        handleClear();
        treeManager.unloadTree();
        stage.setTitle("Tree Visualizer");
    }

    private void handleClear(){
        //removing all boxes from the screen
        rootPane.getChildren().clear();
        //adding menu button again since it is also removed
        rootPane.getChildren().add(menuButton);
        treeManager.makeEditable(false);
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

    private void handleFlip(){
        handleClear();
        treeManager.drawTree(rootPane, PADDING +200, PADDING, (int)stage.getWidth()-2*PADDING-200, ((int)stage.getHeight()-4*PADDING)/2-PADDING/2);
        flipText.setX(PADDING +200 + (stage.getWidth()-2*PADDING-200)/2 - 80);
        if(treeManager.checkIfTreeCanBeFlipped()){
            treeManager.drawReversedTree(rootPane, PADDING+200, PADDING + ((int)stage.getHeight()-4*PADDING)/2-PADDING, (int)stage.getWidth()-2*PADDING-200, ((int)stage.getHeight()-4*PADDING)/2-PADDING/2);
            flipText.setText("Tree can be flipped");
        }
        else{
            treeManager.drawReversedTree(rootPane, PADDING+200, PADDING + ((int)stage.getHeight()-4*PADDING)/2+PADDING, (int)stage.getWidth()-2*PADDING-200, ((int)stage.getHeight()-4*PADDING)/2-PADDING/2);
            treeManager.drawRedLine(rootPane, PADDING +200, PADDING + (stage.getHeight()-4*PADDING)/2 + 10, PADDING + 200 + stage.getWidth()-2*PADDING-200, PADDING + (stage.getHeight()-4*PADDING)/2 + 10, 10);
            flipText.setText("Tree cannot be flipped");
        }
        treeManager.makeEditable(false);
        editTreeButton.setLayoutX((stage.getWidth()-2*PADDING-200)/2 + 200);
        editTreeButton.setLayoutY(stage.getHeight()-2*PADDING);
        rootPane.getChildren().add(editTreeButton);
        rootPane.getChildren().add(flipText);
    }

    public void setStage(Stage stage) {
        //this gets called from HelloApplication to set the stage for this controller
        this.stage = stage;
        stage.setFullScreenExitHint("");
    }

    public void processClick(MouseEvent event, boolean rightClick){
        //processing mouse click, checking if a box was clicked
        if(treeManager.isTreeLoaded() && treeManager.canBeEdited()){
            int[] treeDimension = treeManager.getBoxDimensions();
            if(event.getX() >= treeDimension[1] && event.getX() <= treeDimension[1]+treeDimension[0] && event.getY() >= treeDimension[3] && event.getY() <= treeDimension[3]+treeDimension[2]){
                if(rightClick){
                    treeManager.removeChildAt(event.getX(), event.getY());                }
                else{
                    treeManager.addChildAt(event.getX(), event.getY());
                }
                redraw();
                if(stage.getTitle().charAt(stage.getTitle().length()-1) != '*')
                    stage.setTitle(stage.getTitle() + "*");
            }
        }
    }

    public void redraw(){
        rootPane.getChildren().clear();
        rootPane.getChildren().add(menuButton);
        if(treeManager.isTreeLoaded()){
            if(treeManager.isTreeFlipped()){
                handleClear();
                flipText.setX(PADDING +200 + (stage.getWidth()-2*PADDING-200)/2 - 80);
                treeManager.drawTree(rootPane, PADDING +200, PADDING, (int)stage.getWidth()-2*PADDING-200, ((int)stage.getHeight()-4*PADDING)/2-PADDING/2);
                if(treeManager.checkIfTreeCanBeFlipped()){
                    treeManager.drawReversedTree(rootPane, PADDING+200, PADDING + ((int)stage.getHeight()-4*PADDING)/2-PADDING, (int)stage.getWidth()-2*PADDING-200, ((int)stage.getHeight()-4*PADDING)/2-PADDING/2);
                    flipText.setText("Tree can be flipped");
                }
                else{
                    treeManager.drawReversedTree(rootPane, PADDING+200, PADDING + ((int)stage.getHeight()-4*PADDING)/2+PADDING, (int)stage.getWidth()-2*PADDING-200, ((int)stage.getHeight()-4*PADDING)/2-PADDING/2);
                    treeManager.drawRedLine(rootPane, PADDING +200, PADDING + (stage.getHeight()-4*PADDING)/2, PADDING + 200 + stage.getWidth()-2*PADDING-200, PADDING + (stage.getHeight()-4*PADDING)/2, 10);
                    flipText.setText("Tree cannot be flipped");
                }
                editTreeButton.setLayoutX((stage.getWidth()-2*PADDING-200)/2 + 200);
                editTreeButton.setLayoutY(stage.getHeight()-2*PADDING);
                rootPane.getChildren().add(editTreeButton);
                rootPane.getChildren().add(flipText);
            }else{
                treeManager.drawTree(rootPane, PADDING +200, PADDING, (int)stage.getWidth()-2*PADDING-200, (int)stage.getHeight()-4*PADDING);

                flipButton.setLayoutX((stage.getWidth()-2*PADDING-200)/2 + 200);
                flipButton.setLayoutY(stage.getHeight()-2*PADDING);
                rootPane.getChildren().add(flipButton);
            }
        }
    }
}