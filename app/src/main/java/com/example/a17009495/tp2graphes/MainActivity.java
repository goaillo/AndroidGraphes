package com.example.a17009495.tp2graphes;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ActionBarContextView;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import classes.Arc;
import classes.ArcBoucle;
import classes.ArcFinal;
import classes.DrawableGraph;
import classes.Graph;
import classes.Node;

public class MainActivity extends AppCompatActivity {

    private static Graph firstGraph;
    private ImageView imgv;
    private static DrawableGraph graphe;
    private float lastTouchDownX;
    private float lastTouchDownY;
    private AlertDialog alertDialog;
    private String etiquette;
    private int largeur, largeurEtiquette;
    private Boolean onNode = false, onArc =false, wasOnArc=false;
    private Node activNode;
    private ArcFinal activArc;
    private String value;
    private boolean modeCreationArc = true,modeDeplacementNoeuds = false, modeCreationNoeud = false, modeModification = false, modeCourbure = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle(R.string.modeCreationArc);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialisation du graph et d'image view
        this.registerForContextMenu(this.findViewById(R.id.imgView));
        if (firstGraph == null) {
            firstGraph = new Graph();
        }
        imgv = (ImageView) findViewById(R.id.imgView);
        if (graphe == null) {
            graphe = new DrawableGraph(firstGraph);
        }
        imgv.setImageDrawable(graphe);


        imgv.setOnTouchListener(new View.OnTouchListener() {
            boolean wasOnNode = false;
            Float xBeginArc, yBeginArc;
            Node arcBeginNode;
            ArcFinal arc;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //on recupere les coordonnées
                lastTouchDownX = event.getX();
                lastTouchDownY = event.getY();

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (isOnNode()) {
                            arcBeginNode = activNode;
                            wasOnNode = true;
                            xBeginArc = lastTouchDownX;
                            yBeginArc = lastTouchDownY;

                            //on initialise l'arc temporaire pour la création de l'arc
                            if(modeCreationArc){
                                firstGraph.initArcTemp(lastTouchDownX ,lastTouchDownY);
                            }
                            updateView();
                        } else if (isOnArc()){
                            wasOnArc = true;
                        }
                        else {
                            activNode = null;
                            activArc = null;
                            arc = null;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        //Mode CREATION d'ARC
                        if (modeCreationArc) {
                            //Si on étais sur un noeud et que l'on arrive sur un noeud (fin creation arc)
                            if (wasOnNode && isOnNode()) {

                                //affichage dialogue pour l'étiquette de l'arc
                                final EditText input = new EditText(MainActivity.this);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                alertDialogBuilder.setTitle("Etiquette de l'arc");

                                // set dialog message
                                alertDialogBuilder
                                        .setMessage("Entrez l'étiquette")
                                        .setPositiveButton("Ajouter",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // if this button is clicked, close
                                                // current activity
                                                etiquette = input.getText().toString();

                                                if(etiquette.length()>0){

                                                    //Si l'arc de début = l'arc de fin, on créer un objet boucle
                                                    if (arcBeginNode == activNode) {
                                                        firstGraph.addArc(new ArcBoucle(activNode,etiquette));

                                                    //sinon on creer un objet arc simple
                                                    } else {
                                                        firstGraph.addArc(new ArcFinal(arcBeginNode, activNode,etiquette));
                                                    }
                                                    input.setText("");
                                                    arcBeginNode = null;
                                                    wasOnNode = false;
                                                }

                                            }
                                        });

                                alertDialogBuilder.setView(input);
                                // create alert dialog
                                alertDialog = alertDialogBuilder.create();
                                // show it
                                alertDialog.show();

                            }else {
                                arcBeginNode = null;
                                wasOnNode = false;
                            }

                            //on detruit l'arc temporaire
                            firstGraph.makeArcTempNull();
                            updateView();
                        //Si on veut déplacer un noeud, on change son centre avec les dernieres coordonnées
                        }else if (modeDeplacementNoeuds && isOnNode()){
                            activNode.setCenter(lastTouchDownX, lastTouchDownY);
                            onNode = false;
                            updateView();

                        //Fin de la courbure de l'arc, on calcule une derniere fois le milieu
                        } else if (modeCourbure){
                            if(arc!=null){
                                activArc = arc;
                                calculerMilieuArc();
                            }
                        }
                        else {
                            arcBeginNode = null;
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //On créer un arc temporaire en suivant le doigt
                        if (modeCreationArc) {
                            if(firstGraph.getArcTemp() != null){
                                firstGraph.setArcTemp(lastTouchDownX,lastTouchDownY);
                                updateView();
                            }

                        //On change le placement du noeud
                        }else if(modeDeplacementNoeuds && isOnNode()){
                            activNode.setCenter(lastTouchDownX, lastTouchDownY);
                            updateView();

                        //On change la courbure de l'arc
                        } else if(modeCourbure && isOnArc()){
                            arc = activArc;
                            calculerMilieuArc();
                        }

                        break;
                }
                return false;
            }

