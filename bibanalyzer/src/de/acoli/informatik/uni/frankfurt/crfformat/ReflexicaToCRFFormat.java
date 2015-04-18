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

package de.acoli.informatik.uni.frankfurt.crfformat;

import de.acoli.informatik.uni.frankfurt.processing.ReferenceUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Description:
 * This class handles Reflexica's HTML visualization output
 * and converts it to its Bibanalyzer/CRF-based (line-separated) 
 * token-format equivalent.
 * 
 * Specifically, each Reflexica-defined color is mapped to a
 * Springer-specific A++ label. E.g., BCBCBC is mapped to "<FamilyName>".
 * (Colors are defined internally by Reflexica).
 * 
 * Note that the Reflexica input file should be encoded to UTF-8 first.
 *
 *
 * @author niko
 */
public class ReflexicaToCRFFormat {

    
    public static final String DIR = "input/reranker/Reflexica/";
    
    // Reflexica input HTML visualization.
    //public static String REFLEXICA_HTML_INPUT = DIR + "2000_article_REFLEXICA_raw.txt.html";
    public static String REFLEXICA_HTML_INPUT = DIR + "2000_article_REFLEXICA_raw.txt.utf8.html";
    
    // Bibanalyzer CRF-format output file.
    public static String REFLEXICA_CONVERTED_TO_CRF_FORMAT_OUTPUT = DIR + "2000_Reflexica_CRFoutput.txt";

    public static boolean verbose = false;
    
    
    static HashMap<String, String> replacements = new HashMap<String, String>();
    static HashMap<String, String> colorMap = new HashMap<String, String>();

    // Each Reflexica color has a Bibanalyzer-specific (Springer) label.
    static {
        
        // Springer specific
        colorMap.put("BCBCBC", "<FamilyName>");
        colorMap.put("DDDDDD", "<Initials>");

        // Reflexica Specific.
        colorMap.put("FFD1E8", "<InitialsEditor>");
        colorMap.put("FF95CA", "<FamilyNameEditor>");

        colorMap.put("FFFF80", "<Prefix>");
        colorMap.put("FFA86D", "<Suffix>");
        colorMap.put("66FF66", "<Year>");
        colorMap.put("CCCCFF", "<ArticleTitle>");
        colorMap.put("CCFF99", "<JournalTitle>");
        colorMap.put("FFD9B3", "<BookTitle>");
        colorMap.put("FFCC66", "<VolumeID>");
        colorMap.put("FFCC66'", "<VolumeID>");
        colorMap.put("C8BE84", "<IssueID>");

        //colorMap.put("D279FF", "<Page>");
        colorMap.put("D279FF", "<Pages>");
        //colorMap.put("D279FF", "<FirstPage>");
        //colorMap.put("D279AA", "<LastPage>");

        colorMap.put("FFFF49", "<PublisherName>");
        colorMap.put("00A5E0", "<PublisherLocation>");
        colorMap.put("C0FFC0", "<PublisherLocation>"); // from Reference Manager!
        // Maybe ConfEventName ?
        
        colorMap.put("FF9933", "<ChapterTitle>");
        colorMap.put("9999FF", "<EditionNumber>");
        colorMap.put("BDBAD6", "<InstitutionalAuthorName>");
        colorMap.put("00FFFF", "<SeriesTitle>");

        colorMap.put("FF0099", "<ConfEventName>");
        colorMap.put("FF6666", "<BibComments>");
        colorMap.put("FFFF0F", "<BibComments>"); // from Reference Manager! (looks like PublisherName).
        
        colorMap.put("CC0000", "<NumberInSeries>");
        colorMap.put("CC9900", "<ConfEventLocation>");

        colorMap.put("04B486", "<Url>");
        colorMap.put("FF3300", "<Url>"); // from Reference Manager!
        colorMap.put("04B486", "<RefSource>");

        colorMap.put("CFBFB1", "<Doi>");

        colorMap.put("<dammy>", "<dammy>");

        // "et. al" with dotted border marker.
        colorMap.put("='border:dotted windowtext 1.0pt;padding:0pt", "<etal>");
        colorMap.put("='border:dotted maroon 1.0pt;padding:0pt", "<bla>");
        colorMap.put("CBCBC", "<Nothing>");

    }

