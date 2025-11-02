package net.rezzix.fhirservice.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import net.rezzix.fhirservice.model.Declaration;
import net.rezzix.fhirservice.repository.DeclarationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class RetryService {

    private static final Logger logger = LoggerFactory.getLogger(RetryService.class);
    private final DeclarationRepository declarationRepository;
    private final KafkaProducerService kafkaProducerService;
    private final FhirService fhirService;
    
	@Value("${app.kafka.topic.declarationssih}")
	private String topicName;

    public RetryService(DeclarationRepository declarationRepository, KafkaProducerService kafkaProducerService, FhirService fhirService) {
        this.declarationRepository = declarationRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.fhirService = fhirService;
    }

    @Scheduled(fixedRate = 60000) // 5 minutes
    @Transactional
    public void retryFailedDeclarations() {
        logger.info("Starting retry job for failed declarations.");
        List<Declaration> failedDeclarations = declarationRepository.findByStatus("PENDING_RETRY", PageRequest.of(0, 200, Sort.by("id")));

        for (Declaration declaration : failedDeclarations) {
            try {
                kafkaProducerService.sendMessage(topicName, declaration.getSource());
                declaration.setStatus("VALIDATED");
                declaration.setTransferDate(LocalDate.now());
                declarationRepository.save(declaration);
                logger.info("Successfully resent declaration with id: {}", declaration.getId());
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Failed to resend declaration with id: {}. Error: {}", declaration.getId(), e.getMessage());
            }
        }
        logger.info("Finished retry job for failed declarations.");
    }
}
