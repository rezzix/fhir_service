package net.rezzix.fhirservice.repository;

import org.springframework.data.domain.Pageable;
import net.rezzix.fhirservice.model.Declaration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeclarationRepository extends JpaRepository<Declaration, Long> {

    List<Declaration> findByStatus(String status);
    List<Declaration> findByStatus(String status, Pageable pageable);
}
