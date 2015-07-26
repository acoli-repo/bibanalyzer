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
 * @author Niko
 */
public class Util {
    
    static String IN = "C:\\Users\\Niko\\Documents\\Springer_Reflexica_StatistischeModelle\\einModell3Bibtypes\\training\\total\\crf++\\12900references\\fromSpringerDUMP\\total\\aligned_with_reflexica\\only_reflexica_annotations\\features\\"
            + "train_12561.final.txt";
    
    static String OUT = "C:/Users/niko/Desktop/out.txt";
    
    public static void main(String[] args) throws FileNotFoundException {
        
    }
    
    public static void cleanUp(String in, String out) throws FileNotFoundException {
        
        PrintWriter w = new PrintWriter(new File(out));
        int linenum = 0;
        Scanner s = new Scanner(new File(in));
        while(s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            linenum++;
            String[] l = aLine.split("\\s");
            
            w.write(aLine + "\n");
            
        }
        s.close();
        w.flush();
        w.close();
    }
    
}
