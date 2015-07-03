/**
 * *****************************************************************************
 * Copyright (c) 2014 Christian Chiarcos, Niko Schenk Applied Computational
 * Linguistics Lab (ACoLi) Goethe-Universität Frankfurt am Main
 * http://acoli.cs.uni-frankfurt.de/en.html Robert-Mayer-Straße 10 60325
 * Frankfurt am Main
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Niko Schenk - initial API and implementation.
 * *****************************************************************************
 */
package de.acoli.informatik.uni.frankfurt.reranking;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * Program which evaluates the label accuracy of a CRF model based on an output
 * tagged text file.
 *
 *
 *
 * @author niko
 */
public class GeneralLabelAccuracyEvaluator {

    static boolean MALLET = false; // false = CRF++; // 

    // Gold--Predicted.
    public static final String TEST_TAGGED = "/home/niko/Desktop/"
            //+ "Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/mallet/model_output.txt";
            + "Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/"
            + "total/crf++/4500references/alignment/reflexica_annotations/only_reflexica_annotations/"
            + "output_model_onlyreflexicaannotations_mit12900ermodell.txt";

    public static ArrayList<String> labels = new ArrayList<String>();

    static {

        // General.
        labels.add("<Year>");
        //labels.add("<&comma;>");
        labels.add("<FirstPage>");
        labels.add("<LastPage>");
        labels.add("<Pages>");

        labels.add("<Initials>");
        labels.add("<FamilyName>");
        labels.add("<FirstName>");
        labels.add("<Suffix>");
        labels.add("<Particle>");
        labels.add("<Degrees>");
        labels.add("<InstitutionalAuthorName>");
        labels.add("<BibInstitutionalEditorName>");

        labels.add("<Handle>");
        labels.add("<BibComments>");
        labels.add("<RefSource>");
        labels.add("<EquationSource>");

        // Articles.
        labels.add("<ArticleTitle>");
        labels.add("<JournalTitle>");

        labels.add("<VolumeID>");
        labels.add("<IssueID>");

        labels.add("<Url>");
        labels.add("<URL>");

        labels.add("<Isbn>");
        labels.add("<ISBN>");

        // Books.
        labels.add("<BookTitle>");
        labels.add("<PublisherName>");
        labels.add("<PublisherLocation>");
        labels.add("<SeriesTitle>");
        labels.add("<EditionNumber>");
        labels.add("<NumberInSeries>");

        // Chapters.
        labels.add("<ChapterTitle>");
        labels.add("<ConfEventName>"); // Will most likely be tagged "PublisherLocation".

        labels.add("<ConfEventLocation>");

    }

