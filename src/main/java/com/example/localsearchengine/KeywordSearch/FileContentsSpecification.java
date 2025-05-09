package com.example.localsearchengine.KeywordSearch;

import com.example.localsearchengine.DTOs.FileSearchCriteria;
import com.example.localsearchengine.Entites.FileContents;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FileContentsSpecification {

    public static Specification<FileContents> withCriteria(FileSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getPaths() != null && !criteria.getPaths().isEmpty()) {
                predicates.add(root.get("file").get("path").in(criteria.getPaths()));
            }

            if (criteria.getNames() != null && !criteria.getNames().isEmpty()) {
                predicates.add(root.get("file").get("filename").in(criteria.getNames()));
            }

            if (criteria.getKeywords() != null && !criteria.getKeywords().isEmpty()) {
                Expression<String> contentExpression = root.get("contents");
                String tsQuery = String.join(" & ", criteria.getKeywords());
                predicates.add(cb.like(cb.lower(contentExpression), "%" + tsQuery.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

