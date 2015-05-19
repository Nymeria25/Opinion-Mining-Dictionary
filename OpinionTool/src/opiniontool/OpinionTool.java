/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opiniontool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.tagging.ro.RomanianTagger;

/**
 *
 * @author Lavinia
 */
public class OpinionTool {

    public OpinionTool(String normFile, String lexFile) throws IOException {
        stringBuilder_ = new StringBuilder();
        sentences1_ = new ArrayList();
        sentences2_ = new ArrayList();
        sentences3_ = new ArrayList();
        normalizations_ = new HashMap();
        opinions1_ = new ArrayList();
        opinions2_ = new ArrayList();
        opinions3_ = new ArrayList();
        romanianTagger_ = new RomanianTagger();
        properNouns_ = new HashSet();
        lexicon_ = new HashMap();

        ReadNormalizedFile(normFile);
        ReadLexiconFile(lexFile);
    }
    
    void ReadLexiconFile(String fileName) throws FileNotFoundException, IOException {
        InputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        corpusBufferedReader_ = new BufferedReader(isr);

        String line = "";
        String[] tokens;
        double score;
        while ((line = corpusBufferedReader_.readLine()) != null) {
            tokens = line.split(" ");
            score = Double.valueOf(tokens[1]);
            lexicon_.put(tokens[0], score);
        }

    }

    void ReadNormalizedFile(String fileName) throws FileNotFoundException, IOException {
        InputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        corpusBufferedReader_ = new BufferedReader(isr);

        String line = "";
        String[] tokens;
        while ((line = corpusBufferedReader_.readLine()) != null) {
            tokens = line.split(" ");
            normalizations_.put(tokens[0], tokens[1]);
        }

    }

    private void InitializeBufferedWriters(String file1, String file2,
            String file3) throws FileNotFoundException {
        OutputStream stream = new FileOutputStream(file1);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                stream, Charset.forName("UTF-8"));
        writerForFile1_ = new BufferedWriter(outputStreamWriter);

        stream = new FileOutputStream(file2);
        outputStreamWriter = new OutputStreamWriter(
                stream, Charset.forName("UTF-8"));
        writerForFile2_ = new BufferedWriter(outputStreamWriter);

