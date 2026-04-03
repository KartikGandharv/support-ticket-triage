package com.triage.ticket_triage.analyzer;


import com.triage.ticket_triage.model.Category;
import com.triage.ticket_triage.model.Priority;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnalysisResult {

    private Category category;
    private Priority priority;
    private boolean urgencyDetected;
    private double confidenceScore;
    private List<String> matchedKeywords;
    private List<String> signals;
}
