package net.rezzix.fhirservice.service;

import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.r5.model.Bundle;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.*;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;

@Service
public class ValidationService {
	
	private FhirValidator validator = null;
	
    public ValidationService() {
        FhirContext ctx = FhirContext.forR5();
        
        // 1. Create validation support chain
        var validationSupport = new ValidationSupportChain(
            new InMemoryTerminologyServerValidationSupport(ctx),
            new CommonCodeSystemsTerminologyService(ctx),
            new DefaultProfileValidationSupport(ctx)
        );

        // 2. Enable validation support to use R5 resources (from hapi-fhir-validation-resources-r5)
        //validationSupport.setValidationSupportEnabled(true);
        
        // This loads R5 validation resources (StructureDefinitions, etc.)
        validator = ctx.newValidator();
        validator.setValidateAgainstStandardSchema(false);   // Disable XSD
        validator.setValidateAgainstStandardSchematron(false); // Optional: disable Schematron too
        // HAPI will now use StructureDefinitions from hapi-fhir-validation-resources-r5
    }

    public void validateBundle(Bundle bundle) {
    	ValidationResult result = validator.validateWithResult(bundle);
    }
}
