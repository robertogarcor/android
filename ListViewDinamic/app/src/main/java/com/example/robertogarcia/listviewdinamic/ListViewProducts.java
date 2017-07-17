package com.example.robertogarcia.listviewdinamic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.robertogarcia.listviewdinamic.model.ProductAdapter;
import com.example.robertogarcia.listviewdinamic.model.Product;
import com.example.robertogarcia.listviewdinamic.utils.DataSource;

import java.util.ArrayList;
import java.util.List;

public class ListViewProducts extends AppCompatActivity {
    private ListView lvProducts;
    private View ftListView;
    private List<Product> productsList;
    private ProductAdapter adp;
    private Button btnRefresc;
    private TextView tvInfoList;

    public LinearLayout lytProducts;
    public LayoutInflater li;

    private static Boolean isloading;
    private static Boolean init;
    private static final Integer LIMIT = 11;




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        lvProducts = (ListView) findViewById(R.id.lwProducts);
        lytProducts = (LinearLayout) findViewById(R.id.lytListProducts);
        btnRefresc = (Button) findViewById(R.id.btnRefresc);
        tvInfoList = (TextView) findViewById(R.id.tvInfoList);

        init = true;
        isloading = false;

        productsList = new ArrayList<>();

        // Inflate adapter products
        adp = new ProductAdapter(ListViewProducts.this, productsList);
        lvProducts.setAdapter(adp);

        // execute asynctask for load products
        new LoadData().execute();

        // inflate footer listview
        li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftListView = li.inflate(R.layout.footer_listview, null);

        // Action Listener onScroll
        lvProducts.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // pass

            }

            // Listener control onScroll
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(view.getLastVisiblePosition() == totalItemCount -1 && lvProducts.getCount() >= LIMIT && isloading == false) {
                    isloading = true;
                    new LoadData().execute();
                }
            }
        });

        // Action buttom refresc list
        btnRefresc.setOnClickListener(new View.OnClickListener() {
            // Action button refresc load activity and list View
            @Override
            public void onClick(View view) {
                ListViewProducts.this.onRestart();

            }
        });
    }

    // OnRestart method to refresc activity
    @Override
    protected void onRestart() {
        Intent i = getIntent();
        finish();
        startActivity(i);
    }


    // Implementation asynctask load data productos
    private class LoadData extends AsyncTask<Void, Void, List<Product>> {

        DataSource ds = new DataSource();

        @Override
        protected void onPreExecute() {
            if (!init) {
                lvProducts.addFooterView(ftListView, null, false);
            }
        }

        @Override
        protected List<Product> doInBackground(Void... voids) {
            if (!init){
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return (List<Product>) ds.getMoreProductData(LIMIT, productsList.size());
        }

        @Override
        protected void onPostExecute(List<Product> rs) {
            Integer moreItems = 0;

            if (rs != null) {
                for (Product p: rs) {
                    productsList.add(p);
                    if (!init) {
                        moreItems++;
                    }
                }
                if (moreItems > 0) {
                    adp.notifyDataSetChanged();
                    isloading = false;
                } else {
                    if (!init) {
                        Toast.makeText(ListViewProducts.this, "Not more data", Toast.LENGTH_LONG).show();
                    }
                }
                adp.notifyDataSetChanged();

            } else {
                Toast.makeText(ListViewProducts.this, "Error en load Data", Toast.LENGTH_LONG).show();
            }

            if (!init) {
                lvProducts.removeFooterView(ftListView);
            }

            // Text Info List
            tvInfoList.setText("Displaying " + productsList.size() + " of " + ds.getProducts().size() );

            // init is false for more load products according limit
            init = false;

            System.out.println("new offset " + productsList.size());
            System.out.println("list of products " + productsList.size());

        }
    }

};