    // Reads in a predicted output Mallet file and evaluates accuracy.
    public static void main(String[] args) throws FileNotFoundException {

        //*********************************//
        // Evaluate overall accuracy.
        Scanner sAll = new Scanner(new File(TEST_TAGGED));
        int correct = 0;
        int wrong = 0;
        while (sAll.hasNextLine()) {
            String aLine = sAll.nextLine();
            if (aLine.length() > 0) {
                String[] items = aLine.split("\\s");

                String predicted = "";
                String gold = "";

                // MALLET format.
                if (MALLET) {

                    predicted = items[1].replace("<Reflex", "<");
                    gold = items[2];
                  //  System.out.println("predicted:" + predicted);
                    //  System.out.println("gold:" + gold);

//                    predicted = items[0];
//                    //<JournalTitle> &nbsp; <JournalTitle> 
//                    if (items[items.length - 1].startsWith("<") && items[items.length - 1].endsWith(">")) {
//                        gold = items[items.length - 1];
//
//                    } //<JournalTitle> <JournalTitle> 5th 
//                    else {
//                        gold = items[items.length - 2];
//                    }
                } // CRF++ format.
                else {
                    predicted = items[items.length - 1];
                    gold = items[items.length - 2];
                }

                // Overall computation of accuracy!
                if (predicted.equals(gold)) {
                    correct++;
                } else {
                    wrong++;
                }

            }
        }
        sAll.close();
        System.out.println("All labels (including commas, periods, dummys...): " + (correct + wrong));
        System.out.println("correct: " + correct + " wrong: " + wrong);
        System.out.print("Radio: " + (double) correct / (correct + wrong) * 100);
        System.out.println("% accuracy.\n\n\n");

        //********** 
        // Evaluate accuracy measures for each label.
        int allSpecifiedLabelPredicted = 0;
        int allSpecifiedLabelCorrect = 0;
        int allSpecifiedTagCount = 0;

        for (String LABEL : labels) {
            Scanner s = new Scanner(new File(TEST_TAGGED));

            int labelPredicted = 0; // all hits by program.
            int labelCorrect = 0; // real hit.
            int labelTagCount = 0; // should be title. (from gold) // num gold title tags.

            while (s.hasNextLine()) {
                String aLine = s.nextLine();
                if (aLine.length() > 0) {
                    String[] items = aLine.split("\\s");

                    String predicted = "";
                    String gold = "";

                    // MALLET format.
                    if (MALLET) {

                        predicted = items[1].replace("<Reflex", "<");
                        gold = items[2];

//                        predicted = items[0];
//                        //<JournalTitle> &nbsp; <JournalTitle> 
//                        if (items[items.length - 1].startsWith("<") && items[items.length - 1].endsWith(">")) {
//                            gold = items[items.length - 1];
//                            //System.out.println(gold);
//                        } //<JournalTitle> <JournalTitle> 5th 
//                        else {
//                            gold = items[items.length - 2];
//                        }
                    } // CRF++ format.
                    else {
                        predicted = items[items.length - 1];
                        gold = items[items.length - 2];
                    }

                    //   System.out.println("pred: " + predicted + " gold: " + gold + " tok: " + token);
                    // <JournalTitle>.
                    // We have match! (gold equals predicted).
                    if (predicted.equals(gold) && gold.equals(LABEL)) {
                        labelPredicted++;
                        allSpecifiedLabelPredicted++;
                        labelCorrect++;
                        allSpecifiedLabelCorrect++;
                        labelTagCount++;
                        allSpecifiedTagCount++;
                    } // We predict "journaltitle" but it should not be "journaltitle".
                    else if (predicted.equals(LABEL) && !gold.equals(LABEL)) {
                        //System.out.println(aLine);;
                        labelPredicted++;
                        allSpecifiedLabelPredicted++;
                    } // Gold is "journaltitle", but we predict something else.
                    else if (gold.equals(LABEL)) {
                        //System.out.println(aLine);
                        labelTagCount++;
                        allSpecifiedTagCount++;
                    } else {
                        // Something which does not involve "journaltitle".
                        // Not relevant for computations.
                        //System.out.println(aLine);
                    }
                }

            }
            s.close();

            System.out.println("**** ");
            System.out.println(LABEL);
            double precision = (double) labelCorrect / labelPredicted;
            double recall = (double) labelCorrect / labelTagCount;
            double F1 = (double) 2 * (precision * recall) / (precision + recall);
            System.out.println("number of gold labels: \t" + labelTagCount);
            System.out.println("number of predictions: \t" + labelPredicted);
            System.out.println("correct matches: \t" + labelCorrect);

            System.out.println(" precision: " + precision + ", recall: " + recall + ", F1: " + F1);
            System.out.println();
        }

        // *** ALL.
        System.out.println("\n");
        System.out.println("******* ");
        System.out.println("****** MODEL performance: ");
        System.out.println("**** ");

        System.out.println("Prec, Recall, F1 based on specified labels:");
        double precision = (double) allSpecifiedLabelCorrect / allSpecifiedLabelPredicted;
        double recall = (double) allSpecifiedLabelCorrect / allSpecifiedTagCount;
        double F1 = (double) 2 * (precision * recall) / (precision + recall);
        System.out.println("number of gold labels: \t" + allSpecifiedTagCount);
        System.out.println("number of predictions: \t" + allSpecifiedLabelPredicted);
        System.out.println("correct matches: \t" + allSpecifiedLabelCorrect);

        System.out.println(" precision: " + precision + ", recall: " + recall + ", F1: " + F1);
        System.out.println();
    }
}
