package net.rezzix.fhirservice.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.r5.model.Bundle;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ValidationService {

    private final FhirValidator validator;
    //private final FhirContext fhirContext;

    public ValidationService(FhirContext fhirContext) {
        //this.fhirContext = fhirContext;

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
        this.validator.registerValidatorModule(instanceValidator);
    }
    /**
     * Validates a FHIR Bundle against standard profiles and structure.
     * @param bundle The bundle to validate.
     * @return true if valid, false otherwise.
     */
    public boolean validateBundleStructure(Bundle bundle) {
        //log.info("Performing bundle structure validation...");
        ValidationResult result = validator.validateWithResult(bundle);
        if (!result.isSuccessful()) {
            //log.error("Bundle structure validation failed: {}", result.toString());
        } else {
            //log.info("Bundle structure validation successful.");
        }
        return result.isSuccessful();
    }

    /**
     * Validates the coding systems within the bundle.
     * This is conceptually similar to the main validation but could be extended
     * with more specific terminology rules if needed. For now, it uses the same validator.
     * @param bundle The bundle to validate.
     * @return true if valid, false otherwise.
     */
    public boolean validateCodingSystems(Bundle bundle) {
        //log.info("Performing coding system validation...");
        // For this implementation, we reuse the main validator which already checks terminology.
        // This method can be expanded to connect to a dedicated terminology server for more advanced validation.
        ValidationResult result = validator.validateWithResult(bundle);
        if (!result.isSuccessful()) {
            //log.error("Coding system validation failed: {}", result.toString());
        } else {
            //log.info("Coding system validation successful.");
        }
        return result.isSuccessful();
    }
}