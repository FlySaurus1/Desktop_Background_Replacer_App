package com.example.wallpaper_app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        MainController controller = new MainController(primaryStage, mainView);

        VBox root = new VBox(10,
                mainView.getImagePreview(),
                mainView.getSelectImageBtn(),
                new Label("Время смены:"),
                new VBox(5,
                        new Label("Часы:"), mainView.getHourComboBox(),
                        new Label("Минуты:"), mainView.getMinuteComboBox()),
                mainView.getAddToScheduleBtn(),
                mainView.getScheduleListView(),
                mainView.getRemoveFromScheduleBtn(),
                new VBox(5, mainView.getStartBtn(), mainView.getStopBtn()),
                mainView.getStatusLabel()
        );
        root.setPadding(new javafx.geometry.Insets(10));

        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.setTitle("Планировщик обоев");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
