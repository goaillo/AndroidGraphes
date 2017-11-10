package classes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Region;
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

    public Path findPoint(Path edgePath, RectF r)
    {
        float[] interieur = {0f, 0f};
        float[] ext = {0f, 0f};
        float[] finalext = {0f, 0f};
        float mid = 0.5f;
        int i = 0;
        float x;
        float y;
        int v = 0;
        boolean b = true;
        do
        {
            Path path = new Path();
            path.addRoundRect(r, 40, 40, Path.Direction.CW);
            path.computeBounds(r, true);
            Region region = new Region();
            region.setPath(path, new Region());
            PathMeasure pm = new PathMeasure(edgePath, false);
            float[] point = {0f, 0f};
            pm.getPosTan(pm.getLength() * mid, point, null);
            boolean d = r.contains(point[0], point[1]);
            Log.v("test", "result d :" + d);
            if(d)
            {
                interieur[0] = point[0];
                interieur[1] = point[1];
                mid = mid + mid + (mid/(2));
            }
            else
            {
                ext[0] = point[0];
                ext[1] = point[1];
                mid = mid / (2);
            }
            i++;
            double distance = Math.sqrt(Math.pow((interieur[0]-ext[0]), 2) + Math.pow((interieur[1]-ext[1]), 2));
            if(distance <= 60 && v ==0)
            {
                Log.v("c bon", "on est bon");
                finalext[0] = ext[0];
                finalext[1] = ext[1];
                Node testNode2 = new Node(ext[0],ext[1],"",Color.GREEN);
                testNode2.setRayonDefault(10);
                graph.addNode(testNode2);
                v = 1;
            }
        }while(i != 30);
        //Fait 30 fois la recherche dichotomique, prend le premier point trouvé en dehors du noeud des qu'il y'a une distance minimale de 60
        //et le point intérieur est le dernier trouvé dans la recherche dichotomique
        Node testNode = new Node(interieur[0],interieur[1],"",Color.YELLOW);
        testNode.setRayonDefault(10);
        graph.addNode(testNode);

        //Calcul du vecteur directeur afin d'avoir les autres points
        float deltaX = interieur[0] - finalext[0];
        float deltaY = interieur[1] - finalext[1];
        float frac = (float) 0.1;


        float point_x_1 = finalext[0] + (float) ((1 - frac) * deltaX + frac * deltaY);
        float point_y_1 = finalext[1] + (float) ((1 - frac) * deltaY - frac * deltaX);

        float point_x_2 = interieur[0];
        float point_y_2 = interieur[1];

        float point_x_3 = finalext[0] + (float) ((1 - frac) * deltaX - frac * deltaY);
        float point_y_3 = finalext[1] + (float) ((1 - frac) * deltaY + frac * deltaX);

        Node pointb = new Node(point_x_3,point_y_3,"",Color.RED);
        pointb.setRayonDefault(10);
        graph.addNode(pointb);

        Node pointc = new Node(point_x_3,point_y_3,"",Color.RED);
        pointc.setRayonDefault(10);
        graph.addNode(pointc);

        Path pfleche = new Path();
        pfleche.setFillType(Path.FillType.EVEN_ODD);

        //Cree un path créant la fleche
        pfleche.moveTo(finalext[0], finalext[1]);
        pfleche.lineTo(point_x_1, point_y_1);
        pfleche.lineTo(point_x_2, point_y_2);
        pfleche.lineTo(point_x_3, point_y_3);
        pfleche.lineTo(finalext[0], finalext[1]);
        pfleche.close();

        return pfleche;
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
            path.moveTo(a.getNodeFrom().centerX(), a.getNodeFrom().centerY());

            if (a instanceof ArcBoucle) {
                Node n = a.getNodeFrom();
                // Dessiner boucle
                path.cubicTo(n.centerX()+n.getRayon()+60,n.centerY()+n.getRayon()+40,
                        n.centerX()+n.getRayon()+60,n.centerY()-n.getRayon()-40,
                        n.centerX(),n.centerY());
            } else {
               //
                pathTemp.lineTo(a.getNodeTo().centerX(), a.getNodeTo().centerY());
                PathMeasure pm = new PathMeasure(pathTemp,false);
                pm.getPosTan(pm.getLength()/2,midPoint,tangent);
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
