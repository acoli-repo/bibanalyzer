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
package de.acoli.informatik.uni.frankfurt.processing;

import de.acoli.informatik.uni.frankfurt.crfformat.CRFUtility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Description: This program is supposed to fix CRF errors in a postprocessing
 * step.
 *
 * This program is completely rule-based. The underlying idea is that, e.g.,
 *
 * Something like "Hans-Müller" should NEVER be tagged as "pages". even though
 * the hyphen makes the CRF believe that we have a "firstpage-lastpage" pattern.
 *
 * A pattern of the form "vol." followed by a number, should never be tagged as
 * "dummy" but rather "number" instead.
 *
 * A dummy page before "pages" should be a VolumeID (BibArticles).
 *
 * Moreover, something like "Springer" is always a publisher. Idea: Read in a
 * static dictionary of publisher names and overwrite misclassified tokens.
 *
 * TODO: - "author" tags which are either followed or proceeded by Hrsg. editor,
 * ed. eds, etc should be replaced by "editors" (HTML pink) "editors" instead of
 * "authors".
 *
 * Add tags which definitely ARE confevent names and stuff like that which are
 * too rare to serve as features for the model.
 *
 * TODO: Match all tokens from a list of series titles. (We can be sure that we
 * have high precision with these types of dictionaries.)
 *
 * TODO: Numbers (digits) in general must not be "dummy".
 *
 * A string must never be tagged as "year".
 *
 * TODO: Patterns of the form "Title" In: "Title" should be "ChapterTitle" In:
 * "BookTitle". "ChapterTitle" In: "ChapterTitle" is not plausible.
 *
 *
 *
 * @author niko
 */
public class CRFPostProcessor {

    //public static final String INPUT_CRF_FILE = "/home/niko/Desktop/tagged_combined.txt";
    public static final String INPUT_CRF_FILE = "C://Users/niko/Desktop/featurePipeline/output.tagged";
    public static final String OUTPUT_CRF_FILE = "C://Users/niko/Desktop/featurePipeline/output.tagged.postprocessed";

    public static void main(String[] args) throws FileNotFoundException {

        ArrayList<ArrayList<String[]>> sentences = CRFOutputReader.getPredictedTokensAndTagsForReferences(INPUT_CRF_FILE, true);

        ArrayList<ArrayList<String[]>> rval = postProcessCRFOutput(sentences);

        PrintWriter w = new PrintWriter(new File(OUTPUT_CRF_FILE));
        for (ArrayList<String[]> sentence : rval) {
            for (String[] t : sentence) {
                w.write(t[0] + " " + t[1] + " " + t[2] + "\n");
            }
            w.write("\n");
        }
        w.flush();
        w.close();

    }

