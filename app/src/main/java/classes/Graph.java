package classes;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by 17009495 on 04/10/17.
 */

public class Graph {
    private Collection<Node> nodes;
    private Collection<Arc> arcs;

    public Graph(){
        nodes = new ArrayList<Node>();
        arcs = new ArrayList<Arc>();

        nodes.add(new Node(80, 80, "1", Color.BLACK));
        nodes.add(new Node(200, 80, "2", Color.BLACK));
        nodes.add(new Node(320, 80, "3", Color.BLACK));
        nodes.add(new Node(80, 200, "4", Color.BLACK));
        nodes.add(new Node(200, 200, "5", Color.BLACK));
        nodes.add(new Node(320, 200, "6", Color.BLACK));
        nodes.add(new Node(80, 320, "7", Color.BLACK));
        nodes.add(new Node(200, 320, "8", Color.BLACK));
        nodes.add(new Node(320, 320, "9", Color.BLACK));


       /*arcs.add(new Arc(80,80,200,200,Color.WHITE,""));
        arcs.add(new Arc(80,320,200,320,Color.WHITE,""));
        arcs.add(new Arc(320,200,320,320,Color.WHITE,""));
        arcs.add(new Arc(200,80,320,80,Color.WHITE,""));*/
    }

    public Collection<Arc> getArcs(){
        return new ArrayList<Arc>(arcs);
    }

    public void addNode(Node n){
        nodes.add(n);
    }

    public void removeNode(Node n) {nodes.remove(n);}

    public void addArc(Arc a){
        arcs.add(a);
    }

    public void removeArc(Arc a) {arcs.remove(a);}

    public Collection<Node> getNodes() {
        return new ArrayList<Node>(nodes);
    }

    public void setNodes(Collection<Node> nodes) {
        this.nodes = nodes;
    }
}
