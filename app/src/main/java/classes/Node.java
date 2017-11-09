package classes;


import android.graphics.Color;
import android.graphics.RectF;

/**
 * Created by 17009495 on 04/10/17.
 */

public class Node extends RectF{

    private String etiquette;
    private int color;
    private float centerx, centery;
    private static float rayonDefault = 40;
    private float rayon;

    public Node(float centerx, float centery, String etiquette, int color) {

        super(centerx - 40, centery - 40, centerx + 40, centery + 40);
        rayon = rayonDefault;
        this.centerx = centerx;
        this.centery = centery;
        this.etiquette = etiquette;
        this.color = color;
    }

    public int getColor() {
        return color;
    }


    public void setColor(int color) {
        this.color = color;
    }

    public void setCenter(float centerx, float centery)
    {
        this.centerx = centerx;
        this.centery = centery;
        super.set(centerx - rayon, centery - rayon, centerx + rayon, centery + rayon);
    }

    public String getEtiquette() {
        return etiquette;
    }

    public void setEtiquette(String etiquette) {
        this.etiquette = etiquette;
    }

    public void setRayonDefault(float taille)
    {
        this.rayon = taille;
        super.set(centerx - rayon, centery - rayon, centerx + rayon, centery + rayon);

    }

    public float getRayonDefault(){
        return rayonDefault;
    }

    public float getRayon() {
        return rayon;
    }

    public void setRayon(float rayon) {
        this.rayon = rayon;
    }
}
