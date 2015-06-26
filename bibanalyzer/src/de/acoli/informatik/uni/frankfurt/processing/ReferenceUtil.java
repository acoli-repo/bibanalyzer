/**
 * *****************************************************************************
 * Copyright (c) 2014 
 * Christian Chiarcos, Niko Schenk 
 * Applied Computational Linguistics Lab (ACoLi)
 * Goethe-Universität Frankfurt am Main 
 * http://acoli.cs.uni-frankfurt.de/en.html
 * Robert-Mayer-Straße 10
 * 60325 Frankfurt am Main
 * 
 * All rights reserved.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Niko Schenk - initial API and
 * implementation.
 * *****************************************************************************
 */

package de.acoli.informatik.uni.frankfurt.processing;

import de.acoli.informatik.uni.frankfurt.crfformat.PlaintextReferenceStringToMalletCRFFormatConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Description:
 * Utility class for handling plaintext references.
 * 
 * A plaintext reference example is:
 * 
 * C. Carbonelli, S. Franz, European Transactions on Telecommunications 21(7), 589 (2010). URL db/journals/ett/ett21.html#CarbonelliF10
 * 
 * This class provides methods to read in references from a file and offers
 * methods for tokenization.
 * 
 * @author niko
 */
public class ReferenceUtil {

    /**
     * Reads in gold references from file Format:
     *
     * token1 <label1>
     * token2 <label2>
     * 
     * @param pathToGold
     * @param goldName
     * @throws FileNotFoundException 
     */
    public static void produceUnannotatedReferencesFromGold(String pathToGold, String goldName) throws FileNotFoundException {

        Scanner s = new Scanner(new File(pathToGold + goldName));
        PrintWriter w = new PrintWriter(new File("data/unannotated/" + goldName));
        while (s.hasNextLine()) {
            String aLine = s.nextLine();
            String[] split = aLine.split("\\s");
            if (aLine.length() > 1) {
                String token = split[0];
                String tag = split[1];
                w.write(token + "\n");
            } else {
                w.write(aLine + "\n");
            }
        }
        s.close();
        w.flush();
        w.close();

    }

    /**
     * Returns a list of plaintext strings for each single reference
     * in a document of bibliographic references.
     * 
     * Note that leading BibNumbers and similar items such as 
     * 
     * 1. a reference string ...
     * [1] a Reference string...
     * [Ref] aReference string
     * 
     * will be removed for later processing.
     * 
     * 
     * 
     * If the input file is in A++ format the method assumes that the
     * "BibUnstructed" elements contain the raw reference data.
     * 
     * E.g.: 
     * <Citation ID="CR1"><BibUnstructured>[Deb01] First, Lst.: Title (2001)</BibUnstructured></Citation>
     * 
     * 
     * @param pathToFile
     * @return
     * @throws FileNotFoundException 
     */
    public static ArrayList<String> getPlaintextOnelinersFromPlaintextFile(String pathToFile) throws FileNotFoundException {
        Scanner s = null;
        try {
            s = new Scanner(new File(pathToFile));
        } catch (FileNotFoundException e) {
            System.out.println("\n\n(!) Sorry. Your input file '" + pathToFile + "' was not found.");
            System.exit(0);
        }
        ArrayList<String> plainTextOneliners = new ArrayList<>();
        while (s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            aLine = postprocessLine(aLine);
            plainTextOneliners.add(aLine);
        }
        s.close();
        plainTextOneliners.add("This is the end of the reference list.");
        return plainTextOneliners;
    }
    
    
    /**
     * 
     * @param pathToFile
     * @param useXMLParser
     * @return 
     */
    public static ArrayList<String> getPlaintextOnelinersFromAPlusPlusFile(String pathToFile, boolean useXMLParser) {

        ArrayList<String> plainTextOneliners = new ArrayList<>();

        if (useXMLParser) {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(ReferenceUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
            Document doc = null;
            try {
                doc = dBuilder.parse(pathToFile);
            } catch (SAXException ex) {
                Logger.getLogger(ReferenceUtil.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ReferenceUtil.class.getName()).log(Level.SEVERE, null, ex);
            }

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList bibunstructured = doc.getElementsByTagName("BibUnstructured");
            System.out.println();
            for (int temp = 0; temp < bibunstructured.getLength(); temp++) {
                Node citationNode = bibunstructured.item(temp);
                if (citationNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) citationNode;
                    String aRef = eElement.getTextContent().replace("\n", " ").replace("\r", "");
                    //System.out.println("----- > " + aRef);
                    if (aRef.length() > 0) {
                        plainTextOneliners.add(aRef);
                    }
                }
            }

            return plainTextOneliners;
        } // Primitive scanning of newlines containing "BibUnstrucutred" as a tag on one line.
        else {
            Scanner s = null;
            try {
                s = new Scanner(new File(pathToFile), "UTF-8");
            } catch (FileNotFoundException e) {
                System.out.println("\n\n(!) Sorry. Your input file '" + pathToFile + "' was not found.");
                System.exit(0);
            }

            while (s.hasNextLine()) {
                String aLine = s.nextLine().trim();
            //System.out.println(aLine);
                // Find everything between "BibUnstructred tags".
                //<BibUnstructured>
                Pattern referencePattern = Pattern.compile("<BibUnstructured>(.+)</BibUnstructured>");
                Matcher referenceMatcher = referencePattern.matcher(aLine);
                while (referenceMatcher.find()) {
                    aLine = referenceMatcher.group(1);
                }
                aLine = postprocessLine(aLine);
                plainTextOneliners.add(aLine);
            }
            s.close();
            plainTextOneliners.add("This is the end of the reference list.");
            return plainTextOneliners;
        }
    }
    

