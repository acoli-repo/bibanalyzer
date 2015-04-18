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

package de.acoli.informatik.uni.frankfurt.classifier;
 
import de.acoli.informatik.uni.frankfurt.processing.ReferenceUtil;
import de.acoli.informatik.uni.frankfurt.aplusplusgenerator.APlusPlusBibTypeGeneratorFromCRFOutput;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.acoli.informatik.uni.frankfurt.processing.CRFOutputReader;
import de.acoli.informatik.uni.frankfurt.processing.CRFPostProcessor;
import de.acoli.informatik.uni.frankfurt.processing.bibfieldfeatures.DictReader;
import de.acoli.informatik.uni.frankfurt.processing.bibfieldfeatures.TokenizedPlaintextFeatureAnnotation;
import de.acoli.informatik.uni.frankfurt.processing.bibtypefeatures.BibtypeFeatureValuePairGeneratorFromPlaintextReferences;
import de.acoli.informatik.uni.frankfurt.processing.bibtypefeatures.BibtypeClassifierOutputReader;
import de.acoli.informatik.uni.frankfurt.visualization.CRFVisualizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  
 * This is the demo program of the bibanalyzer project.
 * 
 * 
 * The program adds bibliographic structure to a set of unstructured plaintext
 * references and generates Springer A++ format from the data.
 * (including an HTML visualization of the output).
 *
 * Detailed procedure of the program:
 *
 * 1. Read in the raw (plain text) reference data. 
 * 2. Tokenize them according to custom tokenization technique.
 * 3. Detect bibtype (BibArticle, BibBook, BibChapter) for each single reference.
 *      (Using features from dictionaries such as publisher locations, etc.)
 * 4. Augment tokenized data with features from dictionaries.
 * 5. Tag (augmented) data * separately * with all (six) possible (bibfield) CRF models. 
 *      exactly 3 DBLP and 3 SPRINGER models. (for three separate(!) analyses).
 *      
 * 5. Generate (six) CRF outputs (based on each single model).
 * 6. Based on bibtype predictions, combine CRF output into single output file containing
 *      all references and their correct bibtype.
 *      (Currently, this is done separately for SPRINGER and DBLP data).
 * 7. Convert output to Springer A++ format. (separately for SPRINGER and DBLP).
 * 8. Visualize CRF output (HTML).
 *
 *
 * @author niko
 */
public class BibAnalyzer {

    //public static final String INPUT_FILE_NAME = "2000_article_GOLD_raw.txt";
    
    
    
    //public static final String INPUT_FILE_NAME = "1082_Refs.utf8.htm.oneliners.txt";
    
    public static final String INPUT_FILE_NAME = "References_7518.txt";
    //public static final String INPUT_FILE_NAME = "input_plaintext_springer2.txt";
    //public static final String INPUT_FILE_NAME = "input_plaintext_dblp.txt";
    
    
    public static final String PATH_TO_PLAINTEXT_INPUT = "input/plaintext_references/";

    /**
     * Specifies the path to your plaintext input file.
     */
    public static final String PLAINTEXT_REFERENCES_INPUT_FILE = PATH_TO_PLAINTEXT_INPUT + INPUT_FILE_NAME;

    
    public static boolean useDBLPModels = false;
    public static boolean useSpringerModels = true;
    
    public static boolean useDBLPFeatureModels = false;
    public static boolean useSpringerFeatureModels = false;

    /**
     * Post-processing output is a rule-based component of the program to fix
     * CRF errors.
     */
    public static boolean postprocessOutput = true;

    // CRF tagging.
    public static final String TAGGER_JAR = "crf_tagger.jar";
    // Bibtype prediction.
    public static final String BIBCLASSIFIER_MODEL = "my.MEdictclassifier";

    // The path to the folder location of the models.
    public static final String PATH_TO_MODELS = "models/";

    // These four folders specify four differently trained CRF models for bibliographic
    // reference analysis.
    public static final String PATH_TO_DBLP_TRAINED_ONLYTOKENS = PATH_TO_MODELS + "DBLP_trained_onlytokens/";
    public static final String PATH_TO_DBLP_TRAINED_FEATURED = PATH_TO_MODELS + "DBLP_trained_features/";

    
    public static final String PATH_TO_SPRINGER_TRAINED_ONLYTOKENS = PATH_TO_MODELS + "SPRINGER_trained_onlytokens/";
    public static final String PATH_TO_SPRINGER_TRAINED_FEATURED = PATH_TO_MODELS + "SPRINGER_trained_features/";

