/*
 * Here is the main function of the DictionaryTools,
 * that will create and a DictionaryTools object, in order to refine the results
 * computed in the Dictionary.
 */
package dictionarytools;

import java.io.IOException;

/**
 *
 * @author Lavinia
 */
public class BuildDictionaryTools {
    
    public static void main(String[] args) throws IOException {
        DictionaryTools tool = new DictionaryTools();
        tool.ReadAffectFile("axes.txt");
        tool.ReadScoresFiles("adjectives_scores.txt", "verbs_scores.txt");
        tool.WriteToFiles();
    }
    
}
