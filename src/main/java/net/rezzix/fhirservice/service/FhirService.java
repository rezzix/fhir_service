package net.rezzix.fhirservice.service;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.MedicationAdministration;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.r5.model.Organization;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Practitioner;
import org.hl7.fhir.r5.model.PractitionerRole;
import org.hl7.fhir.r5.model.Procedure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import net.rezzix.fhirservice.exceptions.ValidationException;

@Service
public class FhirService {

	private final FhirContext fhirContext;
	
	FhirService () {
		this.fhirContext = FhirContext.forR5();
	}

	public String format(Bundle bundle) {
		return fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
	}
	
}
