package com.example.main.controllers;

import com.example.main.models.dto.CameraDto;
import com.example.main.models.entities.Camera;
import com.example.main.models.enums.Status;
import com.example.main.models.request.CreateCameraReqDto;
import com.example.main.services.CameraService;
import com.example.main.specifications.CameraSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class CameraController {

    private final CameraService cameraService;

    @GetMapping("/cameras")
    public ResponseEntity<Page<CameraDto>> getCameras(
            @RequestParam(required = false) String label,
            @RequestParam(required = false) Long zoneId,
            @RequestParam(required = false) Status status,
            @PageableDefault Pageable pageable) {
        Specification<Camera> spec = Specification.where(
                CameraSpecifications.hasLabel(label)
                .and(CameraSpecifications.hasZone(zoneId))
                .and(CameraSpecifications.hasStatus(status))
        );
        Page<Camera> cameras = cameraService.getCameras(spec, pageable);
        Page<CameraDto> cameraDtos = cameras.map(CameraDto::mapFromEntity);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cameraDtos);
    }

    @GetMapping("/cameras/{id}")
    public ResponseEntity<CameraDto> getCameraById(@PathVariable Long id){
        CameraDto res = CameraDto.mapFromEntity(cameraService.findById(id));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @PostMapping("/cameras")
    public ResponseEntity<CameraDto> createCamera(@RequestBody CreateCameraReqDto dto) {
        CameraDto res = CameraDto.mapFromEntity(cameraService.createCamera(dto));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }

    @DeleteMapping("/cameras/{id}")
    public ResponseEntity<CameraDto> deleteCameraById(@PathVariable Long id){
        CameraDto res = CameraDto.mapFromEntity(cameraService.deleteCamera(id));
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(res);
    }
}
