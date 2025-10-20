package com.lakomka.utils;

import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;
import com.google.recaptchaenterprise.v1.Assessment;
import com.google.recaptchaenterprise.v1.CreateAssessmentRequest;
import com.google.recaptchaenterprise.v1.ProjectName;
import com.google.recaptchaenterprise.v1.RiskAnalysis;
import com.lakomka.dto.RegistrationDto;
import com.google.recaptchaenterprise.v1.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@Slf4j
public class ReCaptchaV3Util {

    private static final String PROJECT_ID = "lakomka-shop-1760149753543";

    public static ResponseEntity<String> validate(
            RegistrationDto user
    ) throws IOException {
        // Create the reCAPTCHA client.
        try (RecaptchaEnterpriseServiceClient client = RecaptchaEnterpriseServiceClient.create()) {

            // Set the properties of the event to be tracked.
            Event event = Event.newBuilder().setSiteKey(user.getSiteKey()).setToken(user.getToken()).build();

            // Build the assessment request.
            CreateAssessmentRequest createAssessmentRequest =
                    CreateAssessmentRequest.newBuilder()
                            .setParent(ProjectName.of(PROJECT_ID).toString())
                            .setAssessment(Assessment.newBuilder().setEvent(event).build())
                            .build();

            Assessment response = client.createAssessment(createAssessmentRequest);

            // Check if the token is valid.
            if (!response.getTokenProperties().getValid()) {
                log.info("The CreateAssessment call failed because the token was: {}", response.getTokenProperties().getInvalidReason().name());
                return ResponseEntity.status(403).body("Попробуйте еще раз.");
            }

            // Check if the expected action was executed.
            if (!response.getTokenProperties().getAction().equals(user.getExpectedAction())) {
                log.info("The action attribute in reCAPTCHA tag is: {}", response.getTokenProperties().getAction());
                log.info("The action attribute in the reCAPTCHA tag does not match the action ({}) you are expecting to score", user.getExpectedAction());
                return ResponseEntity.status(403).body(
                        "The action attribute in reCAPTCHA tag is: " + response.getTokenProperties().getAction() + "\n" +
                                "The action attribute in the reCAPTCHA tag does not match the action " +
                                user.getExpectedAction() +
                                " you are expecting to score");
            }

            for (RiskAnalysis.ClassificationReason reason : response.getRiskAnalysis().getReasonsList()) {
                log.info(reason.toString());
            }

            float recaptchaScore = response.getRiskAnalysis().getScore();
            log.info("The reCAPTCHA score is: {}", recaptchaScore);

            String assessmentName = response.getName();
            log.info("Assessment name: {}", assessmentName.substring(assessmentName.lastIndexOf("/") + 1));
        }
        return null;
    }

}
