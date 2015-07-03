/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.acoli.informatik.uni.frankfurt.crfformat;

import de.acoli.informatik.uni.frankfurt.crfformat.reflex.vistotext.ReflexHTMLtoXMLAugConverter;
import de.acoli.informatik.uni.frankfurt.processing.ReferenceUtil;
import de.acoli.informatik.uni.frankfurt.visualization.CRFVisualizerNew;
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
        
                String anUnannotatedRef = "Montgomery DC (1997) Response surface methods and other approaches to process optimization. In: Montogmery DC (ed) Design and analysis of experiments. Wiley, New York, pp 427-510";
        tokenizeSinglePlaintextRef(anUnannotatedRef);
        
        
        
//        String anUnannotatedRef = "W. C. Hawkes, D. S. Kelley, "
//                + "and P. C. Taylor, "
//                + "The effects of dietary selenium on the immune system in healthy men, "
//                + "Biol. Trace Element Res. 81, 189–213 (2001).";
//        tokenizeSinglePlaintextRef(anUnannotatedRef);
//        
//        
//        String anAnnotatedRef = 
//                "<Initials>W. C.</Initials> <FamilyName>Hawkes</FamilyName>, <Initials>D. S.</Initials> <FamilyName>Kelley</FamilyName>, "
//                + "and <Initials>P. C.</Initials> <FamilyName>Taylor</FamilyName>, "
//                + "<ArticleTitle>The effects of dietary selenium on the immune system in healthy men</ArticleTitle>, "
//                + "<JournalTitle~Italic>Biol. Trace Element Res.</JournalTitle~Italic> <VolumeID~Bold>81</VolumeID~Bold>, "
//                + "<FirstPage>189</FirstPage>–<LastPage>213</LastPage> (<Year>2001</Year>).";
 
//        String anAnnotatedRef =
//                "<FamilyName>Winkelstein</FamilyName>, <Initials>W.</Initials>, <FamilyName>Lyman</FamilyName>, <Initials>D.</Initials>, <FamilyName>Padian</FamilyName>, <Initials>N.</Initials>, <FamilyName>Grant</FamilyName>, <Initials>R.</Initials>, <FamilyName>Samuel</FamilyName>, <Initials>M.</Initials>, <FamilyName>Wiley</FamilyName>, <Initials>J.</Initials>, <FamilyName>Anderson</FamilyName>, <Initials>R.</Initials>, <FamilyName>Lang</FamilyName>, <Initials>W.</Initials>, <FamilyName>Riggs</FamilyName>, <Initials>J.</Initials>, & <FamilyName>Levy</FamilyName>, <Initials>J.</Initials> (<Year>1987</Year>). <ArticleTitle>Sexual practices and risk of infection by the human immunodeficiency virus: The San Francisco Men’s Health Study</ArticleTitle>. <JournalTitle>Journal of the American Medical Association</JournalTitle>, <VolumeID>257</VolumeID>, <FirstPage>321</FirstPage>–<LastPage>325</LastPage>.";
//        tokenizeSingleXMLAug(anAnnotatedRef);
//        
        
        
//        // 1. Run Reflexica on plaintext references with -o color option and produce HTML output file.
//        
//        
//        // 2. Convert Reflexica HTML output file to XML annotated file.
//        // Input file consists of folder + filename.
//        // Reflexica output HTML file has to be UTF-8 encoded !
//        // First, run:
//        // iconv -f ascii -t utf-8//IGNORE References_7518_utf8encoded.txt.html -o utf8file.html
//        String inputfolder = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/";
//        //String inputfile = "450_biball_TEST_raw.txt.html.utf-8.html";
//        String inputfile = "4500_biball_TRAIN_raw.txt.html.utf-8.html";
//        
//        String outputfile = inputfolder + inputfile + "_refl2xmlaug.txt";
//        convertReflexicaHTMLtoXMLAug(inputfolder, inputfile, outputfile);
//        
//        
//        // 3. Convert XML annotated file to tokenized CRF format.
//        tokenizeMultipleXMLAugsFromFile(
//                // Input.
//                "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/" +
//                "4500_biball_TRAIN_raw.txt.html.utf-8.html_refl2xmlaug.txt", 
//                // Output.
//                "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/" +
//                "4500_biball_TRAIN_raw.txt.html.utf-8.html_refl2xmlaug.txt.crf.txt"
//                );
//        
//        
//        // 4. Visualize tokenized CRF format.
//        CRFVisualizerNew.visualizeCRFOutput(
//                "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/" +
//                "4500_biball_TRAIN_raw.txt.html.utf-8.html_refl2xmlaug.txt.crf.txt",
//                
//                "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/" +
//                "4500_biball_TRAIN_raw.txt.html.utf-8.html_refl2xmlaug.txt.crf.txt.vis.html"
//        );
//        
        
//          String inputfolder = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/";
//        //String inputfile = "450_biball_TEST_raw.txt.html.utf-8.html";
//        String inputfile = "4500_biball_TRAIN_raw.txt.html.utf-8.html";
//        
//        String outputfile = inputfolder + inputfile + "_refl2xmlaug.txt";
//        convertReflexicaHTMLtoXMLAug(inputfolder, inputfile, outputfile);
//      
        
        
//          
//        String inputfolder = "/home/niko/Desktop/"
//                + "Springer_Reflexica_StatistischeModelle/"
//                + "einModell3Bibtypes/"
//                + "training/total/crf++/12900references/analyzed_by_reflexica/html/";
//            //String inputfile = "1290_biball_TEST_raw.txt.utf8.html";
//            String inputfile = "12900_biball_TRAIN_raw.txt.utf8.html";
//        
//        String outputfile = inputfolder + inputfile + "_refl2xmlaug.txt";
//        convertReflexicaHTMLtoXMLAug(inputfolder, inputfile, outputfile);
//      
        
//        
//        
//        tokenizeMultipleXMLAugsFromFile(
//                // Input.
//                "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/12900references/analyzed_by_reflexica/xmlaug/" +
//                "12900_biball_TRAIN_raw.txt.utf8.html_refl2xmlaug.txt", 
//                // Output.
//                "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/12900references/analyzed_by_reflexica/xmlaug/" +
//                "12900_biball_TRAIN_raw.txt.utf8.html_refl2xmlaug.txt.crf.txt"
//                );
//        
        
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
     * Converts an XML-augmented file to its token(line-)separated output file.
     * @param inputFile
     * @param outputFile
     * @throws FileNotFoundException 
     */
    public static void tokenizeMultipleXMLAugsFromFile(String inputFile, String outputFile) throws FileNotFoundException {

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
    public static void tokenizeMultiplePlaintextRefsFromFile(String inputFile, String outputFile) throws FileNotFoundException {

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

    private static void convertReflexicaHTMLtoXMLAug(String inputfolder, String inputfile, String outputfile) {
        
        ReflexHTMLtoXMLAugConverter myEntry = new ReflexHTMLtoXMLAugConverter();
        myEntry.convertFiles(inputfolder, inputfile, outputfile);
        
    }
    
    

}
