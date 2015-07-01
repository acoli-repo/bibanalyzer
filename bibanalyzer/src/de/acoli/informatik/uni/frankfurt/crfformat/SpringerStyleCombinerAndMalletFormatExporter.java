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

package de.acoli.informatik.uni.frankfurt.crfformat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * 
 * Reads in A++ (GOLD) XML-annotated references from Springer data
 * and exports Mallet format for CRF training.
 *
 * @author niko
 *
 */
public class SpringerStyleCombinerAndMalletFormatExporter {

    
    // That's where the augmented XML files are located.
    // The raw reference annotated "training data".
    //public static final String inputpath = "input/dumps/SPRINGER_dump/raw/";
    
    //public static final String EXPORT_TO = "input/dumps/SPRINGER_dump/export/11000/bibbooks/";
    
    
     public static final String inputpath = "/home/niko/Desktop/Springer/springer_demo/Rohdaten/springer_APP/3.filtered/";
     public static final String EXPORT_TO = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/"
             + "total/crf++/15000references/books/";
    
    
    
    // Possible parameters:
    /**
     * bibarticle
     * bibbook
     * bibchapter
     */
    public static final String BIBTYPE = "bibbook";
    public static final String BIBSTYLE = "all";

    public static final int EXPORT_HOW_MANY_TRAIN = 4300;
    public static final int EXPORT_HOW_MANY_TEST = 430;
    
    // Set this to 0.7 or 0.8 if you want everything annotated.
    // 0.0 means, we allow references to be exported that have no annotations.
    public static final double ANNOTATION_THRESHOLD = 0.85;

    
    public static boolean shuffle = true;
    // TODO:
    // Add parameter.
    // public static boolean includeFont = false;
    public static final boolean debug = false; // write raw reference to file.

    static int numberofFirstnames = 0;

    // Where the xml data is located for one bibtype including all styles.
    public static final String PATH_TO_STYLES = inputpath + BIBTYPE + "/";

    public static void main(String[] args) throws FileNotFoundException {
        // Read in all style.
        ArrayList<File> files = new ArrayList<File>();
        collectFiles(files, PATH_TO_STYLES, BIBSTYLE);

        int allRefsCount = 0;
        // Careful. This data strucutre accepts NO duplicates !!!
        Set<String> references = new LinkedHashSet<String>(10000);
        for (File f : files) {
            if (f.getName().endsWith(".txt")) {
                System.out.println("Analyzing file: " + f.getName());
                Scanner s = new Scanner(f);
                while (s.hasNextLine()) {
                    String aLine = s.nextLine();

                    allRefsCount++;
                    if (getCondition(aLine)) {
                        references.add(aLine);

                        if (aLine.contains("FirstName")) {
                            numberofFirstnames++;
                        }
                    }
                }
                s.close();
            }
        }

        System.out.println();
        System.out.println("num (unique) referenences loaded: " + references.size() + " all refs: " + allRefsCount);
        System.out.println("References with firstnames: " + numberofFirstnames);

        List<String> list = new ArrayList<String>(references);
        if (shuffle) {
            Collections.shuffle(list);
        }

        // Export shuffled references including all styles.
        // TRAIN !!!
        PrintWriter w = new PrintWriter(
                new File(EXPORT_TO + EXPORT_HOW_MANY_TRAIN + "_" + BIBTYPE + "_TRAIN" + ".txt"));
        PrintWriter w_raw = new PrintWriter(
                new File(EXPORT_TO + EXPORT_HOW_MANY_TRAIN + "_" + BIBTYPE + "_TRAIN_raw" + ".txt"));
        PrintWriter w_xmlaug = new PrintWriter(
                new File(EXPORT_TO + EXPORT_HOW_MANY_TRAIN + "_" + BIBTYPE + "_TRAIN_xmlaug" + ".txt"));

        int counterTrain = 0;
        int refIdx = 0;
        // Train.
        while (true) {

            String aRef = list.get(refIdx);
            aRef = replaceTags(aRef);

            
            if (counterTrain >= EXPORT_HOW_MANY_TRAIN) {
                break;
            }

            if (debug) {
                w.write(aRef.replaceAll("<[^>]+>", "") + "\n" + aRef + "\n");
            }

            PlaintextReferenceStringToMalletCRFFormatConverter.convertReferenceString(aRef, w);
            counterTrain++;

            w_raw.write(aRef.replaceAll("<[^>]+>", "") + "\n");
            w_xmlaug.write(aRef + "\n");

            refIdx++;
        }
        w.close();
        w_raw.close();
        w_xmlaug.close();

        // Export shuffled references including all styles.
        // TEST !!!
        PrintWriter w2 = new PrintWriter(
                new File(EXPORT_TO + EXPORT_HOW_MANY_TEST + "_" + BIBTYPE + "_TEST" + ".txt"));
        PrintWriter w2_raw = new PrintWriter(
                new File(EXPORT_TO + EXPORT_HOW_MANY_TEST + "_" + BIBTYPE + "_TEST_raw" + ".txt"));
        PrintWriter w2_xmlaug = new PrintWriter(
                new File(EXPORT_TO + EXPORT_HOW_MANY_TEST + "_" + BIBTYPE + "_TEST_xmlaug" + ".txt"));

        int counterTest = 0;
        // Test.
        while (true) {

            String aRef = list.get(refIdx);
            aRef = replaceTags(aRef);

            if (counterTest >= EXPORT_HOW_MANY_TEST) {
                break;
            }

            if (debug) {
                w2.write(aRef.replaceAll("<[^>]+>", "") + "\n" + aRef + "\n");
            }

            PlaintextReferenceStringToMalletCRFFormatConverter.convertReferenceString(aRef, w2);
            counterTest++;

            w2_raw.write(aRef.replaceAll("<[^>]+>", "") + "\n");
            w2_xmlaug.write(aRef + "\n");

            refIdx++;
        }
        w2.close();
        w2_raw.close();
        w2_xmlaug.close();

    }

