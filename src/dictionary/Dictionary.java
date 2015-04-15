/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dictionary;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.tagging.ro.RomanianTagger;

/**
 *
 * @author Lavinia
 */
public class Dictionary {

    /**
     *
     * @param corpus is the Corpus on top of which we build the dictionary.
     * @param adjectivesFile is a String, the name of the adjectives file.
     * @param adverbsFile is a String, the name of the adverbs file.
     * @param verbsFile is a String, the name of the verbs file.
     * @param unrecognizedWordsFile is a String, the name of the unrecognized
     * words file.
     * @throws FileNotFoundException
     */
    public Dictionary(Corpus corpus, String adjectivesFile, String adverbsFile,
            String verbsFile, String properNounsFile, String unrecognizedWordsFile)
            throws FileNotFoundException {

        corpus_ = corpus;
        OutputStream stream = new FileOutputStream(adjectivesFile);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                stream, Charset.forName("UTF-8"));
        writerForAdjectives_ = new BufferedWriter(outputStreamWriter);

        stream = new FileOutputStream(adverbsFile);
        outputStreamWriter = new OutputStreamWriter(
                stream, Charset.forName("UTF-8"));
        writerForAdverbs_ = new BufferedWriter(outputStreamWriter);

        stream = new FileOutputStream(verbsFile);
        outputStreamWriter = new OutputStreamWriter(
                stream, Charset.forName("UTF-8"));
        writerForVerbs_ = new BufferedWriter(outputStreamWriter);
        
        stream = new FileOutputStream(properNounsFile);
        outputStreamWriter = new OutputStreamWriter(
                stream, Charset.forName("UTF-8"));
        writerForProperNouns_ = new BufferedWriter(outputStreamWriter);

        stream = new FileOutputStream(unrecognizedWordsFile);
        outputStreamWriter = new OutputStreamWriter(
                stream, Charset.forName("UTF-8"));
        writerForUnrecognizedWords_ = new BufferedWriter(outputStreamWriter);

        romanianTagger_ = new RomanianTagger();
        adjectives_ = new HashSet();
        adverbs_ = new HashSet();
        verbs_ = new HashSet();
        unrecognizedWords_ = new HashSet();
        properNouns_ = new HashSet();
    }

    public void POSTag(String fileChunk) throws IOException {
        // Use a Set for ensuring there are no multiple appearances
        // of the same word.
        Set words = GetSetOfWordsFromString(fileChunk);

        // The tag function of the RomanianTagger requires a List.
        List<String> listOfWords = new ArrayList<>(words);
        List<AnalyzedTokenReadings> wordTags = romanianTagger_.tag(listOfWords);

        // Add the words to the adjectives, adverbs, verbs, unrec. lists,
        // based on POS.
        ClusterByPOSCategory(wordTags);

    }

    public void CreateDictionary() throws IOException {
        String chunk = "";
        do {
            chunk = corpus_.Split("CAP.");
            if (chunk != null) {
                this.POSTag(chunk);
            }
        } while (chunk != null);
        
       // ApplyFiltersAndNormalizationOnUnrecognizedWords();
    }
    /**
     * This function should be called after the POS Tagging is done.
     * It takes the list of unrecognized words and tries to normalize
     * the words in order to be recognized by the LanguageTagger.
     * All the identified words will be moved from the unrecognized list
     * to the list of which it belongs.
     */
    public void ApplyFiltersAndNormalizationOnUnrecognizedWords() {
        ExtractProperNouns();
    }
    
    private void ExtractProperNouns() {
        for(String word : unrecognizedWords_) {
            if(Character.isUpperCase(word.charAt(0))) {
                properNouns_.add(word);  
            }
        }
        
        for(String word : properNouns_) {
            unrecognizedWords_.remove(word);
        }
    }

    public void writeDictionaryToFiles() throws IOException {
        Iterator iterator = adjectives_.iterator();
        String newLine = "\n";
        System.out.println("No of adjectives = " + adjectives_.size());
        System.out.println("No of adverbs = " + adverbs_.size());
        System.out.println("No of verbs = " + verbs_.size());
        System.out.println("No of unrecognized words = " + unrecognizedWords_.size());

        // Write the Set of adjectives to the adjectives file.
        while (iterator.hasNext()) {
            writerForAdjectives_.write((String) iterator.next());
            writerForAdjectives_.write(newLine);
        }

        // Write the Set of adverbs to the adverbs file.
        iterator = adverbs_.iterator();
        while (iterator.hasNext()) {
            writerForAdverbs_.write((String) iterator.next());
            writerForAdverbs_.write(newLine);
        }

        // Write the Set of verbs to the verbs file.
        iterator = verbs_.iterator();
        while (iterator.hasNext()) {
            writerForVerbs_.write((String) iterator.next());
            writerForVerbs_.write(newLine);
        }
        
        // Write the Set of proper nouns to the proper nouns file.
        iterator = properNouns_.iterator();
        while (iterator.hasNext()) {
            writerForProperNouns_.write((String) iterator.next());
            writerForProperNouns_.write(newLine);
        }


        // Write the Set of unrecognized words to the unrecognized words file.
        iterator = unrecognizedWords_.iterator();
        while (iterator.hasNext()) {
            writerForUnrecognizedWords_.write((String) iterator.next());
            writerForUnrecognizedWords_.write(newLine);
        }

        writerForAdjectives_.close();
        writerForAdverbs_.close();
        writerForVerbs_.close();
        writerForUnrecognizedWords_.close();
    }

    private void ClusterByPOSCategory(List<AnalyzedTokenReadings> wordTags) {

        for (AnalyzedTokenReadings analyzedTokenReadings : wordTags) {
            for (AnalyzedToken token : analyzedTokenReadings) {
                String tokenPOSTag = token.getPOSTag();
                if (tokenPOSTag == null) {
                    unrecognizedWords_.add(token.getTokenInflected());
                } else {
                    AddToPOSCluster(token);    
                }
            }
        }
    }

    private boolean AddToPOSCluster(AnalyzedToken token) {
        if (isAdjective(token)) {
            adjectives_.add(token.getLemma());
        } else if (isAdverb(token)) {
            adverbs_.add(token.getLemma());
        } else if (isVerb(token)) {
            verbs_.add(token.getLemma());
        } else {
            return false;
        }
        return true;
    }

    private boolean isAdjective(AnalyzedToken token) {
        String posTag = token.getPOSTag();
        return posTag.startsWith("A");
    }

    private boolean isAdverb(AnalyzedToken token) {
        String posTag = token.getPOSTag();
        // TODO: make sure it is correct!!
        return posTag.startsWith("G");
    }

    private boolean isVerb(AnalyzedToken token) {
        String posTag = token.getPOSTag();
        return posTag.startsWith("V");
    }

    private Set GetSetOfWordsFromString(String fileChunk) {
        Set words = new HashSet();
        String[] tokens = fileChunk.split("[\\s.,!:;”„\"()-?']");

        for (String token : tokens) {
            if (token.length() > 1) {
                words.add(token);
            }
        }
        return words;
    }

    private final Set <String> adjectives_;
    private final Set <String> adverbs_;
    private final Set <String> verbs_;
    private final Set <String> unrecognizedWords_;
    private final Set <String> properNouns_;
    private final BufferedWriter writerForAdjectives_;
    private final BufferedWriter writerForAdverbs_;
    private final BufferedWriter writerForVerbs_;
    private final BufferedWriter writerForProperNouns_;
    private final BufferedWriter writerForUnrecognizedWords_;
    private final RomanianTagger romanianTagger_;
    private final Corpus corpus_;
}
