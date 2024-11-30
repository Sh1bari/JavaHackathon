package com.example.rtspserver.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@RestController
@Validated
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/rtsp")
@Tag(name = "Face API", description = "")
public class RTSPController {

    private final BlockingQueue<File> videoQueue = new LinkedBlockingQueue<>();

    public RTSPController() {
        startStreamingProcess();
    }

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadVideo(@RequestPart("file") MultipartFile file) {
        try {
            // Сохраняем файл во временной директории
            File tempFile = File.createTempFile("uploaded-", ".mp4");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }

            // Добавляем файл в очередь
            videoQueue.put(tempFile);

            log.info("Видео добавлено в очередь: {}", tempFile.getAbsolutePath());
            return ResponseEntity.ok("Видео успешно загружено и добавлено в очередь.");
        } catch (IOException | InterruptedException e) {
            log.error("Ошибка при загрузке видео: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка при загрузке видео: " + e.getMessage());
        }
    }

    private void startStreamingProcess() {
        new Thread(() -> {
            while (true) {
                try {
                    // Берём файл из очереди
                    File videoFile = videoQueue.take();

                    // Команда для трансляции через FFmpeg
                    String command = String.format(
                            "ffmpeg -re -i %s -c:v libx264 -preset ultrafast -tune zerolatency -c:a aac -f rtsp -rtsp_transport tcp rtsp://0.0.0.0:8554/stream",
                            videoFile.getAbsolutePath()
                    );

                    log.info("Старт трансляции видео: {}", videoFile.getAbsolutePath());

                    // Запускаем процесс
                    Process process = new ProcessBuilder("/bin/bash", "-c", command)
                            .redirectErrorStream(true)
                            .start();

                    // Логируем вывод FFmpeg
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            log.info("FFmpeg: {}", line);
                        }
                    }

                    // Ожидаем завершения процесса
                    int exitCode = process.waitFor();
                    if (exitCode != 0) {
                        log.error("FFmpeg завершился с ошибкой, код возврата: {}", exitCode);
                    }

                    log.info("Трансляция завершена для видео: {}", videoFile.getAbsolutePath());

                    // Удаляем файл после завершения трансляции
                    if (!videoFile.delete()) {
                        log.warn("Не удалось удалить файл: {}", videoFile.getAbsolutePath());
                    }

                } catch (InterruptedException | IOException e) {
                    log.error("Ошибка при обработке видео: {}", e.getMessage());
                }
            }
        }).start();
    }

}
