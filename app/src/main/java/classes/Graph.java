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
    private Collection<ArcFinal> arcs;
    private ArcTemporaire arcTemp;

    public Graph(){
        nodes = new ArrayList<Node>();
        arcs = new ArrayList<ArcFinal>();

        nodes.add(new Node(80, 80, "1", Color.BLACK));
        nodes.add(new Node(200, 80, "2", Color.BLACK));
        nodes.add(new Node(320, 80, "3", Color.BLACK));
        nodes.add(new Node(80, 200, "4", Color.BLACK));
        nodes.add(new Node(200, 200, "5", Color.BLACK));
        nodes.add(new Node(320, 200, "6", Color.BLACK));
        nodes.add(new Node(80, 320, "7", Color.BLACK));
        nodes.add(new Node(200, 320, "8", Color.BLACK));
        nodes.add(new Node(320, 320, "9", Color.BLACK));

    }

    public Collection<ArcFinal> getArcs(){
        return new ArrayList<ArcFinal>(arcs);
    }

    public void addNode(Node _n){
        nodes.add(_n);
    }

    public void removeNode(Node _n) {

        nodes.remove(_n);

        //On supprime également les arcs liés à ce noeud
        Iterator<ArcFinal> it = arcs.iterator();
        while(it.hasNext()){
            ArcFinal a = it.next();
            if(a.getNodeTo() == _n || a.getNodeFrom() == _n){
                it.remove();
            }
        }

    }

    public Collection<Node> getNodes() {
        return new ArrayList<Node>(nodes);
    }

    public ArcTemporaire getArcTemp() {
        return arcTemp;
    }


    public Node getOneNode(float x, float y) {
        for (Node n : nodes) {
            if (n.contains(x, y)) {
                return n;
            }
        }

        return null;
    }

    public void initArcTemp(float x, float y) {
        arcTemp = new ArcTemporaire(getOneNode(x, y));
    }

    public void setArcTemp(float x, float y) {
        arcTemp.setNodeX(x);
        arcTemp.setNodeY(y);
    }

    public void makeArcTempNull() {
        arcTemp = null;
    }
    public void addArc(ArcFinal a) {
        arcs.add(a);
    }

    public void removeArc(ArcFinal a) {
        arcs.remove(a);
    }
}
