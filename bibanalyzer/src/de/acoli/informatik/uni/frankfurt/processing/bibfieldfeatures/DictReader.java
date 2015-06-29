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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Description:
 *
 * A class with utility methods to read in RefLexica, DBLP & Springer
 * dictionaries which are used as token features for various supervised learning
 * scenarios.
 *
 * The data structure format ensures that a CRF output can be augmented with
 * longest matches. (although the program still has some efficiency issues.)
 *
 * 
 * TODO: REFACTOR SIMILAR SOURCE CODE ! (THIS CLASS HAD MAINLY BEEN GENERATED AUTOMATICALLY)
 *
 *
 * @author niko
 */
public class DictReader {
    
    private static String REAL_PATH = "";

    private static final String PATH_TO_DBLP_DICTS = "dicts/DBLP/";
    private static final String PATH_TO_SPRINGER_DICTS = "dicts/SPRINGER/";

    private static final String WORD_DICT_LOCATION_DBLP = PATH_TO_DBLP_DICTS + "TitleWordsDB_DBLP.txt";

    // Publisher names.
    public static final String PUBNAMES_DBLP = PATH_TO_DBLP_DICTS + "PublisherNameDB_DBLP.txt";
    public static final String PUBNAMES_SPRINGER = PATH_TO_SPRINGER_DICTS + "PublisherNameDB_Springer.txt";

    // Publisher locations.
    public static final String PUBLOCS_SPRINGER = PATH_TO_SPRINGER_DICTS + "PublisherLocationDB_Springer.txt";

    // Journal titles.
    public static final String JOURTIT_DBLP = PATH_TO_DBLP_DICTS + "JournalTitleDB_DBLP.txt";
    public static final String JOURTIT_SPRINGER = PATH_TO_SPRINGER_DICTS + "JournalTitleDB_Springer.txt";

    // Institutional editor names.
    public static final String BIBINSTEDNAMES_SPRINGER = PATH_TO_SPRINGER_DICTS + "BibInstitutionalEditorNameDB_Springer.txt";

    // Institutional author names.
    public static final String INSTAUTHNAMES_SPRINGER = PATH_TO_SPRINGER_DICTS + "InstitutionalAuthorNameDB_Springer.txt";

    // Series titles.
    public static final String SERIESTIT_DBLP = PATH_TO_DBLP_DICTS + "SeriesTitleDB_DBLP.txt";
    public static final String SERIESTIT_SPRINGER = PATH_TO_SPRINGER_DICTS + "SeriesTitleDB_Springer.txt";

    // Edition numbers.
    public static final String EDNUM_SPRINGER = PATH_TO_SPRINGER_DICTS + "EditionNumberDB_Springer.txt";

    // Conference event names.
    public static final String CONFEVENTNAMES_SPRINGER = PATH_TO_SPRINGER_DICTS + "ConfEventNameDB_Springer.txt";

    // Conference event locations.
    public static final String CONFEVENTLOCS_SPRINGER = "ConfEventLocationDB_Springer.txt";

    // Linux lexicon words.
    private static final ArrayList<String> linuxwords = new ArrayList<String>();
    
    // DBLP lexicon words.
    private static final ArrayList<String> dblpwords = new ArrayList<String>();

    private static final ArrayList<String> dblpPublisherNames = new ArrayList<String>();
    private static final ArrayList<String> springerPublisherNames = new ArrayList<String>();

    private static final ArrayList<String> springerPublisherLocations = new ArrayList<String>();

    private static final ArrayList<String> dblpJournalTitles = new ArrayList<String>();
    private static final ArrayList<String> springerJournalTitles = new ArrayList<String>();

    private static final ArrayList<String> springerBibInstEdNames = new ArrayList<String>();

    private static final ArrayList<String> springerInstAuthNames = new ArrayList<String>();

