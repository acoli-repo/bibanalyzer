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
public class GeneralHighLevelFeaturesGapFiller {
    
    
    static String IN = "C:\\Users\\Niko\\Documents\\"
            + "Springer_Reflexica_StatistischeModelle\\einModell3Bibtypes\\training\\total\\"
            + "crf++\\12900references\\fromSpringerDUMP\\total\\aligned_with_reflexica\\only_reflexica_annotations\\features\\"
            + "train_12561.txt.jt.txt.fix.ed.txt.fix.dict.fix.year";
    
    static String OUT = "C:bla";
    
    public static void main(String[] args) throws FileNotFoundException {
        fillGaps(IN, OUT, "<isNotYear>", true);
    }
    
    public static void fillGaps(String inputFile, String outputFile, String gapFiller, boolean isTrainingData) throws FileNotFoundException {
        
        PrintWriter w = new PrintWriter(new File(outputFile));
        
        int lineNum = 0;
        int numElements = -1;
        
        Scanner s = new Scanner(new File(inputFile));
        while(s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            //System.out.println(aLine);
            if(aLine.length() > 0) {
                String[] split = aLine.split("\\s");
                
                if(lineNum == 0) {
                    numElements = split.length;
                    lineNum++;
                }
     
                if (split.length == numElements) {
                    if (isTrainingData) {
                        for (int i = 0; i < split.length - 1; i++) {
                            w.write(split[i] + " ");
                        }
                        w.write(gapFiller + " ");
                        w.write(split[split.length - 1] + "\n"); // Gold label.
                    } else {
                        // It has no gold label. Just print everything and add a not present
                        // label to the end.
                        for (int i = 0; i < split.length; i++) {
                            w.write(split[i] + " ");
                        }
                        w.write(gapFiller + "\n");
                    }
                }
                // We have the feature.
                else if(split.length == numElements+1) {
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
