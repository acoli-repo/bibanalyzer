/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.acoli.informatik.uni.frankfurt.crfformat.reflex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * This program takes 2 CRF formats as input.
 * 
 * 1. A Reflex CRF format (produced from HTML).
 * 
 * 2. Another CRF format (with [gold] annotations).
 * 
 * This program tries to align the two because they can possibly have
 * a different number of tokens.
 * We assume that the number of sentences (references) is equal in both files.
 *
 * The output is a file with merged (combined) tokens where Reflex
 * labels are used as features and the original (gold) labels from the other CRF
 * file are kept as the labels.
 * 
 * @author niko
 */
public class RelfexCRFandOtherCRFMerger {
    
    // Reflex CRF file.
    public static String REFLEX_CRF = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/" + 
            "450_biball_TEST_raw.txt.html.utf-8.html_refl2xmlaug.txt.crf.txt";
    // Other CRF file.
    public static String OTHER_CRF = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/4500references/with_font_journaltitle_dict_pubname_year/best_model/" + 
            "450_TEST_font_jourtit_dict_pubname_year.txt";
    
    
    public static void main(String[] args) throws FileNotFoundException {
     
        if(args.length == 2) {
            REFLEX_CRF = args[0];
            OTHER_CRF = args[1];
        }
        
        ArrayList<ArrayList<String[]>> reflexSentences = getSentences(REFLEX_CRF);
        ArrayList<ArrayList<String[]>> otherSentences = getSentences(OTHER_CRF);
        
        
        System.out.println("# Reflexica sentences read in: " + reflexSentences.size());
        System.out.println("# CRF sentences read in: " + otherSentences.size());
        
        if(reflexSentences.size() != otherSentences.size()) {
            System.out.println("Num sentences in two files not equal!");
            System.exit(0);
        }
        
        for(int sentIdx = 0; sentIdx < 100; sentIdx++) {
            ArrayList<String[]> aReflSent = reflexSentences.get(sentIdx);
            ArrayList<String[]> anotherCrfSent = otherSentences.get(sentIdx);
            
            // Compare each token pairwise:
            // TODO: Check ranges.
            int offset = 0;
            int reflOffset = 0;
            for(int tokIdx = 0; tokIdx < aReflSent.size(); tokIdx++) {
                
                if((tokIdx+reflOffset) == aReflSent.size()) {
                    break;
                }
                
                String aReflTok = aReflSent.get(tokIdx+reflOffset)[0];
                String anotherTok = anotherCrfSent.get(tokIdx+offset)[0];
                
                String aReflLabel = aReflSent.get(tokIdx+reflOffset)[1];
                String anotherLabel = anotherCrfSent.get(tokIdx+offset)[1];
                
                
                if(aReflTok.equals(anotherTok)) {
                    // Everything is okay.
                    // Print out the feature augmented line:
                    String augmentedLine = aReflTok + " " + aReflLabel + " " + anotherLabel;
                    //System.out.println("Pairs: " + augmentedLine);
                }
                else {
                    // These tokens do NOT match!
                     getReferenceLine(aReflSent);
                     getReferenceLine(anotherCrfSent);
                            
                    System.out.println(aReflTok + " vs. " + anotherTok);
                    // Search leftwards and rightwards until they match again!
                    // Keep Reflexica tokenization.
                    
                    
                    // 1st CHECK !
                    // Reflexica token is longer than CRF token.
                    // 2009a vs 2009.
                    // Get next Reflexica token 
                    // and check if other CRF has it wihtin window of next 3 toks.
                    // 
                    
                    String nextReflTok = aReflSent.get(tokIdx+1)[0];
                    int start = tokIdx+1;
                    int end = tokIdx +4;
                    int localOffset = 0;
                    boolean foundPointToContinue = false;
                    for(int nextOtherTokIdx = start; nextOtherTokIdx < end; nextOtherTokIdx++) {
                        System.out.println(nextOtherTokIdx);
                        String nextAnotherTok = anotherCrfSent.get(nextOtherTokIdx)[0];
                        if(nextAnotherTok.equals("&nbsp;")) {
                            // do nothing.
                        }
                        else if(nextReflTok.equals(nextAnotherTok)) {
                            System.out.println("We found a match to continue!");
                            System.out.println(nextReflTok + " and " + nextAnotherTok);
                            offset += localOffset;
                            foundPointToContinue = true;
                            break;
                        }
                        localOffset++;
                    }
                    
                    if(!foundPointToContinue) {
                        System.out.println("Haven't found a point to continue.");
                        //System.exit(0);
                    }
                    
                    
                    // 2nd CHECK !
                    // Check if Reflexica has introduced a whitespace which is not present in
                    // the gold data. (skip it if possible).
                    boolean stillNotFound = false;
                    if(aReflTok.equals("&nbsp;")) {
                        // Advance Reflexica by one.
                        if(nextReflTok.equals(anotherTok)) {
                            System.out.println("We found a match to continue!");
                            System.out.println(nextReflTok + " and " + anotherTok);
                            foundPointToContinue = true;
                            tokIdx++;
                            break;
                        }
                        else {
                            stillNotFound = true;
                        }
                    }
                    
                    
                    if(stillNotFound) {
                        System.out.println("Still found no match.");
                        break;
                    }
                    
                    
                    // 3rd check.
                    // CRF token is longer than Reflexica token.
                    // F vs F9
                    // Get SECOND next Reflexica token 
                    // and check if CRF token has it wihtin window of next 3 toks.
                    // 
                    
                    nextReflTok = aReflSent.get(tokIdx+2)[0];
                    start = tokIdx+1;
                    end = tokIdx +4;
                    int localReflOffset = 1;
                    foundPointToContinue = false;
                    for(int nextOtherTokIdx = start; nextOtherTokIdx < end; nextOtherTokIdx++) {
                        System.out.println(nextOtherTokIdx);
                        String nextAnotherTok = anotherCrfSent.get(nextOtherTokIdx)[0];
                        if(nextAnotherTok.equals("&nbsp;")) {
                            // do nothing.
                        }
                        else if(nextReflTok.equals(nextAnotherTok)) {
                            System.out.println("We found a match to continue! II");
                            System.out.println(nextReflTok + " and " + nextAnotherTok);
                            reflOffset += localReflOffset;
                            foundPointToContinue = true;
                            break;
                        }
                        localReflOffset++;
                    }
                    
                    
                    
                    
                }
            }
            
        }
        
        
    }

    private static ArrayList<ArrayList<String[]>> getSentences(String aCrfFile) throws FileNotFoundException {
        ArrayList<ArrayList<String[]>> rval = new ArrayList<>();
       
        Scanner s = new Scanner(new File(aCrfFile));
        ArrayList<String[]> aSentence = new ArrayList<>();
        while(s.hasNextLine()) {
            
            String aLine = s.nextLine().trim();
            if(aLine.length() == 0)  {
                rval.add(aSentence);
                aSentence = new ArrayList<>();
            }
            String[] split = aLine.split("\\s");
            String token = split[0];
            String label = split[split.length-1];
            
            String[] oneEntry = new String[2];
            oneEntry[0] = token;
            oneEntry[1] = label;
            aSentence.add(oneEntry);
        }
        s.close();
        return rval;
    }

    private static void getReferenceLine(ArrayList<String[]> aSent) {
        for(String s[] : aSent) {
            String tok = s[0].replace("&nbsp;", " ");;
            System.out.print(tok);
        }
        System.out.println();
    }
}