    // The CRF models for Springer and DBLP data
    // which are either trained * with * or * without * features. (only tokens).
    // IMPORTANT NOTE:
    // Naming conventions for models:
    // [DBLP|SPRINGER]_[BibArticle|BibBook|BibChapter]_[onlytokens|featured][.*]
    // E.g., 
    // DBLP_BibArticle_onlytokens
    // SPRINGER_BibChapter_featured
    //
    // Three DBLP specific CRF models (trained only on tokens).
    public static final String CRF_MODEL_FILE_DBLP_A_ONLYTOK = "DBLP_BibArticle_onlytokens_12000";
    public static final String CRF_MODEL_FILE_DBLP_B_ONLYTOK = "DBLP_BibBook_onlytokens_12000";
    public static final String CRF_MODEL_FILE_DBLP_C_ONLYTOK = "DBLP_BibChapter_onlytokens_12000";
    
    // Three DBLP models trained on DBLP data * with additional features *.
    public static final String CRF_MODEL_FILE_DBLP_A_FEAT = "DBLP_BibArticle_featured-year-initials-dblpdict-dblpjournaltitle_12000";
    public static final String CRF_MODEL_FILE_DBLP_B_FEAT = "DBLP_BibBook_featured-year-initials-dblpdict-dblppubname_12000";
    public static final String CRF_MODEL_FILE_DBLP_C_FEAT = "DBLP_BibChapter_featured-year-initials-dblpdict-dblppubname_12000";

    
    // TODO: Improve with more data! > 12k references.
    
    // Three Springer specific CRF models (trained only on tokens).
    //public static final String CRF_MODEL_FILE_SPRINGER_A_ONLYTOK = "SPRINGER_BibArticle_onlytokens_2000";
    //public static final String CRF_MODEL_FILE_SPRINGER_B_ONLYTOK = "SPRINGER_BibBook_onlytokens_2000";
    //public static final String CRF_MODEL_FILE_SPRINGER_C_ONLYTOK = "SPRINGER_BibChapter_onlytokens_2000";

    public static final String CRF_MODEL_FILE_SPRINGER_A_ONLYTOK = "SPRINGER_BibArticle_onlytokens_15000";
    public static final String CRF_MODEL_FILE_SPRINGER_B_ONLYTOK = "SPRINGER_BibBook_onlytokens_15000";
    public static final String CRF_MODEL_FILE_SPRINGER_C_ONLYTOK = "SPRINGER_BibChapter_onlytokens_15000";

    
    
    
    
    // Three Springer models trained on Springer data * with additional features *.
    public static final String CRF_MODEL_FILE_SPRINGER_A_FEAT = "SPRINGER_BibArticle_featured-year-initials-dblpdict-springerjournaltitle_11000";
    public static final String CRF_MODEL_FILE_SPRINGER_B_FEAT = "SPRINGER_BibBook_featured-year-initials-dblpdict-springerpubname_11000";
    public static final String CRF_MODEL_FILE_SPRINGER_C_FEAT = "SPRINGER_BibChapter_featured-year-initials-dblpdict-springerpubname_11000";

    public static ArrayList<String> models = new ArrayList<String>(4);

    /**
     * Load CRF models. Note that you have to pick three different models for
     * each bibtype and data type, respectively.
     *
     * E.g., in this setting, we have chosen to load three DBLP models including
     * features and two SPRINGER models without features (trained only on
     * tokens) and a last SPRINGER model for BibChapters including features.
     */
    private static void loadModels(String realPath) {
        System.out.print("Loading models...");

        if (useDBLPModels) {
            // Three DBLP models without features.
            //models.add(realPath + PATH_TO_DBLP_TRAINED_ONLYTOKENS + CRF_MODEL_FILE_DBLP_A_ONLYTOK);
            //models.add(realPath + PATH_TO_DBLP_TRAINED_ONLYTOKENS + CRF_MODEL_FILE_DBLP_B_ONLYTOK);
            //models.add(realPath + PATH_TO_DBLP_TRAINED_ONLYTOKENS + CRF_MODEL_FILE_DBLP_C_ONLYTOK);

            // DBLP models with features.
            models.add(realPath + PATH_TO_DBLP_TRAINED_FEATURED + CRF_MODEL_FILE_DBLP_A_FEAT);
            models.add(realPath + PATH_TO_DBLP_TRAINED_FEATURED + CRF_MODEL_FILE_DBLP_B_FEAT);
            models.add(realPath + PATH_TO_DBLP_TRAINED_FEATURED + CRF_MODEL_FILE_DBLP_C_FEAT);
        }

        if (useSpringerModels) {
            // Three SPRINGER models.
            // For articles and books it is okay to have only token level features...
            models.add(realPath + PATH_TO_SPRINGER_TRAINED_ONLYTOKENS + CRF_MODEL_FILE_SPRINGER_A_ONLYTOK);
            models.add(realPath + PATH_TO_SPRINGER_TRAINED_ONLYTOKENS + CRF_MODEL_FILE_SPRINGER_B_ONLYTOK);
            models.add(realPath + PATH_TO_SPRINGER_TRAINED_ONLYTOKENS + CRF_MODEL_FILE_SPRINGER_C_ONLYTOK);

            // For BibChapters we need more features ...
            //models.add(realPath + PATH_TO_SPRINGER_TRAINED_FEATURED + CRF_MODEL_FILE_SPRINGER_A_FEAT);
            //models.add(realPath + PATH_TO_SPRINGER_TRAINED_FEATURED + CRF_MODEL_FILE_SPRINGER_B_FEAT);
            //models.add(realPath + PATH_TO_SPRINGER_TRAINED_FEATURED + CRF_MODEL_FILE_SPRINGER_C_FEAT);
        }

        System.out.println("...(" + models.size() + ")");

        // Check if models exist.
        for (String m : models) {
            File f = new File(m);
            if (f.exists()) {
                // okay.
            } else {
                System.out.println("Sorry, model " + m + " cannot be found.");
                System.out.println("Check your path or model names.");

            }
        }
    }

