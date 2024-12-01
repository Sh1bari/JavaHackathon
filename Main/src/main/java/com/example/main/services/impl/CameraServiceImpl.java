package com.example.main.services.impl;

import com.example.main.exceptions.GeneralException;
import com.example.main.models.entities.Camera;
import com.example.main.models.enums.Status;
import com.example.main.models.request.CreateCameraReqDto;
import com.example.main.repositories.CameraRepository;
import com.example.main.services.CameraService;
import com.example.main.services.ZoneService;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CameraServiceImpl implements CameraService {

    private final CameraRepository cameraRepo;
    private final ZoneService zoneService;

    @Override
    @Transactional(readOnly = true)
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
}