    // Replace Reflexica's escape references.
    static {
        
        // TODO: StringEscapeUtils.unescapeHtml4();
        
        replacements.put("&#151;", "—");
        replacements.put("&#8211;", "–");
        replacements.put("&#150;", "-");
        replacements.put("&#8212;", "—");
        
        replacements.put("&#252;", "ü");
        replacements.put("&#236;", "ì");
        replacements.put("&#246;", "ö");
        replacements.put("&#248;", "ø");
        replacements.put("&#224;", "à");
        replacements.put("&#194;", "Â");
        replacements.put("&#956;", "μ");
        replacements.put("&#163;", "£");
        replacements.put("&#161;", "¡");
        replacements.put("&#215;", "×");
        replacements.put("&#955;", "λ");
        
         replacements.put("&#920;", "Θ");
        replacements.put("&#353;", "š");
        replacements.put("&#972;", "ό");
        replacements.put("&#957;", "ν");
        replacements.put("&#969;", "ω");
        replacements.put("&#933;", "Υ");
        replacements.put("&#963;", "σ");
        replacements.put("&#942;", "ή");
        replacements.put("&#917;", "Ε");
        replacements.put("&#922;", "Κ");
        replacements.put("&#940;", "ά");
        replacements.put("&#953;", "ι");
        replacements.put("&#919;", "Η");
        replacements.put("&#924;", "Μ");
        replacements.put("&#949;", "ε");
        replacements.put("&#952;", "θ");
        replacements.put("&#948;", "δ");
        replacements.put("&#961;", "ρ");
        replacements.put("&#941;", "έ");
        replacements.put("&#964;", "τ");
        replacements.put("&#951;", "η");
        replacements.put("&#962;", "ς");
        replacements.put("&#928;", "Π");
        replacements.put("&#943;", "ί");
        replacements.put("&#959;", "ο");
        replacements.put("&#965;", "υ");
        replacements.put("&#8729;", "∙");
        replacements.put("&#8222;", "„");
        replacements.put("&#8218;", "‚");
        
        replacements.put("&#176;", "°");
        replacements.put("&#945;", "α");
        replacements.put("&#946;", "β");
        replacements.put("&#211;", "Ó");
        replacements.put("&#249;", "ù");
        replacements.put("&#966;", "φ");
        replacements.put("&#250;", "ú");
        replacements.put("&#186;", ""); // ?
        replacements.put("&#189;", ""); // ?
        replacements.put("&#139;", "‹");
        replacements.put("&#8224;", "†");
        replacements.put("&#234;", "ê");
        replacements.put("&#167;", "§");
        replacements.put("&#207;", "Ï");
        replacements.put("&#339;", "œ");
        replacements.put("&#281;", "ę");
        replacements.put("&#347;", "ś");
        replacements.put("&#281;", "ę");
        replacements.put("&#223;", "ß");
        replacements.put("&#132;", "„");
        replacements.put("&#283;", "ě");
        replacements.put("&#382;", "ž");
        replacements.put("&#367;", "ů");
        replacements.put("&#380;", "ż");
        replacements.put("&#346;", "Ś");
        replacements.put("&#245;", "õ");
        replacements.put("&#169;", "©");
        replacements.put("&#238;", "î");
        replacements.put("&#8208;", "‐");
        replacements.put("&#197;", "Å");
        replacements.put("&#268;", "Č");
        replacements.put("&#153;", "™");
        replacements.put("&#228;", "ä");
        replacements.put("&#351;", "ş");
        replacements.put("&#225;", "á");
        replacements.put("&#216;", "Ø");
        replacements.put("&#381;", "Ž");
        replacements.put("&#243;", "ó");
        replacements.put("&#235;", "ë");
        replacements.put("D&#261;", "ą");
        replacements.put("&#305;", "ı");
        replacements.put("&#226;", "â");
        replacements.put("&#229;", "å");
        replacements.put("&#201;", "É");
        replacements.put("&#230;", "æ");
        replacements.put("&#231;", "ç");
        replacements.put("&#220;", "Ü");
        replacements.put("&#232;", "è");
        replacements.put("&#241;", "ñ");
        replacements.put("&#244;", "ô");
        replacements.put("&#227;", "ã");
        replacements.put("&#199;", "Ç");
        replacements.put("&#239;", "ï");
        replacements.put("&#253;", "ý");
        replacements.put("&#322;", "ł");
        replacements.put("&#914;", "Β");
        replacements.put("&#947;", "γ");
        replacements.put("&#269;", "č");
        replacements.put("&#195;", "à");
        replacements.put("&#913;", "Α");
        replacements.put("&#180;", "´");
        replacements.put("&#196;", "Ä");
        replacements.put("&#954;", "κ");
        replacements.put("&#178;", "dammy");
        replacements.put("&#149;", "•");
        replacements.put("&#324;", "ń");
        replacements.put("&#337;", "ő");
        replacements.put("&#251;", "û");
        replacements.put("&#242;", "ò");
        replacements.put("&#350;", "Ş");
        replacements.put("&#193;", "Á");
        replacements.put("&#187;", "»");
        replacements.put("&#171;", "«");
        replacements.put("&#263;", "ć");
        replacements.put("&#134;", "′");
        replacements.put("&#8220;", "“");
        

        replacements.put("&#214;", "Ö");
        replacements.put("&#237;", "í");
        replacements.put("&#301;", "ĭ");
        replacements.put("&#233;", "é");
        replacements.put("&#8217;", "’");
        replacements.put("&#8216;", "‘");
        replacements.put("&#148;", "”");
        replacements.put("&#147;", "“");
        replacements.put("&#8221;", "”");

        replacements.put("&amp;nbsp;", " ");

        replacements.put("&#173;", ""); // ??

    }
    
    
    public static String STYLEINFO = " class=MsoNormal style='margin-top:0in;margin-right:0in;margin-bottom:0in;\n" +
"margin-left:.25in;margin-bottom:.0001pt;text-align:justify;text-justify:inter-ideograph;\n" +
"text-indent:-.25in;line-height:normal;text-autospace:none'";

