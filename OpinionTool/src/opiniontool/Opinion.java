/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opiniontool;

import java.util.Set;

/**
 *
 * @author Lavinia
 */
public class Opinion {
    
    private String sentence_;
    private Set<String> affects_;
    private Set<String> entities_;
    private Set<SentimentSegment> sentimentSegments_;
    double weightS_;
}
