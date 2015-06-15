/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.acoli.informatik.uni.frankfurt.crfformat.reflex.vistotext;

import de.acoli.informatik.uni.frankfurt.crfformat.PlaintextReferenceStringToMalletCRFFormatConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * 
 * 
 *
 * @author niko
 */
public class ReflexXMLAugToMalletCRF {
    
   public static void main(String[] args) throws FileNotFoundException {
       
       String inputFile = 
               "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/out/"
               + "References_7518_utf-8.txt.html";
       
       Scanner s = new Scanner(new File(inputFile));
       PrintWriter w = new PrintWriter(new File("/home/niko/Desktop/out.txt"));
       
       while(s.hasNextLine()) {
           String aLine = s.nextLine().trim();
           PlaintextReferenceStringToMalletCRFFormatConverter.convertReferenceString(aLine, w);
       }
       
       s.close();
       w.flush();
       w.close();
       
       
       
   } 
    
    
    
}
