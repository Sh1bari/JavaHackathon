package com.example.mockservice.services;

import com.example.mockservice.models.entities.Face;
import com.example.mockservice.repositories.FaceRepository;
import com.example.mockservice.utils.MatSerializer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.bytedeco.opencv.global.opencv_core.minMaxLoc;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
public class FaceService {
    private final CascadeClassifier faceDetector;

    @Autowired
    private FaceRepository faceRepository;

    @Autowired
    private MatSerializer matSerializer;

    public void addFace(UUID uuid, MultipartFile file) {
        try {
            // Получаем байты изображения
            byte[] bytes = file.getBytes();
            if (bytes.length == 0) {
                throw new IllegalArgumentException("Файл изображения пуст или поврежден.");
            }

            // Декодируем байты изображения в Mat
            Mat mat = opencv_imgcodecs.imdecode(new Mat(bytes), opencv_imgcodecs.IMREAD_COLOR);
            if (mat.empty()) {
                throw new IllegalArgumentException("Ошибка декодирования изображения: Mat пуст.");
            }

            // Преобразуем в черно-белое изображение
            Mat grayMat = new Mat();
            opencv_imgproc.cvtColor(mat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);
            if (grayMat.empty()) {
                throw new RuntimeException("Ошибка преобразования изображения в черно-белое.");
            }

            // Изменяем размер изображения для унификации (например, 100x100)
            Mat resizedMat = new Mat();
            opencv_imgproc.resize(grayMat, resizedMat, new Size(300, 300), 0, 0, opencv_imgproc.INTER_AREA);
            if (resizedMat.empty()) {
                throw new RuntimeException("Ошибка изменения размера изображения.");
            }

            // Преобразуем в нужный тип, например, CV_8U
            Mat standardizedMat = new Mat();
            resizedMat.convertTo(standardizedMat, opencv_core.CV_8U);
            if (standardizedMat.empty()) {
                throw new RuntimeException("Ошибка преобразования изображения в стандартный тип.");
            }

            // Сериализация Mat
            byte[] serializedMat = matSerializer.serializeMat(standardizedMat);
            if (serializedMat == null || serializedMat.length == 0) {
                throw new RuntimeException("Ошибка сериализации изображения: результат пустой.");
            }

            // Сохраняем в базу данных
            Face face = Face.builder()
                    .id(uuid)
                    .faceData(serializedMat)
                    .build();
            faceRepository.save(face);

            // Отладочная информация
            System.out.println("Лицо успешно добавлено. UUID: " + uuid);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла", e);
        }
    }

    public List<UUID> findMatchingFaces(MultipartFile file) throws ClassNotFoundException {
        try {
            // Декодируем изображение
            byte[] bytes = file.getBytes();
            if (bytes.length == 0) {
                throw new IllegalArgumentException("Файл изображения пуст или поврежден.");
            }

            Mat mat = opencv_imgcodecs.imdecode(new Mat(bytes), opencv_imgcodecs.IMREAD_COLOR);
            if (mat.empty()) {
                throw new IllegalArgumentException("Ошибка декодирования изображения: Mat пуст.");
            }

            // Преобразуем в черно-белое изображение
            Mat grayMat = new Mat();
            opencv_imgproc.cvtColor(mat, grayMat, opencv_imgproc.COLOR_BGR2GRAY);

            // Выполняем детекцию лиц
            RectVector detectedFaces = new RectVector();
            faceDetector.detectMultiScale(grayMat, detectedFaces, 1.1, 5, 0, new Size(30, 30), new Size());

            if (detectedFaces.size() == 0) {
                return Collections.emptyList(); // Лица не найдены
            }

            // Список UUID совпавших лиц
            List<UUID> matchingFaces = new ArrayList<>();

            // Загружаем сохранённые лица
            List<Face> savedFaces = faceRepository.findAll();

            for (int i = 0; i < detectedFaces.size(); i++) {
                // Извлекаем область лица
                Rect rect = detectedFaces.get(i);
                Mat detectedFaceMat = new Mat(grayMat, rect);

                // Приведение к стандартному формату
                Mat processedFace = processFace(detectedFaceMat);

                for (Face face : savedFaces) {
                    Mat savedFaceMat = matSerializer.deserializeMat(face.getFaceData());
                    Mat processedSavedFace = processFace(savedFaceMat);

                    // Сравниваем лица
                    if (compareFaces(processedFace, processedSavedFace)) {
                        matchingFaces.add(face.getId());
                        break; // Лицо найдено
                    }
                }
            }

            return matchingFaces;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла", e);
        }
    }

