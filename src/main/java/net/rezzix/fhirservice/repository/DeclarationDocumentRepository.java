package net.rezzix.fhirservice.repository;

import net.rezzix.fhirservice.model.DeclarationDocument;
import net.rezzix.fhirservice.model.DeclarationDocumentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeclarationDocumentRepository extends JpaRepository<DeclarationDocument, Long> {
    @Query("SELECT new net.rezzix.fhirservice.model.DeclarationDocumentDto(" +
           "d.id, d.declarationId, d.filename, d.contentType, d.filePath, d.fileSize, d.uploadDate, d.description) " +
           "FROM DeclarationDocument d WHERE d.declarationId = :declarationId")
    List<DeclarationDocumentDto> findByDeclarationId(@Param("declarationId") Long declarationId);
}