    public static void main(String[] args) throws FileNotFoundException {
        
        if(args.length==2) {
            REFLEXICA_HTML_INPUT = args[0];
            REFLEXICA_CONVERTED_TO_CRF_FORMAT_OUTPUT = args[1];
        }
        
        convertReflexicaHTMLVisualizationToCRFOutput(REFLEXICA_HTML_INPUT, REFLEXICA_CONVERTED_TO_CRF_FORMAT_OUTPUT);
    
    }
        
    
    // Get all references enclosed between <p> </p> attributes.
    public static ArrayList<String> getPAttributes(String reflexInputFile) throws FileNotFoundException {
        
        ArrayList<String> rval = new ArrayList<String>();
        
        StringBuilder sb = new StringBuilder();
        Scanner s = new Scanner(new File(reflexInputFile), "UTF-8");
        while(s.hasNextLine()) {
            String aLine = s.nextLine().trim();
            sb.append(aLine + " ");
        }
        s.close();

        
        // Split between "<p " and "</p>"
        final Pattern pattern = Pattern.compile("(<p |<p>)(.+?)</p>");
        final Matcher matcher = pattern.matcher(sb.toString());
        while(matcher.find()) {
            String aMatch = matcher.group(0);
            if(verbose)
            System.out.println("A reference match in the HTML file: -> " + aMatch);
            rval.add(aMatch);
        }
        System.out.println("Read in : " + rval.size() + " html references.");
        return rval;
    }
    
    

