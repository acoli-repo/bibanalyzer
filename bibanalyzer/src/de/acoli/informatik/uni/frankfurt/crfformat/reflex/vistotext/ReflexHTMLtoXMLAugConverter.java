package de.acoli.informatik.uni.frankfurt.crfformat.reflex.vistotext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.commons.io.output.TeeOutputStream;

/**
 * Program reads a bibliography file in html (Springer Model CRF Visualization?)
 * format and writes it out as Springer CRF Token Format.
 *
 * @author Bastian_Kaiser
 */
public class ReflexHTMLtoXMLAugConverter {

    public void convertFiles(String pathName, String pattern, String outputFilePath) {

        String fileInProgres;
        File path = new File(pathName);

            try {

                
                File[] listOfFiles = path.listFiles();
                if (listOfFiles.length == 0) {
                    System.out.println("file not found.");
                }
                for (int i = 0; i < listOfFiles.length; i++) {
                    //System.out.println(listOfFiles[i].getName());
                    if (listOfFiles[i].isFile() && listOfFiles[i].getName().equals(pattern)) {
                        System.out.println(listOfFiles[i].getName());
                        fileInProgres = listOfFiles[i].getName();
                        InNOutFiles input = new InNOutFiles(pathName, pathName + fileInProgres, outputFilePath,   "TXT", 0);
                        input.convertBiblioFormat();
                        input.scanner.close();
                    }
                }
			//	fos.close();
                //	fos2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        
    }

    /**
     * Determines path and names of files.
     */
    public static void main(String[] args) {

        String folder = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/";
        String file = "450_biball_TEST_raw.txt.html.utf-8.html";

        String outputFilePath = "/home/niko/Desktop/outi.txt";
        
        ReflexHTMLtoXMLAugConverter myEntry = new ReflexHTMLtoXMLAugConverter();
        myEntry.convertFiles(folder, file, outputFilePath);
    }

}
