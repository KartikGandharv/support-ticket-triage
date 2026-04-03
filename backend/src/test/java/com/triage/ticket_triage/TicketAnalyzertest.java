package com.triage.ticket_triage;


import com.triage.ticket_triage.analyzer.AnalysisResult;
import com.triage.ticket_triage.analyzer.TicketAnalyzer;
import com.triage.ticket_triage.config.KeywordProperties;
import com.triage.ticket_triage.model.Category;
import com.triage.ticket_triage.model.Priority;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableConfigurationProperties(KeywordProperties.class)
@TestPropertySource(locations = "classpath:keywords.yml")
class TicketAnalyzerTest {

    @Autowired
    private TicketAnalyzer ticketAnalyzer;

    // ----------------------------------------------------------------
    // CATEGORY CLASSIFICATION TESTS
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("Category Classification")
    class CategoryClassificationTests {

        @Test
        @DisplayName("Should classify billing related message")
        void shouldClassifyBillingTicket() {
            String message = "I was overcharged on my invoice and need a refund immediately";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getCategory()).isEqualTo(Category.BILLING);
        }

        @Test
        @DisplayName("Should classify technical issue message")
        void shouldClassifyTechnicalTicket() {
            String message = "The application keeps crashing with an error and is very slow";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getCategory()).isEqualTo(Category.TECHNICAL);
        }

        @Test
        @DisplayName("Should classify account related message")
        void shouldClassifyAccountTicket() {
            String message = "I cannot login to my account, my password reset is not working";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getCategory()).isEqualTo(Category.ACCOUNT);
        }

        @Test
        @DisplayName("Should classify feature request message")
        void shouldClassifyFeatureRequestTicket() {
            String message = "It would be great if you could add dark mode, suggestion for improvement";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getCategory()).isEqualTo(Category.FEATURE_REQUEST);
        }

        @Test
        @DisplayName("Should classify security related message")
        void shouldClassifySecurityTicket() {
            String message = "I think my account has been hacked and there are unauthorized transactions";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getCategory()).isEqualTo(Category.SECURITY);
        }

        @Test
        @DisplayName("Should classify unknown message as OTHER")
        void shouldClassifyUnknownTicketAsOther() {
            String message = "Hello I have a question about something general";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getCategory()).isEqualTo(Category.OTHER);
        }

        @Test
        @DisplayName("Should be case insensitive")
        void shouldBeCaseInsensitive() {
            String message = "I need a REFUND for my INVOICE";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getCategory()).isEqualTo(Category.BILLING);
        }
    }

    // ----------------------------------------------------------------
    // PRIORITY ASSIGNMENT TESTS
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("Priority Assignment")
    class PriorityAssignmentTests {

        @Test
        @DisplayName("Should assign P0 for security tickets")
        void shouldAssignP0ForSecurityTickets() {
            String message = "My account was hacked and credentials were stolen";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getPriority()).isEqualTo(Priority.P0);
        }

        @Test
        @DisplayName("Should assign P0 for urgent technical outage")
        void shouldAssignP0ForUrgentTechnicalOutage() {
            String message = "Production is down urgently, we have an outage right now";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getPriority()).isEqualTo(Priority.P0);
        }

        @Test
        @DisplayName("Should assign P1 for urgent billing issue")
        void shouldAssignP1ForUrgentBillingIssue() {
            String message = "I am being charged incorrectly, this is urgent please help";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getPriority()).isIn(Priority.P0, Priority.P1);
        }

        @Test
        @DisplayName("Should assign P3 for feature requests")
        void shouldAssignP3ForFeatureRequests() {
            String message = "It would be great to have a suggestion for dark mode feature";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getPriority()).isEqualTo(Priority.P3);
        }

        @Test
        @DisplayName("Should assign P2 for moderate billing issue")
        void shouldAssignP2ForModerateBillingIssue() {
            String message = "I have a question about my billing invoice and payment";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getPriority()).isEqualTo(Priority.P2);
        }
    }

    // ----------------------------------------------------------------
    // URGENCY DETECTION TESTS
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("Urgency Detection")
    class UrgencyDetectionTests {

        @Test
        @DisplayName("Should detect urgency keyword — urgent")
        void shouldDetectUrgencyKeyword() {
            String message = "This is urgent, my system is broken";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.isUrgencyDetected()).isTrue();
        }

        @Test
        @DisplayName("Should detect urgency keyword — asap")
        void shouldDetectAsapKeyword() {
            String message = "Please fix this asap, I cannot wait";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.isUrgencyDetected()).isTrue();
        }

        @Test
        @DisplayName("Should detect urgency keyword — production")
        void shouldDetectProductionKeyword() {
            String message = "Our production environment is having issues";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.isUrgencyDetected()).isTrue();
        }

        @Test
        @DisplayName("Should not detect urgency in calm message")
        void shouldNotDetectUrgencyInCalmMessage() {
            String message = "I would like to request a new feature for the dashboard";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.isUrgencyDetected()).isFalse();
        }
    }

    // ----------------------------------------------------------------
    // CUSTOM SECURITY RULE TESTS
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("Custom Security Rule")
    class CustomSecurityRuleTests {

        @Test
        @DisplayName("Security should always win over other categories")
        void securityShouldAlwaysWinOverOtherCategories() {
            // Message has both billing AND security keywords
            String message = "My payment was stolen and my account was hacked, unauthorized charge";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            // Security must win even though billing keywords also match
            assertThat(result.getCategory()).isEqualTo(Category.SECURITY);
        }

        @Test
        @DisplayName("Security should always be P0 regardless of urgency")
        void securityShouldAlwaysBeP0() {
            String message = "There is a vulnerability in the system";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getCategory()).isEqualTo(Category.SECURITY);
            assertThat(result.getPriority()).isEqualTo(Priority.P0);
        }

        @Test
        @DisplayName("Security signal should appear in signals list")
        void securitySignalShouldAppearInSignals() {
            String message = "I received a phishing email and think I am compromised";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getSignals())
                    .anyMatch(s -> s.contains("SECURITY escalation rule triggered"));
        }

        @Test
        @DisplayName("Fraud keyword should trigger security rule")
        void fraudKeywordShouldTriggerSecurityRule() {
            String message = "I think there is fraud happening on my account";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getCategory()).isEqualTo(Category.SECURITY);
            assertThat(result.getPriority()).isEqualTo(Priority.P0);
        }
    }

    // ----------------------------------------------------------------
    // CONFIDENCE SCORE TESTS
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("Confidence Score")
    class ConfidenceScoreTests {

        @Test
        @DisplayName("Should return low confidence for OTHER category")
        void shouldReturnLowConfidenceForOther() {
            String message = "Hello I have a general question";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getConfidenceScore()).isLessThanOrEqualTo(0.15);
        }

        @Test
        @DisplayName("Should return high confidence for strong security match")
        void shouldReturnHighConfidenceForSecurityMatch() {
            String message = "My account was hacked and compromised with malware";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getConfidenceScore()).isGreaterThanOrEqualTo(0.50);
        }

        @Test
        @DisplayName("Confidence score should be between 0 and 1")
        void confidenceScoreShouldBeBetweenZeroAndOne() {
            String message = "invoice payment billing charge refund subscription overcharged";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getConfidenceScore())
                    .isGreaterThanOrEqualTo(0.0)
                    .isLessThanOrEqualTo(1.0);
        }
    }

    // ----------------------------------------------------------------
    // KEYWORD EXTRACTION TESTS
    // ----------------------------------------------------------------
    @Nested
    @DisplayName("Keyword Extraction")
    class KeywordExtractionTests {

        @Test
        @DisplayName("Should extract matched keywords")
        void shouldExtractMatchedKeywords() {
            String message = "I need a refund for this invoice charge";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getMatchedKeywords()).isNotEmpty();
            assertThat(result.getMatchedKeywords())
                    .anyMatch(k -> k.equals("refund") || k.equals("invoice") || k.equals("charge"));
        }

        @Test
        @DisplayName("Should return empty keywords for OTHER category")
        void shouldReturnEmptyKeywordsForOther() {
            String message = "Hello there, just saying hi";
            AnalysisResult result = ticketAnalyzer.analyze(message);
            assertThat(result.getMatchedKeywords()).isEmpty();
        }
    }
}
