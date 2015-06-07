package de.acoli.informatik.uni.frankfurt.crfformat.reflex.vistotext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class sets basic parameters for reading in a file.
 */
public abstract class ReadFile {

    File file;
    Scanner scanner;
    String filename;
    String fileName;
    Reader r = null;
    BufferedReader in = null;
    String fileType;
    String[] defaultCharSets = new String[]{"ISO-8859-15", "UTF-8", "UTF-16", "windows-1252",
        "Big5", "Cp1252"};
    int charSetId;
    protected HashMap<String, String> tags = new HashMap<String, String>();
    String fileNameWOExt;

    /**
     * Checks if file at filename location or in certain relative std folder
     * exists and sets/creates required attributes.
     *
     * @param filename
     * @param fileType
     * @param charSetId
     */
    protected ReadFile(String filename, String fileType, int charSetId) {

        this.charSetId = charSetId;
        this.fileType = fileType;
        this.filename = filename;
        file = new File(filename);
        this.fileName = file.getName();

        if (!file.exists()) {
            System.out.println("Can't find file " + filename);
            System.exit(1);
        }

        switch (fileType) {
            case ("XML"):
                try {
                    fileNameWOExt = file.getName().split("\\.")[0];
                    System.out.println(fileName);
                    r = new InputStreamReader(new FileInputStream(file), defaultCharSets[charSetId]);
                } catch (UnsupportedEncodingException | FileNotFoundException e) {
                    e.printStackTrace();
                }
                in = new BufferedReader(r);
                break;
            default:
                try {
                    scanner = new Scanner(file, defaultCharSets[charSetId]);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }

    }

    protected String readcompleteFile() {

        String fileContent = "";
        while (scanner.hasNextLine()) {
            fileContent += scanner.nextLine();
        }
        return fileContent;
    }

    protected String readLine() {
        return scanner.nextLine();
    }

    protected boolean hasNext() {
        return scanner.hasNext();
    }

    /**
     * Can read in csv data out file and stores it into a Hash Map.
     *
     * @return word to word mapping
     */
    HashMap<String, String> readCSV() {

        String[] mapping = null;
        HashMap<String, String> posMapping = new HashMap<String, String>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.length() > 0) {
                mapping = line.trim().split(" ");
                // System.out.println(mapping.length);
                posMapping.put(mapping[0].toLowerCase(), ";" + mapping[1].toLowerCase() + ";");
            }
        }
        scanner.close();
        return posMapping;
    }
}
