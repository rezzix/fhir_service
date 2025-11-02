package net.rezzix.fhirservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Date;

import org.hl7.fhir.r5.model.CapabilityStatement;
import org.hl7.fhir.r5.model.Enumerations.FHIRVersion;
import org.hl7.fhir.r5.model.Reference;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.CapabilityStatement.CapabilityStatementRestComponent;
import org.hl7.fhir.r5.model.CapabilityStatement.CapabilityStatementRestResourceComponent;
import org.hl7.fhir.r5.model.CapabilityStatement.ResourceInteractionComponent;

import ca.uhn.fhir.context.FhirContext;

@RestController
public class MetadataController {

    private final FhirContext fhirContext = FhirContext.forR5();

    @GetMapping(value = "/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getCapabilityStatement() {
        CapabilityStatement cs = new CapabilityStatement();

        cs.setUrl("https://msps.ma/dpp/metadata");
        cs.setName("FhirDeclarationService");
        cs.setTitle("DPP FHIR Declaration Submission Service");
        cs.setStatus(org.hl7.fhir.r5.model.Enumerations.PublicationStatus.ACTIVE);
        cs.setDate(new Date());
        cs.setPublisher("MSPS");
        cs.setDescription("FHIR server pour la declaration des dossier patient.");
        //cs.setKind(CapabilityStatement.CapabilityStatementKind.INSTANCE);
        cs.setFhirVersion(FHIRVersion._5_0_0);
        cs.addFormat("json");

        // === SOFTWARE INFO ===
        cs.getSoftware()
            .setName("DPP FHIR Declaration Service")
            .setVersion("1.0.0")
            .setReleaseDateElement(new org.hl7.fhir.r5.model.DateTimeType(LocalDate.now().toString()));

        // === SECURITY INFO ===
        CapabilityStatementRestComponent rest = new CapabilityStatementRestComponent();
        rest.setMode(CapabilityStatement.RestfulCapabilityMode.SERVER);
        /*
        rest.getSecurity()
            .setDescription("OAuth2 using SMART-on-FHIR scopes")
            .addService(new CodeableConcept().addCoding(
                    new Coding()
                        .setSystem("http://terminology.hl7.org/CodeSystem/restful-security-service")
                        .setCode("OAuth")
                        .setDisplay("OAuth2")));*/

        // === RESOURCE: Bundle ===
        CapabilityStatementRestResourceComponent bundleResource = new CapabilityStatementRestResourceComponent();
        bundleResource.setType("Bundle");
        bundleResource.addInteraction(new ResourceInteractionComponent()
                .setCode(CapabilityStatement.TypeRestfulInteraction.CREATE));
        bundleResource.addInteraction(new ResourceInteractionComponent()
                .setCode(CapabilityStatement.TypeRestfulInteraction.READ));
        bundleResource.setVersioning(CapabilityStatement.ResourceVersionPolicy.NOVERSION);
        bundleResource.setConditionalCreate(true);
        bundleResource.setConditionalRead(CapabilityStatement.ConditionalReadStatus.FULLSUPPORT);

        // === RESOURCE: OperationOutcome ===
        CapabilityStatementRestResourceComponent opOutcome = new CapabilityStatementRestResourceComponent();
        opOutcome.setType("OperationOutcome");
        opOutcome.addInteraction(new ResourceInteractionComponent()
                .setCode(CapabilityStatement.TypeRestfulInteraction.READ));

        // Add resources to REST definition
        rest.addResource(bundleResource);
        rest.addResource(opOutcome);

        cs.addRest(rest);

        // === DOCUMENTATION ===
        //cs.addImplementationGuide("https://github.com/rezzix/fhir_service");

        // === CONTACT INFO ===
        cs.addContact()
            .addTelecom()
            .setSystem(org.hl7.fhir.r5.model.ContactPoint.ContactPointSystem.EMAIL)
            .setValue("contact@msps.ma");

        // === Serialize to JSON ===
        String json = fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(cs);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }
}