    /**
     * Statically load dictionaries once before the analysis.
     *
     * Dictionaries are used for: 
     * 1. Feature vector generation for bibtype
     * prediction. 
     * 2. Tokenization annotation for CRF tagging. 
     * 3. TODO:
     * Rule-based post-processing match of references. (E.g., "Springer" is
     * always a PublisherName).
     *
     * @throws FileNotFoundException
     */
    private static void loadDictionaries(String realPath) throws FileNotFoundException {
        System.out.println("Loading dictionaries...");

        DictReader dr = new DictReader();
        dr.setRealPathToDictionaries(realPath);
        
        // TODO: Add more if necessary.
        //DictReader.getLinuxWords();
        DictReader.getDBLPTitleWords();

        DictReader.getSplittedDBLPJournalTitles();
        DictReader.getSplittedSpringerJournalTitles();

        DictReader.getSplittedDBLPPublisherNames();
        DictReader.getSplittedSpringerPublisherNames();

        DictReader.getSplittedSpringerPublisherLocations();

        System.out.println("...done.");
    }

    /**
     * Clean up directory structure from previous runs. I.e. delete previously
     * generated A++, etc. files...
     */
    private static void cleanUpDirectories(String realPath) {
        System.out.println("Cleaning up directory structure...\n");
        ArrayList<String> pathsToBeDeleted = new ArrayList<>();

        // Delete everything under data/tokenized including subfolders.
        pathsToBeDeleted.add(realPath + "data/tokenized/");

        pathsToBeDeleted.add(realPath + "data/bibtypes/");

        pathsToBeDeleted.add(realPath + "data/tagged/per_model/");
        pathsToBeDeleted.add(realPath + "data/tagged/per_model_visualized/");
        pathsToBeDeleted.add(realPath + "data/tagged/combined/");
        pathsToBeDeleted.add(realPath + "data/tagged/combined_visualized/");
        
        // Careful. Only delete the contents of these two folders.
        // Do not the stylesheet.
        pathsToBeDeleted.add(realPath + "data/a++/per_model/");
        pathsToBeDeleted.add(realPath + "data/a++/combined/"); // Bibtypes combined into one file.

        for (String pTBD : pathsToBeDeleted) {
            ArrayList<File> toDelete = new ArrayList<>();
            // Collect all files within this directory.
            collectFiles(toDelete, pTBD);
            for (File f : toDelete) {
                f.delete();
            }
            toDelete.clear();
        }
    }

    
    
    
    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        
        // Maven.
        //String realPath = userDir + "/src/main/webapp/modules/";
        // Default.
        String realPath = userDir + "/";
        
