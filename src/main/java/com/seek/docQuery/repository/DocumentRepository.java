package com.seek.docQuery.repository;

import com.seek.docQuery.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document,Long> {
    List<Document> findAllByUserEmail(String email);
    Optional<Document> findByIdAndUserEmail(Long id, String email);
}
