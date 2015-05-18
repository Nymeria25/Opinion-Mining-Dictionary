/*
 * Dictionary tool class, for refining the results.
 * Used e.g. to compute the sentiment axes for each word
 * in the dictionary.
 */
package dictionarytools;

import data.RoWordNet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Lavinia
 */

public class DictionaryTools {
    
    public DictionaryTools() {
        axes_ = new HashSet();
        stringBuilder_ = new StringBuilder();
        affectDictionary_ = new HashSet();
    }
    
    public void ReadAffectFile(String fileName) throws FileNotFoundException, IOException {
        InputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        corpusBufferedReader_ = new BufferedReader(isr);

        String line = "";
        String[] tokens;
        SentimentAxe axe;
        while ((line = corpusBufferedReader_.readLine()) != null) {
            tokens = line.split(" ");
            axe = new SentimentAxe(tokens[0], tokens[1]);
            axes_.add(axe);
        }

    }
    
    public void Read(String fileName)
            throws FileNotFoundException, IOException {
        InputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        corpusBufferedReader_ = new BufferedReader(isr);

        stringBuilder_.setLength(0);
        String line = "", lemma;
        String[] components;
        double score;

        while ((line = corpusBufferedReader_.readLine()) != null) {
            components = line.split(" ");
            lemma = components[0];
            score = Double.valueOf(components[1]);
            System.out.println(score);
        }
        }
    public void ReadScoresFiles(String fileName1, String fileName2)
            throws FileNotFoundException, IOException {

        Read(fileName1);
        Read(fileName2);
    }
    
     public void WriteToFiles() throws IOException {
        System.out.println("No of axes = " + axes_.size());

     }
    
    private BufferedWriter writerForAffect_;
    private BufferedReader corpusBufferedReader_;
     private final StringBuilder stringBuilder_;
    private RoWordNet roWordNet_;
    private Set<SentimentAxe> axes_;
    private Set<WordSense> affectDictionary_;
    
}
