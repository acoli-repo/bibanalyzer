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
 * This class contains methods to add features to Mallet's CRF file format.
 *
 * @author niko
 */
public class FeaturesAdderLowLevel {
    
    static String DIR = "C:\\Users\\Niko\\Documents\\"
            + "Springer_Reflexica_StatistischeModelle\\einModell3Bibtypes\\training\\total\\"
            + "crf++\\12900references\\fromSpringerDUMP\\total\\aligned_with_reflexica\\only_reflexica_annotations\\features\\";

    static String inputFile = DIR 
            + "train_12561.txt.jt.txt.fix.ed.txt.fix.dict.fix";
    
    static String outputFile = DIR 
            + "train_12561.txt.jt.txt.fix.ed.txt.fix.dict.fix.year";
    
    public static void main(String[] args) throws FileNotFoundException {
        addYearFeature(inputFile, outputFile);
    }

    public static void addYearFeature(String anInputFile, String anOutputFile) throws FileNotFoundException {

        Scanner s = new Scanner(new File(anInputFile));
        PrintWriter w = new PrintWriter(new File(anOutputFile));

        while (s.hasNextLine()) {
            String aLine = s.nextLine().trim().replace("  ", " ");

            if (aLine.length() > 0) {
                String[] items = aLine.split("\\s");

                for (int i = 0; i < items.length; i++) {
                    String token = items[0];
                    // the label!
                    if (i == items.length - 1) {
                        String write = "";
                        // It's only plain text data without any annotations.
                        if (i == 0) {
                            
                            // Then, add the token.
                            write = write.concat(items[i] + " ");
                            // Add a new feature first.
                            write = write.concat(isYear(token) + "\n");
                            
                            w.write(write);
                            

                        } else {
                            // Add a new feature first.
                            write = write.concat(isYear(token));
                            // Add the label.
                            write = write.concat(items[i] + " \n");
                            
                            w.write(write);
                        }
                    } else {
                        // A feature.
                        w.write(items[i] + " ");
                    }
                }
            } else {
                w.write("\n");
            }
        }

        w.flush();
        w.close();
        s.close();

    }

    // TODO: Refactor and integrate with previous method.
    /**
     * Add initials features, e.g.
     * A. Müller, B. Maier., where A. and B. are initials.
     * @param anInputFile
     * @param anOutputFile
     * @throws FileNotFoundException 
     */
    public static void addInitialsFeature(String anInputFile, String anOutputFile) throws FileNotFoundException {

        Scanner s = new Scanner(new File(anInputFile));
        PrintWriter w = new PrintWriter(new File(anOutputFile));

        while (s.hasNextLine()) {
            String aLine = s.nextLine().trim().replace("  ", " ");

            if (aLine.length() > 0) {
                String[] items = aLine.split("\\s");

                for (int i = 0; i < items.length; i++) {
                    String token = items[0];
                    // the label!
                    String write = "";
                        
                    if (i == items.length - 1) {

                        // It's only plain text data without any annotations.
                        if (i == 0) {

                            // Then, add the token.
                            write = write.concat(items[i] + " ");
                            // Add a new feature first.
                            write = write.concat(isEitherPunctOrUppercaseLetter(token) + "\n");
                            
                            w.write(write);

                        } else {
                            // Add a new feature first.
                            write = write.concat(isEitherPunctOrUppercaseLetter(token) + " ");
                            // Add the label.
                            write = write.concat(items[i] + " \n");
                            
                            w.write(write);
                            
                        }
                    } else {
                        // A feature.
                        write = items[i] + " ";
                        w.write(write);
                    }
                }
            } else {
                w.write("\n");
            }
        }

        w.flush();
        w.close();
        s.close();

    }

    // TODO: Refactor and integrate with previous method.
    /**
     * Add features to digits.
     * @param anInputFile
     * @param anOutputFile
     * @throws FileNotFoundException 
     */
    public static void addDigitFeature(String anInputFile, String anOutputFile) throws FileNotFoundException {

        Scanner s = new Scanner(new File(anInputFile));
        PrintWriter w = new PrintWriter(new File(anOutputFile));

        while (s.hasNextLine()) {
            String aLine = s.nextLine().trim().replace("  ", " ");

            if (aLine.length() > 0) {
                String[] items = aLine.split("\\s");

                for (int i = 0; i < items.length; i++) {
                    String token = items[0];
                    // the label!
                    if (i == items.length - 1) {

                        String write = "";
                        // It's only plain text data without any annotations.
                        if (i == 0) {
                            
                            // Then, add the token.
                            write = write.concat(items[i] + " ");
                            // Add a new feature first.
                            write = write.concat(isDigit(token) + "\n");
                            
                            w.write(write);
                            
                        } else {
                            // Add a new feature first.
                            write = write.concat(isDigit(token) + " ");
                            // Add the label.
                            write = write.concat(items[i] + " \n");
                            
                            w.write(write);
                            
                        }
                    } else {
                        // A feature.
                        w.write(items[i] + " ");
                    }
                }
            } else {
                w.write("\n");
            }
        }

        w.flush();
        w.close();
        s.close();

    }

    // Add new feature:
    // Consists of only Uppercase or Punctuation (. or -)
    public static String isEitherPunctOrUppercaseLetter(String str) {
        for (int c = 0; c < str.length(); c++) {
            if (!(str.charAt(c) == '.' || Character.isUpperCase(str.charAt(c)))) {
                return "";
            }
        }
        return "<isPunctOrUpper> ";
    }

    public static String isDigit(String str) {
        try {
            int i = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return "";
        }
        return "<isDigit> ";
    }

    public static boolean isDigitOfLength4(String str) {
        try {
            if (str.length() != 4) {
                return false;
            }

            int i = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    
    // TODO:
    // parameterizable via text file.

    public static String isYear(String str) {
        if (str.matches("(19\\d{2}|20\\d{2})([a-z]*)")) {
            return "<isYear> ";
        } else {
            return "";
        }

    }

}
