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

package de.acoli.informatik.uni.frankfurt.visualization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Description:
 * Program to visualize CRF annotated token-separated output.
 *
 * @author niko
 */
public class CRFVisualizerNew {
    
    
    public static String VISUALIZE_WHAT = 
            //"MALLET_GOLD";
            "CRFPLUSPLUS_CRF_OUTPUT"; 
    // 1. MALLET_GOLD, 2. MALLET_CRF_OUTPUT, 3. CRFPLUSPLUS_CRF_OUTPUT

    // An CRF format input file to be visualized.
//    public static final String INPUT_CRF_FILE = 
//            "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/mallet/"
//            + "model_output.txt";
    
    //public static final String INPUT_CRF_FILE = 
    //        "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/total/crf++/4500references/"
    //        + "model_output.txt";
    
            
   //public static final String INPUT_CRF_FILE = 
   //         "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/"
   //        + "total/crf++/4500references/with_font/"
   //         + "out_model_tokfontfeats.txt";
   
   
    public static final String INPUT_CRF_FILE = 
            "/home/niko/Desktop/Springer_Reflexica_StatistischeModelle/einModell3Bibtypes/training/"
            + "total/crf++/4500references/alignment/reflexica_annotations_journame_dict_pubname_year_ednum/"
            + "output_model_reflexicaannotations_journame_dict_pubname_year_ednum.txt";
   
   
             
            
    
    // The output file name of the HTML visualization.
    public static final String OUTPT_HTML_FILE = INPUT_CRF_FILE + ".vis.html";

    // Specifies mappings from tag to HTML color code.
    public static HashMap<String, String> colorMap = new HashMap<String, String>();

    static {

        colorMap.put("<author-fullname>", "BCCCBC");
        colorMap.put("<author-lastname>", "BCBCBC");
        colorMap.put("<editor-fullname>", "FFCCCC");
        colorMap.put("<editor-lastname>", "FFAACC");

        colorMap.put("<title>", "CCCCFF");
        colorMap.put("<publisher>", "FFFF49");
        colorMap.put("<series>", "00FFFF");
        colorMap.put("<booktitle>", "FFD9B3");
        colorMap.put("<journal>", "CCFF99");
        colorMap.put("<year>", "66FF66");
        colorMap.put("<volume>", "FFCC66");
        colorMap.put("<number>", "C8BE84");
        colorMap.put("<pages>", "D279FF");
        colorMap.put("<Isbn>", "02B486");
        colorMap.put("<ISBN>", "02B486");

        // ..
        // TODO: more.
        // Springer specific
        colorMap.put("<FamilyName>", "BCBCBC");
        colorMap.put("<Initials>", "DDDDDD");
        
        colorMap.put("<FamilyNameEditor>", "FF95CA");
        // FamilyNameEditor
        colorMap.put("<InitialsEditor>", "FFD1E8");
         
        
        colorMap.put("<Prefix>", "FFFF80");
        colorMap.put("<Suffix>", "FFA86D");
        colorMap.put("<Year>", "66FF66");
        colorMap.put("<ArticleTitle>", "CCCCFF");
        colorMap.put("<JournalTitle>", "CCFF99");
        colorMap.put("<BookTitle>", "FFD9B3");
        colorMap.put("<VolumeID>", "FFCC66");
        colorMap.put("<IssueID>", "C8BE84");
        colorMap.put("<Page>", "D279FF");
        colorMap.put("<Pages>", "D279FF");
        colorMap.put("<FirstPage>", "D279FF");
        colorMap.put("<LastPage>", "D279AA");
        colorMap.put("<PublisherName>", "FFFF49");
        colorMap.put("<PublisherLocation>", "00A5E0");
        colorMap.put("<ChapterTitle>", "FF9933");
        colorMap.put("<EditionNumber>", "9999FF");
        colorMap.put("<InstitutionalAuthorName>", "BDBAD6"); 
        colorMap.put("<SeriesTitle>", "00FFFF");

        colorMap.put("<ConfEventName>", "FF0099");
        colorMap.put("<BibComments>", "FF6666");
        colorMap.put("<NumberInSeries>", "CC0000");
        colorMap.put("<ConfEventLocation>", "CC9900");

        colorMap.put("<URL>", "04B486");
        colorMap.put("<Url>", "04B486");
        colorMap.put("<RefSource>", "04B486");
        colorMap.put("<Doi>", "CFBFB1"); // From Reflex.

    }

