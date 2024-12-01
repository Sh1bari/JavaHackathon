package com.example.photoprocessing.config;

import com.example.photoprocessing.models.dto.ResponseFaceDto;
import com.example.photoprocessing.utils.MultipartFileResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class MockServiceClient {

    private final RestTemplate restTemplate;

    public MockServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ResponseFaceDto> matchFaces(MultipartFile file) throws IOException {
        String url = "http://mock-service:8082/check_face";

        // Формируем заголовки запроса
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Формируем тело запроса
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartFileResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Выполняем POST-запрос
        ResponseEntity<ResponseFaceDto[]> response = restTemplate.postForEntity(url, requestEntity, ResponseFaceDto[].class);

        // Возвращаем результат в виде списка
        if (response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            throw new RuntimeException("No response from the mock service");
        }
    }
}
