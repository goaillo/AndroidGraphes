package classes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

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
        Paint p2 = new Paint();
        p2.setStrokeWidth(5);
        p2.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        for(Arc a : graph.getArcs()){
            p2.setColor(a.getColor());
            path.moveTo(a.getNodeFrom().centerX(),a.getNodeFrom().centerY());
            path.quadTo((a.getNodeFrom().centerX()+a.getNodeTo().centerX())/2, (a.getNodeFrom().centerY()+a.getNodeTo().centerY())/2, a.getNodeTo().centerX(), a.getNodeTo().centerY());
            canvas.drawPath(path,p2);
        }

        Paint p = new Paint();
        Paint pEt = new Paint();
        pEt.setColor(Color.WHITE);
        pEt.setTextSize(30);
        pEt.setTextAlign(Paint.Align.CENTER);
        for(Node n : graph.getNodes()) {
            p.setColor(n.getColor());
            canvas.drawRoundRect(n, 40, 40, p);
            canvas.drawText(n.getEtiquette(), n.centerX(), n.centerY(), pEt);
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
        return 0;
    }
}
