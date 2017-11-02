package classes;

import android.graphics.Color;

/**
 * Created by 17009495 on 04/10/17.
 */

public class Arc {
    private Node nodeFrom;

    public Arc(Node _nodeFrom) {
        this.nodeFrom = _nodeFrom;
    }

    public Arc(){}

    public Node getNodeFrom() {
        return nodeFrom;
    }

}
