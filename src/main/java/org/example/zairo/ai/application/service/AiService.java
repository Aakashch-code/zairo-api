package org.example.zairo.ai.application.service;

import org.example.zairo.ai.application.dto.FinancialData;
import lombok.RequiredArgsConstructor;
import org.example.zairo.authentication.application.service.SecuredService;
import org.example.zairo.authentication.domain.model.FinanceWorkspace;
import org.example.zairo.authentication.domain.model.Users;
import org.example.zairo.authentication.domain.model.UsersRole;
import org.example.zairo.authentication.infrastructure.persistence.UserRepository;
import org.example.zairo.transaction.application.dto.SavingResponse;
import org.example.zairo.transaction.application.service.TransactionService;
import org.example.zairo.transaction.domain.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiService extends SecuredService {

    private final UserRepository userRepository;
    private final TransactionService transactionService;

    private final RestClient restClient = RestClient.create();

    @Value("${zai.api.url}")
    private String zaiMicroserviceUrl;

    @Transactional(readOnly = true)
    public FinancialData getFinancialDataSummary() {
        FinancialData financialData = getBaseFinancialData();

        String prompt = "Analyze this financial summary and provide a 2-sentence health check on our net position.";
        String aiInsight = callZaiMicroservice(prompt, financialData);

        financialData.setAiInsight(aiInsight);
        return financialData;
    }

    @Transactional(readOnly = true)
    public String askZai(String userPrompt) {
        FinancialData contextData = getBaseFinancialData();
        return callZaiMicroservice(userPrompt, contextData);
    }

    private FinancialData getBaseFinancialData() {
        UUID userId = currentUserId();

        Users currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FinanceWorkspace workspace = currentUser.getWorkspace();
        if (workspace == null) {
            throw new IllegalStateException("User does not belong to a workspace");
        }

        Users organizer = userRepository.findByWorkspaceIdAndRole(workspace.getId(), UsersRole.ORGANIZER)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found for this workspace"));

        SavingResponse savings = transactionService.calculateNetPosition();

        FinancialData financialData = new FinancialData();
        financialData.setWorkspaceName(workspace.getName());
        financialData.setOrganizerName(organizer.getUsername());
        financialData.setTransactionSummary(savings);

        return financialData;
    }

    private String callZaiMicroservice(String prompt, Object contextData) {
        try {
            Map<String, Object> payload = Map.of(
                    "prompt", prompt,
                    "context", contextData
            );

            Map<String, String> response = restClient.post()
                    .uri(zaiMicroserviceUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, String>>() {});

            return (response != null && response.containsKey("response"))
                    ? response.get("response")
                    : "Zai could not generate an insight at this time.";

        } catch (Exception e) {
            return "Zai is currently offline. Please try again later.";
        }
    }
}