    public static void collectFiles(ArrayList<File> fileList, String path, String bibstyle) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) {
            return;
        }

        for (File f : list) {
            if (f.isDirectory()) {
                collectFiles(fileList, f.getAbsolutePath(), bibstyle);
            } else {
                if (bibstyle.equals("all")) {
                    fileList.add(f);
                } else if (f.getName().contains(bibstyle)) {
                    fileList.add(f);
                } else {
                    // Do nothing.
                }
            }
        }
    }

    private static boolean getCondition(String aRef) {

        Pattern pNamesNotProperlyAnnotated1 = Pattern.compile("FamilyName>([A-Z]|[a-z]|,|\\s|\\.){7,17}<FirstName");
        Matcher mNamesNotProperlyAnnotated1 = pNamesNotProperlyAnnotated1.matcher(aRef);
        while (mNamesNotProperlyAnnotated1.find()) {
            String match = mNamesNotProperlyAnnotated1.group();
            //System.out.println("aRef: " + aRef);
            //System.out.println("match: " + match);
            return false;
        }

        Pattern pNamesNotProperlyAnnotated2 = Pattern.compile("</Initials>([A-Z]|[a-z]|,|\\s|\\.){7,17}<FamilyName>");
        Matcher mNamesNotProperlyAnnotated2 = pNamesNotProperlyAnnotated2.matcher(aRef);
        while (mNamesNotProperlyAnnotated2.find()) {
            String match = mNamesNotProperlyAnnotated2.group();
            if (match.contains(" and ") && match.length() < 31) {
                // Thats okay.
                //System.out.println("ok; " + match);
            } else {
                //System.out.println("aRef: " + aRef);
                //System.out.println("match: " + match);
                return false;
            }
        }

        // Throw away those which are not properly annotated.
        // E.g., match: ...</IssueID></VolumeID>
        Pattern p = Pattern.compile("(</[^>]+></[^>]+>)");
        Matcher m = p.matcher(aRef);
        while (m.find()) {
            //String match = m.group();
            //System.out.println("aRef: " + aRef);
            //System.out.println("match: " + match);
            return false;
        }

        // Reject those which are not properly annotated.
        // For example:
        // F. Castaños, R. Ortega, A. van der Schaft, and A. Astolfi. Asymptotic stabilization via control by interconnection of port-<title>Hamiltonian systems.</title> Automatica, 45(7):1611-1618, <volume>2</volume>009.
        // Reject those which have too many untagged stuff in between.
        String tagsReplaced = aRef.replaceAll("<[^>]+>", "");
        //System.out.println(tagsReplaced);
        Pattern pattern = Pattern.compile("(<[^>]+>)(.+?)(<)");
        Matcher matcher = pattern.matcher(aRef);
        //System.out.println("aRef: " + aRef + "<");
        int cumLengthMatchedSpans = 0;
        while (matcher.find()) {
            String match = matcher.group(2);
            //System.out.println(match);
            cumLengthMatchedSpans += match.length();
        }
        //System.out.println("Lengths: " + tagsReplaced.length()  + " vs. " + cumLengthMatchedSpans);
        double ratio = (double) cumLengthMatchedSpans / (double) tagsReplaced.length();
        //System.out.println(ratio);
        //System.out.println();
        if (ratio < ANNOTATION_THRESHOLD) {
            return false;
        }
        
        //if(aRef.contains("–<Initials>") || aRef.contains("–<FirstName>")) {
        //    return false;
        //}

        //if(aRef.startsWith("<FirstName") ) {
        //    return true;
        //}
        
        //else {
        //    return false;
        //}
        else {
        return true;
        }

    }

    /**
     * 
     * @param aLine
     * @return 
     */
    private static String replaceTags(String aLine) {
        return aLine.replace("<FirstName>", "<Initials>").replace("</FirstName>", "</Initials>")
                .replace("<Superscript>", "<Superscript>").replace("</Superscript>", "");
    }
}
