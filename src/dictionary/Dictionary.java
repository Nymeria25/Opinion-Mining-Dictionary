/*
 * Dictionary is the class containing the lists of lemmas clustered by POS,
 * unrecognized words, proper nouns and metrics for all the computed glossaries.
 */
package dictionary;

import data.Literal;
import data.RoWordNet;
import data.Synset;
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
     * @param adjectivesFile String, the name of the adjectives file.
     * @param adverbsFile String, the name of the adverbs file.
     * @param verbsFile String, the name of the verbs file.
     * @param properNounsFile String, the name of the proper nouns file.
     * @param unrecognizedWordsFile String, the name of the unrecognized
     * words file.
     * @throws FileNotFoundException
     */
    public Dictionary(Corpus corpus, String adjectivesFile, String adverbsFile,
            String verbsFile, String properNounsFile, String unrecognizedWordsFile)
            throws FileNotFoundException, Exception {

        corpus_ = corpus;
        InitializeBufferedWriters(adjectivesFile, adverbsFile, verbsFile,
                properNounsFile, unrecognizedWordsFile);
        AllocateMembers();
        InitializeMetricsValues(); 
    }
    
    private void FilterByPolarity(Iterator<String> iterator) {
        String word;

        while (iterator.hasNext()) {
            word = iterator.next();
            if(!MayHoldPolarity(word)) {
                iterator.remove();
            }
        }
    }
    public void ApplyWordNetFiltering() {
        
        Iterator<String> iterator = adjectives_.iterator();
        FilterByPolarity(iterator);
        
        iterator = adverbs_.iterator();
        FilterByPolarity(iterator);
        
        iterator = verbs_.iterator();
        FilterByPolarity(iterator);
        
        iterator = nouns_.iterator();
        FilterByPolarity(iterator);
    }
    
    private boolean MayHoldPolarity(String lemma) {
        String domain = getWordNetDomain(lemma);
        
        if(domain == null ) { //|| domain.contentEquals("")) {
            return true;
        } else if(domain.contentEquals("factotum") || domain.
                contentEquals("quality") || domain.contentEquals("person")) {
            return true;
        } else if(domain.contentEquals("sociology") || domain.
                contentEquals("law") || domain.contentEquals("betting") || 
                domain.contentEquals("enterprise")) {
            return true;
        }
        if(domain.contentEquals("")) {
            noWordsWithNoWordnetDomain_++;
        }
        return false;
    }
    
   

    public void POSTag(String fileChunk) throws IOException {
        // Use a Set for ensuring there are no multiple appearances
        // of the same word.
        Set words = GetSetOfWordsFromString(fileChunk);
        totalNoWords_ += words.size();

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

        ApplyFiltersAndNormalizationOnUnrecognizedWords();
        ApplyWordNetFiltering();
    }

    /**
     * This function should be called after the POS Tagging is done. It takes
     * the list of unrecognized words and tries to normalize the words in order
     * to be recognized by the LanguageTagger. All the identified words will be
     * moved from the unrecognized list to the list of which it belongs.
     * @throws java.io.IOException
     */
    public void ApplyFiltersAndNormalizationOnUnrecognizedWords()
            throws IOException {
        Set<String> normalizedWords = new HashSet<>();
        Iterator<String> iterator = unrecognizedWords_.iterator();

        String normalizedWord;

        while (iterator.hasNext()) {
            String word = iterator.next();
            normalizedWord = NormalizeIfHyphenatedWord(word);
            normalizedWord = StripAccentsOfWord(normalizedWord);
            normalizedWord = RemoveFinalUInWord(normalizedWord);
            normalizedWord = DzGroupToZInWord(normalizedWord);
            normalizedWord = IiGroupToIInWord(normalizedWord);
            
            normalizedWords.add(normalizedWord);
            iterator.remove();
        }
        
        unrecognizedWords_.addAll(normalizedWords);

        ExtractProperNouns();
        POSTagUnrecognizedWords();

    }
    
     public void writeDictionaryToFiles() throws IOException {
        Iterator iterator = adjectives_.iterator();
        String newLine = "\n";
        System.out.println("No of adjectives = " + adjectives_.size());
        System.out.println("No of adverbs = " + adverbs_.size());
        System.out.println("No of verbs = " + verbs_.size());
        System.out.println("No of common nouns = " + nouns_.size());
        System.out.println("No of proper nouns = " + properNouns_.size());
        System.out.println("No of unrecognized words = " + unrecognizedWords_.size());
        System.out.println("No of words without WordNet domain information = " + 
                noWordsWithNoWordnetDomain_);
        System.out.println("Total no of words (singular occurence) = " + totalNoWords_);

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
    
     private void InitializeMetricsValues() {
        totalNoWords_ = 0;
        noWordsWithNoWordnetDomain_ = 0;
    }
     
    public String getWordNetDomain(String word) {
        Literal literal = new Literal(word);
        Synset firstSynset;
        String result = "";
                
        if( roWordNet_.getSynsetsFromLiteral(literal).size() > 0) {
            firstSynset = roWordNet_.getSynsetsFromLiteral(literal).get(0);
            if(firstSynset != null) {
                result = firstSynset.getDomain();
            }
        }
        
        return result;
    }
    
    private void AllocateMembers() throws Exception {
        romanianTagger_ = new RomanianTagger();
        roWordNet_ = RoWordNet.deserializeFromFile("wordnet-resources\\RoWordNet.data");
        adjectives_ = new HashSet();
        adverbs_ = new HashSet();
        verbs_ = new HashSet();
        unrecognizedWords_ = new HashSet();
        properNouns_ = new HashSet();
        nouns_ = new HashSet();
    }
    
    private void InitializeBufferedWriters(String adjectivesFile, String adverbsFile,
            String verbsFile, String properNounsFile, String unrecognizedWordsFile) 
            throws FileNotFoundException {
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
    }

    private String NormalizeIfHyphenatedWord(String word) {
        String[] splitWord;
        if (word.contains("­")) {
            splitWord = word.split("[\\s­]");
            if (splitWord.length < 2) {
                return splitWord[0];
            } else {
                return (splitWord[0] + splitWord[1]);
            }
        } else {
            return word;
        }
    }

    private String StripAccentsOfWord(String word) {
        String strippedWord = "";
        boolean stripped = false;

        for (int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            if (letter == 'ŭ') {
                stripped = true;
                strippedWord += "u";
            } else if (letter == 'é' || letter == 'è') {
                stripped = true;
                strippedWord += 'e';
            } else {
                strippedWord += letter;
            }
        }

        if (stripped) {
            return strippedWord;
        } else {
            return word;
        }
    }

    private String RemoveFinalUInWord(String word) {
        if (word.endsWith("u") && word.length() > 2) {
            if (HasConsonantBeforeLastLetter(word)) {
                return word.substring(0, word.length() - 1);
            }
        }
        return word;
    }

    private String DzGroupToZInWord(String word) {
        int index;

        if (word.contains("dz")) {
            index = word.indexOf("dz");
            return (word.substring(0, index) + word.substring(
                    index + 1, word.length()));
        } else {
            return word;
        }
    }
    
    private String IiGroupToIInWord(String word) {
        int index;

        if (word.contains("ii")) {
            index = word.indexOf("ii");
            return (word.substring(0, index) + word.substring(
                    index + 1, word.length()));
        } else {
            return word;
        }
    }

    private void POSTagUnrecognizedWords() throws IOException {
        String chunk = unrecognizedWords_.toString();
        unrecognizedWords_.clear();
        this.POSTag(chunk);
    }

    private boolean HasConsonantBeforeLastLetter(String word) {
        String vowels = "aeiouăâ";
        char letter = word.charAt(word.length() - 2);

        if (vowels.contains(String.valueOf(letter))) {
            return false;
        }

        return true;
    }

    private void ExtractProperNouns() {
        for (String word : unrecognizedWords_) {
            if (Character.isUpperCase(word.charAt(0))) {
                properNouns_.add(word);
            }
        }

        for (String word : properNouns_) {
            unrecognizedWords_.remove(word);
        }
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
        } else if (isNoun(token)){
            nouns_.add(token.getLemma());
           // System.out.println(token.getLemma() + " : " + getWordNetDomain(token.getLemma()));
        }
        else {
            return false;
        }
        return true;
    }

    public boolean isAdjective(AnalyzedToken token) {
        String posTag = token.getPOSTag();
        return posTag.startsWith("A");
    }

    public boolean isAdverb(AnalyzedToken token) {
        String posTag = token.getPOSTag();
        // TODO: make sure it is correct!!
        return posTag.startsWith("G");
    }

    public boolean isVerb(AnalyzedToken token) {
        String posTag = token.getPOSTag();
        return posTag.startsWith("V");
    }
    
    public boolean isNoun(AnalyzedToken token) {
        String posTag = token.getPOSTag();
        return posTag.startsWith("S");
    }


    public Set GetSetOfWordsFromString(String fileChunk) {
        Set words = new HashSet();
        String[] tokens = fileChunk.split("[\\s.,!:;”„\"()-?']");

        for (String token : tokens) {
            if (token.length() > 1) {
                words.add(token);
            }
        }
        return words;
    }

    private Set<String> adjectives_;
    private Set<String> adverbs_;
    private Set<String> verbs_;
    private Set<String> unrecognizedWords_;
    private Set<String> properNouns_;
    private Set<String> nouns_;
    private BufferedWriter writerForAdjectives_;
    private BufferedWriter writerForAdverbs_;
    private BufferedWriter writerForVerbs_;
    private BufferedWriter writerForProperNouns_;
    private BufferedWriter writerForUnrecognizedWords_;
    private RomanianTagger romanianTagger_;
    private RoWordNet roWordNet_;
    private final Corpus corpus_;
    private int totalNoWords_;
    private int noWordsWithNoWordnetDomain_;
}
