package net.rezzix.fhirclient;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.CodeableReference;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.DateTimeType;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.Immunization;
import org.hl7.fhir.r5.model.Medication;
import org.hl7.fhir.r5.model.MedicationAdministration;
import org.hl7.fhir.r5.model.Organization;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Period;
import org.hl7.fhir.r5.model.Practitioner;
import org.hl7.fhir.r5.model.PractitionerRole;
import org.hl7.fhir.r5.model.Procedure;
import org.hl7.fhir.r5.model.Reference;
import org.junit.jupiter.api.Test;

import ca.uhn.fhir.context.FhirContext;

class NominalDeclarations {

	@Test
	void testNominalDeclaration1() throws IOException, InterruptedException {
        FhirContext ctx = FhirContext.forR5();

        // Patient
        Patient patient = new Patient();
        patient.setId("p12314978");
        patient.addIdentifier().setSystem("http://msps.ma/mrn").setValue("MRN-INS");
        patient.addName().setFamily("Hallal").addGiven("Maroua");
        patient.setGender(Enumerations.AdministrativeGender.FEMALE);
        patient.setBirthDate(java.sql.Date.valueOf(LocalDate.of(1990, 5, 14)));

        // Practitioner
        Practitioner practitioner = new Practitioner();
        practitioner.setId("inpe/4587621");
        practitioner.addName().setFamily("Rahmani").addGiven("Ikram");

        // Organization (Facility)
        Organization facility = new Organization();
        facility.setId("inpe/9872855");
        facility.setName("Hopital Moulay Youssef");

        // PractitionerRole
        PractitionerRole role = new PractitionerRole();
        role.setId("affectation/xyz");
        role.setPractitioner(new Reference(practitioner.getId()));
        role.setOrganization(new Reference(facility.getId()));
        role.addCode().addCoding()
            .setSystem("http://snomed.info/sct")
            .setCode("309343006")
            .setDisplay("Orthopedic surgeon");
        
     // Encounter: links patient, practitioner, and facility
        Encounter encounter = new Encounter();
        encounter.setId("Encounter/enc1");

        // Status of the encounter
        encounter.setStatus(Enumerations.EncounterStatus.COMPLETED);

        // Class (inpatient, outpatient, emergency, etc.)
        ArrayList<CodeableConcept> encounterClasses=new ArrayList<CodeableConcept>();
        encounterClasses.add( new CodeableConcept().addCoding(
        	    new Coding()
        	        .setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode")
        	        .setCode("AMB")
        	        .setDisplay("ambulatory")
        	) );
        encounterClasses.add( new CodeableConcept().addCoding(
        	    new Coding()
        	        .setSystem("http://snomed.info/sct")
        	        .setCode("396112002")
        	        .setDisplay("ambulatory care encounter")
        	) );
        encounter.setClass_(encounterClasses);

        // Subject (the patient)
        encounter.setSubject(new Reference(patient.getId()));

        // Participant (the practitioner)
        Encounter.EncounterParticipantComponent participant = new Encounter.EncounterParticipantComponent();
        participant.setActor(new Reference(practitioner.getId()));
        encounter.addParticipant(participant);

        // Service provider (the facility/organization)
        encounter.setServiceProvider(new Reference(facility.getId()));

        // Period (when the encounter happened)
        Period period = new Period();
        period.setStartElement(new DateTimeType("2025-09-20T09:00:00Z"));
        period.setEndElement(new DateTimeType("2025-09-20T11:00:00Z"));
        encounter.setActualPeriod(period);


        // Condition (Fractured bone) - ICD-11 example
        Condition condition = new Condition();
        condition.setId("Condition/c1");
        condition.setSubject(new Reference(patient.getId()));
        //condition.set
        condition.getCode().addCoding()
            .setSystem("http://id.who.int/icd/release/11/mms")
            .setCode("NC72.30") // Example; choose the right code for production
            .setDisplay("Fracture of femur");
        condition.setClinicalStatus(new CodeableConcept().addCoding(
            new Coding("http://terminology.hl7.org/CodeSystem/condition-clinical", "active", "Active")
        ));

        // Procedure: Radiology imaging (LOINC example)
        Procedure radiology = new Procedure();
        radiology.setId("Procedure/proc1");
        radiology.setSubject(new Reference(patient.getId()));
        radiology.getCode().addCoding()
            .setSystem("http://loinc.org")
            .setCode("36626-4")
            .setDisplay("Radiology imaging study");
        radiology.getCode().addCoding("NGAP", "C23", "IRM");
        
        CodeableConcept category = new CodeableConcept();
        category.addCoding()
            .setSystem("http://snomed.info/sct")
            .setCode("363679005")
            .setDisplay("Imaging");
        radiology.addCategory(category);
        
        CodeableConcept bodySite = new CodeableConcept();
        bodySite.addCoding()
            .setSystem("http://snomED.info/sct")
            .setCode("71341007")
            .setDisplay("Femur structure");
        radiology.addBodySite(bodySite);
        
        Procedure.ProcedurePerformerComponent performer = new Procedure.ProcedurePerformerComponent();
        Reference practitionerRef = new Reference("Practitioner/p987");
        practitionerRef.setDisplay("Dr. Alae Bahiri, Radiologist");
        performer.setActor(practitionerRef);
        radiology.addPerformer(performer);
        
        radiology.setOccurrence(new DateTimeType("2024-01-15"));
        //radiology.setStatus(Procedure.ProcedureStatus.COMPLETED);

        // Procedure: Surgery (SNOMED CT)
        Procedure surgery = new Procedure();
        surgery.setId("Procedure/proc2");
        surgery.setSubject(new Reference(patient.getId()));
        surgery.getCode().addCoding()
            .setSystem("http://snomed.info/sct")
            .setCode("387713003")
            .setDisplay("Surgical procedure");
        surgery.getCode().addCoding("NGAP", "K35", "Réduction de fracture");
        //surgery.setStatus(Procedure.ProcedureStatus.COMPLETED);

        
     // Medication: Anti-inflammatory (NSAID) with commercial name + ATC coding
        Medication medication = new Medication();
        medication.setId("Medication/med1");

        // SNOMED CT coding
        medication.getCode().addCoding()
            .setSystem("http://snomed.info/sct")
            .setCode("373270004")
            .setDisplay("Non-steroidal anti-inflammatory drug (product)");

        // ATC coding
        medication.getCode().addCoding()
            .setSystem("http://www.whocc.no/atc")
            .setCode("M01AE01")
            .setDisplay("Ibuprofen");

        // Commercial drug name
        medication.getCode().setText("Ibuprofen 400mg tablet (Brufen®)");

        // MedicationAdministration (instead of MedicationRequest)
        MedicationAdministration medAdmin = new MedicationAdministration();
        medAdmin.setId("MedicationAdministration/ma1");
        medAdmin.setSubject(new Reference(patient.getId()));
        medAdmin.setStatus(MedicationAdministration.MedicationAdministrationStatusCodes.COMPLETED);
        medAdmin.setMedication(new CodeableReference(new Reference(medication.getId())));
     // Set effective date/time
        medAdmin.setOccurence(new DateTimeType("2025-09-22T10:00:00Z"));

        // Add dosage with text
        MedicationAdministration.MedicationAdministrationDosageComponent dosage = new MedicationAdministration.MedicationAdministrationDosageComponent();
        dosage.setText("400mg orally, once");
        medAdmin.setDosage(dosage);
        
        Immunization tetanus = new Immunization();
        tetanus.setId("Immunization/imm1");
        tetanus.setStatus(Immunization.ImmunizationStatusCodes.COMPLETED);
        tetanus.setPatient(new Reference(patient.getId()));
        tetanus.setOccurrence(new DateTimeType("2023-05-10")); // example date
        tetanus.getVaccineCode().addCoding()
            .setSystem("http://snomed.info/sct")
            .setCode("1119349007") // SNOMED CT: Tetanus vaccine
            .setDisplay("Tetanus vaccine");
        tetanus.getVaccineCode().setText("Tetanus vaccine");

        // Immunization: Influenza vaccine
        Immunization influenza = new Immunization();
        influenza.setId("Immunization/imm2");
        influenza.setStatus(Immunization.ImmunizationStatusCodes.COMPLETED);
        influenza.setPatient(new Reference(patient.getId()));
        influenza.setOccurrence(new DateTimeType("2024-11-15")); // example date
        influenza.getVaccineCode().addCoding()
            .setSystem("http://snomed.info/sct")
            .setCode("871751000000103") // SNOMED CT: Influenza vaccine
            .setDisplay("Influenza vaccine");
        influenza.getVaccineCode().setText("Influenza vaccine");

        
        // Bundle (COLLECTION)
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.COLLECTION);
        bundle.addEntry().setResource(practitioner);
        bundle.addEntry().setResource(facility);
        bundle.addEntry().setResource(role);
        bundle.addEntry().setResource(patient);
        bundle.addEntry().setResource(encounter);
        bundle.addEntry().setResource(condition);
        bundle.addEntry().setResource(radiology);
        bundle.addEntry().setResource(surgery);
        bundle.addEntry().setResource(medication);
        bundle.addEntry().setResource(medAdmin);
        bundle.addEntry().setResource(tetanus);
        bundle.addEntry().setResource(influenza);

        // Serialize to JSON
        String json = ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
        System.out.println("=== Outgoing FHIR Bundle ===");
        System.out.println(json);
        System.out.println("=== End Outgoing FHIR Bundle ===");

        // POST to server
        HttpClient http = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/"))
            .header("Content-Type", "application/fhir+json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Server HTTP status: " + response.statusCode());
        System.out.println("Server response: " + response.body());
	}

}
