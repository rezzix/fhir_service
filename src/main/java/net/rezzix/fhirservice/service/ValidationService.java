package net.rezzix.fhirservice.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
//import lombok.extern.slf4j.Slf4j;
import net.rezzix.fhirservice.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.hl7.fhir.r5.model.Base;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.CodeableConcept;
import org.hl7.fhir.r5.model.Coding;
import org.hl7.fhir.r5.model.Condition;
import org.hl7.fhir.r5.model.Encounter;
import org.hl7.fhir.r5.model.MedicationAdministration;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r5.model.OperationOutcome.IssueType;
import org.hl7.fhir.r5.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.r5.model.Organization;
import org.hl7.fhir.r5.model.Patient;
import org.hl7.fhir.r5.model.Practitioner;
import org.hl7.fhir.r5.model.PractitionerRole;
import org.hl7.fhir.r5.model.Procedure;
import org.hl7.fhir.r5.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
//@Slf4j
public class ValidationService {
	@Value("${coding.encounterClass.system}")
    private String encounterClassSystem;
	
	@Value("${coding.encounterClass.validation}")
	private String encounterClassValidation;

    static final String REQUIRED = "required";
    static final String OPTIONAL = "optional";

    public ValidationService() {
        
/*
        // Create a validation support chain (this part was correct)
        ValidationSupportChain validationSupportChain = new ValidationSupportChain(
            new DefaultProfileValidationSupport(fhirContext),
            new InMemoryTerminologyServerValidationSupport(fhirContext),
            new CommonCodeSystemsTerminologyService(fhirContext)
        );

        // 1. Create the FhirInstanceValidator module and give it the support chain
        FhirInstanceValidator instanceValidator = new FhirInstanceValidator(validationSupportChain);

        // 2. Create the main validator engine
        this.validator = fhirContext.newValidator();

        // 3. Register the configured instance validator module with the engine
        this.validator.registerValidatorModule(instanceValidator);*/
    }
    /**
     * Validates a FHIR Bundle against standard profiles and structure.
     * @param bundle The bundle to validate.
     * @return true if valid, false otherwise.
     */
    public ValidationResult validateBundleStructure(Bundle bundle) {
    	/**FIXME add FHIR validator
    	/*ValidationResult result = validator.validateWithResult(bundle);
        if (!result.isSuccessful()) {
        	//structureOutcome.setSeverity(IssueSeverity.ERROR);
        } else {
        	//log.info("Bundle structure validation successful.");
        }*/
        return null;
    }

    /**
     * Validates the coding systems within the bundle.
     * This is conceptually similar to the main validation but could be extended
     * with more specific terminology rules if needed. For now, it uses the same validator.
     * @param bundle The bundle to validate.
     * @return true if valid, false otherwise.
     * @throws ValidationException 
     */
    public OperationOutcome validateCodingSystems(Bundle bundle) throws ValidationException {
    	OperationOutcome outcome = new OperationOutcome();
    	//ValidationResult result = new ValidationResult(null, null)
    	for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
    		OperationOutcomeIssueComponent codingValidation = validateCodingSystem(entry.getResource());
    		if (outcome!=null)
    			outcome.addIssue(codingValidation);
    	}
    	
    	return outcome;
    }
    
    public OperationOutcomeIssueComponent validateCodingSystem(Resource resource) throws ValidationException {
    	//new Coding().setSystem("http://snomed.info/sct");
    	OperationOutcomeIssueComponent issue = null;	
        if (resource instanceof Patient patient) {
        	 
        }
        else if (resource instanceof Organization organization) {
            
        }
        else if (resource instanceof Practitioner practitioner) {
            
        }
        else if (resource instanceof PractitionerRole practitionerRole) {
        	
        }
        else if (resource instanceof Encounter encounter) {        	
        	CodeableConcept encounterClass = encounter.getClass_().stream().filter( encclass-> encclass.hasCoding(encounterClassSystem)).findAny().orElse(null);
        	issue = new OperationOutcomeIssueComponent();
        	if (encounterClass != null) {
        		return null;
        	} else if (OPTIONAL.equals(encounterClassValidation)) {
                issue.setSeverity(IssueSeverity.WARNING);
                issue.setCode(IssueType.INCOMPLETE);
                issue.setDiagnostics("the encounter should have " + encounterClassSystem);
        	} else if (REQUIRED.equals(encounterClassValidation)) {
        		//throw new ValidationException("no " + encounterClassSystem + " found in resource " + encounterClass + " " + encounter);
                issue.setSeverity(IssueSeverity.ERROR);
                issue.setCode(IssueType.CODEINVALID);
                issue.setDiagnostics("the encounter must have " + encounterClassSystem);
        	}
        	return issue;           
            
        }
        else if (resource instanceof Condition condition) {
        	
        }
        else if (resource instanceof Procedure procedure) {
            
        }
        else if (resource instanceof MedicationAdministration medAdmin) {
            
        } 
            
            
        return issue;
        
    }

}