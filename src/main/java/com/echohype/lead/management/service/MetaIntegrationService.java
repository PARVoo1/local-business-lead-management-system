package com.echohype.lead.management.service;

import com.echohype.lead.management.entity.User;
import com.echohype.lead.management.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetaIntegrationService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${meta.app.id}")
    private String appId;

    @Value("${meta.app.secret}")
    private String appSecret;

    @Value("${meta.redirect.uri}")
    private String redirectUri;

    public void processMetaCode(String code, String username) {
        try {
            String tokenUrl = String.format(
                    "https://graph.facebook.com/v19.0/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s",
                    appId, redirectUri, appSecret, code
            );

            ResponseEntity<String> tokenResponse = restTemplate.getForEntity(tokenUrl, String.class);
            JsonNode tokenRoot = objectMapper.readTree(tokenResponse.getBody());
            String metaAccessToken = tokenRoot.path("access_token").asText();

            User user = userRepository.findByUsername(username);
            if (user == null) {
                log.error("User not found: {}", username);
                return;
            }

            user.setMetaAccessToken(metaAccessToken);

            String instagramId = fetchInstagramId(metaAccessToken);
            if (instagramId != null) {
                user.setInstagramId(instagramId);
            }

            String whatsappPhoneId = fetchWhatsappPhoneNumberId(metaAccessToken);
            if (whatsappPhoneId != null) {
                user.setWhatsappNumberId(whatsappPhoneId);
            }

            userRepository.save(user);
            log.info("Successfully linked Meta accounts for user: {}", username);

        } catch (Exception e) {
            log.error("Failed to process Meta connection: {}", e.getMessage());
            throw new RuntimeException("Meta API connection failed");
        }
    }

    private String fetchInstagramId(String accessToken) {
        try {
            String url = "https://graph.facebook.com/v19.0/me/accounts?fields=instagram_business_account&access_token=" + accessToken;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode dataArray = objectMapper.readTree(response.getBody()).path("data");

            if (dataArray.isArray()) {
                for (JsonNode page : dataArray) {
                    JsonNode igAccount = page.path("instagram_business_account");
                    if (!igAccount.isMissingNode() && igAccount.has("id")) {
                        return igAccount.path("id").asText();
                    }
                }
            }
        } catch (Exception e) { log.error("Error fetching IG ID"); }
        return null;
    }

    private String fetchWhatsappPhoneNumberId(String accessToken) {
        try {
            String businessUrl = "https://graph.facebook.com/v19.0/me/businesses?access_token=" + accessToken;
            JsonNode bizData = objectMapper.readTree(restTemplate.getForEntity(businessUrl, String.class).getBody()).path("data");

            if (bizData.isArray() && !bizData.isEmpty()) {
                String businessId = bizData.get(0).path("id").asText();
                String wabaUrl = "https://graph.facebook.com/v19.0/" + businessId + "/owned_whatsapp_business_accounts?access_token=" + accessToken;
                JsonNode wabaData = objectMapper.readTree(restTemplate.getForEntity(wabaUrl, String.class).getBody()).path("data");

                if (wabaData.isArray() && !wabaData.isEmpty()) {
                    String wabaId = wabaData.get(0).path("id").asText();
                    String phoneUrl = "https://graph.facebook.com/v19.0/" + wabaId + "/phone_numbers?access_token=" + accessToken;
                    JsonNode phoneData = objectMapper.readTree(restTemplate.getForEntity(phoneUrl, String.class).getBody()).path("data");

                    if (phoneData.isArray() && !phoneData.isEmpty()) {
                        return phoneData.get(0).path("id").asText();
                    }
                }
            }
        } catch (Exception e) { log.error("Error fetching WA ID"); }
        return null;
    }
}