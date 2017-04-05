package com.ioc.robertogarciacorcoles.eac3_2016s1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Roberto on 22/10/2016.
 * Classe gestora ProfileActivity
 */
public class ProfileActivity extends AppCompatActivity implements OnClickListener {


    // Varibles witges
    private EditText nom;
    private EditText cognom;
    private Spinner sexe;
    private EditText comentari;
    private ImageView imgFoto;
    private Button acceptar;
    private Button cancelar;

    // Constant per identificar permis i acció camera
    private static final int EXTERNAL_STORAGE_PERMISSION = 1;
    private static final int CAMERA_ACTION = 0;

    // Varibles adp, identificador imatge i preferencies usuari
    private ArrayAdapter<CharSequence> adpSexe;
    private Uri identificadorImage = null;
    private SharedPreferences prefs;

    /**
     * Mètode per la creacio de la activitat
     * @param savedInstanceState estat de la activitat si ha estat guardada
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Mapatge Witges
        nom = (EditText) findViewById(R.id.edtNom);
        cognom = (EditText) findViewById(R.id.edtCognom);
        comentari = (EditText) findViewById(R.id.edtComentari);
        sexe = (Spinner) findViewById(R.id.spSexe);
        imgFoto = (ImageView) findViewById(R.id.imgViewFoto);
        acceptar = (Button) findViewById(R.id.btnAcceptar);
        cancelar = (Button) findViewById(R.id.btnCancelar);

        // Listners de btns i ImageView
        imgFoto.setOnClickListener(this);
        acceptar.setOnClickListener(this);
        cancelar.setOnClickListener(this);

        // Inflate adpsexe
        carregarDadesSexe();

        // Inicialitazar preferències
        prefs = getSharedPreferences("FilePreferencies",MODE_PRIVATE);

        /**
         *  Preguntem si tenim permisos sino els demanem.
         *  El permis de escriptura WRITE_EXTERNAL_STORAGE engloba READ_EXTERNAL_STORAGE.
         *
         *  Si tenim permis:
         *
         *  Hem hagut de introduir les dades al perfil i desades a les preferencies
         *  ja que son obligatories d'omplir, això vol dir que la seguent vegada poden carregar
         *  les prefencies d'usuari si ja tenim permis.
         *
         *  Tenim en compte que l'usuari faci una cancelació de les dades sense confirmar
         *  Hem d'asegurar-nos que tant la dada sexe con l'imatge no donin cap excepció
         *  per referencia nula.
         *  Respecte a les dades nom, cognom i comentari són strings i poden ser cadenes buides.
         *
         *  Si no tenim permis: Vol dir que no ha entrant encara i no hi ha preferències.
         *
         *  Si versio skd < 23 no demanem permisos, cas contrari >= 23 si demanem permisos
         *  En versions sdk inferior sembla que funciona sense aquesta comprovació
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION);
            } else {
                carregarPreferences();
            }
        } else {
            carregarPreferences();
        }
    }

    /**
     * Mètode reposta del resultat de la resposta del usuari a donar permis o no.
     * @param requestCode constant que identifica la acció
     * @param permissions Array del permisos demanants
     * @param grantResults el resultat de la resposta del usuari.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Codi si s'han acceptat els permissos
                    // En principi no fem res entra directament a la activity
                    return;
                } else {
                    // Codi si s'ha rebutjat
                    missage("Error, has d'acceptar el permís per poder continuar");
                    finish();
                }
                break;
        }
    }


    /**
     * Metode per comprovar si els camps del perfil s'usuari no són buides.
     * Totes han de ser omplides.
     * @return cert si on omplides , fals en cas contrari.
     */
    private boolean comprovarDades() {
        System.out.println("identificador imatge: " + identificadorImage);
        if (nom.getText().toString().isEmpty() ||
                cognom.getText().toString().isEmpty() ||
                comentari.getText().toString().isEmpty() ||
                sexe.getSelectedItem().toString().isEmpty() ||
                identificadorImage == null ) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * Mètode que carrega el items disponibles i infla la combobox > array-string sexe (string.xml)
     */
    private void carregarDadesSexe(){
        adpSexe = ArrayAdapter.createFromResource(this,R.array.sexe,android.R.layout.simple_spinner_item);
        adpSexe.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexe.setAdapter(adpSexe);
    }


    /**
     * Metodo per mostrar un missagte SnackBar a la interface (activity)
     * @param s Missatge a mostrar
     */
    private void missatgeSnackBar(String s, View vista) {
        Snackbar.make(vista, s, Snackbar.LENGTH_LONG).show();
    }


    /**
     * Metodo per mostrar un missagte Toast a la interface (activity)
     * @param s Missatge a mostrar
     */
    private void missage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    /**
     * Mètode per captura les accions de l'activity i realitzar acció corresponent
     * @param v element view que a disparat una acció.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnAcceptar:
                if (comprovarDades()){
                    desarPreferences();
                    finish();
                }else {
                    missatgeSnackBar("[Error] has d'emplenat totes les dades", v);
                }
                break;
            case R.id.btnCancelar:
                finish();
                break;
            case R.id.imgViewFoto:
                ferDesarFotoProfile();
                break;

        }
    }

    /**
     * Mètode que permet carregar les preferències de usuari
     */
    private void carregarPreferences() {
        nom.setText(prefs.getString("nom",""));
        cognom.setText(prefs.getString("cognom",""));
        comentari.setText(prefs.getString("comentari",""));

        if (prefs.getString("sexe","") != "") {
            sexe.setSelection(Integer.parseInt(prefs.getString("sexe","")));
        }

        if (prefs.getString("foto","") != "") {
            identificadorImage = Uri.fromFile(new File(prefs.getString("foto", "")));
            //System.out.println(identificadorImage);
            Bitmap foto = BitmapFactory.decodeFile(prefs.getString("foto", ""));
            imgFoto.setImageBitmap(foto);
        }

    }

    /**
     * Mètode que permet desar les preferències a SharePreferences
     */
    private void desarPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nom", nom.getText().toString());
        editor.putString("cognom", cognom.getText().toString());
        editor.putString("comentari", comentari.getText().toString());
        editor.putString("sexe", String.valueOf(sexe.getSelectedItemPosition()));
        editor.putString("foto",identificadorImage.toString().replace("file://", ""));
        editor.commit();
    }

