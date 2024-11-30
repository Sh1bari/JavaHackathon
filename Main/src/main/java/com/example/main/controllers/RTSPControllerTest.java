package com.example.main.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("")
@Tag(name = "Test API", description = "")
public class RTSPControllerTest {
    private static final String RTSP_URL = "rtsp://localhost:8554/stream";

    @GetMapping("/frame")
    public void getFrame() {
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(RTSP_URL)) {
            grabber.setOption("rtsp_transport", "tcp");
            grabber.setFormat("rtsp");
            grabber.setVideoCodec(avcodec.AV_CODEC_ID_H264);

            grabber.start();
            log.info("Подключено к RTSP потоку: {}", RTSP_URL);

            Java2DFrameConverter converter = new Java2DFrameConverter();
            Instant lastCaptureTime = Instant.now();
            int frameCount = 0; // Счётчик для именования файлов

            // Захват кадров в цикле
            while (true) {
                Frame frame = grabber.grabImage();
                if (frame == null) {
                    log.warn("Кадр не получен, возможно поток завершён.");
                    break;
                }

                // Проверяем, прошла ли одна секунда
                Instant currentTime = Instant.now();
                if (Duration.between(lastCaptureTime, currentTime).toMillis() >= 1000) {
                    lastCaptureTime = currentTime;

                    // Преобразуем кадр в BufferedImage
                    BufferedImage bufferedImage = converter.getBufferedImage(frame);

                    // Выводим размер кадра в консоль и сохраняем файл
                    int fileSize = getFrameSizeInBytes(bufferedImage, ++frameCount);
                    log.info("Размер кадра: {} байт", fileSize);
                }
            }

            // Останавливаем захват потока
            grabber.stop();
            log.info("Поток RTSP завершён.");
        } catch (Exception e) {
            log.error("Ошибка при обработке RTSP потока: {}", e.getMessage());
            throw new RuntimeException("Ошибка при обработке RTSP потока.", e);
        }
    }

    private int getFrameSizeInBytes(BufferedImage image, int frameNumber) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Кодируем изображение в JPG
            ImageIO.write(image, "jpg", outputStream);

            // Сохраняем файл в директорию проекта
            File outputFile = new File("frames", "frame_" + frameNumber + ".jpg");
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs(); // Создаём директорию, если она отсутствует
            }
            ImageIO.write(image, "jpg", outputFile);

            // Логируем путь сохранённого файла
            log.info("Кадр сохранён: {}", outputFile.getAbsolutePath());

            return outputStream.size();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при вычислении размера кадра и сохранении.", e);
        }
    }
}