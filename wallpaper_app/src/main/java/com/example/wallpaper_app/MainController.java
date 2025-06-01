
package com.example.wallpaper_app;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainController {
    private final Stage stage;
    private final MainView view;
    private final WallpaperChanger wallpaperChanger;
    private final WallpaperScheduler scheduler;
    private File selectedImage;

    public MainController(Stage stage, MainView view) {
        this.stage = stage;
        this.view = view;
        this.wallpaperChanger = new WallpaperChanger();
        this.scheduler = new WallpaperScheduler();
        this.scheduler.loadSchedule(); // Загружаем расписание при старте
        setupEventHandlers();
        updateScheduleView(); // Обновляем отображение расписания
    }

    private void setupEventHandlers() {
        view.getSelectImageBtn().setOnAction(e -> selectImage());
        view.getAddToScheduleBtn().setOnAction(e -> addToSchedule());
        view.getRemoveFromScheduleBtn().setOnAction(e -> removeFromSchedule());
        view.getStartBtn().setOnAction(e -> startScheduler());
        view.getStopBtn().setOnAction(e -> stopScheduler());
    }

    private void selectImage() {
        selectedImage = view.showImageChooser(stage);
        if (selectedImage != null) {
            view.showPreview(selectedImage);
            view.getStatusLabel().setText("Выбрано: " + selectedImage.getName());
        }
    }

    private void addToSchedule() {
        if (selectedImage == null) {
            view.getStatusLabel().setText("Сначала выберите изображение!");
            return;
        }

        int hour = view.getHourComboBox().getValue();
        int minute = view.getMinuteComboBox().getValue();
        LocalTime time = LocalTime.of(hour, minute);

        scheduler.addToSchedule(time, selectedImage);
        updateScheduleView();
        view.getStatusLabel().setText("Добавлено в расписание: " + time.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void removeFromSchedule() {
        int selectedIndex = view.getScheduleListView().getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            List<LocalTime> times = new ArrayList<>(scheduler.getSchedule().keySet());
            if (selectedIndex < times.size()) {
                scheduler.removeFromSchedule(times.get(selectedIndex));
                updateScheduleView();
            }
        }
    }

    private void updateScheduleView() {
        List<String> scheduleItems = new ArrayList<>();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

        for (Map.Entry<LocalTime, File> entry : scheduler.getSchedule().entrySet()) {
            scheduleItems.add(entry.getKey().format(timeFormat) + " - " + entry.getValue().getName());
        }

        view.updateScheduleList(scheduleItems);
    }

    private void changeWallpaper() {
        if (selectedImage == null) {
            view.getStatusLabel().setText("Сначала выберите изображение!");
            return;
        }

        try {
            wallpaperChanger.setWallpaper(selectedImage);
            view.showPreview(selectedImage);
            view.getStatusLabel().setText("Обои изменены: " +
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) +
                    " - " + selectedImage.getName());
        } catch (Exception e) {
            view.getStatusLabel().setText("Ошибка: " + e.getMessage());
        }
    }

    private void startScheduler() {
        if (scheduler.getSchedule().isEmpty()) {
            view.getStatusLabel().setText("Расписание пусто! Добавьте элементы");
            return;
        }

        scheduler.start(() -> {
            scheduler.getNextScheduledWallpaper().ifPresent(nextEntry -> {
                Platform.runLater(() -> {
                    view.getStatusLabel().setText(
                            "Следующая смена: " +
                                    nextEntry.getKey().format(DateTimeFormatter.ofPattern("HH:mm")) +
                                    " - " + nextEntry.getValue().getName()
                    );
                });
            });
        });

        // Показываем текущий статус
        scheduler.getNextScheduledWallpaper().ifPresent(nextEntry -> {
            view.getStatusLabel().setText(
                    "Планировщик запущен. Следующая смена: " +
                            nextEntry.getKey().format(DateTimeFormatter.ofPattern("HH:mm")) +
                            " - " + nextEntry.getValue().getName()
            );
        });
    }

    private void stopScheduler() {
        scheduler.stop();
        view.getStatusLabel().setText("Планировщик остановлен");
    }
}
