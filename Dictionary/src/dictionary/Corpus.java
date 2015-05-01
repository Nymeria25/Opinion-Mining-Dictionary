/*
 * This class maintains the corpus of the Dictionary. 
 * Holds the three input documents and offers ways
 * of chunking the documents.
 */
package dictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 *
 * @author Lavinia
 */
public class Corpus {

   
    /**
     * 
     * @param fileName1 Is the name of the first input file.
     * @param fileName2 Is the name of the second input file.
     * @param fileName3 Is the name of the third input file.
     * @throws FileNotFoundException If one of these files is not found.
     * 
     * Initializes the file inputs for the corpus.
     */
    public Corpus(String fileName1, String fileName2, String fileName3) 
            throws FileNotFoundException {
        
        inputFile1 = fileName1;
        inputFile2 = fileName2;
        inputFile3 = fileName3;
        
        InputStream fis = new FileInputStream(fileName1);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        corpusBufferedReader = new BufferedReader(isr);
        stringBuilder = new StringBuilder();
        
        /**
        * Use these to check whether we reached the second and the third document.
        */
        reachedTheSecondDocument = false;
        reachedTheThirdDocument = false;
    }
    

    /**
     * 
     * @param splitKeyword Is the keyword use to split the documents.
     *      If it is not present, return the entire chunk of document.
     * @return String representing a chunk of the document, of null if 
     *      we reached the end of all three documents.
     * @throws IOException 
     */
    public String Split(String splitKeyword) throws IOException {
        
        /**
        * Make sure the stringBuilder is empty before we start filling it.
        */
        stringBuilder.setLength(0);
        String line = "";
        String separator = "\n";
        InputStreamReader isr;
        InputStream fis;
        
         while ((line = corpusBufferedReader.readLine()) != null) {
             stringBuilder.append(line);
             stringBuilder.append(separator);
            if(line.contains(splitKeyword)) { 
                return stringBuilder.toString();
            }
                       
        }
         
        if(stringBuilder.length() > 0) return stringBuilder.toString();
        /**
         * If we reached the end of the first document, we keep trying
         * with the next two, until all documents are split.
         */
         if(reachedTheSecondDocument == false) {
            fis = new FileInputStream(inputFile2);
            isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            corpusBufferedReader = new BufferedReader(isr);
            reachedTheSecondDocument = true;
            return Split(splitKeyword);
         } else if (reachedTheThirdDocument == false) {
            fis = new FileInputStream(inputFile3);
            isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            corpusBufferedReader = new BufferedReader(isr);
            reachedTheThirdDocument = true;
            return Split(splitKeyword);
         } else return null;  
        
 
    }
    

    private final String inputFile1, inputFile2, inputFile3;
    private BufferedReader corpusBufferedReader;
    private final StringBuilder stringBuilder;
    private boolean reachedTheSecondDocument, reachedTheThirdDocument;
}
