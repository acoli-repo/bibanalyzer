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
 *
 * @author niko
 */
public class GeneralHighLevelFeaturesAdder {

    // Input file which we want to augment with journal information from RefLexica DB.
    private static final String INPUT_FILE = "C:\\Users\\Niko\\Documents\\"
            + "Springer_Reflexica_StatistischeModelle\\einModell3Bibtypes\\training\\total\\"
            + "crf++\\12900references\\fromSpringerDUMP\\total\\aligned_with_reflexica\\only_reflexica_annotations\\features\\"
            //+ "test_1255.txt";
         
            //+ "train_12561.txt";
            + "train_12561.txt.jt.txt.fix";

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        
        //String outputFile = INPUT_FILE + ".jt.txt";
        //addDataBaseFeatures(INPUT_FILE, outputFile, DictReader.JOURTIT_SPRINGER, "", true);
        //GeneralHighLevelFeaturesGapFiller.fillGaps(outputFile, "<isNotJT>", true);
        
        
        String newOutputFile = INPUT_FILE + ".ed.txt";
        addDataBaseFeatures(INPUT_FILE, newOutputFile, DictReader.EDNUM_SPRINGER, "", true);
        GeneralHighLevelFeaturesGapFiller.fillGaps(newOutputFile, "C:bla", "<isNotED>", true);
        
        
    }
    
    

    /**
     * 
     * @param anInputCrfFile
     * @param anOutputFile
     * @param typeOfFeature
     * @param fileWithAnnotations
     * @param isTrainingData, set to true if you annotate training (or test) data 
     * CONTAINING LABELS !
     * set this to false if you annotate raw tokenized data WITHOUT labels.
     * @throws FileNotFoundException 
     */
    public static void addDataBaseFeatures(String anInputCrfFile, String anOutputFile, String typeOfFeature,
            String fileWithAnnotations, boolean isTrainingData) throws FileNotFoundException {

        PrintWriter w = new PrintWriter(new File(anOutputFile));
        // Read in file as a List of list of items.
        // E.g. feature1 feature2 featuren label.

        Scanner s = new Scanner(new File(anInputCrfFile));

        ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<String> alreadyPresentFeatures = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();

        while (s.hasNextLine()) {
            
            String aLine = s.nextLine().replace("  ", " ");
            if(isTrainingData)
                aLine = aLine.trim();
            if (aLine.length() > 0) {

                String[] items = aLine.split("\\s");

                // Add token.
                tokens.add(items[0]);

                // Add everything up to label.
                // Token (plus potential features...)
                //System.out.println("aLine : >"+ aLine + "<");
                if(aLine.contains(" ")) {
                    String currentFeatures = aLine.substring(aLine.indexOf(" "), aLine.lastIndexOf(" "));
                    alreadyPresentFeatures.add(currentFeatures.trim());
                    //System.out.println("AlreadyPresentFeatures: " + currentFeatures);
                }
                else {
                    alreadyPresentFeatures.add("");
                }
                
                
                if(isTrainingData) {
                // Add the label.
                String label = items[items.length - 1];
                labels.add(label);
                }

            } else {
                tokens.add("");
                alreadyPresentFeatures.add("");
                if(isTrainingData) {
                    labels.add("");
                }
            }
        }
        s.close();

        // 2. Read in RefLexica database.
        ArrayList<ArrayList<String>> databaseItems = null;

            
                
        switch(typeOfFeature) {
            case DictReader.PUBLOCS_SPRINGER:
                databaseItems = DictReader.getSplittedSpringerPublisherLocations();
                break;
                
            case DictReader.JOURTIT_SPRINGER:
                databaseItems = DictReader.getSplittedSpringerJournalTitles();
                break;
                
                
                    
                
                
            case DictReader.PUBNAMES_DBLP:
                databaseItems = DictReader.getSplittedDBLPPublisherNames();
                break;
            case DictReader.PUBNAMES_SPRINGER:
                databaseItems = DictReader.getSplittedSpringerPublisherNames();
                break;
                
                
            case DictReader.INSTAUTHNAMES_SPRINGER:
                databaseItems = DictReader.getSplittedSpringerInstAuthNames();
                break;
                
            case DictReader.EDNUM_SPRINGER:
                databaseItems = DictReader.getSplittedSpringerEditionNumbers();
                break;
                
                
            case DictReader.SERIESTIT_DBLP:
                databaseItems = DictReader.getSplittedDBLPSeriesTitles();
                break;    
            case DictReader.SERIESTIT_SPRINGER:
                databaseItems = DictReader.getSplittedSpringerSeriesTitles();
                break;
                
            case DictReader.CONFEVENTNAMES_SPRINGER:
                databaseItems = DictReader.getSplittedSpringerConfEventNames();
                break;    
                
            case DictReader.CONFEVENTLOCS_SPRINGER:
                databaseItems = DictReader.getSplittedSpringerConfEventLocations();
                break;    
                
        }

        // This list stores information on whether a token is a publisher token = 1,
        // or not (0).
        ArrayList<Integer> occupiedTokens = new ArrayList<>();
        // Init. All zero's.
        for (int i = 0; i < tokens.size(); i++) {
            occupiedTokens.add(0);
        }
        //System.out.println(occupiedTokens.size());

        
        
        // 3. Try to find matches in the data.
        for (int i = 0; i < databaseItems.size(); i++) {
            //System.out.println(i);
            String initialWordOfDatabaseMatch = databaseItems.get(i).get(0);
            // Check the whole input if we find this first word in our data.
            for (int t = 0; t < tokens.size(); t++) {
                String currentToken = tokens.get(t);
                // If they match.
                if (initialWordOfDatabaseMatch.equals(currentToken)) {
                    // Now check the rest of the tokens!
                    boolean found = true;
                    int next = t + 1;
                    for (int r = 1; r < databaseItems.get(i).size(); r++) {
                        // New. JDK 1.7 vs. 1.8 bug(?)
                        if(next == tokens.size()) {
                            found = false;
                            break;
                        }
                        // end.
                        String nextDatabaseToken = databaseItems.get(i).get(r);
                        String nextTokenInInput = tokens.get(next);
                        if (!nextTokenInInput.equals(nextDatabaseToken)) {
                            found = false;
                            break;
                        }
                        next++;
                    }
                    if (found) {
                        int fromIdx = t;
                        int endIdx = t + databaseItems.get(i).size();

                        // Reset journal annotations map.
                        for (int f = fromIdx; f < endIdx; f++) {
                            occupiedTokens.set(f, 1);
                        }

                    }
                }
            }
        }

        // 4. Print out 
        for (int i = 0; i < occupiedTokens.size(); i++) {
            if (tokens.get(i).length() > 0) // Ignore reference boundaries.
            {

                String line = tokens.get(i) + " "
                        + alreadyPresentFeatures.get(i) + " "
                        + subst(occupiedTokens.get(i), typeOfFeature)
                        ;
                
                // Add the label if we have training data.
                if(isTrainingData) {
                    line = line.concat(" " + labels.get(i));
                }
                
                

                line = line.replace("  ", " ");
                w.write(line + "\n");
                //System.out.println(line);
            } else {
                //System.out.println();
                w.write("\n");
            }
        }

        w.flush();
        w.close();
        //System.err.println(occFound + " matches found from " + typeOfFeature + " database.");

    }

    private static String subst(int anInput, String dataBase) {
        if (anInput == 0) {
            return "";
        }
        if (anInput == 1) {
            
            switch (dataBase) {
                
               
                case DictReader.JOURTIT_DBLP:
                    return "<isDBLPJournalTitle> ";
                case DictReader.JOURTIT_SPRINGER:
                    return "<isSpringerJournalTitle> ";
                    
                case DictReader.PUBLOCS_SPRINGER:
                    return "<isSpringerPublisherLocation> ";
                    
                case DictReader.PUBNAMES_DBLP:
                    return "<isDBLPPublisherName> ";
                case DictReader.PUBNAMES_SPRINGER:
                    return "<isSpringerPublisherName> ";
               
                    
                case DictReader.INSTAUTHNAMES_SPRINGER:
                    return "<isSpringerInstAuthName> ";

                case DictReader.EDNUM_SPRINGER:
                    return "<isSpringerEditionNumber> ";
                    
                    
                case DictReader.SERIESTIT_DBLP:
                    return "<isDBLPSeriesTitle> ";
                case DictReader.SERIESTIT_SPRINGER:
                    return "<isSpringerSeriesTitle> ";
                    
                case DictReader.CONFEVENTNAMES_SPRINGER:
                    return "<isSpringerConfEventName> ";
                    
                case DictReader.CONFEVENTLOCS_SPRINGER:
                    return "<isSpringerConfEventLocation> ";    
                    
                    
                default:
                    System.out.println("Something wrong.");
                    System.exit(0);
            }
        } else {
            return "Error.";
        }
        return "Error.";
    }

}
