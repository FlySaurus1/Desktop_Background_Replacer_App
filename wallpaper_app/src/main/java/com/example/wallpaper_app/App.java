package com.example.wallpaper_app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        MainController controller = new MainController(primaryStage, mainView);

        VBox root = new VBox(5,
                mainView.getImagePreview(),
                mainView.getSelectImageBtn(),
                mainView.getTimeSetting(),
                new HBox(5,
                        new Label("Часы:"), mainView.getHourComboBox(),
                        new Label("Минуты:"), mainView.getMinuteComboBox()),
                new VBox (0,
                        new Label(" ")),
                new HBox(5, mainView.getAddToScheduleBtn(), mainView.getRemoveFromScheduleBtn()),
                mainView.getScheduleListView(),
                new HBox(5, mainView.getStartBtn(), mainView.getStopBtn()),
                mainView.getStatusLabel()
        );
        root.setPadding(new javafx.geometry.Insets(10));
        VBox.setMargin(mainView.getTimeSetting(), new Insets(15, 0, 0, 0));
        VBox.setMargin(mainView.getScheduleListView(), new Insets(0, 0, 15, 0));

        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.setTitle("Планировщик обоев");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