        try {
            analyzeBibliography(args, realPath);
        } catch (IOException ex) {
            Logger.getLogger(BibAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
        
        
    /**
     * 
     * @param args
     * @param realPath
     * @throws FileNotFoundException
     * @throws IOException 
     */
     public static void analyzeBibliography(String[] args, String realPath) throws FileNotFoundException, IOException {
        
         // Use internal input file.
        if(args.length == 0) {
            // Take the input programmatically.
            args = new String[1];
            args[0] = realPath + PLAINTEXT_REFERENCES_INPUT_FILE;
            System.out.println("Predefined input file: " + args[0]);
        }
        // Use external input file.
        if(args.length == 1) {
            // Take input from command line.
            System.out.println("External input file: " + args[0]);
        }
        else {
            showHelp();
        }
        

        // Clean up directory structure,
        // i.e. delete files from previous runs.
        cleanUpDirectories(realPath);

        // Load all necessary statistical models for tagging.
        loadModels(realPath);

        // Load all dictionaries for annotation.
        loadDictionaries(realPath);

        // Read in all plaintext references from input file.
        System.out.print("Reading plaintext references...");
        // Get input file.
        String pathToRaw = args[0];
        
        ArrayList<String> plaintextReferences = ReferenceUtil.getPlaintextOnelinersFromPlaintextFile(pathToRaw);
        // Alternatively:
        //ArrayList<String> plaintextReferences = ReferenceUtil.getPlaintextOnelinersFromAPlusPlusFile(pathToRaw, true);
        System.out.println("(" + (plaintextReferences.size()) + " lines.)");

        // Write bibtype specific feature vectors to new file.
        String bibtypesFeatsFile = realPath + "data/bibtypes/bibtypes_featvec.txt";
        System.out.println("Generating bibtype feature vectors...");
        BibtypeFeatureValuePairGeneratorFromPlaintextReferences
                .convertPlaintextReferencesToFeatureVectors(plaintextReferences, bibtypesFeatsFile);

        // Do inference for every reference.
        System.out.println("Predicting bibtypes for references...");
        ArrayList<String> bibtypePredictions = classifyBibtypes(realPath, bibtypesFeatsFile);

        // Tokenize them with custom tokenization technique and write to file.
        System.out.println("Tokenizing...");
        String pathToTokenized = realPath + "data/tokenized/input_tok.txt";
        tokenize(plaintextReferences, pathToTokenized);

        // Add features for each of the three bibtypes.
        // Add features to tokenized articles.
        System.out.println("\tAdding features to tokenized data.");
        if (useDBLPModels) {
        // * Important note: * 
            // When you use a CRF model which has been trained on features,
            // then you need to add these features to the plaintext tokenized input
            // in order to get proper predictions when you run the tagger on it.
            // In our current setting, we use three feature-trained DBLP models.
            // Therefore, we add features to all three bibtypes.
            if(useDBLPFeatureModels) {
                System.out.println("\t\tAugmenting tokenized (DBLP) article data...");
                TokenizedPlaintextFeatureAnnotation.augmentTokenizedDBLPArticles(pathToTokenized);
                System.out.println("\t\tAugmenting tokenized (DBLP) book data...");
                TokenizedPlaintextFeatureAnnotation.augmentTokenizedDBLPBooks(pathToTokenized);
                System.out.println("\t\tAugmenting tokenized (DBLP) chapter data...");
                TokenizedPlaintextFeatureAnnotation.augmentTokenizedDBLPChapters(pathToTokenized);
            }
        }

        if (useSpringerModels) {
            // In our current setting, only the Springer BibChapters are augmented with features.
            // BibArticles and BibBooks use non-feature models:
            if(useSpringerFeatureModels) {
                System.out.println("\t\tAugmenting tokenized (Springer) article data...");
                TokenizedPlaintextFeatureAnnotation.augmentTokenizedSpringerArticles(pathToTokenized);
                // Add features to tokenized books.
                System.out.println("\t\tAugmenting tokenized (Springer) book data...");
                TokenizedPlaintextFeatureAnnotation.augmentTokenizedSpringerBooks(pathToTokenized);
                // Add features to tokenized chapters.
                System.out.println("\t\tAugmenting tokenized (Springer) chapter data...");
                TokenizedPlaintextFeatureAnnotation.augmentTokenizedSpringerChapters(pathToTokenized);
            }
        }

        // Tag the data with every model.
        System.out.println("Tagging...");
        String pathToTagged = realPath + "data/tagged/per_model/";
        if (useSpringerModels) {
            System.out.println("\tSpringer...");
            tag(realPath, pathToTokenized, pathToTagged, "SPRINGER");
        }
        if (useDBLPModels) {
            System.out.println("\tDBLP...");
            tag(realPath, pathToTokenized, pathToTagged, "DBLP");
        }

        // TODO: file separator.
        // Use bibtype classification to combine best CRF analyses for each 
        // reference into one single file.
        // Do this for Springer and DBLP data separately.
        System.out.println("Combining bibtypes into single CRF output file...");
        if (useSpringerModels) {
            combineBibtypes(bibtypePredictions, pathToTagged + "SPRINGER/", "SPRINGER");
        }
        if (useDBLPModels) {
            combineBibtypes(bibtypePredictions, pathToTagged + "DBLP/", "DBLP");
        }

        // Visualize the CRF output of every model.
        System.out.println("Visualizing CRF data...");
        if (useSpringerModels) {
            visualizeCRF(pathToTagged + "/SPRINGER/", "per_model_visualized/SPRINGER");
            // Also visualize combined data.
            visualizeCRF(realPath + "data/tagged/combined/SPRINGER/", "/combined_visualized/SPRINGER/");
        }
        if (useDBLPModels) {
            visualizeCRF(pathToTagged + "/DBLP/", "per_model_visualized/DBLP");
            // Also visualize combined data.
            visualizeCRF(realPath + "data/tagged/combined/DBLP/", "/combined_visualized/DBLP/");
        }

        // Generate A++ for every CRF output.
        if (useSpringerModels) {
            ArrayList<File> taggedByModelSpringer = new ArrayList<>();
            collectFiles(taggedByModelSpringer, pathToTagged + "/SPRINGER/");
            System.out.println("Converting to A++...(" + taggedByModelSpringer.size() + ") files.");
            generateAPlusPlus(taggedByModelSpringer, realPath + "data/a++/per_model/SPRINGER/");
        }
        if (useDBLPModels) {
            ArrayList<File> taggedByModelDblp = new ArrayList<>();
            collectFiles(taggedByModelDblp, pathToTagged + "/DBLP/");
            System.out.println("Converting to A++...(" + taggedByModelDblp.size() + ") files.");
            generateAPlusPlus(taggedByModelDblp, realPath + "data/a++/per_model/DBLP/");
        }

        // Also generate A++ for the combined CRF file.
        if (useSpringerModels) {
            generateAPlusPlusFinal(realPath + "data/tagged/combined/SPRINGER/tagged_combined.txt",
                    realPath + "data/a++/combined/SPRINGER/aplusplus_SPRINGER_combined.xml",
                    bibtypePredictions);
        }
        if (useDBLPModels) {
            generateAPlusPlusFinal(realPath + "data/tagged/combined/DBLP/tagged_combined.txt",
                    realPath + "data/a++/combined/DBLP/aplusplus_DBLP_combined.xml",
                    bibtypePredictions);
        }

        // We're done. (hopefully).
        System.out.println("... done (success).");
    }

    /**
     * Collect all (non-directory) files for a specific folder.
     *
     * @param fileList
     * @param path
     */
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

    /**
     *
     * @param args
     */
    private static void showHelp() {
        System.out.println("Please refer to the README.txt in this directory"
                + " for instructions on how to use the program.\n"
                + "In short:\n>>> java -jar bibanalyzer.jar inputfile.txt\n\n"
                + "where 'inputfile.txt' is the file (or path) containing line-separated plain-text bibliographic references.");
        System.exit(0);
    }

    /**
     * Tokenize plaintext references and write them to an output file.
     *
     * @param plaintextReferences
     * @param pathToTokenized, the output file path.
     * @throws FileNotFoundException
     */
    private static void tokenize(ArrayList<String> plaintextReferences, String pathToTokenized) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new File(pathToTokenized));
        for (String aRef : plaintextReferences) {
            ArrayList<String> tokens = ReferenceUtil.tokenize(aRef, true);
            for (String t : tokens) {
                writer.write(t + "\n");
                writer.flush();
            }
            writer.write("\n");
        }
        writer.close();
    }

