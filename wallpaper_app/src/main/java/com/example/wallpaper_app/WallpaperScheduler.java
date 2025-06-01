package com.example.wallpaper_app;

import java.io.*;
import java.time.LocalTime;
import java.util.*;

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

    public Optional<Map.Entry<LocalTime, File>> getNextChange() {
        LocalTime now = LocalTime.now();
        return schedule.entrySet().stream()
                .filter(entry -> entry.getKey().isAfter(now))
                .findFirst()
                .or(() -> schedule.isEmpty() ? Optional.empty()
                        : Optional.of(schedule.firstEntry()));
    }

    public void start(Runnable onChangeAction) {
        stop();
        scheduleNextChange(onChangeAction);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void scheduleNextChange(Runnable onChangeAction) {
        getNextChange().ifPresent(entry -> {
            long delay = calculateDelay(LocalTime.now(), entry.getKey());
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    onChangeAction.run();
                    scheduleNextChange(onChangeAction);
                }
            }, delay);
        });
    }

    private long calculateDelay(LocalTime now, LocalTime scheduledTime) {
        long nowSeconds = now.toSecondOfDay();
        long scheduledSeconds = scheduledTime.toSecondOfDay();
        return (scheduledSeconds > nowSeconds)
                ? (scheduledSeconds - nowSeconds) * 1000
                : (24*3600 - nowSeconds + scheduledSeconds) * 1000;
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
