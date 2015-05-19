/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opiniontool;

/**
 *
 * @author Lavinia
 */
public class SentimentSegment {
    
    public SentimentSegment(String negation, String modifier, String trigger, 
            double triggerValue) {
        negation_ = negation;
        modifier_ = modifier;
        trigger_ = trigger;
        triggerValue_ = triggerValue;
    }
    
    public String getNegation() {
        return negation_;
    }
    
    public double getTriggerValue() {
        return triggerValue_;
    }
    
    public String getModifier() {
        return modifier_;
    }
    
    public String getTrigger() {
        return trigger_;
    }
    
    @Override
    public String toString() {
        return "( " + negation_ + ", " + modifier_ + ", " + trigger_ + ") ";
    }
    
   private String negation_, modifier_, trigger_;
   private double triggerValue_;
}
