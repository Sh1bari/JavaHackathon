package com.example.main.services;

import com.example.main.models.entities.Camera;
import com.example.main.models.request.CreateCameraReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CameraService {

    public Camera findByUrl(String url);

    public Page<Camera> getCameras(Specification<Camera> spec, Pageable pageable);

    public Camera createCamera(CreateCameraReqDto dto);

    public Camera deleteCamera(Long id);

    public Camera findById(Long id);
}
