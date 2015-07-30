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

package de.acoli.informatik.uni.frankfurt.processing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Description:
 *
 * Utility class to obtain a list of tuples (token + label) for a CRF mallet
 * format file.
 *
 *
 *
 * @author niko
 */
public class CRFOutputReader {

    /**
     * Reads in a tagged CRF file and returns a list (for each reference) of
     * String array lists (for each token-label tuple).
     *
     * @param fileName
     * @param includeBOSandEOS
     * @return
     * @throws FileNotFoundException
     */
    public static ArrayList<ArrayList<String[]>> getPredictedTokensAndTagsForReferences(String fileName, boolean includeBOSandEOS) throws FileNotFoundException {

        ArrayList<ArrayList<String[]>> referencesPlusTokens = new ArrayList<>();

        Scanner crfOutputScan = new Scanner(new File(fileName));

        
                ArrayList<String[]> aRefToks = new ArrayList<String[]>();
        while (crfOutputScan.hasNextLine()) {

            String aLine = crfOutputScan.nextLine().trim();
            //System.out.println(aLine);

            
            if (aLine.length() > 0) {

                //System.out.println(aLine);
                String[] split = aLine.split("\\s");

                // Handle features.
                String goldlab = split[split.length - 2];
                String predlab = split[split.length - 1];
                
                String tok = split[0];

                String[] arr = new String[3];
                arr[0] = tok;
                arr[1] = goldlab;
                arr[2] = predlab;

                if (predlab.equals("<EOR>")) {
                    arr[0] = "EOR"; arr[1] = "<EOR>"; arr[2] = "<EOR>";    aRefToks.add(arr);
                    referencesPlusTokens.add(aRefToks);
                    aRefToks = new ArrayList<>();
                } else {
                    aRefToks.add(arr);

                }

                

                
            }
        }
        crfOutputScan.close();

        System.out.println("Read in " + referencesPlusTokens.size() + " complete references.");
        return referencesPlusTokens;
    }
}
