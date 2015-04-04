/*
 * This is the main function of the Dictionary,
 * that will create and test a dictionary with scores,
 * based on the pre-processing of the 3 documents.
 */
package dictionary;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lavinia
 */
public class BuildDictionary {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Corpus corp = new Corpus("cantemir.txt","ureche.txt","costin.txt");
            testCorpusOutput(corp);
            
        } catch (IOException ex) {
            Logger.getLogger(BuildDictionary.class.getName()).log(Level.SEVERE, null, ex);
        }
      
    }
    
    
     public static void testCorpusOutput(Corpus corp) throws UnsupportedEncodingException, 
             FileNotFoundException, IOException {
               
         String test = "";
         int i=0;
         
         OutputStream fis = new FileOutputStream("result.txt");
         OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                 fis, Charset.forName("UTF-8"));
         BufferedWriter writer = new BufferedWriter(outputStreamWriter);
     
            
         while(true) {
            i++;
            try {
                test = corp.Split("CAP.");
               // if(test == null) break;
                
             } catch (IOException ex) {
                Logger.getLogger(BuildDictionary.class.getName()).log(Level.SEVERE, null, ex);
             }
                System.out.println(test);
                
                if(test!= null) writer.write(test);
		
                 if(i == 2) break;
                
            }
         
         writer.close();
         
    }
    
}
