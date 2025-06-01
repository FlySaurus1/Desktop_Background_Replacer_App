package com.example.wallpaper_app;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalTime;
import java.util.List;

public class MainView {
    private final FileChooser fileChooser = new FileChooser();
    private final ImageView imagePreview = new ImageView();
    private final ListView<String> scheduleListView = new ListView<>();
    private final ComboBox<Integer> hourComboBox = new ComboBox<>();
    private final ComboBox<Integer> minuteComboBox = new ComboBox<>();
    private final Button selectImageBtn = new Button("Выбрать изображение");
    private final Button addToScheduleBtn = new Button("Добавить в расписание");
    private final Button removeFromScheduleBtn = new Button("Удалить из расписания");
    private final Button startBtn = new Button("Старт");
    private final Button stopBtn = new Button("Стоп");
    private final Label statusLabel = new Label("Готово к работе");

    public MainView() {
        setupFileChooser();
        setupImagePreview();
        setupTimeSelectors();
    }

    private void setupFileChooser() {
        fileChooser.setTitle("Выберите изображение");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.jpg", "*.jpeg", "*.png", "*.bmp")
        );
    }

    private void setupImagePreview() {
        imagePreview.setFitWidth(300);
        imagePreview.setFitHeight(200);
        imagePreview.setPreserveRatio(true);
    }

    private void setupTimeSelectors() {
        hourComboBox.getItems().addAll(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23);
        hourComboBox.setValue(8);
        minuteComboBox.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
        11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59);
        minuteComboBox.setValue(0);
    }

    public File showImageChooser(Stage stage) {
        return fileChooser.showOpenDialog(stage);
    }

    public void showPreview(File imageFile) {
        try {
            Image image = new Image(imageFile.toURI().toString());
            imagePreview.setImage(image);
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки изображения: " + e.getMessage());
        }
    }

    public void updateScheduleList(List<String> scheduleItems) {
        scheduleListView.getItems().setAll(scheduleItems);
    }

    // Геттеры для всех UI-компонентов
    public ImageView getImagePreview() { return imagePreview; }
    public ListView<String> getScheduleListView() { return scheduleListView; }
    public ComboBox<Integer> getHourComboBox() { return hourComboBox; }
    public ComboBox<Integer> getMinuteComboBox() { return minuteComboBox; }
    public Button getSelectImageBtn() { return selectImageBtn; }
    public Button getAddToScheduleBtn() { return addToScheduleBtn; }
    public Button getRemoveFromScheduleBtn() { return removeFromScheduleBtn; }
    public Button getStartBtn() { return startBtn; }
    public Button getStopBtn() { return stopBtn; }
    public Label getStatusLabel() { return statusLabel; }
}