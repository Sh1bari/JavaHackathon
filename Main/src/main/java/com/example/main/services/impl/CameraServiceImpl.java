package com.example.main.services.impl;

import com.example.main.exceptions.GeneralException;
import com.example.main.models.entities.Camera;
import com.example.main.models.entities.PersonLastSeen;
import com.example.main.models.enums.Status;
import com.example.main.models.request.CreateCameraReqDto;
import com.example.main.repositories.CameraRepository;
import com.example.main.services.CameraService;
import com.example.main.services.FileService;
import com.example.main.services.ZoneService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepo;
    private final ZoneService zoneService;

    private final FileService fileService;
    private final KafkaProducerService kafkaProducerService;

    private final Map<Long, Thread> activeStreams = new ConcurrentHashMap<>();

    @Override
    public Camera findById(Long id) {
        return cameraRepo.findById(id)
                .orElseThrow(() -> new GeneralException(404, "Camera not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Camera findByUrl(String url){
        return cameraRepo.findByUrl(url)
                .orElseThrow(() -> new GeneralException(404, "Camera not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Camera> getCameras(Specification<Camera> spec, Pageable pageable) {
        return cameraRepo.findAll(spec, pageable);
    }

    @Transactional
    public Camera save(Camera camera) {
        return cameraRepo.save(camera);
    }

    @Override
    @Transactional
    public Camera createCamera(CreateCameraReqDto dto) {
        Camera camera = CreateCameraReqDto.mapToEntity(dto);
        camera.setZone(zoneService.findById(dto.getZoneId()));
        return save(camera);
    }

    @Transactional
    public Camera deleteCamera(Long id) {
        Camera camera = findById(id);
        if (camera.getStatus() == Status.DELETED){
            throw new GeneralException(400, "camera is already deleted");
        }
        camera.setStatus(Status.DELETED);
        return save(camera);
    }

    @Override
    public void subscribeToCamera(Long cameraId) {
        Camera camera = findById(cameraId);
        if (activeStreams.containsKey(camera.getId())) {
            log.warn("Камера с ID={} уже подписана на поток.", camera.getId());
            return;
        }

        Thread thread = new Thread(() -> processCameraStream(camera));
        activeStreams.put(camera.getId(), thread);
        thread.start();
    }

    @Override
    public void unsubscribeFromCamera(Long cameraId) {
        Thread thread = activeStreams.remove(cameraId);
        if (thread != null) {
            thread.interrupt();
            log.info("Подписка на камеру с ID={} успешно отменена.", cameraId);
        } else {
            log.warn("Камера с ID={} не была подписана.", cameraId);
        }
    }

    @Override
    public void startStreaming() {
        log.info("Запуск потоков для всех активных камер.");

        List<Camera> cameras = cameraRepo.findAll();

        for (Camera camera : cameras) {
            if (camera.getStatus() == Status.ACTIVE) {
                log.info("Подключение к камере с ID={} и URL={}", camera.getId(), camera.getUrl());
                subscribeToCamera(camera.getId());
            } else {
                log.warn("Камера с ID={} неактивна и пропущена.", camera.getId());
            }
        }

        log.info("Все активные камеры подключены.");
    }

    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void subscribeToCameras() {
        startStreaming();
    }

    @Override
    public void stopStreaming() {
        activeStreams.values().forEach(Thread::interrupt);
        activeStreams.clear();
        log.info("Все активные потоки остановлены.");
    }

    private void processCameraStream(Camera camera) {
        String rtspUrl = camera.getUrl();
        log.info("Запуск обработки потока камеры с ID={} по URL={}", camera.getId(), rtspUrl);

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl)) {
            grabber.setOption("rtsp_transport", "tcp");
            grabber.setFormat("rtsp");
            grabber.setVideoCodec(avcodec.AV_CODEC_ID_H264);

            grabber.start();
            log.info("Подключено к RTSP потоку: {}", rtspUrl);

            Java2DFrameConverter converter = new Java2DFrameConverter();
            Instant lastCaptureTime = Instant.now();

            while (!Thread.currentThread().isInterrupted()) {
                Frame frame = grabber.grabImage();
                if (frame == null) {
                    log.warn("Кадр не получен, возможно поток завершён.");
                    break;
                }

                Instant currentTime = Instant.now();
                if (Duration.between(lastCaptureTime, currentTime).toMillis() >= 5000) {
                    lastCaptureTime = currentTime;

                    BufferedImage bufferedImage = converter.getBufferedImage(frame);
                    MultipartFile multipartImage = fileService.convertBufferedImageToMultipartFile(
                            bufferedImage, rtspUrl + "_" + currentTime.toString());
                    com.example.main.models.entities.File file = fileService.uploadFile(multipartImage, null);

                    kafkaProducerService.sendDataToKafka(
                            rtspUrl, file.getBucket(), file.getName(), currentTime
                    );

                    log.info("Данные отправлены в Kafka: CameraId={}, Bucket={}, Name={}, Time={}",
                            camera.getId(), file.getBucket(), file.getName(), currentTime);
                }
            }

            grabber.stop();
            log.info("Поток камеры с ID={} завершён.", camera.getId());
        } catch (Exception e) {
            log.error("Ошибка при обработке камеры с ID={}: {}", camera.getId(), e.getMessage());
        }
    }
}
