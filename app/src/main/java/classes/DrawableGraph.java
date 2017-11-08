package classes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.midi.MidiInputPort;
import android.util.Log;
import android.widget.Toast;

import com.example.a17009495.tp2graphes.MainActivity;

/**
 * Created by 17009495 on 04/10/17.
 */

public class DrawableGraph extends Drawable {
    private Graph graph;

    public DrawableGraph (Graph g){
        graph = g;
    }

    @Override
    public void draw(Canvas canvas) {

        Paint pArc = new Paint();
        pArc.setStrokeWidth(5);
        pArc.setColor(Color.WHITE);
        pArc.setStyle(Paint.Style.STROKE);
        Path path;
        Path pathTemp;
        //On dessine d'abord les arcs
        for(ArcFinal a : graph.getArcs()){
            float [] midPoint = {0f, 0f};
            float [] tangent = {0f, 0f};
            path = new Path();
            pathTemp = new Path();
            pathTemp.moveTo(a.getNodeFrom().centerX(), a.getNodeFrom().centerY());

            if (a instanceof ArcBoucle) {
                // Dessiner boucle

            } else {
                pathTemp.lineTo(a.getNodeTo().centerX(), a.getNodeTo().centerY());
                PathMeasure pm = new PathMeasure(pathTemp,false);
                pm.getPosTan(pm.getLength(),midPoint,tangent);
                path.moveTo(a.getNodeFrom().centerX(), a.getNodeFrom().centerY());
                path.quadTo(midPoint[0],midPoint[1], a.getNodeTo().centerX(), a.getNodeTo().centerY());
                a.setMidPoint(midPoint);
                a.setTangent(tangent);
            }
            canvas.drawPath(path, pArc);

        }

        //On dessine un arc temporaire si il est en cours de création
        ArcTemporaire tempArc = graph.getArcTemp();
        if (tempArc != null) {
            path = new Path();
            path.moveTo(tempArc.getNodeFrom().centerX(), tempArc.getNodeFrom().centerY());
            path.lineTo(tempArc.getNodeX(), tempArc.getNodeY());
            canvas.drawPath(path, pArc);
        }

        Paint p = new Paint();
        Paint pTexte = new Paint();
        pTexte.setColor(Color.WHITE);
        pTexte.setTextSize(30);
        pTexte.setTextAlign(Paint.Align.CENTER);

        //On dessine ensuite les noeuds
        for(Node n : graph.getNodes()) {
            float tailleTexte = pTexte.measureText(n.getEtiquette())/ 2;
            if(n.getRayonDefault()< tailleTexte){
                n.setRayonDefault(tailleTexte + 10);
            }else if (n.getRayonDefault() != 40 && tailleTexte<40){
                n.setRayonDefault(40);
            }

            p.setColor(n.getColor());

            canvas.drawRoundRect(n, 40, 40, p);
            canvas.drawText(n.getEtiquette(), n.centerX(), n.centerY(), pTexte);
        }


    }


    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
