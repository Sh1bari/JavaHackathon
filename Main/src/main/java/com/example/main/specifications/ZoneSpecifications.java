package com.example.main.specifications;

import com.example.main.models.entities.Zone;
import com.example.main.models.enums.Status;
import org.springframework.data.jpa.domain.Specification;

public class ZoneSpecifications {

    public static Specification<Zone> hasLabel(String label) {
        return (root, query, criteriaBuilder) -> {
            if (label == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("label"), label);
        };
    }

    public static Specification<Zone> hasStatus(Status status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
