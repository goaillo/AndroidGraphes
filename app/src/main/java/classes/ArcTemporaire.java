package classes;

/**
 * Created by quentineono on 31/10/2017.
 */

public class ArcTemporaire extends Arc{

    private float nodeX, nodeY;

    public ArcTemporaire(Node _nodeFrom) {
        super(_nodeFrom);
        nodeX = _nodeFrom.centerX();
        nodeY = _nodeFrom.centerY();
    }

    public float getNodeX() {
        return nodeX;
    }

    public float getNodeY() {
        return nodeY;
    }

    public void setNodeX(float _nodeX) {
        this.nodeX = _nodeX;
    }

    public void setNodeY(float _nodeY) {
        this.nodeY = _nodeY;
    }

}
