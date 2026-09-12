package com.seek.docQuery.controller;

import com.seek.docQuery.dto.QueryRequest;
import com.seek.docQuery.dto.QueryResponse;
import com.seek.docQuery.entity.Document;
import com.seek.docQuery.repository.DocumentRepository;
import com.seek.docQuery.service.DocumentQueryService;
import com.seek.docQuery.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentUploadService documentUploadService;
    private final DocumentQueryService documentQueryService;
    private final DocumentRepository documentRepository;

    @PostMapping("/upload")
    public Document uploadDocument(@RequestParam("file") MultipartFile file, Principal principal) {
        return documentUploadService.uploadDocument(file, principal.getName());
    }

    @GetMapping
    public List<Document> getDocuments(Principal principal) {
        return documentRepository.findAllByUserEmail(principal.getName());
    }

    @PostMapping("/{id}/query")
    public QueryResponse queryDocument(@PathVariable Long id, @RequestBody QueryRequest request, Principal principal) {
        if (request.query() == null || request.query().isBlank()) {
            throw new IllegalArgumentException("query field is required");
        }
        String answer = documentQueryService.queryDocument(id, request.query(), principal.getName());
        return new QueryResponse(answer);
    }

    @DeleteMapping("/{id}")
    public void deleteDocument(@PathVariable Long id, Principal principal) {
        Document document = documentRepository.findByIdAndUserEmail(id, principal.getName())
                .orElseThrow(() -> new RuntimeException("Document not found!"));
        documentRepository.delete(document);
    }
}
