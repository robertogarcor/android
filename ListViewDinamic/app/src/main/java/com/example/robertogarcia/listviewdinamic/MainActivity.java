package com.example.robertogarcia.listviewdinamic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnllprod = (Button) findViewById(R.id.btnLlproductos);
        btnllprod.setOnClickListener(this);

    }

    // Action button listview products
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnLlproductos:
                Intent i = new Intent(this, ListViewProducts.class);
                startActivity(i);
        }

    }
}
