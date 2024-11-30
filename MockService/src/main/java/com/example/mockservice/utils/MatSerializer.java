package com.example.mockservice.utils;

import lombok.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class MatSerializer {

    /**
     * Сериализация Mat в байтовый массив
     */
    public byte[] serializeMat(Mat mat) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        // Сохраняем базовые параметры Mat
        oos.writeInt(mat.rows());
        oos.writeInt(mat.cols());
        oos.writeInt(mat.type());

        // Сохраняем данные
        byte[] data = new byte[(int) (mat.total() * mat.elemSize())];
        mat.data().get(data); // Используем метод JavaCV для получения данных
        oos.writeObject(data);

        oos.flush();
        return bos.toByteArray();
    }

    /**
     * Десериализация байтового массива обратно в Mat
     */
    public Mat deserializeMat(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);

        // Читаем параметры Mat
        int rows = ois.readInt();
        int cols = ois.readInt();
        int type = ois.readInt();

        // Читаем данные
        byte[] matData = (byte[]) ois.readObject();
        Mat mat = new Mat(rows, cols, type);
        mat.data().put(matData); // Используем метод JavaCV для записи данных

        return mat;
    }
}
