package com.example.main.services;

import com.example.main.exceptions.GeneralException;
import com.example.main.models.entities.Camera;
import com.example.main.models.entities.Zone;
import com.example.main.repositories.CameraRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CameraService {
    private final CameraRepository cameraRepo;

    @Transactional(readOnly = true)
    public Camera findById(Long id) {
        return cameraRepo.findById(id)
                .orElseThrow(()->new GeneralException(404, "Camera not found"));
    }
    @Transactional
    public Camera save(Camera camera) {
        return cameraRepo.save(camera);
    }
}
