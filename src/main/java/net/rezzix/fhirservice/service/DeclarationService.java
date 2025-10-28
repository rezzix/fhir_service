package net.rezzix.fhirservice.service;

import java.util.ArrayList;
import java.util.Date;
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
import net.rezzix.fhirservice.utils.Utils;

@Service
public class DeclarationService {

	private final KafkaProducerService kafkaProducerService;
	private final ValidationService validationService;
	private final MedicalCorrespondenceService medicalCorrespondenceService;
	private final FhirService fhirService;

	@Value("${app.kafka.active}")
	private Boolean activeBroker;
	
	@Value("${app.kafka.topic.declarationssih}")
	private String topicName;

	@Value("${app.validation.bundle-enabled}")
	private boolean isBundleValidationEnabled;

	@Value("${app.validation.coding-enabled}")
	private boolean isCodingValidationEnabled;

	public DeclarationService(KafkaProducerService kafkaProducerService, ValidationService validationService,
			MedicalCorrespondenceService medicalCorrespondenceService, FhirService fhirService) {
		this.kafkaProducerService = kafkaProducerService;
		this.validationService = validationService;
		this.medicalCorrespondenceService = medicalCorrespondenceService;
		this.fhirService = fhirService;
	}

	public Bundle declare(Bundle bundle) throws ValidationException {
		List<OperationOutcomeIssueComponent> outcomes = new ArrayList<OperationOutcomeIssueComponent>();
		validationService.validateBundleStructure(bundle);

		OperationOutcome outcome = validationService.validateCodingSystems(bundle);

		medicalCorrespondenceService.addQualifiers(bundle);

		/*String prettyJson = fhirService.format(bundle);
		
		System.out.println(Utils.getInstance().stringBundle(bundle));*/
		
		if (activeBroker && !Utils.getInstance().containsError(outcome)) {
			kafkaProducerService.sendMessage(topicName, fhirService.format(bundle));
		}
		
		Bundle declarationresponseBundle = new Bundle();
        declarationresponseBundle.setType(Bundle.BundleType.TRANSACTIONRESPONSE);
        declarationresponseBundle.setTimestamp(new Date());
        
        declarationresponseBundle.addEntry(Utils.getInstance().createResourceResponse(outcome));
		
		return declarationresponseBundle;
	}

}