    private static final ArrayList<String> dblpSeriesTitles = new ArrayList<String>();
    private static final ArrayList<String> springerSeriesTitles = new ArrayList<String>();

    private static final ArrayList<String> springerEditionNumbers = new ArrayList<String>();

    private static final ArrayList<String> springerConfEventNames = new ArrayList<String>();

    private static final ArrayList<String> springerConfEventLocations = new ArrayList<String>();

    // "Splitted" means: tokenized by the rules of tokenization.
    private static final ArrayList<ArrayList<String>> splittedDBLPPublisherNames = new ArrayList<ArrayList<String>>();
    private static final ArrayList<ArrayList<String>> splittedSpringerPublisherNames = new ArrayList<ArrayList<String>>();

    private static final ArrayList<ArrayList<String>> splittedSpringerPublisherLocations = new ArrayList<ArrayList<String>>();

    private static final ArrayList<ArrayList<String>> splittedDBLPJournalTitles = new ArrayList<ArrayList<String>>();
    private static final ArrayList<ArrayList<String>> splittedSpringerJournalTitles = new ArrayList<ArrayList<String>>();

    private static final ArrayList<ArrayList<String>> splittedSpringerBibInstEdNames = new ArrayList<ArrayList<String>>();

    private static final ArrayList<ArrayList<String>> splittedSpringerInstAuthNames = new ArrayList<ArrayList<String>>();

    private static final ArrayList<ArrayList<String>> splittedDBLPSeriesTitles = new ArrayList<ArrayList<String>>();
    private static final ArrayList<ArrayList<String>> splittedSpringerSeriesTitles = new ArrayList<ArrayList<String>>();

    private static final ArrayList<ArrayList<String>> splittedSpringerEditionNumbers = new ArrayList<ArrayList<String>>();

    private static final ArrayList<ArrayList<String>> splittedSpringerConfEventNames = new ArrayList<ArrayList<String>>();

    private static final ArrayList<ArrayList<String>> splittedSpringerConfEventLocations = new ArrayList<ArrayList<String>>();

