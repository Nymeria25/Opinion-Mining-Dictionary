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
    
    public void computeWeightS() {
        int numberOfSegments = sentimentSegments_.size();
        double score;
        for(SentimentSegment sent : sentimentSegments_) {
            if(sent.getNegation()!= null && sent.getModifier()!=null && 
                    sent.getModifier().equals("cel mai")) {
                score = 13/10 * sent.getTriggerValue();
            } else if(sent.getNegation()== null && sent.getModifier()!=null && 
                    sent.getModifier().equals("cel mai")) {
                score = 18/10 * sent.getTriggerValue();
            } else if(sent.getNegation()!= null && sent.getModifier()!=null && 
                    sent.getModifier().equals("mai")) {
                score = sent.getTriggerValue();
            } else if(sent.getNegation()== null && sent.getModifier()!=null && 
                    sent.getModifier().equals("mai")) {
                score = 14/10 * sent.getTriggerValue();
            } else if(sent.getNegation()!= null && sent.getModifier()== null) {
                score = 2/10 * sent.getTriggerValue();
            } else {
                score = sent.getTriggerValue();
            }
        
        weightS_ += score;
        }
        if(numberOfSegments > 0) {
            weightS_ /= numberOfSegments;
        } else {
            weightS_ = 0;
        }
        
    }
    
    public double getWeightS() {
        return weightS_;
    }
    
    private String normalizedSentence_;
    private String sentence_;
    private Set<String> affects_;
    private Set<String> entities_;
    private Set<SentimentSegment> sentimentSegments_;
    double weightS_;
    String document_;
}
