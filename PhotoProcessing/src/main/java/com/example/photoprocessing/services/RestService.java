package com.example.photoprocessing.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestService {

    private final RestTemplate restTemplate;
    public List<UUID> checkFace(String bucket, String name) {
        String url = String.format("http://mock-service:8082/api/mock/check_face?bucket=%s&name=%s", bucket, name);
        UUID[] response = restTemplate.getForObject(url, UUID[].class);
        return Arrays.asList(response);
    }
}
