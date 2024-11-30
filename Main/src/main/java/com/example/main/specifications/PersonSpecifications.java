package com.example.main.specifications;

import com.example.main.models.entities.Person;
import com.example.main.models.entities.Zone;
import com.example.main.models.enums.Status;
import org.springframework.data.jpa.domain.Specification;

public class PersonSpecifications {
    public static Specification<Person> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("name"), name);
        };
    }

    public static Specification<Person> hasMiddleName(String middleName){
        return (root, query, criteriaBuilder) -> {
            if (middleName == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("middleName"), middleName);
        };
    }

    public static Specification<Person> hasSurname(String surname){
        return (root, query, criteriaBuilder) -> {
            if (surname == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("surname"), surname);
        };
    }

    public static Specification<Person> hasStatus(Status status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
