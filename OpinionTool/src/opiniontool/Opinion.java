/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opiniontool;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Lavinia
 */
public class Opinion {
    
    public Opinion() {
        affects_ = new HashSet();
        entities_ = new HashSet();
        sentimentSegments_ = new HashSet();
        weightS_ = 0;
        document_ = "";
    }
    
    public void setNormalizedSentence(String sentence) {
        normalizedSentence_ = sentence;
    }
    
    public String getNormalizedSentence() {
        return normalizedSentence_;
    }
    public void setSentence(String sentence) {
        sentence_ = sentence;
    }
    
    public String getSentence() {
        return sentence_;
    }
    
    public void setEntities(Set<String> entities) {
        entities_ = entities;
    }
    
    public Set<String> getEntities() {
        return entities_;
    }
    
    public Set<SentimentSegment> getSegments() {
        return sentimentSegments_;
    }
    
    public void addSentimentSegment(SentimentSegment sent) {
        sentimentSegments_.add(sent);
    }
    
    public void addSentimentSegments(Set<SentimentSegment> sent) {
        sentimentSegments_.addAll(sent);
    }
    
    private String normalizedSentence_;
    private String sentence_;
    private Set<String> affects_;
    private Set<String> entities_;
    private Set<SentimentSegment> sentimentSegments_;
    double weightS_;
    String document_;
}
