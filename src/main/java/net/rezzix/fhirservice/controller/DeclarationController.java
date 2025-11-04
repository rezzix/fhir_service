package net.rezzix.fhirservice.controller;

import net.rezzix.fhirservice.model.Declaration;
import net.rezzix.fhirservice.repository.DeclarationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class DeclarationController {

    private final DeclarationRepository declarationRepository;

    public DeclarationController(DeclarationRepository declarationRepository) {
        this.declarationRepository = declarationRepository;
    }

    /**
     * Endpoint to get a declaration by its ID
     * @param id The ID of the declaration to retrieve
     * @return ResponseEntity containing the declaration data in JSON format
     */
    @GetMapping("/declarations/{id}")
    public ResponseEntity<Declaration> getDeclarationById(@PathVariable("id") Long id) {
        Optional<Declaration> declaration = declarationRepository.findById(id);
        
        if (declaration.isPresent()) {
            return ResponseEntity.ok(declaration.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}