    /**
     * Mètode que crear Intent per realizar una foto (CAMERA_ACTION) i
     * crea un directory i fitxer per emmagatzemar la foto, un identificador de la ruta imatge
     */
    private void ferDesarFotoProfile() {
        Intent intentFerFoto = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ProfilePhotos");
        if (!imagesFolder.exists()) {
            imagesFolder.mkdir();
        }
        File nomImageFoto = new File(imagesFolder, "fotoProfile.jpg");
        intentFerFoto.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(nomImageFoto));
        identificadorImage = Uri.fromFile(nomImageFoto);
        startActivityForResult(intentFerFoto,CAMERA_ACTION);
    }


    /**
     * Mètode que retorna el resulta de la acció ferDesarFotoProfie (CAMERA_ACTION)
     * i desar la foto al fitxer correponent creat
     * @param requestCode constant que identifica la acció
     * @param resultCode el resutat de la acció
     * @param data les dades rebudes  (En aquest cas es crea content del resultat foto)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_ACTION:
                if (resultCode == Activity.RESULT_OK) {
                    ContentResolver contRes = getContentResolver();
                    contRes.notifyChange(identificadorImage, null);
                    try {
                        Bitmap bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(contRes, identificadorImage);
                        // Reduïm la imatge per no tenir problemes de visualització.
                        int height = (int) (bitmap.getHeight() * 800 / bitmap.getWidth());
                        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 800, height, true);
                        // Guardem el Bitmap generat al fitxer URI corresponent
                        FileOutputStream stream = new FileOutputStream(identificadorImage.toString().replace("file://", ""));
                        resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        stream.flush();
                        stream.close();
                        // L'assignem a l'ImageView
                        imgFoto.setImageBitmap(resized);
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
                break;
        }

    }



    /*********************  Metodes no utilitzats ***************************/


    /**
     * Metode que resfresca el camps de text a buits
     */
    private void refrescarCampsEditText() {
        nom.setText("");
        cognom.setText("");
        comentari.setText("");
    }

    /**
     * Mètode que refresca la image View a buit
     */
    private void refrecarImageView() {
        imgFoto.setImageBitmap(null);
    }


}
