/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.acoli.informatik.uni.frankfurt.processing.bibfieldfeatures;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 *
 * @author niko
 */
public class FeaturesJournalTitleGapFiller {
    
//    static String IN = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/4500references/with_font_year_journaltitle/"
//            + "4500_biball_TRAIN.txt.fontfeats.txt.jourtit.txt";
//    
//    static String IN = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/4500references/with_font_journaltitle_dict_pubname_year/"
//            + "4500_TRAIN_font_jourtit_dict_pubname_year.txt";
//    
    
    static String IN = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/4500references/"
            + "alignment/reflexica_annotations_journame_dict_pubname_year_ednum_confeventname_seriestitle/"
            + "437_test.txt.seriestit.txt";
            //+ "4331_train.txt.seriestit.txt";
    
    
    public static void main(String[] args) throws FileNotFoundException {
        
        PrintWriter w = new PrintWriter(new File(IN + ".fix.txt"));
        Scanner s = new Scanner(new File(IN));
        while(s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            //System.out.println(aLine);
            if(aLine.length() > 0) {
                String[] split = aLine.split("\\s");
                
                
//                if(split.length == 3) {
//                    w.write(split[0] + " " + split[1] + " <isNotSJT> " + split[2] + "\n");
//                }
//                else if(split.length == 4) {
//                    w.write(aLine + "\n");
//                }
//                
                
//                
//                if(split.length == 4) {
//                    w.write(split[0] + " " + split[1] + " " + split[2] + " <notInDict> " + split[3] + "\n");
//                }
//                else if(split.length == 5) {
//                    w.write(aLine + "\n");
//                }
//                
//                
                
//                if(split.length == 5) {
//                    w.write(split[0] + " " + split[1] + " " + split[2] +  " " + split[3] +  " <isNotSPN> " + split[4] + "\n");
//                }
//                else if(split.length == 6) {
//                    w.write(aLine + "\n");
//                }
                
                
                
//                   
//                
//                if(split.length == 6) {
//                    w.write(split[0] + " " + split[1] + " " + split[2] +  " " + split[3] + " " + split[4] +  " <isNotYear> " + split[5] + "\n");
//                }
//                else if(split.length == 7) {
//                    w.write(aLine + "\n");
//                }
                
//                
//                   
//                
//                if(split.length == 7) {
//                    w.write(split[0] + " " + split[1] + " " + split[2] +  " " + split[3] + " " + split[4] +  " " + split[5] +  " <isNotSEN> " + split[6] + "\n");
//                }
//                else if(split.length == 8) {
//                    w.write(aLine + "\n");
//                }
                
                 
//                   
//                
//                if(split.length == 8) {
//                    w.write(split[0] + " " + split[1] + " " + split[2] +  " " + split[3] + " " + split[4] +  " " + split[5] + " " + split[6] +  " <isNotSCEN> " + split[7] + "\n");
//                }
//                else if(split.length == 9) {
//                    w.write(aLine + "\n");
//                }
                
                
                      
                
                if(split.length == 9) {
                    w.write(split[0] + " " + split[1] + " " + split[2] +  " " + split[3] + " " + split[4] +  " " + split[5] + " " + split[6] + " " + split[7]  + " <isNotSST> " + split[8] + "\n");
                }
                else if(split.length == 10) {
                    w.write(aLine + "\n");
                }
                
                else {
                    System.out.println("Something wrong with the input:");
                    System.out.println(aLine);
                    System.exit(0);
                }
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
