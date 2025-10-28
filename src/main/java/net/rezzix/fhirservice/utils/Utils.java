package net.rezzix.fhirservice.utils;

import java.util.Date;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.MedicationAdministration;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r5.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.r5.model.Organization;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Practitioner;
import org.hl7.fhir.r5.model.PractitionerRole;
import org.hl7.fhir.r5.model.Procedure;

public class Utils {
	static Utils utils;
	public static Utils getInstance() {
		if (utils == null)
			utils = new Utils();
		
		return utils;
	}
	
	public String stringBundle(Bundle bundle) {
		StringBuffer ret = new StringBuffer();
		
		for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
			if (entry.getResource() instanceof Patient patient) {
				ret.append("Patient: " + patient.getNameFirstRep().getNameAsSingleString());
				continue;
			}
			if (entry.getResource() instanceof Organization organization) {
				ret.append("Organization: " + organization.getName());
				continue;
			} else if (entry.getResource() instanceof Practitioner practitioner) {
				ret.append("Practitioner: " + practitioner.getNameFirstRep().getNameAsSingleString());
				continue;
			} else if (entry.getResource() instanceof PractitionerRole practitionerRole) {
				/*
				 * for (Coding practitionerCoding : practitionerRole.getCode().getCoding())
				 * System.out.println("Role: " + practitionerCoding.getDisplay() + " Code:" +
				 * practitionerCoding.getCode() + " System:" + practitionerCoding.getSystem() );
				 */
				continue;
			} else if (entry.getResource() instanceof Condition condition) {
				for (Coding conditionCoding : condition.getCode().getCoding())
					ret.append("Diagnosis: " + conditionCoding.getDisplay() + " Code:"
							+ conditionCoding.getCode() + " System:" + conditionCoding.getSystem());
				continue;
			} else if (entry.getResource() instanceof Procedure procedure) {
				for (Coding procedureCoding : procedure.getCode().getCoding())
					ret.append("Procedure: " + procedureCoding.getDisplay() + " Code:"
							+ procedureCoding.getCode() + " System:" + procedureCoding.getSystem());
				continue;
			} else if (entry.getResource() instanceof MedicationAdministration medAdmin) {
				ret.append("MedicationAdministration ID: " + medAdmin.getIdElement().getIdPart());
				ret.append("Status: " + medAdmin.getStatus());

				// Occurrence time
				if (medAdmin.hasOccurenceDateTimeType()) {
					ret.append("Occurred: " + medAdmin.getOccurenceDateTimeType().getValueAsString());
				}

				// Medication reference or concept
				if (medAdmin.hasMedication()) {
					if (medAdmin.getMedication().hasReference()) {
						ret.append(
								"Medication reference: " + medAdmin.getMedication().getReference().getReference());
					}
					if (medAdmin.getMedication().hasConcept()) {
						CodeableConcept cc = medAdmin.getMedication().getConcept();
						cc.getCoding().forEach(c -> System.out.println(
								"Medication code: " + c.getSystem() + " | " + c.getCode() + " | " + c.getDisplay()));
						if (cc.hasText()) {
							ret.append("Medication text: " + cc.getText());
						}
					}
				}

				// Dosage
				if (medAdmin.hasDosage() && medAdmin.getDosage().hasText()) {
					ret.append("Dosage: " + medAdmin.getDosage().getText());
				}

				continue;
			} else {
				ret.append("received a ressource of type " + entry.getResource().getClass());
			}

		}
		
		return ret.toString();

	}
	

	public Bundle.BundleEntryComponent createResourceResponse(
            OperationOutcome outcome) {
        
        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        Bundle.BundleEntryResponseComponent response = new Bundle.BundleEntryResponseComponent();
        
        //response.setStatus(status);
        
        if (outcome != null) {
            response.setOutcome(outcome);
        }
        
        entry.setResponse(response);
        return entry;
    }
	
	public Boolean containsError(OperationOutcome outcome) {
		for (OperationOutcomeIssueComponent issue : outcome.getIssue()) {
			if (issue.getSeverity().equals(IssueSeverity.ERROR)) {
				return true;
			}
		}
		
		return false;
		
	}


}
