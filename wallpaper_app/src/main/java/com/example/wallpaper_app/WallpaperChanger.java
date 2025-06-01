package com.example.wallpaper_app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WallpaperChanger {
    private static final String BATCH_FILE = "change_wallpaper.bat";

    public void setWallpaper(File imageFile) throws Exception {
        // Проверяем существование файла
        if (!imageFile.exists()) {
            throw new IOException("Файл изображения не найден: " + imageFile.getAbsolutePath());
        }

        // Создаем временный batch-файл
        createTempBatchFile(imageFile);

        // Выполняем batch-файл
        executeBatchFile();
    }

    private void createTempBatchFile(File imageFile) throws IOException {
        // Получаем абсолютный путь к изображению с двойными обратными слешами
        final char dm = (char) 34;
        String imagePath = dm + imageFile.getAbsolutePath().replace("\\\\", "\\") + dm;
        System.out.println(imagePath);

        // Содержимое batch-файла
        String batchContent = String.format(
                "@echo off\n" +
                        "reg add \"HKCU\\control panel\\desktop\" /v wallpaper /t REG_SZ /d \"\" /f\n" +
                        "reg add \"HKCU\\control panel\\desktop\" /v wallpaper /t REG_SZ /d " + imagePath +  " /f\n" +
                        "reg delete \"HKCU\\Software\\Microsoft\\Internet Explorer\\Desktop\\General\" /v WallpaperStyle /f\n" +
                        "reg add \"HKCU\\control panel\\desktop\" /v WallpaperStyle /t REG_SZ /d 2 /f\n" +
                        "RUNDLL32.EXE user32.dll,UpdatePerUserSystemParameters , 1, True\n" +
                        "exit",
                imagePath
        );

        // Записываем в файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BATCH_FILE))) {
            writer.write(batchContent);
        }
    }

    private void executeBatchFile() throws IOException, InterruptedException {
        Path batchPath = Paths.get(BATCH_FILE);
        if (!Files.exists(batchPath)) {
            throw new IOException("Batch-файл не найден: " + BATCH_FILE);
        }

        // Выполняем скрипт с правами администратора (если нужно)
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", BATCH_FILE);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        // Ждем завершения
        int exitCode = process.waitFor();

        // Удаляем временный файл
        Files.deleteIfExists(batchPath);

        if (exitCode != 0) {
            throw new IOException("Ошибка выполнения batch-файла. Код: " + exitCode);
        }
    }
}

