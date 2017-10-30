package classes;

import android.graphics.Color;

/**
 * Created by 17009495 on 04/10/17.
 */

public class Arc {
    private Node nodeFrom, nodeTo;
    private int color;
    private String etiquette;

    public Arc(Node nodeFrom, Node nodeTo, int color, String etiquette) {
        this.nodeFrom = nodeFrom;
        this.nodeTo = nodeTo;
        this.color = color;
        this.etiquette = etiquette;
    }

    public Node getNodeFrom() {
        return nodeFrom;
    }

    public void setNodeFrom(Node nodeFrom) {
        this.nodeFrom = nodeFrom;
    }

    public Node getNodeTo() {
        return nodeTo;
    }

    public void setNodeTo(Node nodeTo) {
        this.nodeTo = nodeTo;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getEtiquette() {
        return etiquette;
    }

    public void setEtiquette(String etiquette) {
        this.etiquette = etiquette;
    }
}