    public static void convertReflexicaHTMLVisualizationToCRFOutput(String reflexInput,
            String crfOutput) throws FileNotFoundException {
    
        // Produce output file.
        PrintWriter w = new PrintWriter(new File(crfOutput));

        // Read in Reflexica visualization.
        ArrayList<String> htmlReferences = getPAttributes(reflexInput);
        
        for(String anHTMLRef : htmlReferences) {
            String aLine = anHTMLRef.trim();
            
            // Replace some stuff (ReferenceManager second version).
            aLine = aLine.replace(STYLEINFO, "");
            
            // Remove unnecessary whitespace.
            aLine = aLine.replace("# ", "#");
            aLine = aLine.replace(": #", ":#");
            aLine = aLine.replace("' >", "'>");
            aLine = aLine.replace("</ ", "</");
            
            
            if(verbose) {
                System.out.println(aLine);
            }
            
            // Completely unannotated line.
            if(!aLine.contains("background:") && !aLine.contains("<aug><span")) {
                // Get unannotated line.
                if(aLine.endsWith("&lt;/bib&gt;</span></p>")) {
                    aLine = aLine.substring(aLine.indexOf("&quot;&gt;")+10, aLine.lastIndexOf("&lt;/bib&gt;</span></p>"));
                }
                else {
                    aLine = aLine.substring(aLine.indexOf("&quot;&gt;")+10, aLine.lastIndexOf("</span>"));
                }
                // This is reflexica-specific.
                aLine = aLine.replace("  ", " ");
                // This, too.
                aLine = aLine.replace("&amp;nbsp;", " ");
                
                aLine = aLine.trim();
                
                for (String replacement : replacements.keySet()) {
                if (aLine.contains(replacement)) {
                    aLine = aLine.replace(replacement, replacements.get(replacement));
                }
                }
                
                //System.out.println("->" + aLine + "<-");
                ArrayList<String> tokensForLeftPart = ReferenceUtil.tokenize(aLine, false);
                w.write("<BOR> BOR\n<&nbsp;> &nbsp;\n");
                for (String t : tokensForLeftPart) {
                    if(verbose)
                        System.out.println("<dum>"+ " " + t);
                    w.write("<dum>" + " " + t + "\n");
                }
                w.write("<&nbsp;> &nbsp;\n" +
                            "<EOR> EOR\n\n");
                w.flush();
            }
            
            
            
            
            else {
                
                //System.out.println(aLine);
                
                
            TreeMap<Integer, String> m = getMap(aLine);

            w.write("<BOR> BOR\n<&nbsp;> &nbsp;\n");
            for (Integer i : m.keySet()) {
                String leftPart = m.get(i).split("\\|")[0];
                String label = m.get(i).split("\\|")[1];
                if (leftPart.contains("&#")) {
                    System.out.println("Unescaped escape reference!");
                    System.out.println(leftPart);
                    System.exit(0);
                }
                ArrayList<String> tokensForLeftPart = ReferenceUtil.tokenize(leftPart, false);
                for (String t : tokensForLeftPart) {
                    if(verbose)
                        System.out.println(label + " " + t);
                    w.write(label + " " + t + "\n");
                }

            }

            // Add an extra period or bracket... at the end of the CRF sequence 
            // if the line ends with something like this.
            // This is a hack.
            //if(aLine.endsWith(".&lt;/bib&gt;</span></p>")) 
            // dowtext 1.0pt;padding:0pt'><span style='background:#DDDDDD'>M.</span> <span style='background:#BCBCBC'>Debbabi</span></span>, <span style='border:dotted windowtext 1.0pt;padding:0pt'><span style='background:#DDDDDD'>M.</span> <span style='background:#BCBCBC'>Saleh</span></span>, <span style='border:dotted windowtext 1.0pt;padding:0pt'><span style='background:#DDDDDD'>C.</span> <span style='background:#BCBCBC'>Talhi</span></span>, and <span style='border:dotted windowtext 1.0pt;padding:0pt'><span style='background:#DDDDDD'>S.</span> <span style='background:#BCBCBC'>Zhioua</span></span></aug>, &#147;Security Evaluation of J2ME CLDC Embedded Java Platform.&#148; Journal of Object Technology, vol.&amp;nbsp;5, no. <span style='background:#FFCC66'>2</span>, pp.&amp;nbsp;<span style='background:#D279FF'>125&#150;154</span>, <span style='background:#66FF66'>2006</span>. [Online]. Available: db/journals/jot/jot5.html#DebabbiSTZ06&lt;/bib&gt;</span></p>
            if (aLine.endsWith("&lt;/bib&gt;</span></p>")) {
                aLine = aLine.substring(0, aLine.length() - 23);
                int lastIndexOfSpan = aLine.lastIndexOf("</span>");
                String addAlso = aLine.substring(lastIndexOfSpan + 7);

                addAlso = addAlso.replace("</span></aug>", "");
                addAlso = addAlso.replace("</span>", "");
                addAlso = addAlso.replace("</aug>", "");
                addAlso = addAlso.replace("<edrg>", "");
                addAlso = addAlso.replace("</edrg>", "");

                for (String replacement : replacements.keySet()) {
                    if (addAlso.contains(replacement)) {
                        addAlso = addAlso.replace(replacement, replacements.get(replacement));
                    }
                }

                ArrayList<String> aA = ReferenceUtil.tokenize(addAlso, false);
                for (String t : aA) {
                    if(verbose)
                        System.out.println("<dum>" + " " + t);
                    w.write("<dum>" + " " + t + "\n");
                }

            }

            w.write("<&nbsp;> &nbsp;\n<EOR> EOR\n");
            if(verbose)
                System.out.println();
            w.write("\n");
        
            }
        
        }

        w.flush();
        w.close();
    }

