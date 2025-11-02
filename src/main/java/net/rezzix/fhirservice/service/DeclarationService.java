package net.rezzix.fhirservice.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import net.rezzix.fhirservice.exceptions.ValidationException;
import net.rezzix.fhirservice.utils.Utils;

import net.rezzix.fhirservice.model.Declaration;
import net.rezzix.fhirservice.repository.DeclarationRepository;

@Service
public class DeclarationService {

	private final KafkaProducerService kafkaProducerService;
	private final ValidationService validationService;
	private final MedicalCorrespondenceService medicalCorrespondenceService;
	private final FhirService fhirService;
	private final DeclarationRepository declarationRepository;

	@Value("${app.kafka.active}")
	private Boolean activeBroker;
	
	@Value("${app.kafka.topic.declarationssih}")
	private String topicName;

	@Value("${app.validation.bundle-enabled}")
	private boolean isBundleValidationEnabled;

	@Value("${app.validation.coding-enabled}")
	private boolean isCodingValidationEnabled;

	public DeclarationService(KafkaProducerService kafkaProducerService, ValidationService validationService,
			MedicalCorrespondenceService medicalCorrespondenceService, FhirService fhirService,
			DeclarationRepository declarationRepository) {
		this.kafkaProducerService = kafkaProducerService;
		this.validationService = validationService;
		this.medicalCorrespondenceService = medicalCorrespondenceService;
		this.fhirService = fhirService;
		this.declarationRepository = declarationRepository;
	}

	public Bundle declare(Bundle bundle) throws ValidationException {
		Declaration declaration = new Declaration();
        declaration.setDeclarationDate(java.time.LocalDate.now());
        declaration.setSource(fhirService.format(bundle).toString());

        try {
			List<OperationOutcomeIssueComponent> outcomes = new ArrayList<OperationOutcomeIssueComponent>();
			validationService.validateBundleStructure(bundle);

			OperationOutcome outcome = validationService.validateCodingSystems(bundle);

			medicalCorrespondenceService.addQualifiers(bundle);

			/*String prettyJson = fhirService.format(bundle);
			
			System.out.println(Utils.getInstance().stringBundle(bundle));*/

			if (Utils.getInstance().containsError(outcome)) {
                declaration.setStatus("FAILED");
            } else {
                declaration.setStatus("VALIDATED");
            }
            declarationRepository.save(declaration);
            outcome.addIssue().setSeverity(IssueSeverity.INFORMATION).setDiagnostics("Message received with ID : " + declaration.getId());
            
			if (activeBroker && !Utils.getInstance().containsError(outcome)) {
				try {
					SendResult<String, String> result = kafkaProducerService.sendMessage(topicName, fhirService.format(bundle));
					declaration.setTransferDate(java.time.LocalDate.now());
					declarationRepository.save(declaration);
					//outcome.addIssue().setSeverity(IssueSeverity.INFORMATION).setDiagnostics("Message sent to Kafka with ID " + result.getRecordMetadata().topic() + "-" + result.getRecordMetadata().partition() + "-" + result.getRecordMetadata().offset());
					System.out.println("Message sent to Kafka with ID \" + result.getRecordMetadata().topic() + \"-\" + result.getRecordMetadata().partition() + \"-\" + result.getRecordMetadata().offset()");
				} catch (Exception e) { //InterruptedException | ExecutionException | KafkaException
					declaration.setStatus("PENDING_RETRY");
                    declarationRepository.save(declaration);
				}
			}

			Bundle declarationresponseBundle = new Bundle();
	        declarationresponseBundle.setType(Bundle.BundleType.TRANSACTIONRESPONSE);
	        declarationresponseBundle.setTimestamp(new Date());
	        
	        declarationresponseBundle.addEntry(Utils.getInstance().createResourceResponse(outcome));
			
			return declarationresponseBundle;
		} catch (Exception e) {
            declaration.setStatus("FAILED");
            declarationRepository.save(declaration);
            // Re-throw the exception to be handled by the controller
            throw e;
        }
	}

}