    private static String postprocessLine(String aLine) {
        if (aLine.length() > 0) {
                // Check if it starts with a number, such as 1. or [1].
                // Remove them before we analyze the plain text reference.
                Pattern digitPattern = Pattern.compile("^\\d+(\\.)");
                Matcher digitMatcher = digitPattern.matcher(aLine);
                boolean foundDigitAtBeginningOfReference = false;
                while (digitMatcher.find()) {
                    //System.out.println(">" + digitMatcher.group()  + "<");
                    int begin = digitMatcher.start();
                    int end = digitMatcher.end();
                    String before = aLine.substring(0, begin);
                    String after = aLine.substring(end, aLine.length());
                    aLine = after.trim();
                    foundDigitAtBeginningOfReference = true;
                }
                
                // Add check for reference beginning in [1].
                Pattern digitBracketPattern = Pattern.compile("^\\[\\d+\\]");
                Matcher digitBracketMatcher = digitBracketPattern.matcher(aLine);
                boolean foundDigitBracketAtBeginningOfReference = false;
                while (digitBracketMatcher.find()) {
                    //System.out.println(">" + digitBracketMatcher.group()  + "<");
                    int begin = digitBracketMatcher.start();
                    int end = digitBracketMatcher.end();
                    String before = aLine.substring(0, begin);
                    String after = aLine.substring(end, aLine.length());
                    aLine = after.trim();
                    foundDigitBracketAtBeginningOfReference = true;
                }
                
                // TODO: Add check for references starting in [LastnameBla]
                
                
            }
        return aLine;
    }
    
    
    

    /**
     * Remove all tags from XML augmented training data.
     * @param aReference
     * @return 
     */
    private String removeAnnotations(String aReference) {
        return aReference.replaceAll("<[^>]+>", "");
    }

    /**
     * Tokenize a * single * plaintext input reference 
     * according to our tokenization scheme.
     *
     * @param aReference
     * @return
     */
    public static ArrayList<String> tokenize(String aReference, boolean checkBeginning) {

        if(checkBeginning) {
        if (!aReference.startsWith("BOR ")) {
            aReference = "BOR " + aReference;
        }
        if (!aReference.endsWith(" EOR")) {
            aReference = aReference + " EOR";
        }
        }

        ArrayList<String> rval = new ArrayList<>(100);

        String[] split = aReference.split(
                    PlaintextReferenceStringToMalletCRFFormatConverter.SPLIT_REGEX
        );

        for (String s : split) {
            if (s.length() > 0) {
                if (s.equals(" ")) {
                    s = "&nbsp;"; // Replace whitespace by special tag.
                }
                rval.add(s);
                //System.out.println("token: ->"  + s + "<-");
            } else {
                //System.out.println("There is a wrong item here.");
            }
        }
        return rval;
    }
    
    
    
    /*
     * 
     * @param tokensAndLabels
     * @return 
     */
    private static String helpUntokenize(ArrayList<String[]> tokensAndLabels) {
        String rval = "";
        for(String[] tokenLabel : tokensAndLabels) {
            String token = tokenLabel[1];
            if(token.equals("&nbsp;")) {
                token = " ";
            }
            rval = rval.concat(token);
        }
        return rval;
    }
    
    
    /**
     * "Untokenize" one reference.
     * Untokenize means: append all tokens to form a plaintext reference (one-line!) string.
     * 
     * @param crfFormatFile
     * @return
     * @throws FileNotFoundException 
     */
    public static ArrayList<String> untokenize(String crfFormatFile) throws FileNotFoundException {
        ArrayList<String> rval = new ArrayList<>();
        ArrayList<ArrayList<String[]>> allReferences = CRFOutputReader.getPredictedTokensAndTagsForReferences(crfFormatFile, true);
        for(ArrayList<String[]> oneReference : allReferences) {
            String plaintextOneliner = helpUntokenize(oneReference);
            plaintextOneliner = plaintextOneliner.replace("BOR ", "").replace(" EOR", "");
            rval.add(plaintextOneliner);
        }
        return rval;
    }
    
    

    /**
     * Tokenization demo.
     *
     * @param args
     */
    public static void main(String[] args) {

        String rawReference = "C. Carbonelli, S. Franz, European Transactions on Telecommunications 21(7), 589 (2010). URL db/journals/ett/ett21.html#CarbonelliF10";

        ArrayList<String> tokens = ReferenceUtil.tokenize(rawReference, true);
        for (String t : tokens) {
            System.out.println(t);
        }
    }
}
