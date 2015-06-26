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
public class FeaturesFontAdder {
    
    static String IN = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/4500references/with_font/"
            + "4500_biball_TRAIN.txt";
    
    public static void main(String[] args) throws FileNotFoundException {
        
        PrintWriter w = new PrintWriter(new File(IN + ".fontfeats.txt"));
        Scanner s = new Scanner(new File(IN));
        while(s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            //System.out.println(aLine);
            if(aLine.length() > 0) {
                String[] split = aLine.split("\\s");
                if(split.length == 2) {
                    w.write(split[0] + " <noFont> " + split[1] + "\n");
                }
                else if(split.length == 3) {
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
