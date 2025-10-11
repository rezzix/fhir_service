package net.rezzix.fhirservice.controller;

import org.hl7.fhir.r5.model.Bundle;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import net.rezzix.fhirservice.exceptions.ValidationException;
import net.rezzix.fhirservice.service.DeclarationService;

@Component
public class TransactionProvider {

    private final DeclarationService declarationService;

    public TransactionProvider(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    /**
     * This method is the endpoint for receiving FHIR transaction and batch bundles.
     * It delegates the processing to the DeclarationService.
     *
     * @param theBundle The incoming FHIR Bundle from the POST request.
     * @return A response Bundle.
     */
    @Transaction
    public Bundle processDeclarationBundle(@TransactionParam Bundle theBundle) {
        try {
            // The existing declaration service is called to handle the bundle
            declarationService.declare(theBundle);
        } catch (ValidationException e) {
            // In a production system, you should return an OperationOutcome resource
            // to provide structured error feedback to the client.
            throw new ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException("Validation failed: " + e.getMessage());
        }

        // According to the FHIR specification, a transaction should return a response bundle.
        // For this use case, we can return a minimal response.
        Bundle responseBundle = new Bundle();
        responseBundle.setType(Bundle.BundleType.TRANSACTIONRESPONSE);
        return responseBundle;
    }
}