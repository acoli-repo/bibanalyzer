package de.acoli.informatik.uni.frankfurt.crfformat.reflex.vistotext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;

public class InNOutFiles extends ReadFile {

    WriteOutputFile out;
    static ArrayList<String> completeText = new ArrayList<String>();
    HashMap<String, String> colorMap = new HashMap<String, String>();
    HashMap<String, String> sign2Tag = new HashMap<String, String>();
    private int refNum = 0;
    static String dummy = "";

    protected InNOutFiles(String DIR, String filename, String fileType, int charSetId) {

        super(filename, fileType, charSetId);
        out = new WriteOutputFile(DIR + "/out/" + fileName, 1);

		// colorMap.put("66FF66", "<Year>");
        // colorMap.put("BDBAD6", "<Title?>");
        // colorMap.put("FF3300", "<Url>");
        // colorMap.put("BCBCBC", "<FamilyName>");
        // colorMap.put("CCCCFF", "<ArticleTitle>");
        // colorMap.put("DDDDDD", "<Initials>");
        // colorMap.put("CCFF99", "<JournalTitle>");
        // colorMap.put("FFCC66", "<VolumeID>");
        // colorMap.put("D279FF", "<Pages>");
        // colorMap.put("000000", "<BibComments>");
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

        // colorMap.put("D279FF", "<Page>");
        colorMap.put("D279FF", "<Pages>");
		// colorMap.put("D279FF", "<FirstPage>");
        // colorMap.put("D279AA", "<LastPage>");

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

        colorMap.put("", "");
        colorMap.put("", "");
        colorMap.put("", "");

        sign2Tag.put("", "<&nbsp;>");
        sign2Tag.put(",", "<&comma;>");
        sign2Tag.put(".", "<&period;>");
        sign2Tag.put(":", "<&colon;>");
        sign2Tag.put(";", "<&semicolon;>");
        // sign2Tag.put("", "");
    }

    /**
     * Executes conversion. Expects one reference per line.
     */
    public void convertBiblioFormat() {

        // String pattern = "\\([^(]*?\\)";
        String pattern = "<p>";
        scanner.useDelimiter(pattern);
        while (hasNext()) {
            refNum++;
            Node root = new Node(null);
            String line = scanner.nextLine();
            if (line.trim().equals("")) {
                continue; // line.replaceAll("&#8211;", "-").replaceAll("&#224;",
            }													// "à").replaceAll("&#231;", "ç").replaceAll("&#233;", "é")
            line = line.replaceAll("lang=EN-US>&lt;bib id=&quot;bib.+&quot;&gt;", "style='#000000'>").replaceAll("&lt;/bib&gt;", "")
                    .replaceAll("<.edrg>", "");
            line = StringEscapeUtils.unescapeHtml4(line);
			// line = line.replaceAll("<", "<");
            // newNode(line.substring(beginIndex, endIndex), root);
            if (buildTree2(line, root) != null) {
                // createTextArray(root);
                context = dummy;
                // out.write("<BOR> BOR\n");
                outputNode(root);
                // out.write("<EOR> EOR\n");
            } else {
                // System.out.println(scanner.nextLine());
            }
            out.write("\n");
        }
        out.close();
    }

    private String setContext(String text) {
        int startIndex = text.indexOf("#") + 1;
        int endIndex = startIndex + 6;
        int textEnd = text.indexOf("<");
        if (0 < textEnd && textEnd < startIndex) {
            write(text.substring(0, textEnd));
        }
        String colorNum = text.substring(startIndex, endIndex);
        context = colorMap.get(colorNum);
        if (context == null) { // && startIndex != 0 // if no color tag exist startIndex is -1 + 1.
            System.out.println("Missing color for " + colorNum);
            context = this.parentContext;
        }
        return text.substring(text.indexOf(">") + 1);
    }

    private String removeTags(String text) {
        int textEnd = text.length();
        if (text.contains("<")) {
            textEnd = text.indexOf("<");
        }
        return text.substring(0, textEnd);
    }

    String context = dummy;
    String childContext = dummy;
    String parentContext = dummy;

    private void write(String text) {
        text = text.replaceAll("<.?aug>", "");
        if (!text.equals("")) {
            if (!context.equals("")) {
                out.write(context + text + "</" + context.substring(1));
            } else {
                out.write(text);
            }
        }
    }

    private void write3(String text) {
        text = text.replaceAll("<.?aug>", "");
        if (text.equals(" ")) {
            out.write(text);
            return;
        }
        String[] words = text.split(" ");
        for (int i = 0; i < words.length; i++) {
            String tag = sign2Tag.get(words[i].trim());
            if (tag != null) {
                out.write(words[i]);
            } else {
                out.write(context + words[i] + "</" + context.substring(1));
            }
            if (i + 1 < words.length) {
                out.write(" ");
            }
        }
    }

    private void write2(String text) {
        String[] words = text.replaceAll("<.?aug>", "").split(" ");
        for (int i = 0; i < words.length; i++) {
            String tag = sign2Tag.get(words[i].trim());
            if (tag != null) {
                out.write(tag + " " + words[i] + "\n");
            } else {
                out.write(context + " " + words[i] + "\n");
            }
            if (i + 1 < words.length) {
                out.write(context + " " + "&nbsp;" + "\n");
            }
        }
    }

    private void outputNode(Node node) {
        System.out.println(node.level + " Before " + node.before);
        if (node.before.contains("style")) {
            if (node.before.contains("span>")) {
                childContext = node.parent.context;
            }
            node.before = removeTags(setContext(node.before));
            node.context = context;
        }
        if (!node.before.equals("") && node.level > 1) {
            write(node.before);
        }
        context = childContext;

        for (Node child : node.childs) {
            outputNode(child);
        }
        System.out.println(node.level + " After " + node.after);
        if (node.after.contains("style")) {
            if (node.after.contains("span>")) {
                childContext = node.parent.context;
            }
            node.after = removeTags(setContext(node.after));
            node.context = context;
        }
        if (!node.after.equals("") && node.level > 1) {
            write(node.after);
        }
        if (node.parent != null && node.parent.context != null) {
            context = node.parent.context;
        }
    }

    public static int[] indexOf(String p, String text) {
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? new int[]{matcher.start(), matcher.end() - 1} : new int[]{-1, -1};
    }

    private String buildTree2(String content, Node parent) {
        Node current = new Node(parent);
        parent.add(current);

        int[] limits = indexOf("<.*?<", content);
        // int[] limits2 = indexOf("<.*?<", content.last);
        if (limits[0] != -1) { // content contains
            current.before = content.substring(0, limits[1]);
            String newText = content.substring(limits[1]);
            while (newText.contains("<span") || newText.contains("</span>")) {
                int begin = newText.indexOf("<span");
                int end = newText.indexOf("</span>");
                if (newText.trim().startsWith("<span")) { // newText contains embedded tags only
                    newText = buildTree2(newText, current);
                    if (newText == null) {
                        return null;
                    }
                } else if (begin < end && begin > -1) { // embedded tags begin after some text
                    Node child = new Node(current);
                    current.add(child);
                    child.before = newText.substring(0, begin);
                    newText = newText.substring(begin);
                } else if (newText.trim().startsWith("</span>")) {
                    return newText.substring(7);
                } else {
                    if (end != -1) {
                        current.after = newText.substring(0, end);
                        newText = newText.substring(end);
                    } else {
                        System.err.println("Closing span tag is missing!");
                        // current.after = newText;
                        return null; // newText;
                    }
                }
            }
        } else {
            System.err.println("Unknown error. This code shouldn't be reached.");
        }
        return "";
    }

}