        stream = new FileOutputStream(file3);
        outputStreamWriter = new OutputStreamWriter(
                stream, Charset.forName("UTF-8"));
        writerForFile3_ = new BufferedWriter(outputStreamWriter);
    }

    public boolean HasKeyword(String line, HashSet<String> keywords) {
        for (String keyword : keywords) {
            if (line.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    public void Read(String fileName, HashSet<String> keywords, int index)
            throws FileNotFoundException, IOException {
        InputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        corpusBufferedReader_ = new BufferedReader(isr);

        stringBuilder_.setLength(0);
        String line = "";
        String[] sentences;

        while ((line = corpusBufferedReader_.readLine()) != null) {
            sentences = line.split("(?<=[.!?])\\s* ");
            for (String sentence : sentences) {
                if (HasKeyword(sentence, keywords)) {
                    if (index == 1) {
                        sentences1_.addAll(Arrays.asList(sentence));
                    } else if (index == 2) {
                        sentences2_.addAll(Arrays.asList(sentence));
                    } else {
                        sentences3_.addAll(Arrays.asList(sentence));
                    }
                }
            }

        }
    }

    public void ReadFiles(String fileName1, String fileName2, String fileName3,
            HashSet<String> keywords)
            throws FileNotFoundException, IOException {

        Read(fileName1, keywords, 1);
        Read(fileName2, keywords, 2);
        Read(fileName3, keywords, 3);
    }

    public void ExtractSentencesWithKeywords(HashSet<String> keywords)
            throws FileNotFoundException, IOException {
        InitializeBufferedWriters("sentence_costin.txt", "sentence_cantemir.txt",
                "sentence_ureche.txt");
        ReadFiles("costin.txt", "cantemir.txt", "ureche.txt", keywords);
    }

    public void NormalizeSentences() throws IOException {
        NormalizeSentencesImplementation(sentences1_);
        NormalizeSentencesImplementation(sentences2_);
        NormalizeSentencesImplementation(sentences3_);
    }

    public void DetermineEntitiesInOpinions() {
        DetermineEntitiesInOpinionsImplementation(opinions1_);
        DetermineEntitiesInOpinionsImplementation(opinions2_);
        DetermineEntitiesInOpinionsImplementation(opinions3_);
    }

    public void ComputeSentimentSegmentsInOpinions() {
        ComputeSentimentSegmentsInOpinionsImplementation(opinions1_,1);
        ComputeSentimentSegmentsInOpinionsImplementation(opinions2_,2);
        ComputeSentimentSegmentsInOpinionsImplementation(opinions3_,3);
    }

    public void WriteToFiles() throws IOException {
        System.out.println("No of sentences 1 = " + opinions1_.size());
        System.out.println("No of sentences 2 = " + opinions2_.size());
        System.out.println("No of sentences 3 = " + opinions3_.size());

        String newLine = "\n";

        for (Opinion opinion : opinions1_) {
            writerForFile1_.write(opinion.getSentence());
            writerForFile1_.write(newLine + "Normalized Sentence [ ");
            writerForFile1_.write(opinion.getNormalizedSentence());
            writerForFile1_.write("]");
            writerForFile1_.write(newLine + "Entities ");
            writerForFile1_.write(opinion.getEntities().toString());
            writerForFile1_.write(newLine + "Sentiment Segments ");
            writerForFile1_.write(opinion.getSegments().toString());
            writerForFile1_.write(newLine + newLine + newLine);
        }
        
        writerForFile1_.close();

    }

    public Set<SentimentSegment> ComputeSentimentSegmentFromOpinion(Opinion opinion) {

        Set<SentimentSegment> segmentSet = new HashSet();
        String normalizedSentence = opinion.getNormalizedSentence();
        String words[] = normalizedSentence.split("[\\s.,!:;”„\"()-?']");
        SentimentSegment segment;

        for (int iter = 0; iter < words.length; iter++) {
            if (lexicon_.containsKey(words[iter])) {
                // Check for negation and modifiers
                if (iter >= 3 && words[iter - 1].equals("mai") && (words[iter - 2].
                        equals("cel") || (words[iter - 2].equals("cea")))
                        && words[iter - 3].equals("nu")) {
                    segment = new SentimentSegment("nu", "cel mai", words[iter]);
                } else if (iter >= 2 && words[iter - 1].equals("mai") && (words[iter - 2].
                        equals("cel") || (words[iter - 2].equals("cea")))) {
                    segment = new SentimentSegment(null, "cel mai", words[iter]);
                } else if (iter >= 2 && words[iter - 1].equals("mai")
                        && words[iter - 2].equals("nu")) {
                    segment = new SentimentSegment("nu", "mai", words[iter]);
                } else if (iter >= 1 && words[iter - 1].equals("mai")) {
                    segment = new SentimentSegment(null, "mai", words[iter]);
                } else if (iter >= 2 && words[iter - 2].equals("nu")) {
                    segment = new SentimentSegment("nu", null, words[iter]);
                } else if (iter >= 3 && words[iter - 3].equals("nu")) {
                    segment = new SentimentSegment("nu", null, words[iter]);
                } else {
                    segment = new SentimentSegment(null, null, words[iter]);
                }
                
             segmentSet.add(segment);
            }
            
        }

        return segmentSet;
    }

    private void ComputeSentimentSegmentsInOpinionsImplementation(
            ArrayList<Opinion> opinions, int index) {

        Iterator<Opinion> iterator = opinions.iterator();
        Opinion opinion;
        Set<Opinion> newOpinions = new HashSet();
        Set<SentimentSegment> sent;

        while (iterator.hasNext()) {
            opinion = iterator.next();
            sent = ComputeSentimentSegmentFromOpinion(opinion);
            opinion.addSentimentSegments(sent);
            newOpinions.add(opinion);
            iterator.remove();
        }
        
        if(index == 1) {
            opinions1_.addAll(newOpinions);
        } else if(index == 2) {
            opinions2_.addAll(newOpinions);
        } else {
            opinions3_.addAll(newOpinions);
        }
    }

    private void DetermineEntitiesInOpinionsImplementation(ArrayList<Opinion> opinions) {
        String[] words;
        String entity = "";
        Set<String> entities;
        int pos = -1;

        for (Opinion opinion : opinions) {
            pos++;
            words = opinion.getSentence().split("[\\s.,!:;”„\"()-?']");
            entities = new HashSet();
            for (int iter = 0; iter < words.length; iter++) {
                if (!words[iter].isEmpty()) {
                    if (iter == 0) {
                        if (properNouns_.contains(words[iter])) {
                            entity = words[iter];
                        }
                    } else if (!words[iter - 1].isEmpty() && Character.
                            isUpperCase(words[iter - 1].charAt(0))
                            && Character.isUpperCase(words[iter].charAt(0))) {

                        entity += " " + words[iter];
                    } else if (Character.isUpperCase(words[iter].charAt(0))) {
                        entity += words[iter];
                    } else if (Character.isLowerCase(words[iter].charAt(0))
                            && !entity.equals("")) {
                        entities.add(entity);
                        entity = "";
                    }
                }

            }

            opinion.setEntities(entities);
            if (opinions == opinions1_) {
                opinions1_.set(pos, opinion);
            } else if (opinions == opinions2_) {
                opinions2_.set(pos, opinion);
            } else {
                opinions3_.set(pos, opinion);
            }

        }

    }

    private String TryToReTag(String word) throws IOException {
        // read from normalized file, try to find the word there, then do a retag

        Iterator iterator = normalizations_.entrySet().iterator();
        Map.Entry pair;
        while (iterator.hasNext()) {
            pair = (Map.Entry) iterator.next();
            if (pair.getKey().equals(word)) {
                // try to retag the found modern correspondent
                List<AnalyzedTokenReadings> wordTags;
                List<String> listOfWords = new ArrayList();
                listOfWords.add((String) pair.getValue());
                wordTags = romanianTagger_.tag(listOfWords);

                for (AnalyzedTokenReadings analyzedTokenReadings : wordTags) {
                    for (AnalyzedToken token : analyzedTokenReadings) {
                        return token.getLemma();
                    }
                }

            }
        }
        return "";
    }

    private String NormalizeSentenceFromWordTags(List<AnalyzedTokenReadings> wordTags) throws IOException {
        String normalizedSentence = "";
        String retaggedLemma = "";

        for (AnalyzedTokenReadings analyzedTokenReadings : wordTags) {
            for (AnalyzedToken token : analyzedTokenReadings) {
                String tokenPOSTag = token.getPOSTag();
                if (tokenPOSTag == null) {
                    retaggedLemma = TryToReTag(token.getTokenInflected());
                    if (retaggedLemma != null) {
                        normalizedSentence += retaggedLemma + " ";
                        break;
                    }
                } else {
                    normalizedSentence += token.getLemma() + " ";
                    break;
                }
            }
        }

        normalizedSentence += ".";
        return normalizedSentence;
    }

    private void NormalizeSentencesImplementation(ArrayList<String> sentences) throws IOException {
        Opinion opinion;
        List<String> listOfWords;
        List<AnalyzedTokenReadings> wordTags;
        String normalizedSentence;

        String[] words;

        for (String s : sentences) {
            opinion = new Opinion();
            words = s.split("[\\s.,!:;”„\"()-?']");
            listOfWords = Arrays.asList(words);
            wordTags = romanianTagger_.tag(listOfWords);

            normalizedSentence = NormalizeSentenceFromWordTags(wordTags);
            opinion.setNormalizedSentence(normalizedSentence);
            opinion.setSentence(s);
            if (sentences == sentences1_) {
                opinions1_.add(opinion);
            } else if (sentences == sentences2_) {
                opinions2_.add(opinion);
            } else {
                opinions3_.add(opinion);
            }
        }

    }

    private BufferedWriter writerForFile1_;
    private BufferedWriter writerForFile2_;
    private BufferedWriter writerForFile3_;
    private BufferedReader corpusBufferedReader_;
    private final StringBuilder stringBuilder_;
    private ArrayList<String> sentences1_;
    private ArrayList<String> sentences2_;
    private ArrayList<String> sentences3_;
    private ArrayList<Opinion> opinions1_;
    private ArrayList<Opinion> opinions2_;
    private ArrayList<Opinion> opinions3_;
    private RomanianTagger romanianTagger_;
    private Map<String, String> normalizations_;
    private Set<String> properNouns_;
    private Map<String,Double> lexicon_;

}