    /**
     * Tag a list of tokenized references.
     *
     * @param pathToTokenized, the path to the text file containing the
     * tokenized data
     * @param pathToTagged, the output path to the tagged text file (will be
     * generated)
     * @param datatype, DBLP or SPRINGER
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void tag(String realPath, String pathToTokenized, String pathToTagged, String datatype) throws FileNotFoundException, IOException {
        // For every model
        for (String aModel : models) {

            if (aModel.contains(datatype)) {

                String modelname = aModel.substring(aModel.lastIndexOf("/") + 1);

                String tokFolder = pathToTokenized.substring(0, pathToTokenized.lastIndexOf("/") + 1);

                PrintWriter taggerWriter = null;
                ProcessBuilder pb = null;

                // TODO:
                // Convert to switch statement !
                String bibtype = "";
                String fileToTag = "";
                // We have a CRF model which has been trained on features (additional annotations),
                // not just plaintext.
                // I.e. use the feature_augmented tokenization files.
                if (aModel.contains("featured")) {
                    if (aModel.contains("_BibArticle_")) {
                        bibtype = "BibArticle";
                    } else if (aModel.contains("_BibBook_")) {
                        bibtype = "BibBook";
                    } else if (aModel.contains("_BibChapter_")) {
                        bibtype = "BibChapter";
                    }
                    // If we have a feature trained model, then we need to use the "final" augmented text file for tagging!
                    fileToTag = "feature_augmented_" + datatype + "/" + bibtype + "_FeatureAnnotationPipeline/out_final_temp.txt";
                    // It's just a plaintext trained model.
                    // Use plaintext tokenization as input to the tagger!
                } else if (aModel.contains("onlytokens")) {
                    fileToTag = "input_tok.txt";
                } else {
                    System.out.println("Error while tagging.");
                    System.exit(0);
                }

                String toTag = tokFolder + fileToTag;
                //System.out.println("toTag: "+ toTag);
                taggerWriter = new PrintWriter(new File(pathToTagged + datatype + "/tagged_" + modelname + ".txt"));
                pb = new ProcessBuilder("java", "-jar", realPath + TAGGER_JAR,
                        "--model-file", aModel, toTag);

                if (pb != null) {
                    //System.out.println("Tagging \"" + toTag + "\" with model \"" + modelname + "\"");

                    pb.directory(new File("./"));
                    Process p = pb.start();
                    String aLine = "";
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    while ((aLine = input.readLine()) != null) {
                        taggerWriter.write(aLine.trim() + "\n");
                    }
                    input.close();

                    taggerWriter.flush();
                    taggerWriter.close();
                }
            }
        }
    }

    /**
     * Generate Springer A++ format for a set of tagged files.
     *
     * @param taggedByModel, the set of files with the tagged data.
     * @param pathToAPlusPlus, the output path to which the data should be
     * written to.
     * @throws FileNotFoundException
     */
    private static void generateAPlusPlus(ArrayList<File> taggedByModel, String pathToAPlusPlus) throws FileNotFoundException {
        for (File f : taggedByModel) {

            String fn = f.getName();
            String bibtype = "none";
            String ext = "";

            // BibArticles.
            if (fn.contains("SPRINGER_BibArticle")) {
                bibtype = "BibArticle";
                if (fn.contains("featured")) {
                    ext = "springer_A_featured_";
                }
                if (fn.contains("onlytokens")) {
                    ext = "springer_A_onlytokens_";
                }
            }
            if (fn.contains("DBLP_BibArticle")) {
                bibtype = "BibArticle";
                if (fn.contains("featured")) {
                    ext = "DBLP_A_featured_";
                }
                if (fn.contains("onlytokens")) {
                    ext = "DBLP_A_onlytokens_";
                }
            }

            // BibBooks.
            if (fn.contains("SPRINGER_BibBook")) {
                bibtype = "BibBook";
                if (fn.contains("featured")) {
                    ext = "springer_B_featured_";
                }
                if (fn.contains("onlytokens")) {
                    ext = "springer_B_onlytokens_";
                }
            }
            if (fn.contains("DBLP_BibBook")) {
                bibtype = "BibBook";
                if (fn.contains("featured")) {
                    ext = "DBLP_B_featured_";
                }
                if (fn.contains("onlytokens")) {
                    ext = "DBLP_B_onlytokens_";
                }
            }

            // BibChapters.
            if (fn.contains("SPRINGER_BibChapter")) {
                bibtype = "BibChapter";
                if (fn.contains("featured")) {
                    ext = "springer_C_featured_";
                }
                if (fn.contains("onlytokens")) {
                    ext = "springer_C_onlytokens_";
                }
            }
            if (fn.contains("DBLP_BibChapter")) {
                bibtype = "BibChapter";
                if (fn.contains("featured")) {
                    ext = "DBLP_C_featured_";
                }
                if (fn.contains("onlytokens")) {
                    ext = "DBLP_C_onlytokens_";
                }
            }

            PrintWriter w = new PrintWriter(new File(pathToAPlusPlus + ext + "aplusplus_" + bibtype + ".xml"));
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<?xml-stylesheet type=\"text/css\" href=\"../../stylesheet/References_Stylesheet.css\"?>\n"
                    + "<Bibliography ID=\"Bib\">\n"
                    + "<Heading>References</Heading>\n\n");

            // Get tokens and tags for each list of references.
            ArrayList<ArrayList<String[]>> referencesPlusTokensAndTags = CRFOutputReader.getPredictedTokensAndTagsForReferences(f.getPath(), false);

            int crCounter = 0;

            for (ArrayList<String[]> aReferencePlusTokensAndTags : referencesPlusTokensAndTags) {
                APlusPlusBibTypeGeneratorFromCRFOutput.generateAPlusPlus(w, bibtype, aReferencePlusTokensAndTags, crCounter);
                crCounter++;
            }

            w.write("</Bibliography>\n");

            w.flush();
            w.close();

        }
    }

    @Deprecated
    private static ArrayList<String> generateBestAPlusPlusOutOfAllPossibilities(ArrayList<File> aplusplusByModel) throws FileNotFoundException {
        // Map of ID to 3-tuple. (each tuple represents an A++ entry for the same reference / ID.)
        LinkedHashMap<String, ArrayList<String>> aPlusPlusChain = new LinkedHashMap<String, ArrayList<String>>();
        for (File f : aplusplusByModel) {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String aLine = s.nextLine().trim();
                if (aLine.contains("<Citation ID=\"")) {
                    // Get ID:
                    String ID = aLine.substring(aLine.indexOf("<Citation ID=\"") + 14, aLine.indexOf("_"));
                    //System.out.println("ID:>" + ID + "<");
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        sb.append(aLine + "\n");
                        aLine = s.nextLine();
                        if (aLine.length() == 0) {
                            // Add a++ reference to list.
                            ArrayList<String> aPlusPlusReferencesSoFar = null;
                            if (aPlusPlusChain.containsKey(ID)) {
                                // Get old contents.
                                aPlusPlusReferencesSoFar = aPlusPlusChain.get(ID);
                            } else {
                                // Found a new (first) a++ reference for that sentence.
                                aPlusPlusReferencesSoFar = new ArrayList<>();
                            }
                            // Add new A++ reference.
                            aPlusPlusReferencesSoFar.add(sb.toString());
                            // Add back to map.
                            aPlusPlusChain.put(ID, aPlusPlusReferencesSoFar);
                            break;
                        }
                    }
                }
            }
            s.close();
        }

        // Collect best a++ generations for every reference.
        ArrayList<String> best = new ArrayList<>();
        // Heristics:
        // Idea and some heuristic rules.
        // Check how many different! fields are filled.
        // TODO: Reject empty FamilyName tags.
        // TODO: Initials should be one of [A-Za-z]    
        // Include title including spaces.
        for (ArrayList<String> anAPlusPlusTuple : aPlusPlusChain.values()) {

            int bestAPlusPlus = -1;
            int globalFilledTags = 0;
            for (int i = 0; i < anAPlusPlusTuple.size(); i++) {
                String anAPlusPlus = anAPlusPlusTuple.get(i);
                //System.out.println("Analyzing a++: " + anAPlusPlus);

                // Find patterns of the form: >stuffinbetween</
                Pattern pattern = Pattern.compile(">([^\\s])+?</");
                Matcher matcher = pattern.matcher(anAPlusPlus);
                int foundFilledTags = 0;
                while (matcher.find()) {
                    foundFilledTags++;
                    //System.out.println("Match: " + matcher.group());
                }

                if (foundFilledTags >= globalFilledTags) {
                    globalFilledTags = foundFilledTags;
                    bestAPlusPlus = i;
                }
            }

            // Best A++
            //System.out.println(bestAPlusPlus);
            best.add(anAPlusPlusTuple.get(bestAPlusPlus));

        }
        return best;
    }

    @Deprecated
    private static void writeAPlusPlusFinalToFile(ArrayList<String> best, String pathToAPlusPlusFinal) throws FileNotFoundException {
        PrintWriter w = new PrintWriter(new File(pathToAPlusPlusFinal + "bibtypes_combined.xml"));
        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<?xml-stylesheet type=\"text/css\" href=\"../../stylesheet/References_Stylesheet.css\"?>\n"
                + "<Bibliography ID=\"FinalCombinedBibTypes\">\n"
                + "<Heading>References</Heading>\n\n");
        for (String aBest : best) {
            w.write(aBest + "\n");
        }
        w.write("</Bibliography>\n");
        w.flush();
        w.close();
    }

    /**
     * Takes three input files (a CRF analysis for BibArticles, BibBooks and
     * BibChapters) and a vector of bibtype predictions and outputs a single CRF
     * analysis based on the predictions.
     *
     *
     * @param bibtypePredictions
     * @param pathToTagged
     * @param datatype
     * @throws FileNotFoundException
     */
    private static void combineBibtypes(ArrayList<String> bibtypePredictions, String pathToTagged, String datatype) throws FileNotFoundException {
        // Collect all crf files.
        ArrayList<File> allCrfFiles = new ArrayList<>();
        collectFiles(allCrfFiles, pathToTagged);
        Collections.sort(allCrfFiles); // Sort ! A first, B second, C third.

        ArrayList<ArrayList<ArrayList<String[]>>> allAnalyses = new ArrayList<ArrayList<ArrayList<String[]>>>();

        for (File crfFile : allCrfFiles) {
            //System.out.println(allCrfFiles);
            ArrayList<ArrayList<String[]>> anAnalysis = CRFOutputReader.getPredictedTokensAndTagsForReferences(crfFile.getAbsolutePath(), true);
            allAnalyses.add(anAnalysis);
        }

        //System.out.println(allAnalyses.size() + " analyses collected.");
        ArrayList<ArrayList<String[]>> combinedAnalysis = new ArrayList<>();
        // For every bibtype prediction.
        for (int b = 0; b < bibtypePredictions.size(); b++) {
            String bibtype = bibtypePredictions.get(b);
            // Careful. Limited to only three analyses. (one for articles. one for books. one for chapters).
            // TODO: Include multiple analyses for the same bibtype.
            switch (bibtype) {
                case "1":
                    ArrayList<String[]> articleParsedReference = allAnalyses.get(0).get(b);
                    combinedAnalysis.add(articleParsedReference);
                    break;
                case "0":
                    ArrayList<String[]> bookParsedReference = allAnalyses.get(1).get(b);
                    combinedAnalysis.add(bookParsedReference);
                    break;
                case "-1":
                    ArrayList<String[]> chapterParsedReference = allAnalyses.get(2).get(b);
                    combinedAnalysis.add(chapterParsedReference);
                    break;
                default:
                    break;
            }
        }

        // Post-process output.
        if (postprocessOutput) {
            CRFPostProcessor.postProcessCRFOutput(combinedAnalysis, bibtypePredictions);
        }

        // Write combined data to new file.
        PrintWriter w = new PrintWriter(new File(pathToTagged + "/../../" + "combined/" + datatype + "/tagged_combined.txt"));
        for (ArrayList<String[]> aTaggedRef : combinedAnalysis) {
            for (String[] tokPlusLab : aTaggedRef) {
                w.write(tokPlusLab[0] + " " + tokPlusLab[1] + "\n");
            }
            w.write("\n");
        }
        w.flush();
        w.close();

    }

    /**
     *
     * @param pathToTagged
     * @param outputFolder
     * @throws FileNotFoundException
     */
    private static void visualizeCRF(String pathToTagged, String outputFolder) throws FileNotFoundException {
        // Collect all crf files.
        ArrayList<File> allCrfFiles = new ArrayList<>();
        collectFiles(allCrfFiles, pathToTagged);

        // Visualize them.
        for (File crfFile : allCrfFiles) {
            String outputFileName = crfFile.getPath().substring(0, crfFile.getPath().lastIndexOf("/")) + "/../../" + outputFolder + "/" + crfFile.getName() + "_vis.html";
            CRFVisualizer.visualizeCRFOutput(crfFile.getAbsolutePath(), outputFileName);
        }

    }

    /**
     * Get bibtype predictions for plaintext references based on the feature
     * vector file.
     *
     * @param bibtypesFeatsFile
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static ArrayList<String> classifyBibtypes(String realPath, String bibtypesFeatsFile) throws FileNotFoundException, IOException {

        String bibtypesClassified = realPath + "data/bibtypes/bibtypes_classified.txt";
        String bibtypesPredicted = realPath + "data/bibtypes/bibtypes_predicted.txt";

        PrintWriter classifyBibtypesWriter = new PrintWriter(new File(bibtypesClassified));
        ProcessBuilder pb = new ProcessBuilder(
                realPath + "mallet-2.0.7/bin/mallet",
                "classify-file",
                "--input",
                bibtypesFeatsFile,
                "--output",
                "-",
                "--classifier",
                realPath + "models/bibtype_model/" + BIBCLASSIFIER_MODEL
        );

        pb.directory(new File("./"));
        Process p = pb.start();
        String aLine = "";
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while ((aLine = input.readLine()) != null) {
            classifyBibtypesWriter.write(aLine.trim() + "\n");
        }
        input.close();

        classifyBibtypesWriter.flush();
        classifyBibtypesWriter.close();

        // Write final bibtype predictions to new file
        // and return predicted labels for all plaintext references.
        return BibtypeClassifierOutputReader.getPredictions(bibtypesFeatsFile, bibtypesClassified, bibtypesPredicted);

    }

    /**
     *
     * @param taggedCombinedCrfData
     * @param outputAPlusPlus
     * @param bibtypePredictions
     * @throws FileNotFoundException
     */
    private static void generateAPlusPlusFinal(String taggedCombinedCrfData, String outputAPlusPlus,
            ArrayList<String> bibtypePredictions) throws FileNotFoundException {
        PrintWriter w = new PrintWriter(new File(outputAPlusPlus));
        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<?xml-stylesheet type=\"text/css\" href=\"../../stylesheet/References_Stylesheet.css\"?>\n"
                + "<Bibliography ID=\"Bib\">\n"
                + "<Heading>References</Heading>\n\n");

        ArrayList<ArrayList<String[]>> referencesPlusTokens = CRFOutputReader.getPredictedTokensAndTagsForReferences(taggedCombinedCrfData, false);

        int crCounter = 0;
        // Cafeful: Demo client assumes that every bibtype is a "BibArticle".
        for (int i = 0; i < referencesPlusTokens.size(); i++) {
            ArrayList<String[]> aReferencePlusTokens = referencesPlusTokens.get(i);
            String aBibtype = "";
            switch (bibtypePredictions.get(i)) {
                case "1":
                    aBibtype = "BibArticle";
                    break;
                case "0":
                    aBibtype = "BibBook";
                    break;
                case "-1":
                    aBibtype = "BibChapter";
                    break;
            }
            APlusPlusBibTypeGeneratorFromCRFOutput.generateAPlusPlus(w, aBibtype, aReferencePlusTokens, crCounter);
            crCounter++;
        }
        w.write("</Bibliography>\n");

        w.flush();
        w.close();

    }
    
    
    /**
     * 
     * @param realPath
     * @param type is either SPRINGER or DBLP
     * @return
     * @throws FileNotFoundException 
     */
    public static String getFinalAPlusPlus(String realPath, String type) throws FileNotFoundException {
        Scanner s = new Scanner(new File(realPath + "/data/a++/combined/" + type + "/aplusplus_" + type + "_combined.xml"));
        StringBuilder sb = new StringBuilder();
        while(s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            sb.append(aLine + "\n");
        }
        s.close();
        return sb.toString();
    }
    
}
