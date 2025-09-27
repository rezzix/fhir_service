package net.rezzix.fhirservice.controller;

import ca.uhn.fhir.context.FhirContext;
import net.rezzix.fhirservice.service.KafkaProducerService;

import org.hl7.fhir.r5.model.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dpp")
public class DeclarationController {

    private final FhirContext ctx = FhirContext.forR5();
    private final KafkaProducerService kafkaProducerService;

    public DeclarationController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping(
        value = "/declaration",
        consumes = { "application/fhir+json", MediaType.APPLICATION_JSON_VALUE },
        produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> receiveDeclaration(@RequestBody String bundleJson) {
        try {
            Bundle bundle = (Bundle) ctx.newJsonParser().parseResource(bundleJson);
            String pretty = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);

            System.out.println("=== Received FHIR Bundle ===");
            System.out.println(pretty);
            System.out.println("=== End FHIR Bundle ===");

            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                if (entry.getResource() instanceof Patient patient) {
                    System.out.println("Patient: " + patient.getNameFirstRep().getNameAsSingleString());
                    continue;
                }
                if (entry.getResource() instanceof Organization organization) {
                    System.out.println("Organization: " + organization.getName());
                    continue;
                }
                else if (entry.getResource() instanceof Practitioner practitioner) {
                    System.out.println("Practitioner: " + practitioner.getNameFirstRep().getNameAsSingleString());
                    continue;
                }
                else if (entry.getResource() instanceof PractitionerRole practitionerRole) {
                	/*for (Coding practitionerCoding : practitionerRole.getCode().getCoding())
                		System.out.println("Role: " + practitionerCoding.getDisplay() + " Code:" + practitionerCoding.getCode() + " System:" + practitionerCoding.getSystem() );*/
                	continue;
                }
                else if (entry.getResource() instanceof Condition condition) {
                	for (Coding conditionCoding : condition.getCode().getCoding())
                		System.out.println("Diagnosis: " + conditionCoding.getDisplay() + " Code:" + conditionCoding.getCode() + " System:" + conditionCoding.getSystem() );
                	continue;
                }
                else if (entry.getResource() instanceof Procedure procedure) {
                    for (Coding procedureCoding : procedure.getCode().getCoding())
                		System.out.println("Procedure: " + procedureCoding.getDisplay() + " Code:" + procedureCoding.getCode() + " System:" + procedureCoding.getSystem() );
                    continue;
                }
                else if (entry.getResource() instanceof MedicationAdministration medAdmin) {
                    System.out.println("MedicationAdministration ID: " + medAdmin.getIdElement().getIdPart());
                    System.out.println("Status: " + medAdmin.getStatus());

                    // Occurrence time
                    if (medAdmin.hasOccurenceDateTimeType()) {
                        System.out.println("Occurred: " + medAdmin.getOccurenceDateTimeType().getValueAsString());
                    }

                    // Medication reference or concept
                    if (medAdmin.hasMedication()) {
                        if (medAdmin.getMedication().hasReference()) {
                            System.out.println("Medication reference: " + medAdmin.getMedication().getReference().getReference());
                        }
                        if (medAdmin.getMedication().hasConcept()) {
                            CodeableConcept cc = medAdmin.getMedication().getConcept();
                            cc.getCoding().forEach(c ->
                                System.out.println("Medication code: " + c.getSystem() + " | " + c.getCode() + " | " + c.getDisplay())
                            );
                            if (cc.hasText()) {
                                System.out.println("Medication text: " + cc.getText());
                            }
                        }
                    }

                    // Dosage
                    if (medAdmin.hasDosage() && medAdmin.getDosage().hasText()) {
                        System.out.println("Dosage: " + medAdmin.getDosage().getText());
                    }
                    
                    
                    continue;
                } else {
                	System.out.println("received a ressource of type " + entry.getResource().getClass());
                }
                
            }
            
            String prettyJson = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
            
            kafkaProducerService.sendMessage("declarationssih", prettyJson);

            return ResponseEntity.ok("Declaration received successfully");
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid FHIR Bundle: " + e.getMessage());
        }
    }
    
   
}
