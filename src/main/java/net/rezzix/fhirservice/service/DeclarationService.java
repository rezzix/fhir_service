package net.rezzix.fhirservice.service;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.MedicationAdministration;
import org.hl7.fhir.r5.model.Organization;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Practitioner;
import org.hl7.fhir.r5.model.PractitionerRole;
import org.hl7.fhir.r5.model.Procedure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;

@Service
public class DeclarationService {
	
	private final KafkaProducerService kafkaProducerService;
    private final ValidationService validationService;
    private final FhirContext fhirContext;
    
    @Value("${app.kafka.topic.declarationssih}")
    private String topicName;

    @Value("${app.validation.bundle-enabled}")
    private boolean isBundleValidationEnabled;

    @Value("${app.validation.coding-enabled}")
    private boolean isCodingValidationEnabled;
    
    public DeclarationService(KafkaProducerService kafkaProducerService, ValidationService validationService, FhirContext fhirContext) {
        this.kafkaProducerService = kafkaProducerService;
        this.validationService = validationService;
        this.fhirContext = fhirContext;
    }
    
    public void declare(Bundle bundle) {
    	
    	validationService.validateBundleStructure(bundle);
    	
    	validationService.validateCodingSystems(bundle);
    	
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
             
             String prettyJson = fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
             
             kafkaProducerService.sendMessage(topicName, prettyJson);
             
         }
    }
    
}
