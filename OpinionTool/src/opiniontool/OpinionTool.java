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
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Lavinia
 */
public class OpinionTool {

    public OpinionTool() {
        stringBuilder = new StringBuilder();
        sentences1_ = new ArrayList();
        sentences2_ = new ArrayList();
        sentences3_ = new ArrayList();
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
        for(String keyword : keywords) {
            if(line.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void Read(String fileName, HashSet<String> keywords, int index) 
            throws FileNotFoundException, IOException {
        InputStream fis = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        corpusBufferedReader = new BufferedReader(isr);
        
        stringBuilder.setLength(0);
        String line = "";
        String[] sentences;
        
         while ((line = corpusBufferedReader.readLine()) != null) {
            if(HasKeyword(line, keywords)) {
                sentences = line.split("(?<=[.!?])\\s* ");
                if(index == 1) {
                    sentences1_.addAll(Arrays.asList(sentences));
                } else if(index == 2) {
                    sentences2_.addAll(Arrays.asList(sentences));
                } else {
                    sentences3_.addAll(Arrays.asList(sentences));
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
    
    public void ExtractOpinionsWithKeywords(HashSet<String> keywords) 
            throws FileNotFoundException, IOException {
        InitializeBufferedWriters("sentence_costin.txt", "sentence_cantemir.txt",
                "sentence_ureche.txt");
        ReadFiles("costin.txt","cantemir.txt","ureche.txt", keywords);
    }
    
    public void WriteToFiles() throws IOException {
        System.out.println("No of sentences 1 = " + sentences1_.size());
        System.out.println("No of sentences 2 = " + sentences2_.size());
        System.out.println("No of sentences 3 = " + sentences3_.size());
        
        Iterator iterator = sentences1_.iterator();
        String newLine = "\n";
        while (iterator.hasNext()) {
            writerForFile1_.write((String) iterator.next());
            writerForFile1_.write(newLine);
        }
        
        iterator = sentences2_.iterator();
        while (iterator.hasNext()) {
            writerForFile2_.write((String) iterator.next());
            writerForFile2_.write(newLine);
        }
        
        iterator = sentences3_.iterator();
        while (iterator.hasNext()) {
            writerForFile3_.write((String) iterator.next());
            writerForFile3_.write(newLine);
        }
    }
    
    private BufferedWriter writerForExtraction_;
    private BufferedWriter writerForFile1_;
    private BufferedWriter writerForFile2_;
    private BufferedWriter writerForFile3_;
    private BufferedReader corpusBufferedReader;
    private final StringBuilder stringBuilder;
    private ArrayList<String> sentences1_;
    private ArrayList<String> sentences2_;
    private ArrayList<String> sentences3_;
    
}
