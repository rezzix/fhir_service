package net.rezzix.fhirservice.controller;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import net.rezzix.fhirservice.exceptions.ValidationException;
import net.rezzix.fhirservice.service.DeclarationService;
import net.rezzix.fhirservice.service.KafkaProducerService;
import net.rezzix.fhirservice.service.ValidationService;

import org.hl7.fhir.r5.model.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dpp")
public class DeclarationController {
	/*
    private final DeclarationService declarationService;
    private final FhirContext fhirContext;
    
    public DeclarationController(DeclarationService declarationService, FhirContext fhirContext) {
        this.declarationService = declarationService;
        this.fhirContext = fhirContext;
    }
    
    @PostMapping(
        value = "/declaration",
        consumes = { "application/fhir+json", MediaType.APPLICATION_JSON_VALUE },
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> receiveDeclaration(@RequestBody String bundleJson) {
        try {
            Bundle bundle = (Bundle) fhirContext.newJsonParser().parseResource(bundleJson);
            String pretty = fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);

            System.out.println("=== Received FHIR Bundle ===");
            System.out.println(pretty);
            System.out.println("=== End FHIR Bundle ===");
            
            declarationService.declare(bundle);
           
            return ResponseEntity.ok("Declaration received successfully");
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid FHIR Bundle: " + e.getMessage());
        }
    }
    */
   
}
