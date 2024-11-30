package com.example.mockservice.controllers;

import com.example.mockservice.services.FaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("")
@Tag(name = "Face API", description = "")
public class FaceController {
    private final FaceService faceService;

    @Operation(summary = "Добавить лицо")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(value = "/add_face", consumes = {"multipart/form-data"})
    public ResponseEntity<?> addFace(
            @RequestParam("uuid") UUID uuid,
            @RequestPart("file") MultipartFile file) {
        try {
            faceService.addFace(uuid, file);
            return ResponseEntity.ok().body("Face added successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Проверка лица")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(value = "/check_face", consumes = {"multipart/form-data"})
    public ResponseEntity<List<UUID>> matchFaces(@RequestPart("file") MultipartFile file) {
        try {
            List<UUID> matchingFaces = faceService.findMatchingFaces(file);
            return ResponseEntity.ok(matchingFaces);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.emptyList());
        }
    }

    @Operation(summary = "Удаление лица из базы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @DeleteMapping(value = "/remove_face", consumes = {"multipart/form-data"})
    public ResponseEntity<String> removeFaces(@RequestParam UUID id) {
        faceService.deleteFace(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Removed successfully");
    }

    @Operation(summary = "Вернуть все лица из базы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @DeleteMapping(value = "/get_face", consumes = {"multipart/form-data"})
    public ResponseEntity<List<UUID>> getAllFaces() {
        List<UUID> uuids = faceService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(uuids);
    }
}
