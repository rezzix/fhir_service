package net.rezzix.fhirservice.model;

import java.time.LocalDateTime;

public class DeclarationDocumentDto {

    private Long id;
    private Long declarationId;
    private String filename;
    private String contentType;
    private String filePath;
    private Long fileSize;
    private LocalDateTime uploadDate;
    private String description;

    // Constructors
    public DeclarationDocumentDto() {
    }

    public DeclarationDocumentDto(Long id, Long declarationId, String filename, String contentType, 
                                  String filePath, Long fileSize, LocalDateTime uploadDate, String description) {
        this.id = id;
        this.declarationId = declarationId;
        this.filename = filename;
        this.contentType = contentType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.description = description;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeclarationId() {
        return declarationId;
    }

    public void setDeclarationId(Long declarationId) {
        this.declarationId = declarationId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}