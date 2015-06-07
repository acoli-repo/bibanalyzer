package de.acoli.informatik.uni.frankfurt.crfformat.reflex.vistotext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Is used for writing data
 * 
 * @author s0366088
 */
public class WriteOutputFile {
	
	String[]		defaultCharSets	= new String[] { "ISO-8859-1", "UTF-8", "UTF-16" };
	BufferedWriter	writer;
	int				charSetId;
	File			file;
	//Writer			out;
	
	
	
	protected WriteOutputFile(String filename, int charSetId) {
	
		Path path = Paths.get(filename);
		
		try {
			writer = Files.newBufferedWriter(path, Charset.forName(defaultCharSets[charSetId]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	void write(String line) {
	
		try {
			writer.write(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	void close() {
	
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
