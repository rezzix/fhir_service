package net.rezzix.fhirservice.controller;

import net.rezzix.fhirservice.model.Declaration;
import net.rezzix.fhirservice.model.DeclarationDocument;
import net.rezzix.fhirservice.model.DeclarationDocumentDto;
import net.rezzix.fhirservice.repository.DeclarationDocumentRepository;
import net.rezzix.fhirservice.repository.DeclarationRepository;
import net.rezzix.fhirservice.utils.Const;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DeclarationDocumentRepository documentRepository;
    private final DeclarationRepository declarationRepository;

    // Define the upload directory
    @Value("${app.upload.directory:uploads}")
    private String uploadDir;

    public DocumentController(DeclarationRepository declarationRepository, DeclarationDocumentRepository documentRepository) {
        this.declarationRepository = declarationRepository;
    	this.documentRepository = documentRepository;
    }

    @PostConstruct
    public void init() {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("Could not create upload directory", e);
            }
        }
    }

    /**
     * Upload a document related to a declaration
     * @param declarationId The ID of the declaration
     * @param file The document file to upload
     * @param description Optional description of the document
     * @return DeclarationDocument containing information about the uploaded document, or error response
     */
    @PostMapping(value = "/declarations/{declarationId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(
            @PathVariable("declarationId") Long declarationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        
        // Validate the file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("File is empty. Please select a file to upload.");
        }

        // Validate that the declaration exists
        Optional<Declaration> declaration = declarationRepository.findById(declarationId);
        if (!declaration.isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Declaration with ID " + declarationId + " does not exist.");
        }
        
        if (declaration.get().getStatus().equals(Const.STATUS_FAILED)) {
            return ResponseEntity.badRequest()
                    .body("Cannot upload documents to a failed declaration.");
        }

        try {
            // Get the original filename
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.badRequest()
                        .body("File name is missing or invalid.");
            }

            // Generate a unique filename to prevent conflicts
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            
            // Define the file path
            Path filePath = Paths.get(uploadDir).resolve(uniqueFilename);
            
            // Save the file
            Files.copy(file.getInputStream(), filePath);
            
            // Create a DeclarationDocument entity
            DeclarationDocument document = new DeclarationDocument();
            document.setDeclarationId(declarationId);
            document.setFilename(originalFilename);
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setFilePath(filePath.toString());
            document.setUploadDate(LocalDateTime.now());
            document.setDescription(description);
            
            // Save the document metadata to the database
            DeclarationDocument savedDocument = documentRepository.save(document);
            
            return ResponseEntity.ok(savedDocument);
            
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body("Failed to save file: " + e.getMessage());
        }
    }

    /**
     * Get all documents related to a specific declaration
     * @param declarationId The ID of the declaration
     * @return List of DeclarationDocumentDto 
     */
    @GetMapping("/declarations/{declarationId}/documents")
    public ResponseEntity<?> getDocumentsByDeclaration(@PathVariable("declarationId") Long declarationId) {
        // Check if declaration exists first
        Optional<Declaration> declaration = declarationRepository.findById(declarationId);
        if (!declaration.isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Declaration with ID " + declarationId + " does not exist.");
        }
        
        List<DeclarationDocumentDto> documents = documentRepository.findByDeclarationId(declarationId);
        return ResponseEntity.ok(documents);
    }

    /**
     * Download a specific document by its ID
     * @param documentId The ID of the document to download
     * @return The file as a resource
     */
    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("documentId") Long documentId) {
        Optional<DeclarationDocument> documentOpt = documentRepository.findById(documentId);
        
        if (!documentOpt.isPresent()) {
            // For download endpoints, we typically return 404 without body to avoid confusion
            return ResponseEntity.notFound().build();
        }
        
        DeclarationDocument document = documentOpt.get();
        
        try {
            Path filePath = Paths.get(document.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(document.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + document.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(500).build();
        }
    }
}