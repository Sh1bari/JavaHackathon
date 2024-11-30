package com.example.main.specifications;

import com.example.main.models.entities.Camera;
import com.example.main.models.enums.Status;
import org.springframework.data.jpa.domain.Specification;

public class CameraSpecifications {

    public static Specification<Camera> hasLabel(String label) {
        return (root, query, criteriaBuilder) -> {
            if (label == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("label"), label);
        };
    }

    public static Specification<Camera> hasZone(Long zoneId) {
        return (root, query, criteriaBuilder) -> {
            if (zoneId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("zone").get("id"), zoneId);
        };
    }

    public static Specification<Camera> hasStatus(Status status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
