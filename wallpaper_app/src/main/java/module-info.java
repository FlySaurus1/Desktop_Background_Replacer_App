module com.example.wallpaper_app {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.wallpaper_app to javafx.fxml;
    exports com.example.wallpaper_app;
}