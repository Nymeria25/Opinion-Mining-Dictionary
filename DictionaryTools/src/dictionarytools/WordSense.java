/*
 * WordSense is a wrapper for a lemma and a Set of sentiment axes matching that
 * lemma.
 */
package dictionarytools;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Lavinia
 */
public class WordSense {
    
    public WordSense(String lemma) {
        this.lemma_ = lemma;
        axes_ = new HashSet<>();
    }
    
    public String getLemma() {
        return lemma_;
    }
    
    public Set<SentimentAxe> getAxes() {
        return axes_;
    }
    
    public void addAxe(SentimentAxe axe) {
        this.axes_.add(axe);
    }
    
    private final String lemma_;
    Set<SentimentAxe> axes_;
}