package com.ioc.roberto.eac1_2016s1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    //Declarem els btns i elements text de la activity
    private ImageButton btnEditOp1;
    private ImageButton btnEditOp2;
    private ImageButton btnDelOp1;
    private ImageButton btnDelOp2;
    private Button btnEnviar;
    private EditText nomCognom;
    private TextView cicle1, cicle2;
    private TextView curs1, curs2;

    //Declaracio de varibles i contants
    private String opcioSel,cicleSel,cursSel;
    private static final int ACTIVITAT_SELECCIO = 0;
    private static final String OPCIO_1 = "1";
    private static final String OPCIO_2 = "2";
    private static final String UNDEFINED = "Valor no definit";


    /**
     * Metode per crear la activity
     * Iniciar/obtenir elements activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtenim el btns i els inicialitzem com a Listeners de la Activity
        btnEditOp1 = (ImageButton)findViewById(R.id.btnEditOp1);
        btnEditOp1.setOnClickListener(this);
        btnEditOp2 = (ImageButton)findViewById(R.id.btnEditOp2);
        btnEditOp2.setOnClickListener(this);
        btnDelOp1 = (ImageButton)findViewById(R.id.btnDelOp1);
        btnDelOp1.setOnClickListener(this);
        btnDelOp2 = (ImageButton)findViewById(R.id.btnDelOp2);
        btnDelOp2.setOnClickListener(this);
        btnEnviar = (Button)findViewById(R.id.btnEnviar);
        btnEnviar.setOnClickListener(this);

        //Obtenim els elements tipus text
        nomCognom = (EditText)findViewById(R.id.edtNom);
        cicle1 = (TextView)findViewById(R.id.tvCicleOp1res);
        cicle2 = (TextView)findViewById(R.id.tvCicleOp2res);
        curs1 = (TextView)findViewById(R.id.tvCursOp1res);
        curs2 = (TextView)findViewById(R.id.tvCursOp2res);

    }

    /**
     *  Metodo de la classe OnClickListener per la
     *  execució de la acció segons el btn click.
     * @param v event(id btn) que ha disparat acció
     */
    @Override
    public void onClick(View v) {
        // Si Btn Enviar click comproven/validem dades no buides
        if (v == btnEnviar) {
            comprobarDadesNoBuides();
        }

        // Segons BtnEdit click passen referencia opcioEdit i iniciem activity selecció
        if (v == btnEditOp1 || v == btnEditOp2) {

            opcioSel = v == btnEditOp1 ? OPCIO_1 : OPCIO_2;

            //Creen Intent per passar referencia
            Intent i = new Intent(getApplicationContext(), SeleccioActivity.class);

            //Creem Bundle per afegir referencia i passen opcioEdit a activity seleccio
            Bundle extra = new Bundle();
            extra.putString("opcio",opcioSel);
            i.putExtras(extra);
            startActivityForResult(i,ACTIVITAT_SELECCIO);
        }

        // Segons BtnDel click esborrem dades opcio escollida
        if ( v == btnDelOp1) {
           //Esborrar dades opcio 1
            cicle1.setText(UNDEFINED);
            curs1.setText(UNDEFINED);
        }

        if ( v == btnDelOp2) {
            //Esborrar dades opcio 2
            cicle2.setText(UNDEFINED);
            curs2.setText(UNDEFINED);
        }
    }

    /**
     * Metode per obtenir resposta de SeleccioActivity
     * Dades seleccio segons opcio escollida
     * @param requestCode codi de la crida a SeleccioActivity
     * @param resultCode codi resultat resultSet SeleccioActivity
     * @param data el paquet amb les dades retornades de la SeleccioActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITAT_SELECCIO){
            if (resultCode == RESULT_OK){
                if (data != null ){
                    opcioSel = data.getStringExtra("opcio");
                    cicleSel = data.getStringExtra("cicleSel");
                    cursSel = data.getStringExtra("cursSel");
                    mostrarResultatSeleccio(opcioSel,cicleSel,cursSel);

                }
            }
        }
    }

    /**
     * Metodo per comprobar si todes les dades de la activity estan
     * completes i no son buides
     * Segons cas llancem missatge (toast)
     */
    private void comprobarDadesNoBuides(){
        if ( nomCognom.getText().toString().isEmpty() ||
                cicle1.getText().equals(UNDEFINED)  ||
                cicle2.getText().equals(UNDEFINED) ||
                curs1.getText().equals(UNDEFINED) ||
                curs2.getText().equals(UNDEFINED) )
        {
            missage("Error!, has d'emplear totes les dades.");
        } else {
            missage("Dades Enviades!");
        }
    }


    /**
     * Metodo per mostrar resultats seleccio opcio
     * No 2 opcions iguals
     * Si 2 cicles iguals amb curs diferent
     * Si seleccio opcions ja seleccionades (iguals) mostrem missagte
     * opcio ja seleccionada
     */
    private void mostrarResultatSeleccio(String opcioSel, String cicleSel, String cursSel){
        if (opcioSel.equals(OPCIO_1)) {
            if (cicle2.getText().equals(cicleSel) && curs2.getText().equals(cursSel)){
                missage("Opció ja seleccionada, no es faran canvis.");
            } else {
                curs1.setText(cursSel);
                cicle1.setText(cicleSel);
            }
        }

        if (opcioSel.equals(OPCIO_2) ) {
            if (cicle1.getText().equals(cicleSel) && curs1.getText().equals(cursSel)){
                missage("Opció ja seleccionada, no es faran canvis.");
            } else {
                curs2.setText(cursSel);
                cicle2.setText(cicleSel);
            }
        }
    }

    /**
     * Metodo per mostrar un missagte a la interface (activity)
     * @param s Missatge a mostrar
     */
    private void missage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }



}
