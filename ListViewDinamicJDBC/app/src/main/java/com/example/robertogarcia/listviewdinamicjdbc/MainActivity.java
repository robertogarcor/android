package com.example.robertogarcia.listviewdinamicjdbc;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by Roberto on 17/07/2017.
 * Class Main
 */

public class MainActivity extends AppCompatActivity implements OnClickListener {

    protected static ConnectivityManager connMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        Button btnProductList = (Button) findViewById(R.id.btnLlproducts);
        btnProductList.setOnClickListener(this);

    }

    // Action button listview products
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnLlproducts:
                if (!checkNetwork()){
                    showAlertDialogInf("Information","No network connection!\n\nEnable network connection.");
                } else {
                    Intent i = new Intent(this, ListViewProductsJDBC.class);
                    startActivity(i);
                }
                break;
            default:
                break;
        }
    }


    //Check status of the network connection (is Online)
    public static Boolean checkNetwork(){
        NetworkInfo netinfo = connMgr.getActiveNetworkInfo();
        return (netinfo != null && netinfo.isConnected());
    }


    // Construct msg
    private void showMissage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    // Construct Alert Dialog Information
    private void showAlertDialogInf(String title, String missage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(missage)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
}