    /**
     *
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Reading in...");
        ArrayList<ArrayList<String>> splittedJourNames = getSplittedDBLPJournalTitles();
        for (ArrayList<String> jourNames : splittedJourNames) {
            //System.out.println(jourNames);
        }
        System.out.println("...done.");
    }
    
    public void setRealPathToDictionaries(String realPath) {
        this.REAL_PATH = realPath;
    }
    

    /**
     * Reads in DBLP generated word list from all titles which occur in the DBLP
     * file.
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getDBLPTitleWords() throws FileNotFoundException {
        if (dblpwords.isEmpty()) {
            Scanner s = new Scanner(new File(REAL_PATH + WORD_DICT_LOCATION_DBLP));
            while (s.hasNextLine()) {
                String aWord = s.nextLine().trim();
                dblpwords.add(aWord);
            }
            s.close();
            //System.out.println(words.size());
            System.out.println("\tRead in " + dblpwords.size() + " DBLP dictionary words.");

        }
        return dblpwords;
    }

    // ************************************ //
    //*** P U B L I S H E R    N A M E S ***//
    /**
     * Get DBLP PublisherNames
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getDBLPPublisherNames() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + PUBNAMES_DBLP), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!dblpPublisherNames.contains(aWord)) {
                dblpPublisherNames.add(aWord);
            }
        }
        s.close();
        return dblpPublisherNames;
    }

    /**
     * Get Springer PublisherNames
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getSpringerPublisherNames() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + PUBNAMES_SPRINGER), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!springerPublisherNames.contains(aWord)) {
                springerPublisherNames.add(aWord);
            }
        }
        s.close();
        return springerPublisherNames;
    }

    // ******************************************** //
    //*** P U B L I S H E R    L O C A T I O N S ***//
    /**
     * Get Springer PublisherLocations
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getSpringerPublisherLocations() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + PUBLOCS_SPRINGER), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!springerPublisherLocations.contains(aWord)) {
                springerPublisherLocations.add(aWord);
            }
        }
        s.close();
        return springerPublisherLocations;
    }

    // ******************************************** //
    //***     J O U R N A L   T I T L E S        ***//
    /**
     * DBLP JournalTitles
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getDBLPJournalTitles() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + JOURTIT_DBLP), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!dblpJournalTitles.contains(aWord)) {
                dblpJournalTitles.add(aWord);
            }
        }
        s.close();
        return dblpJournalTitles;
    }

    /**
     * Get Springer JournalTitles
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getSpringerJournalTitles() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + JOURTIT_SPRINGER), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!springerJournalTitles.contains(aWord)) {
                springerJournalTitles.add(aWord);
            }
        }
        s.close();
        return springerJournalTitles;
    }

    // ******************************************** //
    //***    BIBINSTITUTIONAL EDITOR NAMES       ***//
    /**
     * Get Springer BibInstEdNames
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getSpringerBibInstEdNames() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + BIBINSTEDNAMES_SPRINGER), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!springerBibInstEdNames.contains(aWord)) {
                springerBibInstEdNames.add(aWord);
            }
        }
        s.close();
        return springerBibInstEdNames;
    }

    // ******************************************** //
    //***      INSTITUTIONAL AUTHOR NAMES        ***//
    /**
     * Get Springer InstAuthNames
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getSpringerInstAuthNames() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + INSTAUTHNAMES_SPRINGER), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!springerInstAuthNames.contains(aWord)) {
                springerInstAuthNames.add(aWord);
            }
        }
        s.close();
        return springerInstAuthNames;
    }

    // ******************************************** //
    //***      S E R I E S    T I T L E S        ***//
    /**
     * Get DBLP SeriesTitles
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getDBLPSeriesTitles() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + SERIESTIT_DBLP), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!dblpSeriesTitles.contains(aWord)) {
                dblpSeriesTitles.add(aWord);
            }
        }
        s.close();
        return dblpSeriesTitles;
    }

    /**
     * Get Springer SeriesTitles
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getSpringerSeriesTitles() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + SERIESTIT_SPRINGER), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!springerSeriesTitles.contains(aWord)) {
                springerSeriesTitles.add(aWord);
            }
        }
        s.close();
        return springerSeriesTitles;
    }

    // ******************************************** //
    //***     E D I T I O N   N U M B E R S      ***//
    /**
     * Get Springer EditionNumbers
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getSpringerEditionNumbers() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + EDNUM_SPRINGER), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!springerEditionNumbers.contains(aWord)) {
                springerEditionNumbers.add(aWord);
            }
        }
        s.close();
        return springerEditionNumbers;
    }

    // ******************************************** //
    //***    C O N F E V E N T    N A M E S      ***//
    /**
     * Get Springer ConfEventNames
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getSpringerConfEventNames() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + CONFEVENTNAMES_SPRINGER), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!springerConfEventNames.contains(aWord)) {
                springerConfEventNames.add(aWord);
            }
        }
        s.close();
        return springerConfEventNames;
    }

    // *********************************************** //
    //***   C O N F E V E N T    L O C A T I O N S  ***//
    /**
     * Get Springer ConfEventLocations
     *
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<String> getSpringerConfEventLocations() throws FileNotFoundException {
        Scanner s = new Scanner(new File(REAL_PATH + CONFEVENTLOCS_SPRINGER), "UTF-8");
        while (s.hasNextLine()) {
            String aWord = s.nextLine().trim();
            aWord = replace(aWord);
            //System.out.println(aWord);
            if (!springerConfEventLocations.contains(aWord)) {
                springerConfEventLocations.add(aWord);
            }
        }
        s.close();
        return springerConfEventLocations;
    }

    //**//
    //**////**//
    //**//
    public static ArrayList<ArrayList<String>> getSplittedDBLPPublisherNames() throws FileNotFoundException {
        if (splittedDBLPPublisherNames.isEmpty()) {
            splittedDBLPPublisherNames.clear();
            ArrayList<String> items = getDBLPPublisherNames();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedDBLPPublisherNames.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedDBLPPublisherNames, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedDBLPPublisherNames.size() + " DBLP publisher names.");
        }
        return splittedDBLPPublisherNames;
    }

    public static ArrayList<ArrayList<String>> getSplittedSpringerPublisherNames() throws FileNotFoundException {
        if (splittedSpringerPublisherNames.isEmpty()) {
            ArrayList<String> items = getSpringerPublisherNames();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedSpringerPublisherNames.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedSpringerPublisherNames, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedSpringerPublisherNames.size() + " Springer publisher names.");
        }
        return splittedSpringerPublisherNames;
    }

    public static ArrayList<ArrayList<String>> getSplittedSpringerPublisherLocations() throws FileNotFoundException {
        if (splittedSpringerPublisherLocations.isEmpty()) {
            ArrayList<String> items = getSpringerPublisherLocations();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedSpringerPublisherLocations.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedSpringerPublisherLocations, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedSpringerPublisherLocations.size() + " Springer publisher locations.");
        }
        return splittedSpringerPublisherLocations;
    }

    public static ArrayList<ArrayList<String>> getSplittedDBLPJournalTitles() throws FileNotFoundException {
        if (splittedDBLPJournalTitles.isEmpty()) {
            ArrayList<String> localJournalNames = getDBLPJournalTitles();
            for (String pubLoc : localJournalNames) {
                String[] split = split(pubLoc);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedDBLPJournalTitles.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedDBLPJournalTitles, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedDBLPJournalTitles.size() + " DBLP journal titles.");
        }
        //System.out.println("Read in " + splittedReflexicaJournalTitles.size() + " journal names from DB.");
        return splittedDBLPJournalTitles;
    }

    public static ArrayList<ArrayList<String>> getSplittedSpringerJournalTitles() throws FileNotFoundException {
        if (splittedSpringerJournalTitles.isEmpty()) {
            ArrayList<String> items = getSpringerJournalTitles();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedSpringerJournalTitles.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedSpringerJournalTitles, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedSpringerJournalTitles.size() + " Springer journal titles.");
        }
        return splittedSpringerJournalTitles;
    }

    public static ArrayList<ArrayList<String>> getSplittedSpringerBibInstEdNames() throws FileNotFoundException {
        if (splittedSpringerBibInstEdNames.isEmpty()) {
            ArrayList<String> items = getSpringerBibInstEdNames();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedSpringerBibInstEdNames.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedSpringerBibInstEdNames, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedSpringerBibInstEdNames.size() + " Springer Bib Inst Ed names.");
        }
        return splittedSpringerBibInstEdNames;
    }

    public static ArrayList<ArrayList<String>> getSplittedSpringerInstAuthNames() throws FileNotFoundException {
        if (splittedSpringerInstAuthNames.isEmpty()) {
            ArrayList<String> items = getSpringerInstAuthNames();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedSpringerInstAuthNames.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedSpringerInstAuthNames, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedSpringerInstAuthNames.size() + " Springer institutional author names.");
        }
        return splittedSpringerInstAuthNames;
    }

    public static ArrayList<ArrayList<String>> getSplittedDBLPSeriesTitles() throws FileNotFoundException {
        if (splittedDBLPSeriesTitles.isEmpty()) {
            ArrayList<String> items = getDBLPSeriesTitles();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedDBLPSeriesTitles.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedDBLPSeriesTitles, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedDBLPSeriesTitles.size() + " DBLP series titles.");
        }
        return splittedDBLPSeriesTitles;
    }

    public static ArrayList<ArrayList<String>> getSplittedSpringerSeriesTitles() throws FileNotFoundException {
        if (splittedSpringerSeriesTitles.isEmpty()) {
            ArrayList<String> items = getSpringerSeriesTitles();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedSpringerSeriesTitles.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedSpringerSeriesTitles, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedSpringerSeriesTitles.size() + " Springer series titles.");
        }
        return splittedSpringerSeriesTitles;
    }

    public static ArrayList<ArrayList<String>> getSplittedSpringerEditionNumbers() throws FileNotFoundException {
        if (splittedSpringerEditionNumbers.isEmpty()) {
            ArrayList<String> items = getSpringerEditionNumbers();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedSpringerEditionNumbers.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedSpringerEditionNumbers, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedSpringerEditionNumbers.size() + " Springer edition numbers.");
        }
        return splittedSpringerEditionNumbers;
    }

    public static ArrayList<ArrayList<String>> getSplittedSpringerConfEventNames() throws FileNotFoundException {
        if (splittedSpringerConfEventNames.isEmpty()) {
            ArrayList<String> items = getSpringerConfEventNames();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedSpringerConfEventNames.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedSpringerConfEventNames, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedSpringerConfEventNames.size() + " Springer conf event names.");
        }
        return splittedSpringerConfEventNames;
    }

    public static ArrayList<ArrayList<String>> getSplittedSpringerConfEventLocations() throws FileNotFoundException {
        if (splittedSpringerConfEventLocations.isEmpty()) {
            ArrayList<String> items = getSpringerConfEventLocations();
            for (String item : items) {
                String[] split = split(item);
                ArrayList<String> splitList = new ArrayList<>();
                for (int i = 0; i < split.length; i++) {
                    splitList.add(split[i].replace(" ", "&nbsp;"));
                }
                splittedSpringerConfEventLocations.add(splitList);
            }
            // Sort array lists by number of tokens. Largest come first.
            Collections.sort(splittedSpringerConfEventLocations, new Comparator<ArrayList>() {
                public int compare(ArrayList a1, ArrayList a2) {
                    return a2.size() - a1.size(); // assumes you want biggest to smallest
                }
            });
            System.out.println("\tRead in " + splittedSpringerConfEventLocations.size() + " Springer conf event locations.");
        }
        return splittedSpringerConfEventLocations;
    }

    private static String replace(String aString) {

        String rval = aString
                .replace("ÃÂ¢ÃÂÃÂ", "'")
                .replace("ÃÂÃÂ€", "ä")
                .replace("à€Œ", "é")
                .replace("Ã©", "é")
                .replace("Ã€", "ä")
                .replace("ÃŒ", "ü")
                .replace("Ã¶", "ö")
                .replace("ÃÂ", "Ö")
                .replace("Ã§", "ç")
                .replace("Ãº", "ú")
                .replace("â", "-")
                .replace("Ã", "ß");

        if (rval.contains("< ---- >")) {
            rval = rval.substring(0, rval.indexOf("< ---- >"));
        }

        //System.out.println(rval);
        return rval;

    }

    private static String[] split(String string) {

        String[] field = string.split(
                "(?<=\\s)|(?=\\s)|" + // space
                "(?<=\\.)|(?=\\.)|" + // period
                "(?<=,)|(?=,)|" + // comma
                "(?<=\\))|(?=\\))|" + // closing bracket 
                "(?<=\\()|(?=\\()|" + // opening bracket
                "(?<=:)|(?=:)|" + // colon
                "(?<=-)|(?=-)|" + // hyphen
                "(?<=–)|(?=–)|" + // hyphen
                "(?<=“)|(?=“)|" + //
                "(?<=”)|(?=”)|"
                + "(?<=')|(?=')|"
                + "(?<=/)|(?=/)|"
                + "(?<=„)|(?=„)|"
                + "(?<=\\[)|(?=\\[)|" + // opening square bracket
                "(?<=\\])|(?=\\])" // closing square bracket
        );
        return field;
    }

}
