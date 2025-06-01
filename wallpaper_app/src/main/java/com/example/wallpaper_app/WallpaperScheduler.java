package com.example.wallpaper_app;

import javafx.application.Platform;

import java.io.*;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WallpaperScheduler implements Serializable {
    private static final String SCHEDULE_FILE = "schedule.dat";
    private final TreeMap<LocalTime, File> schedule = new TreeMap<>();
    private transient Timer timer;

    public void addToSchedule(LocalTime time, File imageFile) {
        schedule.put(time, imageFile);
        saveSchedule();
    }

    public void removeFromSchedule(LocalTime time) {
        schedule.remove(time);
        saveSchedule();
    }

    public Map<LocalTime, File> getSchedule() {
        return Collections.unmodifiableMap(schedule);
    }

    public Optional<Map.Entry<LocalTime, File>> getNextScheduledWallpaper() {
        LocalTime now = LocalTime.now();

        for (Map.Entry<LocalTime, File> entry : schedule.entrySet()) {
            if (entry.getKey().isAfter(now)) {
                return Optional.of(entry);
            }
        }

        // Если ничего не найдено в оставшейся части дня, берем первое на завтра
        if (!schedule.isEmpty()) {
            return Optional.of(schedule.firstEntry());
        }

        return Optional.empty();
    }

    public void start(Runnable onChangeAction) {
        stop();
        timer = new Timer();

        // Планируем следующую смену
        scheduleNextChange(onChangeAction);
    }


    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void scheduleNextChange(Runnable onChangeAction) {
        getNextScheduledWallpaper().ifPresent(nextEntry -> {
            long delay = calculateDelay(LocalTime.now(), nextEntry.getKey());

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        new WallpaperChanger().setWallpaper(nextEntry.getValue());
                        Platform.runLater(onChangeAction);

                        // Планируем следующую смену
                        scheduleNextChange(onChangeAction);
                    } catch (Exception e) {
                        Platform.runLater(() ->
                                System.err.println("Ошибка: " + e.getMessage()));
                    }
                }
            }, delay);

            Platform.runLater(() ->
                    System.out.println("Следующая смена через " +
                            TimeUnit.MILLISECONDS.toMinutes(delay) + " минут"));
        });
    }


    private long calculateDelay(LocalTime now, LocalTime scheduledTime) {
        long nowSeconds = now.toSecondOfDay();
        long scheduledSeconds = scheduledTime.toSecondOfDay();

        if (scheduledSeconds > nowSeconds) {
            return (scheduledSeconds - nowSeconds) * 1000;
        } else {
            // Если время уже прошло сегодня, планируем на завтра
            return (24 * 3600 - nowSeconds + scheduledSeconds) * 1000;
        }
    }

    public void saveSchedule() {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SCHEDULE_FILE))) {
            // Конвертируем File в String для сериализации
            Map<LocalTime, String> serializableMap = new TreeMap<>();
            for (Map.Entry<LocalTime, File> entry : schedule.entrySet()) {
                serializableMap.put(entry.getKey(), entry.getValue().getAbsolutePath());
            }
            oos.writeObject(serializableMap);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения расписания: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadSchedule() {
        File file = new File(SCHEDULE_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SCHEDULE_FILE))) {
            Map<LocalTime, String> serializedMap = (Map<LocalTime, String>) ois.readObject();
            schedule.clear();
            for (Map.Entry<LocalTime, String> entry : serializedMap.entrySet()) {
                schedule.put(entry.getKey(), new File(entry.getValue()));
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки расписания: " + e.getMessage());
        }
    }
}
