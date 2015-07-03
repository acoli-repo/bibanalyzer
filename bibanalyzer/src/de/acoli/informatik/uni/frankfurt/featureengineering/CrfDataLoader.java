package de.acoli.informatik.uni.frankfurt.featureengineering;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CrfDataLoader {

	public static void main(String[] args) throws FileNotFoundException {
		String filePath = "model_optimizations/testing/train450_with_features2.txt";
		String outFilePath = "model_optimizations/testing/train450_with_features4.txt";
		List<Reference> references = loadCRFData(filePath);

		//addLowerCaseFeature(references);
		//addNumericFeatures(references);
		//addOrthographicFeatures(references);
		//addTokenPositionFeatures(references);
		addTokenBasedFeatures(references);
		
		
		printCRFData(references);
		saveCRFData(outFilePath, references);

	}

	private static void addTokenBasedFeatures(List<Reference> references) {
		for(Reference reference : references) {
			reference.addLowerCaseFeatures();
			reference.addNumericFeatures();
			reference.addOrthographicFeatures();
			reference.addTokenPositionFeatures();
		}
	}

	private static void addOrthographicFeatures(List<Reference> references) {
		for(Reference reference : references) {
			reference.addOrthographicFeatures();
		}
	}

	private static void addTokenPositionFeatures(List<Reference> references) {
		for(Reference reference : references) {
			reference.addTokenPositionFeatures();
		}
	}

	private static void addNumericFeatures(List<Reference> references) {
		for(Reference reference : references) {
			reference.addNumericFeatures();
		}
	}

	private static void printCRFData(List<Reference> references) {
		for(Reference reference : references) {
			System.out.println(reference.toPrintString());
			//System.out.println();
		}
	}

	private static void addLowerCaseFeature(List<Reference> references) {
		for(Reference reference : references) {
			reference.addLowerCaseFeatures();
		}
		
	}

	private static void saveCRFData(String outFilePath,
			List<Reference> references) throws FileNotFoundException {
		try (PrintStream out = new PrintStream(new FileOutputStream(outFilePath))) {
			for(Reference reference : references) {
				out.println(reference);
				out.println();
			}
			out.close();
		}
		
	}

	private static List<Reference> loadCRFData(String filePath) {
		// Training File Path
		Path path = Paths.get(filePath);
		Charset charset = Charset.forName("UTF-8");
		String line ;
		List<Reference> references = new ArrayList<Reference>();

		try (BufferedReader reader = Files.newBufferedReader(path , charset)) {
			Reference reference = new Reference();
			while ((line = reader.readLine()) != null ) {
				//separate all fields into string array
				//String[] lineVariables = line.split(" "); 
				//System.out.println(line);
				if(line.isEmpty()) {
					continue;
				}
				if(line.startsWith("BOR ") && line.endsWith(" <BOR>")) {
					reference = new Reference();
				}
				

				String[] tokens = line.split(" ");
				if(tokens.length == 2) {
					reference.addToken(tokens[0], tokens[1]);
				}
				else {
					reference.addToken(tokens);
				}
				
				//for(int i=0; i<tokens.length; i++) {
				//	reference.addToken(tokens[0], tokens[1]);
				//}
				
				if(line.startsWith("EOR ") && line.endsWith(" <EOR>")) {
					
					references.add(reference);
					//System.out.println(reference);
					//System.out.println();
				}
				
			}
		} catch (IOException e) {
			System.err.println(e);
		}
		return references;

	}

}
