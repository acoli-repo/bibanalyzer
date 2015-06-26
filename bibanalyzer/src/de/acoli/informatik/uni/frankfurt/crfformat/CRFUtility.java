/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.acoli.informatik.uni.frankfurt.crfformat;

import de.acoli.informatik.uni.frankfurt.processing.ReferenceUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * Utility class to tokenize input references (with or without annotations).
 *
 * @author niko
 */
public class CRFUtility {
    
    
    /**
     * 
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        
        String anUnannotatedRef = "W. C. Hawkes, D. S. Kelley, "
                + "and P. C. Taylor, "
                + "The effects of dietary selenium on the immune system in healthy men, "
                + "Biol. Trace Element Res. 81, 189–213 (2001).";
        tokenizeSinglePlaintextRef(anUnannotatedRef);
        
        
        String anAnnotatedRef = 
                "<Initials>W. C.</Initials> <FamilyName>Hawkes</FamilyName>, <Initials>D. S.</Initials> <FamilyName>Kelley</FamilyName>, "
                + "and <Initials>P. C.</Initials> <FamilyName>Taylor</FamilyName>, "
                + "<ArticleTitle>The effects of dietary selenium on the immune system in healthy men</ArticleTitle>, "
                + "<JournalTitle~Italic>Biol. Trace Element Res.</JournalTitle~Italic> <VolumeID~Bold>81</VolumeID~Bold>, "
                + "<FirstPage>189</FirstPage>–<LastPage>213</LastPage> (<Year>2001</Year>).";
        tokenizeSingleXMLAug(anAnnotatedRef);
        
        
        
    }
    
    
    /**
     * 
     * @param anAnnotatedRef
     * @throws FileNotFoundException 
     */
    public static void tokenizeSingleXMLAug(String anAnnotatedRef) throws FileNotFoundException {
          
        // Example 2:
        // -> Convert xml augmented (annotated) annotations to CRF format.
        // (can possibly contain font information which is separated by ~ [included within the tag]
        // and will be added as a feature to the CRF format.
        
        // A reference with font information (separated by ~ )
        System.out.println("Annotated tokens ****");
        
        System.out.println("Annotated: " + anAnnotatedRef);

        String substituted = anAnnotatedRef.replaceAll("<[^>]+>", "");
        System.out.println("Unannotated: " + substituted + "\n");

        PrintWriter w = new PrintWriter(new File("/home/niko/Desktop/out2.txt"));
        ArrayList<String> annotatedTokens = PlaintextReferenceStringToMalletCRFFormatConverter.convertReferenceString(anAnnotatedRef, w);
        System.out.println("Tokenized with annotations:");
        for(String t : annotatedTokens) {
            System.out.println(t);
        }
        w.close();
        System.out.println("# tokens with annotations: " + annotatedTokens.size());
        
    }
    
    
    
    
    /**
     * 
     * @param anUnannotatedRef 
     */
    public static void tokenizeSinglePlaintextRef(String anUnannotatedRef) {
        // Example 1:
        // -> Convert plaintext without annotations to CRF format.
        System.out.println("Unannotated tokens ****");
        
        System.out.println("Unannotated: " + anUnannotatedRef);
        ArrayList<String> tokens = ReferenceUtil.tokenize(anUnannotatedRef, true);
        System.out.println("Tokenized:");
        for (String t : tokens) {
            System.out.println(t);
        }
        System.out.println("# tokens. " + tokens.size());
        
        System.out.println("\n\n");
    }
    
    
    
    /**
     * Converts an file with plaintext references to its tokenized output file.
     * @param inputFile
     * @param outputFile
     * @throws FileNotFoundException 
     */
    public static void tokenizePlaintextRefs(String inputFile, String outputFile) throws FileNotFoundException {

        Scanner s = new Scanner(new File(inputFile));
        PrintWriter w = new PrintWriter(new File(outputFile));

        while (s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            ArrayList<String> tokens = ReferenceUtil.tokenize(aLine, true);
            for (String t : tokens) {
                w.write(t + "\n");
            }
        }
        s.close();
        w.flush();
        w.close();

    }
    
    
    /**
     * Converts an XML-augmented file to its token(line-)separated output file.
     * @param inputFile
     * @param outputFile
     * @throws FileNotFoundException 
     */
    public static void tokenizeXMLAugs(String inputFile, String outputFile) throws FileNotFoundException {

        Scanner s = new Scanner(new File(inputFile));
        PrintWriter w = new PrintWriter(new File(outputFile));

        while (s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            PlaintextReferenceStringToMalletCRFFormatConverter.convertReferenceString(aLine, w);
        }
        s.close();
        w.flush();
        w.close();

    }

}
