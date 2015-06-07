package de.acoli.informatik.uni.frankfurt.crfformat.reflex.vistotext;

import java.util.ArrayList;

public class Node {

    Node parent;
    String before = "";
    ArrayList<Node> childs = new ArrayList<Node>();
    int level = -1;
    public String after = "";
    String context = InNOutFiles.dummy;

    public Node(Node parent) {
        this.parent = parent;
        if (parent == null) {
            this.level = 0;
        } else {
            this.level = parent.level + 1;
        }
    }

    public void add(Node current) {
        childs.add(current);
    }
}
