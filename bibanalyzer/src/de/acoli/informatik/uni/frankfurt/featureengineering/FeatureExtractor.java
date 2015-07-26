package de.acoli.informatik.uni.frankfurt.featureengineering;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Add features to the CRF data (in CRF++ format).
 * 
 * @author Suman Kumar Chalavadi <Suman.Chalavadi@crest.in>
 *
 */
public class FeatureExtractor {
    
    
	public static void main(String[] args) throws FileNotFoundException {
            String DIR = "C:\\Users\\Niko\\Documents\\"
            + "Springer_Reflexica_StatistischeModelle\\einModell3Bibtypes\\training\\total\\"
            + "crf++\\12900references\\fromSpringerDUMP\\total\\aligned_with_reflexica\\only_reflexica_annotations\\features\\";
            
            String input = DIR + "train_12561.txt.jt.txt.fix.ed.txt.fix.dict.fix.year.fix";
            String output = DIR + "train_12561.txt.jt.txt.fix.ed.txt.fix.dict.fix.year.fix.final";
            addSumansLowLevelFeatures(input, output);
        }
        
        public static void addSumansLowLevelFeatures(String inputFile, String outputFile) throws FileNotFoundException {
		
                //String filePath = "model_optimizations/testing/4500_biball_TRAIN.txt";
		//String outFilePath = "model_optimizations/testing/train4500_with_features3a.txt";
                
            String filePath = inputFile;
            String outFilePath = outputFile;
            
		List<Reference> references = CrfDataHandler.loadCRFData(filePath);

		//addLowerCaseFeature(references);
		//addNumericFeatures(references);
		//addOrthographicFeatures(references);
		//addTokenPositionFeatures(references);
		addTokenBasedFeatures(references);
		
		
		//CrfDataHandler.printCRFData(references);
                CrfDataHandler.saveCRFData(outFilePath, references);

	}

	/**
	 * Add all token based features (low level) in one go for a given reference list.
	 * 
	 * @param references
	 */
	private static void addTokenBasedFeatures(List<Reference> references) {
		for(Reference reference : references) {
			reference.addLowerCaseFeatures();
			reference.addNumericFeatures();
			reference.addOrthographicFeatures();
			reference.addTokenPositionFeatures();
			//reference.addTokenSizeFeatures();
		}
	}

	/**
	 * Adds Orthographic features like whether the token is in all caps or title case
	 * or camel case or no case detected (in case of no alphabets found).
	 * 
	 * @param references
	 */
	private static void addOrthographicFeatures(List<Reference> references) {
		for(Reference reference : references) {
			reference.addOrthographicFeatures();
		}
	}

	/**
	 * Adds the relative position of the token w.r.t reference as a feature.
	 * The reference is divided into 12 buckets.
	 * 
	 * @param references
	 */
	private static void addTokenPositionFeatures(List<Reference> references) {
		for(Reference reference : references) {
			reference.addTokenPositionFeatures();
		}
	}

	/**
	 * Checks whether a token is a number or possibly year or not a number.
	 * Adds a feature with three possible values. <NotNumber>, <Number>, <PossiblyYear>
	 * 
	 * @param references
	 */
	private static void addNumericFeatures(List<Reference> references) {
		for(Reference reference : references) {
			reference.addNumericFeatures();
		}
	}
	
	/**
	 * Adds the lowercased form of the token as feature.
	 * @param references
	 */
	private static void addLowerCaseFeature(List<Reference> references) {
		for(Reference reference : references) {
			reference.addLowerCaseFeatures();
		}
		
	}


}
