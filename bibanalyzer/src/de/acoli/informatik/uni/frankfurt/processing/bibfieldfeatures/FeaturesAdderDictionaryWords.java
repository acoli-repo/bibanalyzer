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

package de.acoli.informatik.uni.frankfurt.processing.bibfieldfeatures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Description:
 * Adds dictionary features to Mallet CRF file format.
 * 
 * Dictionaries can be of two types: either consisting of words collected from
 * DBLP article data, or a word list generated from Springer data.
 *
 * @author niko
 */
public class FeaturesAdderDictionaryWords {
    
    
    static String DIR = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/4500references/"
            + "alignment/reflexica_annotations_journame_dict/";
            
    static String inputFile = DIR 
            + "4331_train_aligned.txt.jourtit.txt.fix.txt" ;
    static String outputFile = inputFile 
            + ".dict.txt";

    /**
     * 
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args) throws FileNotFoundException {
        addDictionaryFeature(inputFile, outputFile, "DBLP");
    }
    
    /**
     * 
     * @param anInputFile
     * @param anOutputFile
     * @param type
     * @throws FileNotFoundException 
     */
    public static void addDictionaryFeature(String anInputFile, String anOutputFile, String type) throws FileNotFoundException {
        
        ArrayList<String> words = null;
        
        if(type.equals("LINUX")) {
            //words = DictReader.getLinuxWords();
            
        }
        else if(type.equals("DBLP")) {
            words = DictReader.getDBLPTitleWords();
        }
        else {
            System.out.println("Something wrong with your specified dictionary type.");
            System.exit(0);
        }
        
        
        
        Scanner s = new Scanner(new File(anInputFile));
        PrintWriter w = new PrintWriter(new File(anOutputFile));
        
        while(s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            
            if(aLine.length() > 0) {
                String[] items = aLine.split("\\s");
                
                for(int i = 0; i < items.length; i++) {
                    String token = items[0];
                    // the label!
                    if(i==items.length-1) {
                        
                        // It's only plain text data without any annotations.
                        if(i == 0) {
                        // Then, add the token.
                        w.write(items[i] + " ");
                            // Add a new feature first.
                        w.write(isContainedInDict(token, words) + "\n");
                        
                        }
                        // Line might contain features.
                        else {
                        // Add a new feature first.
                        w.write(isContainedInDict(token, words));
                        // Then, add the label.
                        w.write(items[i] + " \n");
                        }
                    }
                    else {
                        // A feature.
                        w.write(items[i] + " ");
                    }
                }
            }
            else {
                w.write("\n");
            }
        }
        
        
        w.flush();
        w.close();
        s.close();
        
    }
    
    

    /**
     * 
     * @param token
     * @param words
     * @return 
     */
    private static String isContainedInDict(String token, ArrayList<String> words) {
        // Careful. Früher war es nur der tolowercase check.
        if(token.length() > 1 && (words.contains(token.toLowerCase()) || words.contains(token))) {
            return "<inDict> ";
        }
        else {
            //return "<notInDict> ";
            return "";
        }
    }

}
