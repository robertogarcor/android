package com.ioc.roberto.eac1_2016s1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;


public class SeleccioActivity extends AppCompatActivity implements OnClickListener {

    //Declarem combox i btns de la activity
    private Spinner spCicles, spCurs;
    private Button btnAceptar, btnCancelar;

    //Declarem les variables per emmagatzemar els valors
    private String opcioSel;
    private String cicleSel, cursSel;


    /**
     * Metode per crear la activity
     * Iniciar/obtenir elemments activity i verificar intens
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccio);

        //Obtenim els elements tipus spinner
        spCicles = (Spinner) findViewById(R.id.spinnerCicles);
        spCurs=(Spinner)findViewById(R.id.spinnerCurs);

        //Obtenim el btns i els inicialitzem com a Listeners de la Activity
        btnAceptar = (Button)findViewById(R.id.btnAcceptar);
        btnAceptar.setOnClickListener(this);
        btnCancelar = (Button)findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(this);

        //Carregem dades a combo i obtenir intents
        carregarDadesCicles();
        carregarDadesCurs();
        obtenirDadesIntent();

    }

    /**
     * Metodo de la classe OnClickListener per la
     * execució de la acció segons el btn click.
     * @param v event(id btn) que ha disparat la acció
     */
    @Override
    public void onClick(View v) {
        // Si btnAceptar passen resultat a MainActivity
        if (v == btnAceptar) {

            //Obtenir seleccio combox (spinner)
            cicleSel = spCicles.getSelectedItem().toString();
            cursSel = spCurs.getSelectedItem().toString();

            //Creen Intent per passar dades a mainActivity
            Intent i = new Intent(this,MainActivity.class);

            //Creem Bundle preparem el paquet i passen dades a MainActivity
            Bundle extra = new Bundle();
            extra.putString("opcio",opcioSel);
            extra.putString("cicleSel",cicleSel);
            extra.putString("cursSel",cursSel);
            i.putExtras(extra);
            setResult(RESULT_OK,i);
            finish();

        }

        //Si btnCanclear click reiniciem comboxs
        if (v == btnCancelar) {
            carregarDadesCicles();
            carregarDadesCurs();
        }
    }

    /**
     * Metodo inicialitzacio per carregar i omplir dades a combo cicles
     */
    private void carregarDadesCicles() {
        ArrayAdapter<CharSequence> adpCicles = ArrayAdapter.createFromResource(this, R.array.cicles, android.R.layout.simple_spinner_item);
        adpCicles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCicles.setAdapter(adpCicles);
    }

    /**
     * Metodo inicialitzacion per carregar i omplir dades a combo curs
     */
    private void carregarDadesCurs() {
        ArrayAdapter<CharSequence> adpCurs = ArrayAdapter.createFromResource(this, R.array.curs, android.R.layout.simple_list_item_1);
        adpCurs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurs.setAdapter(adpCurs);
    }

    /**
     * Metodo per obtenir dades intent MainActivity referencia opcio seleccionada
     */
   private void obtenirDadesIntent(){
        Bundle extras = getIntent().getExtras();
        if (extras != null ) {
            opcioSel = extras.getString("opcio");
        }
   }

}