    /**
     * TUPLE 0 = token, TUPLE 1 = GOLD, TUPLE 2 = PRED.
     *
     * @param sentences
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String[]>> postProcessCRFOutput(ArrayList<ArrayList<String[]>> sentences) throws FileNotFoundException {

        System.out.println("Postprocessing output.");

        // Make one pass and substitute everything.
        for (int s = 0; s < sentences.size(); s++) {
            ArrayList<String[]> sentencetuples = sentences.get(s);

            // A reference with a journal title needs to have an article title
            // i.e. replace all chpatertitle or booktitle tags by article title.
            replaceChaptersAndBooksByArticles(sentencetuples);

            // Detect all references with only a publisher name and a chapter title.
            // -> Should be replaced by booktitle.
            //replaceChapterTitleByBookTitle(sentencetuples);

            // Fix volume id (number in series).
            fixNumberInSeries(sentencetuples);

            // A digit must not be a "<FamilyName>"
            fixDigits(sentencetuples);

            // TODO:
            // Postprocess data 
            // Read in Series Titles from data base and add exact matches to CRF
            // output.
            // E.g., Lecture Notes in Mathematics
            //
        }

        //System.out.println("\n\n\n\n");
        for (ArrayList<String[]> sentencetuples : sentences) {
            for (int i = 0; i < sentencetuples.size(); i++) {
                String[] tuple = sentencetuples.get(i);
                String label = tuple[2];
                String token = tuple[0];
                //System.out.println("label: " + label + "\t\ttoken:" + token);
            }
            //System.out.println();
        }

        return sentences;

    }

    public static boolean isDigit(String str) {
        try {
            int i = Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static void replaceChaptersAndBooksByArticles(ArrayList<String[]> sentencetuples) {
        for (int i = 0; i < sentencetuples.size(); i++) {
            String[] tuple = sentencetuples.get(i);

            String predlabel = tuple[2];

            if (predlabel.equals("<JournalTitle>")) {
                // Check if one of the previous is something like
                // "ChapterTitle" or "BookTitle" which does not have to be 
                // the case.
                for (int j = i - 1; j > 1; j--) {
                    if (j > 0) {
                        String[] aPreviousTuple = sentencetuples.get(j);
                        String aPreviousLabel = aPreviousTuple[2];
                        if (aPreviousLabel.equals("<ChapterTitle>")
                                || aPreviousLabel.equals("<BookTitle>")) {
                            //System.out.println("Replacing: " + aPreviousTuple[0] + "/" + aPreviousTuple[1] + "/" + aPreviousTuple[2]);
                            aPreviousTuple[2] = "<ArticleTitle>";
                            sentencetuples.set(j, aPreviousTuple);
                        }
                    }
                }
            }

        }
    }

    private static void replaceChapterTitleByBookTitle(ArrayList<String[]> sentencetuples) {

        boolean containsChapterTitle = false;
        boolean containsPublisherName = false;
        boolean containsBookTitle = false;

        for (int i = 0; i < sentencetuples.size(); i++) {
            String[] tuple = sentencetuples.get(i);
            if (tuple[2].equals("<ChapterTitle>")) {
                containsChapterTitle = true;
            }
            if (tuple[2].equals("<BookTitle>")) {
                containsBookTitle = true;
            }
            if (tuple[2].equals("<PublisherName>")) {
                containsPublisherName = true;
            }
        }

        if (containsPublisherName && containsChapterTitle && !containsBookTitle) {
            // Replace all chapter titles by book titles.

            for (int i = 0; i < sentencetuples.size(); i++) {
                String[] aTuple = sentencetuples.get(i);
                String predlabel = aTuple[2];

                if (predlabel.equals("<ChapterTitle>")) {
                    aTuple[2] = "<BookTitle>";
                    sentencetuples.set(i, aTuple);
                }
            }
        }
    }

    private static void fixNumberInSeries(ArrayList<String[]> sentencetuples) {
        for (int i = 0; i < sentencetuples.size(); i++) {
            String[] tuple = sentencetuples.get(i);
            String label = tuple[2];
            String token = tuple[0];
            //System.out.println("label: " + label + "\t\ttoken:" + token);
            if (isDigit(token) && label.equals("<dummy>")) {
                            //System.out.println("tok: "+ token);
                // Check if we find the word "volume" "vol. "vol" "Vol"
                // up to four tokens ahead.
                for (int j = i - 1; j > i - 4; j--) {
                    if (j > 0) {
                        String[] aPreviousTuple = sentencetuples.get(j);
                        String aPreviousToken = aPreviousTuple[0];
                        //System.out.println("prev tok: " + aPreviousToken);
                        if (aPreviousToken.equalsIgnoreCase("vol")
                                || aPreviousToken.equalsIgnoreCase("volume")) {
                            tuple[2] = "<NumberInSeries>";
                            sentencetuples.set(i, tuple);
                        }
                    }
                }
            }

        }
    }

    private static void fixDigits(ArrayList<String[]> sentencetuples) {
        for (int i = 0; i < sentencetuples.size(); i++) {
            String[] tuple = sentencetuples.get(i);
            String label = tuple[2];
            String token = tuple[0];
            //System.out.println("label: " + label + "\t\ttoken:" + token);
            if (isDigit(token) && label.equals("<FamilyName>")) {
                            //System.out.println("tok: "+ token);
                // Check if we find the word "volume" "vol. "vol" "Vol"
                // up to four tokens ahead.
                for (int j = i - 1; j > i - 4; j--) {
                    if (j > 0) {
                        String[] aPreviousTuple = sentencetuples.get(j);
                        String aPreviousLabel = aPreviousTuple[2];
                        if (aPreviousLabel.equals("<VolumeID>")) {
                            tuple[2] = "<FirstPage>";
                            sentencetuples.set(i, tuple);
                        }
                    }
                }
            }
            // Fix numbers which should be years.
            if(isDigit(token) && label.equals("<dummy>") && token.length() == 4
                    && (token.startsWith("19") || token.startsWith("20"))) {
                tuple[2] = "<Year>";
                sentencetuples.set(i, tuple);
            }
        }

    }

}
