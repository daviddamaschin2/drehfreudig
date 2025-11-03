package org.example.baeume;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();

        final int WINDOW_WIDTH = (int)(Screen.getPrimary().getBounds().getWidth() * 0.95);
        final int WINDOW_HEIGHT = (int)(Screen.getPrimary().getBounds().getHeight() * 0.90);

        HelloController controller = loader.getController();
        controller.setStage(stage);
        stage.setTitle("Tree Visualizer");

        Image icon = new Image(getClass().getResource("images/Icon.png").toExternalForm());
        stage.getIcons().add(icon);

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            controller.redraw();
        });

        stage.heightProperty().addListener((obs, oldVale, newVale)->{
            controller.redraw();
        });

        stage.setMinWidth(400);
        stage.setMinHeight(300);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnMouseClicked(event ->{
            if(event.getButton() == MouseButton.PRIMARY){
                controller.processClick(event, false);
            }
            else if(event.getButton() == MouseButton.SECONDARY){
                controller.processClick(event, true);
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}