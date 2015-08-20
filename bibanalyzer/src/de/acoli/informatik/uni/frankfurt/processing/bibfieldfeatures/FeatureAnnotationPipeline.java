/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.acoli.informatik.uni.frankfurt.processing.bibfieldfeatures;

import de.acoli.informatik.uni.frankfurt.featureengineering.FeatureExtractor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * 
 * Description: Takes one CRF format input file with
 * 
 * tokens ReflexicaAnnotation (two features) as input
 * 
 * and annotates
 * 
 * 1.) high level Springer dict features (Springer JournalTitle, Springer EditionNumber)
 * 2.) high level dictionary features (DBLP words)
 * 3.) Suman's low level features.
 *
 * @author Niko
 */
public class FeatureAnnotationPipeline {
    
    public static String inputDir = "C:/Users/niko/Desktop/featurePipeline/";
    public static String inputFile = "test_1255.txt";
    
    public static void main(String[] args) throws FileNotFoundException {
        addFeaturesToFile(inputDir, inputFile);
    }
    
    public static void addFeaturesToFile(String anInputDir, String anInputFileName) throws FileNotFoundException {
        
        String file = anInputDir + anInputFileName;
        
        // 1. Add Journal Titles.
        GeneralHighLevelFeaturesAdder.addDataBaseFeatures(file, file + "1.txt.tmp", DictReader.JOURTIT_SPRINGER, "", true);
        GeneralHighLevelFeaturesGapFiller.fillGaps(file + "1.txt.tmp", file + "2.txt.tmp", "<isNotJT>", true);
        
        // 2. Add Edition Numbers.
        GeneralHighLevelFeaturesAdder.addDataBaseFeatures(file + "2.txt.tmp", file + "3.txt.tmp", DictReader.EDNUM_SPRINGER, "", true);
        GeneralHighLevelFeaturesGapFiller.fillGaps(file + "3.txt.tmp", file + "4.txt.tmp", "<isNotED>", true);
        
        // 3. Add Dictionary words.
        FeaturesAdderDictionaryWords.addDictionaryFeature(file + "4.txt.tmp", file + "5.txt.tmp", "DBLP");
        GeneralHighLevelFeaturesGapFiller.fillGaps(file + "5.txt.tmp", file + "6.txt.tmp", "<isNotDICT>", true);
        
        FeaturesAdderLowLevel.addYearFeature(file + "6.txt.tmp", file + "7.txt.tmp");
        GeneralHighLevelFeaturesGapFiller.fillGaps(file + "7.txt.tmp", file + "8.txt.tmp", "<isNotYear>", true);
        
        FeatureExtractor.addSumansLowLevelFeatures(file + "8.txt.tmp", file + "9.txt.tmp");
                
        // Do cleanup with newlines.
        Util.cleanUp(file + "9.txt.tmp", file  + "_anno.txt");
        deleteTemporaryFiles(anInputDir, anInputFileName);
        
    }
    
    
    private static void deleteTemporaryFiles(String anInputDir, String anInputFileName) {
        System.out.println("Deleting files...\n");
        ArrayList<String> pathsToBeDeleted = new ArrayList<>();
        pathsToBeDeleted.add(anInputDir);

       
        for (String pTBD : pathsToBeDeleted) {
            ArrayList<File> toDelete = new ArrayList<>();
            // Collect all files within this directory.
            collectFiles(toDelete, pTBD);
            for (File f : toDelete) {
                if(f.getName().startsWith(anInputFileName) && 
                        f.getName().endsWith(".tmp")) {
                    f.delete();
                }
            }
            toDelete.clear();
        }
    }
    
    private static void collectFiles(ArrayList<File> fileList, String path) {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                collectFiles(fileList, f.getAbsolutePath());
            } else {
                fileList.add(f);
            }
        }
    }
    
    
}
