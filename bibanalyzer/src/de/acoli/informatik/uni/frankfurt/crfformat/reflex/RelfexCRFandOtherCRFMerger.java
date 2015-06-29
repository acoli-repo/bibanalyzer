/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.acoli.informatik.uni.frankfurt.crfformat.reflex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
 * This program tries to align the two because they can possibly have a
 * different number of tokens. We assume that the number of sentences
 * (references) is equal in both files.
 *
 * The output is a file with merged (combined) tokens where Reflex labels are
 * used as features and the original (gold) labels from the other CRF file are
 * kept as the labels.
 *
 * @author niko
 */
public class RelfexCRFandOtherCRFMerger {

    // Reflex CRF file.
    public static String REFLEX_CRF = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/"
            + "450_biball_TEST_raw.txt.html.utf-8.html_refl2xmlaug.txt.crf.txt";
    // Other CRF file.
    public static String OTHER_CRF = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/4500references/with_font_journaltitle_dict_pubname_year/best_model/"
            + "450_TEST_font_jourtit_dict_pubname_year.txt";

    public static void main(String[] args) throws FileNotFoundException {

        if (args.length == 2) {
            REFLEX_CRF = args[0];
            OTHER_CRF = args[1];
        }

        ArrayList<ArrayList<String[]>> reflexSentences = getSentences(REFLEX_CRF);
        ArrayList<ArrayList<String[]>> otherSentences = getSentences(OTHER_CRF);

        System.out.println("# Reflexica sentences read in: " + reflexSentences.size());
        System.out.println("# CRF sentences read in: " + otherSentences.size());

        if (reflexSentences.size() != otherSentences.size()) {
            System.out.println("Num sentences in two files not equal!");
            System.exit(0);
        }

        
        int numExcluded = 0;
        
        ArrayList<ArrayList<String>> annotatedLines = new ArrayList<>();
        
        for (int sentIdx = 0; sentIdx < reflexSentences.size(); sentIdx++) {
            
            ArrayList<String> annotatedLine = new ArrayList<>();
            
            int numTriesForSentence = 0;
        
            System.out.println("Processing reference # " + sentIdx);
            ArrayList<String[]> aReflSent = reflexSentences.get(sentIdx);
            ArrayList<String[]> anotherCrfSent = otherSentences.get(sentIdx);

            // Compare each token pairwise:
            // TODO: Check ranges.
            int offset = 0;
            int reflOffset = 0;
            
            for (int tokIdx = 0; tokIdx < aReflSent.size(); tokIdx++) {
                
                if(tokIdx == aReflSent.size()-1) {
                    // Add data structure.
                    annotatedLines.add(annotatedLine);
                }

                if ((tokIdx + reflOffset) >= aReflSent.size()) {
                    break;
                }
                if ((tokIdx + offset) >= anotherCrfSent.size()) {
                    break;
                }

                String aReflTok = aReflSent.get(tokIdx + reflOffset)[0];
                String anotherTok = anotherCrfSent.get(tokIdx + offset)[0];

                String aReflLabel = aReflSent.get(tokIdx + reflOffset)[1];
                String anotherLabel = anotherCrfSent.get(tokIdx + offset)[1];

          //      System.out.println("Comparing: " + aReflTok + " <--> " + anotherTok);

                if (aReflTok.equals(anotherTok)) {
                    // Everything is okay.
                    // Print out the feature augmented line:
                    String print = aReflLabel;
                    if(print.length() > 0) {
                        print = "<Reflex" + print.substring(1);
                    }
                    
                    String augmentedLine = aReflTok + " " + print  + " " + anotherLabel;
                    annotatedLine.add(augmentedLine);
                    //System.out.println("Pairs: " + augmentedLine);
                } else {
                    
                    numTriesForSentence++;
                    
                    if(numTriesForSentence > 7) {
                        numExcluded++;
                        System.out.println("too many tries.");
                        //System.exit(0);
                        break;
                    }
                    // These tokens do NOT match!
                    getReferenceLine(aReflSent);
                    getReferenceLine(anotherCrfSent);

                    System.out.println("Mismatch: " + aReflTok + " vs. " + anotherTok);
                    String mismatch = aReflTok + " " + "<Reflex" + aReflLabel.substring(1) + " " + aReflLabel;
                    annotatedLine.add(mismatch);
                    
                    // Search leftwards and rightwards until they match again!
                    // Keep Reflexica tokenization.

                    // 1st CHECK !
                    // Reflexica token is longer than CRF token.
                    // 2009a vs 2009.
                    // Get next Reflexica token 
                    // and check if other CRF has it wihtin window of next 3 toks.
                    // 
                    
                    if ((tokIdx + reflOffset + 1) >= aReflSent.size()) {
                        break;
                    }
                    
                    
                    String nextReflTok = aReflSent.get(tokIdx + reflOffset + 1)[0];
                    String nextReflLab = aReflSent.get(tokIdx + reflOffset + 1)[1];
                    
                    if(nextReflTok.equals("EOR")) {
                        break;
                    }
                    
                    int start = tokIdx + 1;
                    int end = tokIdx + 4;
                    int localOffset = 0;
                    boolean foundPointToContinue = false;
                    if (!aReflTok.equals("&nbsp;")) {
                        for (int nextOtherTokIdx = start; nextOtherTokIdx < end; nextOtherTokIdx++) {
                            //System.out.println(nextOtherTokIdx);
                            
                            if(nextOtherTokIdx >= anotherCrfSent.size()) {
                                break;
                            }
                            
                            String nextAnotherTok = anotherCrfSent.get(nextOtherTokIdx)[0];
                            String nextAnotherLab = anotherCrfSent.get(nextOtherTokIdx)[1];
                            
                            //System.out.println("next other tok: " + nextAnotherTok);
                            //System.out.println("next reflex tok: " + nextReflTok);
                            
                            if (nextAnotherTok.equals("&nbsp;")) {
                                // do nothing.
                            } else if (nextReflTok.equals(nextAnotherTok)) {
                                System.out.println("We found a match to continue!");
                                System.out.println(nextReflTok + " and " + nextAnotherTok);
                                
                                offset += localOffset;
                                foundPointToContinue = true;
                                break;
                            }
                            localOffset++;
                        }
                    }

                    if (!foundPointToContinue) {
                        System.out.println("Haven't found a point to continue.");
                        System.out.println("Trying 2nd check.");
                        //System.exit(0);

                    // 2nd CHECK !
                        // Check if Reflexica has introduced a whitespace which is not present in
                        // the gold data. (skip it if possible).
                        boolean successful = false;
                        if (aReflTok.equals("&nbsp;")) {
                            // Advance Reflexica by one.
                            if (nextReflTok.equals(anotherTok)) {
                                System.out.println("We found a match to continue! (II)");
                                System.out.println(nextReflTok + " and " + anotherTok);

                                
                                String toAdd = nextReflTok + " " + "<Reflex" + nextReflLab.substring(1) + " " + nextReflLab;
                                annotatedLine.add(toAdd);
                    
                                
                                //tokIdx++;
                                reflOffset++;
                                successful = true;
                            }

                        }

                        if (!successful) {
                    // 3rd check.
                            // CRF token is longer than Reflexica token.
                            // F vs F9
                            // Get SECOND next Reflexica token 
                            // and check if CRF token has it wihtin window of next 3 toks.
                            // 

                            
                            nextReflTok = aReflSent.get(tokIdx + reflOffset + 2)[0];  // reflOffset hier raus !?!?
                            start = tokIdx + 1;
                            end = tokIdx + 4;
                            int localReflOffset = 1;

                            for (int nextOtherTokIdx = start; nextOtherTokIdx < end; nextOtherTokIdx++) {
                                System.out.println(nextOtherTokIdx);
                                
                                if(nextOtherTokIdx >= anotherCrfSent.size()) {
                                    break;
                                }
                                
                                String nextAnotherTok = anotherCrfSent.get(nextOtherTokIdx)[0];
                                if (nextAnotherTok.equals("&nbsp;")) {
                                    // do nothing.
                                } else if (nextReflTok.equals(nextAnotherTok)) {
                                    System.out.println("We found a match to continue! (III)");
                                    System.out.println(nextReflTok + " and " + nextAnotherTok);
                                    
                                   if(aReflTok.equals("&nbsp;") || anotherTok.equals("&nbsp;")) {
                                       System.out.println("NOT removing");
                                   }
                                   else {
                                     annotatedLine.remove(annotatedLine.size()-1);
                                     System.out.println("removing");
                                   }
                                    
                                    String toAdd = anotherTok + " " + "<Reflex"  + aReflLabel.substring(1) + " " + anotherLabel;
                                    annotatedLine.add(toAdd);
                                    
                                    
                                    reflOffset += localReflOffset;
                                    
                                    break;
                                }
                                
                                localReflOffset++;
                            }

                            
                            
                        }

                    }
                }
            }

        }
        System.out.println("# excluded: " + numExcluded);
        
        System.out.println(annotatedLines.size());
        
        
        // Export:
        
        PrintWriter w = new PrintWriter(new File("/home/niko/Desktop/test_aligned.txt"));
        for(ArrayList<String> l : annotatedLines) {
            for(String e : l) {
                w.write(e.trim() + "\n");
            }
        }
        w.flush();
        w.close();

    }

    private static ArrayList<ArrayList<String[]>> getSentences(String aCrfFile) throws FileNotFoundException {
        ArrayList<ArrayList<String[]>> rval = new ArrayList<>();

        Scanner s = new Scanner(new File(aCrfFile));
        ArrayList<String[]> aSentence = new ArrayList<>();
        while (s.hasNextLine()) {

            String aLine = s.nextLine().trim();
            if (aLine.length() == 0) {
                rval.add(aSentence);
                aSentence = new ArrayList<>();
            }
            String[] split = aLine.split("\\s");
            String token = split[0];
            String label = split[split.length - 1];

            String[] oneEntry = new String[2];
            oneEntry[0] = token;
            oneEntry[1] = label;
            aSentence.add(oneEntry);
        }
        s.close();
        return rval;
    }

    private static void getReferenceLine(ArrayList<String[]> aSent) {
        for (String s[] : aSent) {
            String tok = s[0].replace("&nbsp;", " ");;
            System.out.print(tok);
        }
        System.out.println();
    }
}
