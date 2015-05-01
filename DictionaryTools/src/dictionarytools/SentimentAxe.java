/*
 * A sentiment axe consists of two words, describing a larger direction ( a
 * cluster) of emotions.
 * 
 */
package dictionarytools;


/**
 *
 * @author Lavinia
 */
public class SentimentAxe {
    
    public SentimentAxe() {
        x_ = y_ = "";
    }
    
    public SentimentAxe(String sense1, String sense2) {
        x_ = sense1;
        y_ = sense2;
    }
    
    public String getX() {
        return x_;
    }
    
    public void setX(String sense) {
        x_ = sense;
    }
    
    public String getY() {
        return y_;
    }
    
    public void setY(String sense) {
        y_ = sense;
    }
    
    private String x_;
    private String y_;
}
