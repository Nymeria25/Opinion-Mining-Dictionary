/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opiniontool;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Lavinia
 */



public class BuildOpinionTool {
    
    public static Set<String> PopulateKeys() {
        Set<String> keys = new HashSet();
        keys.add("otoman");
        keys.add("turc");
        keys.add("Ștefan vodă");
        keys.add("dachi");
        return keys;    
}
    
    public static void main(String[] args) throws IOException {
        
        OpinionTool opinionTool = new OpinionTool("normalization.txt");
        Set<String> keys = PopulateKeys();
        opinionTool.ExtractSentencesWithKeywords((HashSet<String>) keys);
        opinionTool.NormalizeSentences();
        opinionTool.DetermineEntitiesInOpinions();
        opinionTool.WriteToFiles();
    }
}
