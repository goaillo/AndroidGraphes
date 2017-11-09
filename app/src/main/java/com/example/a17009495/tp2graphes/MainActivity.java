package com.example.a17009495.tp2graphes;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
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
    private float lastTouchUpX;
    private float lastTouchUpY;
    private AlertDialog alertDialog;
    private String etiquette;
    private Boolean onNode = false;
    private Node activNode, toNode;
    private String value;
    private boolean modeCreationArc = true,modeDeplacementNoeuds = false, modeCreationNoeud = false, modeModification = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                lastTouchDownX = event.getX();
                lastTouchDownY = event.getY();

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if (isOnNode()) {
                            arcBeginNode = activNode;
                            wasOnNode = true;
                            xBeginArc = lastTouchDownX;
                            yBeginArc = lastTouchDownY;
                            if(modeCreationArc){
                                firstGraph.initArcTemp(lastTouchDownX ,lastTouchDownY);
                            }
                            updateView();
                        } else {
                            activNode = null;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (modeCreationArc) {
                            if (wasOnNode && isOnNode()) {
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
                                                    if (arcBeginNode == activNode) {
                                                        firstGraph.addArc(new ArcBoucle(activNode,etiquette));
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
                                updateView();
                            }else {
                                arcBeginNode = null;
                                wasOnNode = false;
                            }
                            firstGraph.makeArcTempNull();
                        }else if (modeDeplacementNoeuds && isOnNode()){
                            activNode.setCenter(lastTouchDownX, lastTouchDownY);
                            onNode = false;
                            updateView();
                        }//
                        else {
                            arcBeginNode = null;
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:

                        if (modeCreationArc) {
                            if(firstGraph.getArcTemp() != null){
                                firstGraph.setArcTemp(lastTouchDownX,lastTouchDownY);
                                updateView();
                            }
                        }else if(modeDeplacementNoeuds && isOnNode()){
                            activNode.setCenter(lastTouchDownX, lastTouchDownY);
                            updateView();
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
                for(Node n :  firstGraph.getNodes())
                {
                    if(n.contains(lastTouchDownX,lastTouchDownY)) {
                        onNode = true;
                        activNode = n;
                        return false;
                    }
                }

                //test de commit git

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
                } else if (onNode && modeCreationArc){

                }
                return true;
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        if(onNode && modeModification){
            super.onCreateContextMenu(menu, v, menuInfo);
            MenuInflater inflater = this.getMenuInflater();
            inflater.inflate(R.menu.menu_contextuel_noeud, menu);

        }
        onNode=false;

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.supprimerNoeud:
                firstGraph.removeNode(activNode);
                updateView();
                return true;
            case R.id.modifierCouleur:
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
                                //if this button is clicked, close
                                // current activity

                                int pos = inputColor.getSelectedItemPosition();
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

                alertDialogBuilderColor.setView(inputColor);
                // create alert dialog
                alertDialog = alertDialogBuilderColor.create();
                alertDialog.show();
                return true;
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


    private void updateView (){
        graphe = new DrawableGraph(firstGraph);
        imgv.setImageDrawable(graphe);
    }

    private void modifierAlertDialog(String title, String button)
    {
        final EditText input = new EditText(this);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setPositiveButton(button,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        value = input.getText().toString();
                        input.setText("");

                    }
                });

        alertDialogBuilder.setView(input);
        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
                modeCreationNoeud = false;
                modeModification = true;
                this.setTitle(R.string.modeModifier);
                return true;
            case R.id.modeCreationArc:
                modeDeplacementNoeuds = false;
                modeCreationArc = true;
                modeCreationNoeud = false;
                modeModification = false;
                this.setTitle(R.string.modeCreationArc);
                return true;
            case R.id.modeCreationNoeud:
                modeDeplacementNoeuds = false;
                modeCreationArc = false;
                modeCreationNoeud = true;
                modeModification = false;
                this.setTitle(R.string.modeCreationNoeud);
                return true;
            case R.id.modeDeplacementNoeuds:
                modeDeplacementNoeuds = true;
                modeCreationArc = false;
                modeCreationNoeud = false;
                modeModification = false;
                this.setTitle(R.string.modeDeplacementNoeuds);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isOnNode(){
        activNode = firstGraph.getOneNode(lastTouchDownX,lastTouchDownY);
        return activNode != null;
    }
}