            ;
        });

        imgv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final EditText input = new EditText(MainActivity.this);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                //Si on est sur un noeud, on affiche un menu contextuel
                for(Node n :  firstGraph.getNodes())
                {
                    if(n.contains(lastTouchDownX,lastTouchDownY)) {
                        onNode = true;
                        activNode = n;
                        return false;
                    }
                }

                //On affiche un dialogue permettant de remplir l'étiquette du noeud en mode création de noeud
                if(modeCreationNoeud){
                    // set title
                    alertDialogBuilder.setTitle("Création nouveau noeud");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Entrez l'étiquette du noeud")
                            .setPositiveButton("Ajouter",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    etiquette = input.getText().toString();

                                    Node node = new Node(lastTouchDownX, lastTouchDownY, etiquette, Color.BLACK);
                                    if(etiquette.length()>0){
                                        firstGraph.addNode(node);
                                        updateView();
                                        input.setText("");
                                    }

                                }
                            });

                    alertDialogBuilder.setView(input);
                    // create alert dialog
                    alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                } else if (modeModification){
                    onArc = isOnArc();
                    return false;
                }
                return true;
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        //Sur un long click, en mode de modification et sur un noeud, on affiche le menu contextuel
        if(onNode && modeModification){
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = this.getMenuInflater();
            inflater.inflate(R.menu.menu_contextuel_noeud, menu);

        //Sur un long click, sur le milieu de l'arc en mode Modification, on affiche le menu contextuel
        }else if(onArc && modeModification)
        {
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = this.getMenuInflater();
            inflater.inflate(R.menu.menu_contextuel_arc, menu);
        }
        onNode=false;
        onArc = false;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //OPTION DU MENU CONTEXTUEL

        switch (item.getItemId()) {
            //Modification de la taille de l'étiquette d'un arc
            case R.id.modifierTailleEtiquette:
                final EditText inputLargeurEtiquette = new EditText(this);
                AlertDialog.Builder alertDialogBuilderLargeurEtiquette = new AlertDialog.Builder(
                        this);
                // set title
                alertDialogBuilderLargeurEtiquette.setTitle("Entrer la nouvelle Largeur de l'etiquette");

                // set dialog message
                alertDialogBuilderLargeurEtiquette
                        .setPositiveButton("Modifier",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                largeurEtiquette = Integer.valueOf(inputLargeurEtiquette.getText().toString());
                                if(inputLargeurEtiquette.getText().toString().length()>0) {
                                    activArc.setLargeurEtiquette(largeurEtiquette);
                                    updateView();
                                    inputLargeurEtiquette.setText("");
                                }
                            }
                        });

                alertDialogBuilderLargeurEtiquette.setView(inputLargeurEtiquette);
                // create alert dialog
                alertDialog = alertDialogBuilderLargeurEtiquette.create();
                alertDialog.show();
                return true;

            //Modification de la largeur d'un arc
            case R.id.modifierLargeurArc:
                final EditText inputLargeur = new EditText(this);
                AlertDialog.Builder alertDialogBuilderLargeur = new AlertDialog.Builder(
                        this);
                // set title
                alertDialogBuilderLargeur.setTitle("Entrer la nouvelle largeur de l'arc");

                // set dialog message
                alertDialogBuilderLargeur
                        .setPositiveButton("Modifier",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                largeur = Integer.valueOf(inputLargeur.getText().toString());
                                if(inputLargeur.getText().toString().length()>0){
                                    activArc.setWidth(largeur);
                                    updateView();
                                    inputLargeur.setText("");
                                }
                            }
                        });

                alertDialogBuilderLargeur.setView(inputLargeur);
                // create alert dialog
                alertDialog = alertDialogBuilderLargeur.create();
                alertDialog.show();

                return true;

            //Modification de l'étiquette d'un arc
            case R.id.modifierEtiquetteArc:
                final EditText inputEtiquette = new EditText(this);
                AlertDialog.Builder alertDialogBuilderEtiquette = new AlertDialog.Builder(
                        this);
                // set title
                alertDialogBuilderEtiquette.setTitle("Entrer la nouvelle étiquette");

                // set dialog message
                alertDialogBuilderEtiquette
                        .setPositiveButton("Modifier",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                etiquette = inputEtiquette.getText().toString();

                                if(etiquette.length()>0){
                                    activArc.setEtiquette(etiquette);
                                    updateView();
                                    inputEtiquette.setText("");
                                }

                            }
                        });

                alertDialogBuilderEtiquette.setView(inputEtiquette);
                // create alert dialog
                alertDialog = alertDialogBuilderEtiquette.create();
                alertDialog.show();
                return true;

            //Suppression d'un arc
            case R.id.supprimerArc:
                firstGraph.removeArc(activArc);
                updateView();
                return true;

            //Modification de la couleur d'un arc
            case R.id.modifierCouleurArc:
                final Spinner inputColor = new Spinner(this);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.color_arrays, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                inputColor.setAdapter(adapter);
                AlertDialog.Builder alertDialogBuilderColor = new AlertDialog.Builder(
                        this);
                // set title
                alertDialogBuilderColor.setTitle("Changer la couleur");

                // set dialog message
                alertDialogBuilderColor
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                int pos = inputColor.getSelectedItemPosition();
                                switch (pos){
                                    case 0:
                                        activArc.setColor(Color.RED);
                                        updateView();
                                        break;
                                    case 1:
                                        activArc.setColor(Color.GREEN);
                                        updateView();
                                        break;
                                    case 2:
                                        activArc.setColor(Color.BLUE);
                                        updateView();
                                        break;
                                    case 3:
                                        activArc.setColor(Color.parseColor("#f49542"));
                                        updateView();
                                        break;
                                    case 4:
                                        activArc.setColor(Color.CYAN);
                                        updateView();
                                        break;
                                    case 5:
                                        activArc.setColor(Color.MAGENTA);
                                        updateView();
                                        break;
                                    case 6:
                                        activArc.setColor(Color.BLACK);
                                        updateView();
                                        break;
                                }

                            }
                        });
                alertDialogBuilderColor.setView(inputColor);
                // create alert dialog
                alertDialog = alertDialogBuilderColor.create();
                alertDialog.show();
                return true;

            //Suppression d'un noeud
            case R.id.supprimerNoeud:
                firstGraph.removeNode(activNode);
                updateView();
                return true;

            //Modification de la couleur d'un noeud
            case R.id.modifierCouleur:
                final Spinner inputColorNode = new Spinner(this);
                ArrayAdapter<CharSequence> adapterNode = ArrayAdapter.createFromResource(this, R.array.color_arrays, android.R.layout.simple_spinner_item);
                adapterNode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                inputColorNode.setAdapter(adapterNode);
                AlertDialog.Builder alertDialogBuilderColorNode = new AlertDialog.Builder(
                        this);
                // set title
                alertDialogBuilderColorNode.setTitle("Changer la couleur");

                // set dialog message
                alertDialogBuilderColorNode
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //if this button is clicked, close
                                // current activity

                                int pos = inputColorNode.getSelectedItemPosition();
                                switch (pos){
                                    case 0:
                                        activNode.setColor(Color.RED);
                                        updateView();
                                        break;
                                    case 1:
                                        activNode.setColor(Color.GREEN);
                                        updateView();
                                        break;
                                    case 2:
                                        activNode.setColor(Color.BLUE);
                                        updateView();
                                        break;
                                    case 3:
                                        activNode.setColor(Color.parseColor("#f49542"));
                                        updateView();
                                        break;
                                    case 4:
                                        activNode.setColor(Color.CYAN);
                                        updateView();
                                        break;
                                    case 5:
                                        activNode.setColor(Color.MAGENTA);
                                        updateView();
                                        break;
                                    case 6:
                                        activNode.setColor(Color.BLACK);
                                        updateView();
                                        break;
                                }

                            }
                        });

                alertDialogBuilderColorNode.setView(inputColorNode);
                // create alert dialog
                alertDialog = alertDialogBuilderColorNode.create();
                alertDialog.show();
                return true;

            //Modification de l'étiquette d'un noeud
            case R.id.modifierEtiquette:

                final EditText input = new EditText(this);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        this);
                // set title
                alertDialogBuilder.setTitle("Entrer la nouvelle étiquette");

                // set dialog message
                alertDialogBuilder
                        .setPositiveButton("Modifier",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                etiquette = input.getText().toString();

                                if(etiquette.length()>0){
                                    activNode.setEtiquette(etiquette);
                                    updateView();
                                    input.setText("");
                                }

                            }
                        });

                alertDialogBuilder.setView(input);
                // create alert dialog
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                //activNode.setEtiquette();
                return true;

            //Modification de la taille d'un noeud
            case R.id.modifierTaille:

                final EditText inputTaille = new EditText(this);
                inputTaille.setInputType(InputType.TYPE_CLASS_NUMBER);
                AlertDialog.Builder alertDialogBuilderTaille = new AlertDialog.Builder(
                        this);
                // set title
                alertDialogBuilderTaille.setTitle("Entrer la nouvelle taille");

                // set dialog message
                alertDialogBuilderTaille
                        .setPositiveButton("Modifier",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                value = inputTaille.getText().toString();

                                if(value.length()>0 && value != null){
                                    activNode.setRayonDefault(Float.valueOf(value));
                                    updateView();
                                    inputTaille.setText("");
                                }

                            }
                        });

                alertDialogBuilderTaille.setView(inputTaille);
                // create alert dialog
                alertDialog = alertDialogBuilderTaille.create();
                alertDialog.show();

                return true;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Méthode permettant de charger l'imageview
     */
    private void updateView (){
        graphe = new DrawableGraph(firstGraph);
        imgv.setImageDrawable(graphe);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu_test à l'ActionBar
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.modeModifier:
                modeCreationArc = false;
                modeDeplacementNoeuds = false;
                modeCourbure = false;
                modeCreationNoeud = false;
                modeModification = true;
                this.setTitle(R.string.modeModifier);
                return true;
            case R.id.modeCreationArc:
                modeDeplacementNoeuds = false;
                modeCreationArc = true;
                modeCourbure = false;
                modeCreationNoeud = false;
                modeModification = false;
                this.setTitle(R.string.modeCreationArc);
                return true;
            case R.id.modeCreationNoeud:
                modeDeplacementNoeuds = false;
                modeCreationArc = false;
                modeCourbure = false;
                modeCreationNoeud = true;
                modeModification = false;
                this.setTitle(R.string.modeCreationNoeud);
                return true;
            case R.id.modeDeplacementNoeuds:
                modeDeplacementNoeuds = true;
                modeCreationArc = false;
                modeCourbure = false;
                modeCreationNoeud = false;
                modeModification = false;
                this.setTitle(R.string.modeDeplacementNoeuds);
                return true;
            case R.id.modeCourbure:
                modeCourbure = true;
                modeModification = false;
                modeDeplacementNoeuds = false;
                modeCreationArc = false;
                modeCreationNoeud =false;
                this.setTitle(R.string.modeCourbure);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Méthode qui permet de savoir s'il on est sur un noeud ou non,
     * dans le cas où on est sur un noeud, affecte ce noeud à activNode
     * @return
     */
    public boolean isOnNode(){
        activNode = firstGraph.getOneNode(lastTouchDownX,lastTouchDownY);
        return activNode != null;
    }

    /**
     * Méthode permettant de savoir s'il on est sur un arc ou non,
     * dans le cas où on est sur un arc, affecte cet arc à activArc
     * @return
     */
    public boolean isOnArc(){
        activArc = firstGraph.getOneArc(lastTouchDownX,lastTouchDownY);
        return activArc != null;
    }

    /**
     * Méthode permettant de modifier la courbure de l'arc en prennant en compte la tangente
     * et le point de milieu des deux noeuds (en ligne droite)
     */
    public void calculerMilieuArc(){
        Node nFrom = activArc.getNodeFrom(), nTo = activArc.getNodeTo();
        Path pathTemp = new Path();
        pathTemp.moveTo(nFrom.centerX(),nFrom.centerY());
        pathTemp.quadTo((nFrom.centerX()+nTo.centerX())/2,(nFrom.centerY()+nTo.centerY())/2,nTo.centerX(),nTo.centerY());

        ////La partie de calcul sur les coefficients des différentes droites à été réalisée à l'aide de Kévin Boisgontier

        float[] mid = {0, 0}, tan = {0, 0};
        PathMeasure pm = new PathMeasure(pathTemp,false);
        pm.getPosTan(pm.getLength()/2, mid, tan);

        //Dernieres coordonnées touchées
        float x1 = lastTouchDownX;
        float y1 = lastTouchDownY;

        //Coefficient pour le calcul du projeté orthogonal
        float c = (tan[0]*(mid[0]-x1)+tan[1]*(mid[1]-y1))/(tan[0]*tan[0]+tan[1]*tan[1]);

        //Coefficients de la droite parallèle à la tangente passant par x1 et y1
        float m1 = tan[1]/tan[0];
        float b1 = y1-m1*x1;

        //Coefficients de la droite perpendiculaire à la tangente
        float m2 = (y1-mid[1]+tan[1]*c)/(x1-mid[0]+tan[0]*c);
        float b2 = mid[1]-m2*mid[0];

        //Point d'intersection des deux droites
        float x = (b2-b1)/(m1-m2);
        float y = m1*((b2-b1)/(m1-m2))+b1;


        if(tan[0]==0){
            x = x1;
            y = mid[1];
        }
        if(tan[1]==0){
            x = mid[0];
            y = y1;
        }

        float[] newMid = {x,y};
        activArc.setMidPointCourb(newMid);
        updateView();
    }
}
