package classes;

/**
 * Created by quentineono on 01/11/2017.
 */

public class ArcFinal extends Arc {
    private Node nodeTo;
    private float [] midPoint;
    private float [] tangent;
    private String etiquette;

    public ArcFinal(Node _nodeFrom, Node _nodeTo, String _etiquette){
        super(_nodeFrom);
        this.nodeTo = _nodeTo;
        this.etiquette = _etiquette;

    }

    public ArcFinal(Node _nodeFrom, Node _nodeTo){
        super(_nodeFrom);
        this.nodeTo = _nodeTo;
    }

    public Node getNodeTo(){
        return this.nodeTo;
    }

    public String getEtiquette(){
        return this.etiquette;
    }

    public float[] getMidPoint() {
        return midPoint;
    }

    public void setMidPoint(float[] midPoint) {
        this.midPoint = midPoint;
    }

    public float[] getTangent() {
        return tangent;
    }

    public void setTangent(float[] tangent) {
        this.tangent = tangent;
    }
}