    /**
     * Demo client.
     *
     * @param args
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        visualizeCRFOutput(INPUT_CRF_FILE, OUTPT_HTML_FILE);
    }

    /**
     *
     * @param inputCrfFile
     * @param outputHTMLFile
     * @throws FileNotFoundException
     */
    public static void visualizeCRFOutput(String inputCrfFile, String outputHTMLFile) throws FileNotFoundException {

        int refCount = 1;

        PrintWriter w = new PrintWriter(new File(outputHTMLFile));
        w.write("<html xmlns:my=\"http://acoli.informatik.uni-frankfurt.de/bibref\">\n"
                + "<head>\n"
                + " <title>Our Bibfield (CRF) Visualization</title>\n"
                + " <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + " </head><body lang=\"En-US\" link=\"blue\" vlink=\"purple\">\n"
                + "<div class=\"Section1\">");

        Scanner s = new Scanner(new File(inputCrfFile));
        boolean end = true;
        while (s.hasNextLine()) {
            String aLine = s.nextLine();

            if (aLine.length() > 0) {
                if (end) {
                    w.write("<p>&lt;bib id=\"CR" + refCount + "\"&gt;");
                    end = false;
                    refCount++;
                }
                //System.out.println(aLine);

                String[] split = aLine.split("\\s");
                // Set this to "1" if you want to visualize "gold" data
                // which contains the label at the last position.
                
                String label = "";
                String token = "";
                switch (VISUALIZE_WHAT) {
                    case "MALLET_GOLD":
                        label = split[split.length-1];
                        token = split[0];
                        break;
                    case "MALLET_CRF_OUTPUT":
                        label = split[0];
                        for(int i = 0; i < split.length; i++) {
                            if(!split[i].startsWith("<") && !split[i].endsWith(">")) {
                                token = split[i];
                            }
                        }
                        break;
                    case "CRFPLUSPLUS_CRF_OUTPUT":
                        label = split[split.length - 1];
                        token = split[0];
                        break;

                    default:
                        break;
                }

                
                
                
                //if (split[split.length - 1].contains("<") && split[split.length - 1].contains(">")) {
                //    token = split[split.length - 2];
                //    //System.out.println(label + "<->" + token);
                //} else {
                //    token = split[split.length - 1];
                //    //System.out.println(label + "<->" + token);
                //}
                
                //System.out.println(label + "-" + token);
                String color = colorMap.get(label);
                w.write("<span style='background:#" + color + "'>" + token + "</span>");
            } else {
                end = true;
                w.write("<span lang=\"EN-US\">");
            }

        }

        s.close();

        w.write("<style type=\"text/css\">\n"
                + "<!--\n"
                + "\n"
                + ".beispiel\n"
                + "{\n"
                + "width: 400px;\n"
                + "border: 1px solid #6688AA;\n"
                + "}\n"
                + "\n"
                + ".beispiel caption\n"
                + "{\n"
                + "background-color: #6688AA;\n"
                + "font-family: Arial,Verdana,sans-serif;\n"
                + "font-weight: normal;\n"
                + "font-size: 10px;\n"
                + "color: #FFFFFF;\n"
                + "padding: 6px 0 3px 0;\n"
                + "}\n"
                + "\n"
                + ".beispiel thead th\n"
                + "{\n"
                + "font: bold 15px  Arial,Verdana,sans-serif;\n"
                + "padding: 3px;\n"
                + "background-color: #FFFFFF;\n"
                + "border: 1px solid #FFFFFF;\n"
                + "text-align: center;\n"
                + "color: #000000;\n"
                + "font-weight: normal;\n"
                + "}\n"
                + "\n"
                + ".beispiel td\n"
                + "{\n"
                + "font: normal 15px Arial,Verdana,sans-serif;\n"
                + "padding: 5px 5px;\n"
                + "background-color: #FFFFFF;\n"
                + "border: 1px solid #FFFFFF;\n"
                + "line-height: 130%;\n"
                + "text-align: center;\n"
                + "}\n"
                + "\n"
                + "-->\n"
                + "</style>\n"
                + "\n"
                + "\n"
                + "<br><br><br>\n"
                + "<table class=\"beispiel\" cellspacing=\"0\" cellpadding=\"0\">\n"
                + "\n"
                + "<caption>Color Codes</caption>\n"
                + "\n"
                + "<thead><tr>\n"
                + "<th>Author Initials</th>\n"
                + "<th>Author FamilyName</th>\n"
                + "<th>Editor Initials</th>\n"
                + "<th>Editor FamilyName</th>\n"
                + "<th>Article Title</th>\n"
                + "<th>Journal Title</th>\n"
                + "<th>Year</th>\n"
                + "<th>VolumeID</th>\n"
                + "<th>Issue ID</th>\n"
                + "<th>FirstPage</th>\n"
                + "<th>LastPage</th>\n"
                + "<th>BibComments</th>\n"
                + "<th>    </th>\n"
                + "<th>    </th>\n"
                + "<th>    </th>\n"
                + "<th>    </th>\n"
                + "<th>Book Title</th>\n"
                + "<th>Chapter Title</th>\n"
                + "<th>BibInstitutional EditorName</th>\n"
                + "<th>Publisher Name</th>\n"
                + "<th>Publisher Location</th>\n"
                + "<th>ConfEvent Name</th>\n"
                + "<th>ConfEvent Location</th>\n"
                + "<th>Edition Number</th>\n"
                + "<th>Institutional Author Name</th>\n"
                + "<th>Number in Series</th>\n"
                + "<th>URL</th>\n"
                + "<th>Series Titel</th>\n"
                + "\n"
                + "\n"
                + "\n"
                + "</tr></thead>\n"
                + "\n"
                + "<tbody>\n"
                + "<tr>\n"
                + "<td><span style='background:#DDDDDD'>TEXT</span>  </td>\n"
                + "<td><span style='background:#BCBCBC'>TEXT</span>    </td>\n"
                + "<td><span style='background:#FF95CA'>TEXT</span>    </td>\n"
                + "<td><span style='background:#FFD1E8'>TEXT</span>   </td>\n"
                + "<td><span style='background:#CCCCFF'>TEXT</span>   </td>\n"
                + "<td><span style='background:#CCFF99'>TEXT</span> </td>\n"
                + "<td><span style='background:#66FF66'>TEXT</span> </td>\n"
                + "<td><span style='background:#FFCC66'>TEXT</span> </td> \n"
                + "<td><span style='background:#C8BE84'>TEXT</span> </td>\n"
                + "<td><span style='background:#D279FF'>TEXT</span> </td>\n"
                + "<td><span style='background:#D279AA'>TEXT</span> </td>\n"
                + "<td><span style='background:#FF6666'>TEXT</span> </td>\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "<td>  </td>\n"
                + "<td>  </td>\n"
                + "<td>  </td>\n"
                + "<td>  </td>\n"
                + "<td><span style='background:#FFD9B3'>TEXT</span> </td>\n"
                + "<td><span style='background:#FF9933'>TEXT</span> </td>\n"
                + "<td><span style='background:#C8BE84'>TEXT</span> </td>\n"
                + "<td><span style='background:#FFFF49'>TEXT</span> </td>\n"
                + "<td><span style='background:#00A5E0'>TEXT</span> </td>\n"
                + "<td><span style='background:#FF0099'>TEXT</span> </td>\n"
                + "<td><span style='background:#CC9900'>TEXT</span> </td>\n"
                + "<td><span style='background:#9999FF'>TEXT</span> </td>\n"
                + "<td><span style='background:#BDBAD6'>TEXT</span> </td>\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "<td><span style='background:#CC0000'>TEXT</span> </td>\n"
                + "<td><span style='background:#BDEED6'>TEXT</span> </td>\n"
                + "<td><span style='background:#00FFFF'>TEXT</span> </td>\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "\n"
                + "</tr>\n"
                + "\n"
                + "</tbody>\n"
                + "\n"
                + "</table>\n"
                + "\n"
                + "\n"
                + "\n"
                + "<br><br><br><br><br><br>");
        w.flush();
        w.close();

    }
}
