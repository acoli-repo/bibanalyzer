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
public class Starter {

    public void convertFiles(String pathName, String pattern) {

        String fileInProgres;
        File path = new File(pathName);

        if (path.isDirectory()) {
            FileOutputStream fos;
            TeeOutputStream myOut;
            PrintStream ps;
            FileOutputStream fos2;
            TeeOutputStream myOut2;
            PrintStream ps2;
			//File logFile = new File("./Log/Output.log");
            //File logFile2 = new File("./Log/Error.log");
            try {
//				fos = new FileOutputStream(logFile);
//				fos2 = new FileOutputStream(logFile2);
//				// we will want to print in standard "System.out" and in "file"
//				myOut = new TeeOutputStream(System.out, fos);
//				myOut2 = new TeeOutputStream(System.err, fos2);
//				ps = new PrintStream(myOut);
//				ps2 = new PrintStream(myOut2);
//				System.setOut(ps);
//				System.setErr(ps2);
                File[] listOfFiles = path.listFiles();
                if (listOfFiles.length == 0) {
                    System.out.println("file not found.");
                }
                for (int i = 0; i < listOfFiles.length; i++) {
                    //System.out.println(listOfFiles[i].getName());
                    if (listOfFiles[i].isFile() && listOfFiles[i].getName().equals(pattern)) {
                        System.out.println(listOfFiles[i].getName());
                        fileInProgres = listOfFiles[i].getName();
                        InNOutFiles input = new InNOutFiles(pathName, pathName + fileInProgres, "TXT", 0);
                        input.convertBiblioFormat();
                        input.scanner.close();
                    }
                }
			//	fos.close();
                //	fos2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Path " + pathName + " is not a directory.");
        }
    }

    /**
     * Determines path and names of files.
     */
    public static void main(String[] args) {

        String folder = "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/bibHtml2TokenFormat/in/";
        String file = "450_biball_TEST_raw.txt.html.utf-8.html";

        Starter myEntry = new Starter();

        myEntry.convertFiles(folder, file);
    }

}
