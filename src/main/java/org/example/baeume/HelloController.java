package org.example.baeume;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class HelloController {

    @FXML
    private MenuButton menuButton;

    @FXML
    private MenuItem openItem;

    @FXML
    private MenuItem newItem;

    @FXML
    private MenuItem saveItem;

    @FXML
    private MenuItem fullscreenItem;

    @FXML
    private MenuItem exitItem;


    private TreeManager treeManager;

    private Stage stage;


    @FXML
    private void initialize() {
        menuButton.setLayoutX(50);
        menuButton.setLayoutY(50);
        treeManager = new TreeManager();
    }


    @FXML
    private void handleNew(){
        System.out.println("New clicked");
    }

    @FXML
    private void handleOpen(){
        System.out.println("Open clicked");
    }

    @FXML
    private void handleSave() {
        System.out.println("Save clicked");
    }

    @FXML
    private void fullScreen(){
        if(stage!= null){
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    @FXML
    private void exit(){
        javafx.application.Platform.exit();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setFullScreenExitHint("");
    }
}