    /**
     * 
     * @param aReference
     * @return 
     */
    public static TreeMap<Integer, String> getMap(String aReference) {

        TreeMap<Integer, String> treemap = new TreeMap<Integer, String>();

        
        
        // Extract unannoated stuff RIGHT at the beginning of the reference.
        
        Pattern patternUnanno1 = Pattern.compile("quot;&gt;([A-Z](.*?))<span");
        Matcher matcherUnanno1 = patternUnanno1.matcher(aReference);
        while(matcherUnanno1.find()) {
            String found = matcherUnanno1.group();
            //System.out.println("FOUND: " + found);
            String plaintext = matcherUnanno1.group(1);
            int start = matcherUnanno1.start();
            for (String replacement : replacements.keySet()) {
                if (plaintext.contains(replacement)) {
                    plaintext = plaintext.replace(replacement, replacements.get(replacement));
                }
            }
            treemap.put(start, plaintext + "|" + "<dum>");
        }
        
        

        // Extract all TAGGED plaintext.
        Pattern pattern = Pattern.compile("<span style=\'(.*?)>(.*?)</span>");

        Matcher matcher = pattern.matcher(aReference);
        while (matcher.find()) {
            String found = matcher.group();
            String plaintext = matcher.group(2);
            int start = matcher.start();
            //System.out.println("start: " + matcher.start());
            String color = "<dammy>";
            if (!(plaintext.contains(">")) && plaintext.length() > 0) {
                color = found.substring(found.indexOf("background:") + 12, found.indexOf(">") - 1);
                //System.out.print("color: " + color);
                //System.out.println("   /   annotated: \"" + plaintext + "\"");

            } else {
                // Authors.
                if (plaintext.contains("<span style='background:#BCBCBC'>")) {
                    //System.out.println(plaintext);
                    plaintext = plaintext.replace("<span style='background:#BCBCBC'>", "");
                    color = "BCBCBC";
                } // Editors Initials.
                else if (plaintext.contains("<span style='background:#FFD1E8'>")) {
                    plaintext = plaintext.replace("<span style='background:#FFD1E8'>", "");
                    //System.out.println(plaintext);
                    color = "FFD1E8";
                } // Author Initials.
                else if (plaintext.contains("<span style='background:#DDDDDD'>")) {
                    //System.out.println(plaintext);
                    plaintext = plaintext.replace("<span style='background:#DDDDDD'>", "");
                    color = "DDDDDD";
                } else if (plaintext.contains("<span style='background:#FF95CA'>")) {
                    //System.out.println(plaintext);
                    plaintext = plaintext.replace("<span style='background:#FF95CA'>", "");
                    color = "FF95CA";
                } // Particle. 
                else if (plaintext.contains("<span style='background:#FFFF80'>")) {
                    //System.out.println(plaintext);
                    plaintext = plaintext.replace("<span style='background:#FFFF80'>", "");
                    color = "FFFF80";
                } else if (plaintext.contains("<span style='background:#FFCC66'>")) {
                    //System.out.println(plaintext);
                    plaintext = plaintext.replace("<span style='background:#FFCC66'>", "");
                    color = "FFCC66";
                } // Border without color.
                else if (plaintext.contains("<span style='background:CBCBC'>")) {
                    //System.out.println(plaintext);
                    plaintext = plaintext.replace("<span style='background:CBCBC'>", "");
                    color = "CBCBC";
                } else if (plaintext.contains("<span style='background:#BDBAD6'>")) {
                    System.out.println("drin!");
                    //System.out.println(plaintext);
                    plaintext = plaintext.replace("<span style='background:#BDBAD6'>", "");
                    color = "BDBAD6";
                } else {
                    System.out.println("ERROR!!!!!" + plaintext);
                    System.out.println("Exiting...");
                    System.out.println("->" + plaintext + "<-");
                    System.exit(0);
                }

            }
            for (String replacement : replacements.keySet()) {
                if (plaintext.contains(replacement)) {
                    plaintext = plaintext.replace(replacement, replacements.get(replacement));
                }
            }

            String label = "<no-label>";
            if (colorMap.containsKey(color)) {
                label = colorMap.get(color);
            } else {
                System.out.println("No label found for color: ->" + color + "<-");
                System.exit(0);
            }
            treemap.put(start, plaintext + "|" + label);
        }

        // Unannotated "dummy" spans.
        Pattern pUnanno = Pattern.compile("</span>(.*?)<span ");
        Matcher mUnanno = pUnanno.matcher(aReference);
        //System.out.print("unannotated: ");
        while (mUnanno.find()) {
            String found = mUnanno.group();
            String plaintext = mUnanno.group(1);
            int start = mUnanno.start();
            //System.out.println("start: " + mUnanno.start());
            //System.out.println("-> " + found);
            if (plaintext.length() > 0) {
                //System.out.println("\"" + plaintext + "\"");

                plaintext = plaintext.replace("</span></aug>", "");
                plaintext = plaintext.replace("</span>", "");
                plaintext = plaintext.replace("</aug>", "");
                plaintext = plaintext.replace("<edrg>", "");
                plaintext = plaintext.replace("</edrg>", "");

                for (String replacement : replacements.keySet()) {
                    if (plaintext.contains(replacement)) {
                        plaintext = plaintext.replace(replacement, replacements.get(replacement));
                    }
                }
                treemap.put(start, plaintext + "|<dum>");
            }
        }

        return treemap;
    }
    
    
    
   
}
