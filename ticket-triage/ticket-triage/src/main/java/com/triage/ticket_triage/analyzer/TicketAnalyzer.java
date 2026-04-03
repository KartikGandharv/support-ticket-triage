package com.triage.ticket_triage.analyzer;

import com.triage.ticket_triage.analyzer.AnalysisResult;
import com.triage.ticket_triage.config.KeywordProperties;
import com.triage.ticket_triage.model.Category;
import com.triage.ticket_triage.model.Priority;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketAnalyzer {

    private final KeywordProperties keywordProperties;

    // ----------------------------------------------------------------
    // MAIN ENTRY POINT
    // ----------------------------------------------------------------
    public AnalysisResult analyze(String message) {
        String normalized = message.toLowerCase().trim();

        // 1. Detect urgency
        boolean urgencyDetected = detectUrgency(normalized);

        // 2. Score each category
        Map<Category, CategoryScore> scores = scoreCategories(normalized);

        // 3. Custom Rule: SECURITY auto-escalation
        //    If SECURITY scores > 0, it always wins regardless of other scores
        Category category = resolveCategory(scores);

        // 4. Get matched keywords and signals
        CategoryScore winningScore = scores.get(category);
        List<String> matchedKeywords = winningScore != null
                ? winningScore.getMatchedKeywords()
                : Collections.emptyList();

        // 5. Build signals list (human-readable reasoning)
        List<String> signals = buildSignals(
                category, urgencyDetected, matchedKeywords, scores);

        // 6. Assign priority
        Priority priority = assignPriority(
                category, urgencyDetected, matchedKeywords, scores);

        // 7. Calculate confidence score
        double confidence = calculateConfidence(category, scores, matchedKeywords);

        log.debug("Analysis complete — category={}, priority={}, urgency={}, confidence={}",
                category, priority, urgencyDetected, confidence);

        return AnalysisResult.builder()
                .category(category)
                .priority(priority)
                .urgencyDetected(urgencyDetected)
                .confidenceScore(confidence)
                .matchedKeywords(matchedKeywords)
                .signals(signals)
                .build();
    }

    // ----------------------------------------------------------------
    // STEP 1: URGENCY DETECTION
    // ----------------------------------------------------------------
    private boolean detectUrgency(String message) {
        List<String> urgencyKeywords = keywordProperties.getUrgency().getKeywords();
        return urgencyKeywords.stream()
                .anyMatch(kw -> message.contains(kw.toLowerCase()));
    }

    // ----------------------------------------------------------------
    // STEP 2: SCORE ALL CATEGORIES
    // ----------------------------------------------------------------
    private Map<Category, CategoryScore> scoreCategories(String message) {
        Map<Category, CategoryScore> scores = new EnumMap<>(Category.class);

        keywordProperties.getCategories().forEach((categoryName, config) -> {
            try {
                Category cat = Category.valueOf(categoryName.toUpperCase());
                List<String> matched = config.getKeywords().stream()
                        .filter(kw -> message.contains(kw.toLowerCase()))
                        .collect(Collectors.toList());

                scores.put(cat, new CategoryScore(matched, config.getKeywords().size()));
            } catch (IllegalArgumentException e) {
                log.warn("Unknown category in keywords.yml: {}", categoryName);
            }
        });

        return scores;
    }

    // ----------------------------------------------------------------
    // STEP 3: RESOLVE WINNING CATEGORY
    // Custom Rule: SECURITY always wins if it has ANY keyword match
    // ----------------------------------------------------------------
    private Category resolveCategory(Map<Category, CategoryScore> scores) {
        // Custom Rule: Security escalation
        CategoryScore securityScore = scores.get(Category.SECURITY);
        if (securityScore != null && securityScore.getMatchCount() > 0) {
            log.debug("Custom rule triggered: SECURITY escalation");
            return Category.SECURITY;
        }

        // Normal resolution: pick highest score
        return scores.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().getMatchCount()))
                .filter(e -> e.getValue().getMatchCount() > 0)
                .map(Map.Entry::getKey)
                .orElse(Category.OTHER);
    }

    // ----------------------------------------------------------------
    // STEP 4: BUILD SIGNALS (human-readable reasoning)
    // ----------------------------------------------------------------
    private List<String> buildSignals(
            Category category,
            boolean urgencyDetected,
            List<String> matchedKeywords,
            Map<Category, CategoryScore> allScores) {

        List<String> signals = new ArrayList<>();

        if (urgencyDetected) {
            signals.add("Urgency keywords detected");
        }

        if (category == Category.SECURITY) {
            signals.add("SECURITY escalation rule triggered");
        }

        if (!matchedKeywords.isEmpty()) {
            signals.add("Matched keywords: " + String.join(", ", matchedKeywords));
        }

        // Add runner-up category if close
        allScores.entrySet().stream()
                .filter(e -> e.getKey() != category && e.getValue().getMatchCount() > 0)
                .max(Comparator.comparingInt(e -> e.getValue().getMatchCount()))
                .ifPresent(e -> signals.add(
                        "Secondary match: " + e.getKey() + " (" + e.getValue().getMatchCount() + " signals)"));

        if (signals.isEmpty()) {
            signals.add("No strong signals detected — classified as OTHER");
        }

        return signals;
    }

    // ----------------------------------------------------------------
    // STEP 5: PRIORITY ASSIGNMENT
    // P0 = Security OR (Urgency + Technical/Billing)
    // P1 = Urgency detected OR Technical with 3+ matches
    // P2 = Billing/Account with moderate matches
    // P3 = Feature request OR low signal
    // ----------------------------------------------------------------
    private Priority assignPriority(
            Category category,
            boolean urgencyDetected,
            List<String> matchedKeywords,
            Map<Category, CategoryScore> scores) {

        int matchCount = matchedKeywords.size();

        // Custom Rule: Security is always P0
        if (category == Category.SECURITY) {
            return Priority.P0;
        }

        // P0: Critical urgency + high-impact category
        if (urgencyDetected && (category == Category.TECHNICAL || category == Category.BILLING)) {
            return Priority.P0;
        }

        // P1: Urgency detected in any category OR Technical with strong signals
        if (urgencyDetected || (category == Category.TECHNICAL && matchCount >= 3)) {
            return Priority.P1;
        }

        // P3: Feature requests are always low priority
        if (category == Category.FEATURE_REQUEST) {
            return Priority.P3;
        }

        // P2: Moderate matches for Billing / Account
        if ((category == Category.BILLING || category == Category.ACCOUNT) && matchCount >= 2) {
            return Priority.P2;
        }

        // P3: Everything else with weak signals
        if (matchCount <= 1) {
            return Priority.P3;
        }

        return Priority.P2;
    }

    // ----------------------------------------------------------------
    // STEP 6: CONFIDENCE SCORE
    // Based on: matched keywords / total keywords in winning category
    // Boosted by urgency, penalized for OTHER
    // Capped between 0.0 and 1.0
    // ----------------------------------------------------------------
    private double calculateConfidence(
            Category category,
            Map<Category, CategoryScore> scores,
            List<String> matchedKeywords) {

        if (category == Category.OTHER) return 0.10;

        CategoryScore score = scores.get(category);
        if (score == null || score.getTotalKeywords() == 0) return 0.10;

        double base = (double) score.getMatchCount() / score.getTotalKeywords();

        // Boost if security rule triggered
        if (category == Category.SECURITY && score.getMatchCount() > 0) {
            base = Math.max(base, 0.85);
        }

        // Round to 2 decimal places, cap at 1.0
        return Math.min(Math.round(base * 100.0) / 100.0, 1.0);
    }

    // ----------------------------------------------------------------
    // INNER CLASS: CategoryScore
    // ----------------------------------------------------------------
    static class CategoryScore {
        private final List<String> matchedKeywords;
        private final int totalKeywords;

        CategoryScore(List<String> matchedKeywords, int totalKeywords) {
            this.matchedKeywords = matchedKeywords;
            this.totalKeywords = totalKeywords;
        }

        int getMatchCount() { return matchedKeywords.size(); }
        int getTotalKeywords() { return totalKeywords; }
        List<String> getMatchedKeywords() { return matchedKeywords; }
    }
}