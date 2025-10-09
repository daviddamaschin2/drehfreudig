module org.example.baeume {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.example.baeume to javafx.fxml;
    exports org.example.baeume;
}