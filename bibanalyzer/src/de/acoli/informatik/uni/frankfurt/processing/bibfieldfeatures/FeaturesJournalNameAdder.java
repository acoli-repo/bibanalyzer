/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.acoli.informatik.uni.frankfurt.processing.bibfieldfeatures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 *
 * @author niko
 */
public class FeaturesJournalNameAdder {
    
    static String IN = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/4500references/with_font_year_journaltitle/"
            + "450_biball_TEST_raw.txt";
    
    static String JOURTIT_FILE = "/home/niko/Desktop/gitzeugs/bibanalyzer/bibanalyzer/dicts/SPRINGER/JournalTitleDB_Springer.txt";
    
    public static void main(String[] args) throws FileNotFoundException {
        
        // Read in journal titles.
        HashSet<String> journalTitles = new HashSet<String>();
        Scanner sJ = new Scanner(new File(JOURTIT_FILE));
        while(sJ.hasNextLine()) {
            String aLine = sJ.nextLine().trim();
            journalTitles.add(aLine);
        }
        sJ.close();
        System.out.println("read in " + journalTitles.size() + " journal titles.");
        
        
        // Read in raw file and decide which lines need to be augmented.
        ArrayList<String> rawRefs = new ArrayList<>();
        Scanner rawS = new Scanner(new File(IN));
        int numRawReferences = 0;
        while(rawS.hasNextLine()) {
            String aLine = rawS.nextLine().trim();
            rawRefs.add(aLine);
            numRawReferences++;
        }
        rawS.close();
        System.out.println("# raw references: "+ numRawReferences);
        
        
        // Check which lines need to be annotated with features.
        for(int i = 0; i < rawRefs.size(); i++) {
            String aRawRef = rawRefs.get(i);
            for(String aJournalTitle : journalTitles) {
                
            }
        }
        
        
        PrintWriter w = new PrintWriter(new File(IN + ".journaltitle.txt"));
        Scanner s = new Scanner(new File(IN));
        while(s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            //System.out.println(aLine);
            if(aLine.length() > 0) {
                String[] split = aLine.split("\\s");
                
                
                
            }
            else {
                w.write("\n");
            }
        }
        
        s.close();
        
        w.flush();
        w.close();
        
    }
    
    
    
}
