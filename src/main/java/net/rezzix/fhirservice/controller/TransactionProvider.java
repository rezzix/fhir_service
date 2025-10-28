package net.rezzix.fhirservice.controller;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.OperationOutcome;
import org.hl7.fhir.r5.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r5.model.OperationOutcome.IssueType;
import org.hl7.fhir.r5.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.r5.model.Property;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import net.rezzix.fhirservice.exceptions.ValidationException;
import net.rezzix.fhirservice.service.DeclarationService;
import net.rezzix.fhirservice.utils.Utils;

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
            Bundle declarationBundle = declarationService.declare(theBundle);

            // On success, return a minimal, successful response bundle.
            Bundle responseBundle = new Bundle();
            responseBundle.setType(Bundle.BundleType.TRANSACTIONRESPONSE);

            responseBundle.addEntry(declarationBundle.getEntry().get(0));
            
            // Add an OperationOutcome to the response to indicate success.
            OperationOutcome outcome = new OperationOutcome();
            
            //TODO send this outcome to response
            if (Utils.getInstance().containsError((OperationOutcome) declarationBundle.getEntry().get(0).getResponse().getOutcome())) {
            	 outcome.addIssue()
                 .setSeverity(IssueSeverity.ERROR)
                 .setCode(IssueType.CODEINVALID)
                 .setDiagnostics("Transaction not saved.");
             
	             // Add the outcome as the first entry in the response bundle
	             responseBundle.addEntry()
	                 .setResource(outcome)
	                 .getResponse().setStatus("400 KO");
            } else {
            	 outcome.addIssue()
                 .setSeverity(IssueSeverity.INFORMATION)
                 .setCode(IssueType.SUCCESS)
                 .setDiagnostics("Transaction processed successfully.");
             
            	 // Add the outcome as the first entry in the response bundle
            	 responseBundle.addEntry()
                 .setResource(outcome)
                 .getResponse().setStatus("200 OK");
            	
            }

            return responseBundle;

        } catch (ValidationException e) {
            // On validation failure, create a detailed OperationOutcome.
            OperationOutcome outcome = new OperationOutcome();
            outcome.addIssue()
                .setSeverity(IssueSeverity.ERROR)
                .setCode(IssueType.INVALID)
                .setDiagnostics(e.getMessage());

            // Throw a specific HAPI FHIR exception that carries the OperationOutcome.
            // This will result in a proper HTTP 422 response with the OO as the body.
            throw new UnprocessableEntityException("Validation failed", outcome);
        }

    }
}