package org.example.baeume;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        final int WINDOW_WIDTH = (int)(Screen.getPrimary().getBounds().getWidth() * 0.95);
        final int WINDOW_HEIGHT = (int)(Screen.getPrimary().getBounds().getHeight() * 0.90);


        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Hello!");

        //Create MenuItems
        MenuItem itemNew = new MenuItem("New");
        MenuItem itemOpen = new MenuItem("Open");
        MenuItem itemSave = new MenuItem("Save");
        MenuItem itemFullscreen = new MenuItem("Toggle Fullscreen");
        MenuItem itemExit = new MenuItem("Exit");

        MenuButton menuButton = new MenuButton("Menu", null, itemNew, itemOpen, itemSave, itemFullscreen, itemExit);
        menuButton.getStyleClass().add("glass-menu-button");

        stage.setFullScreenExitHint("");

        itemFullscreen.setOnAction(event -> {
            stage.setFullScreen(!stage.isFullScreen());
        });

        itemExit.setOnAction(event -> {
            stage.close();
        });

        menuButton.setLayoutX(30);
        menuButton.setLayoutY(30);

        Pane root = new Pane();
        root.getStyleClass().add("gradient-bg");

        scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());

        //Add to the Pane
        root.getChildren().add(menuButton);

        scene.setRoot(root);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}