    private Mat processFace(Mat face) {
        // Преобразуем в черно-белое изображение
        if (face.channels() > 1) {
            Mat grayMat = new Mat();
            opencv_imgproc.cvtColor(face, grayMat, opencv_imgproc.COLOR_BGR2GRAY);
            face = grayMat;
        }

        // Изменяем размер до 300x300
        Mat resizedFace = new Mat();
        opencv_imgproc.resize(face, resizedFace, new Size(300, 300), 0, 0, opencv_imgproc.INTER_AREA);

        // Приводим к типу CV_32F
        Mat processedFace = new Mat();
        resizedFace.convertTo(processedFace, opencv_core.CV_32F);

        return processedFace;
    }

    private boolean compareFaces(Mat face1, Mat face2) {
        // Проверяем пустоту изображений
        if (face1.empty() || face2.empty()) {
            throw new IllegalArgumentException("Одно из изображений пустое.");
        }

        // Проверяем размеры изображений
        if (face1.size().width() != face2.size().width() || face1.size().height() != face2.size().height()) {
            throw new IllegalArgumentException("Размеры изображений не совпадают.");
        }

        // Приведение к типу CV_32F
        if (face1.type() != opencv_core.CV_32F) {
            Mat temp = new Mat();
            face1.convertTo(temp, opencv_core.CV_32F);
            face1 = temp;
        }

        if (face2.type() != opencv_core.CV_32F) {
            Mat temp = new Mat();
            face2.convertTo(temp, opencv_core.CV_32F);
            face2 = temp;
        }

        // Используем matchTemplate с методом TM_SQDIFF_NORMED
        Mat result = new Mat();
        matchTemplate(face1, face2, result, TM_SQDIFF_NORMED);

        // Проверяем результат
        if (result.empty() || result.size().width() <= 0 || result.size().height() <= 0) {
            throw new RuntimeException("Матрица result некорректна после matchTemplate.");
        }

        // Вывод размеров и содержимого матрицы result
        System.out.println("Result dimensions: rows = " + result.rows() + ", cols = " + result.cols());

        // Инициализация minMaxLoc
        double[] minVal = new double[1];
        double[] maxVal = new double[1];
        Point minLoc = new Point();
        Point maxLoc = new Point();

        // Получаем минимальное и максимальное значения
        minMaxLoc(result, minVal, maxVal, minLoc, maxLoc, null);

        // Логируем значения
        System.out.println("Минимальное значение совпадения: " + minVal[0]);
        System.out.println("Максимальное значение совпадения: " + maxVal[0]);

        // Возвращаем true, если совпадение ниже порога для TM_SQDIFF_NORMED
        return minVal[0] < 0.28; // Порог можно настроить
    }

    public boolean detectFace(MultipartFile file) {
        try {
            // Получаем байты изображения
            byte[] bytes = file.getBytes();
            if (bytes.length == 0) {
                throw new IllegalArgumentException("Файл изображения пуст или поврежден.");
            }

            // Декодируем байты изображения в Mat
            Mat mat = opencv_imgcodecs.imdecode(new Mat(bytes), opencv_imgcodecs.IMREAD_COLOR);
            if (mat.empty()) {
                throw new IllegalArgumentException("Ошибка декодирования изображения: Mat пуст.");
            }

            // Конвертируем изображение в серый цвет
            Mat grayMat = new Mat();
            cvtColor(mat, grayMat, COLOR_BGR2GRAY);

            // Выполняем детекцию лиц
            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(grayMat, faces, 1.1, 3, 0, new Size(30, 30), new Size());

            // Проверяем, найдены ли лица
            return faces.size() > 0;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла", e);
        }
    }


    public FaceService() {
        try {
            // Загружаем библиотеки JavaCV
            Loader.load(opencv_core.class);

            // Путь к классификатору
            InputStream cascadeStream = new ClassPathResource("haarcascade_frontalface_default.xml").getInputStream();

            // Создаём временный файл для классификатора
            File tempFile = File.createTempFile("haarcascade", ".xml");
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = cascadeStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            // Загружаем классификатор через JavaCV
            this.faceDetector = new CascadeClassifier(tempFile.getAbsolutePath());
            if (this.faceDetector.isNull()) {
                throw new RuntimeException("Не удалось загрузить классификатор лиц");
            }

            // Удаляем временный файл при завершении работы
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла классификатора", e);
        }
    }
}
