package com.example.robertogarcia.listviewdinamicjdbc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
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

import com.example.robertogarcia.listviewdinamicjdbc.db.GestorJDBC;
import com.example.robertogarcia.listviewdinamicjdbc.model.ProductAdapter;
import com.example.robertogarcia.listviewdinamicjdbc.model.Product;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Roberto García Córcoles
 * @version 17/07/2017/A
 * Class management ListViewProductsJDBC
 */

public class ListViewProductsJDBC extends AppCompatActivity {
    private ListView lvProducts;
    private View ftListView;
    private List<Product> productsList;
    private ProductAdapter adp;
    private Button btnRefresc;
    private TextView tvInfoList;
    private static Boolean isloading = false;

    private LinearLayout lytProducts;
    private LayoutInflater li;

    private static Boolean init;
    private final static Integer LIMIT = 11;
    private Integer countProducts;

    private GestorJDBC db;
    private Connection conn;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        // Mapping widgets
        lvProducts = (ListView) findViewById(R.id.lwProducts);
        lytProducts = (LinearLayout) findViewById(R.id.lytListProducts);
        btnRefresc = (Button) findViewById(R.id.btnRefresc);
        tvInfoList = (TextView) findViewById(R.id.tvInfoList);

        mHandler = new MyHandler();
        productsList = new ArrayList<>();

        init = true;
        isloading = false;

        // Inflate adapter products
        adp = new ProductAdapter(ListViewProductsJDBC.this, productsList);
        lvProducts.setAdapter(adp);

        // inflate footer listview
        li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftListView = li.inflate(R.layout.footer_listview, null);

        //******** ASYNCTASK ********//

        //Comment or uncomment task according to needs

        ///// 1.- Run implementacion Thread - comunication UI > Handler - Message
        ThreadHandlerLoadData mThreadHandler = new ThreadHandlerLoadData();
        new Thread(mThreadHandler).start();

        ///// 2.- Run implementation Thread - comunication UI > runOnUiThread
        //ThreadIULoadData mThreadIU = new ThreadIULoadData();
        //mThreadIU.start();

        ///// 3.- Run implementation AsyncTask
        //new AsyncTaskLoadData().execute();

        //***************************//


        // Action Listener onScroll
        lvProducts.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // pass
            }

            // Listener control onScroll
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (MainActivity.checkNetwork()) {
                    if (view.getLastVisiblePosition() == totalItemCount - 1 && lvProducts.getCount() >= LIMIT && isloading == false) {
                        isloading = true;

                        /////*****  ASYNCTASK *****/////

                        //Comment or uncomment task according to needs

                        ///// 1.- Run implementacion Thread - comunication UI > Handler - Message
                        ThreadHandlerLoadData mThreadHandler = new ThreadHandlerLoadData();
                        new Thread(mThreadHandler).start();

                        ///// 2.- Run implementation Thread - comunication UI > runOnUiThread
                        //ThreadIULoadData mThreadIU = new ThreadIULoadData();
                        //mThreadIU.start();

                        //// 3.- Run implementation AsyncTask
                        //new AsyncTaskLoadData().execute();
                    }
                } else {
                    showMissage("No network connection!");
                }
            }
        });

        // Action buttom refresc list (inicializate activity)
        btnRefresc.setOnClickListener(new View.OnClickListener() {
            // Action button refresc load activity and list View
            @Override
            public void onClick(View view) {
                Intent i = getIntent();
                finish();
                startActivity(i);

            }
        });
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


    /************************************************************************/
    ///// 1.- Run implementacion Thread - comunication UI > Handler - Message
    /************************************************************************/

    private class ThreadHandlerLoadData implements Runnable {
        @Override
        public void run() {
            if (!init) {
                // Notify Add panel footer list view
                mHandler.sendEmptyMessage(3);
                //Thread sleep 3s for view progress bar listview footer
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message msg;
            ResultSet rs = null;
            db = new GestorJDBC();
            db.connetion();
            rs = db.loadData(conn, LIMIT, productsList.size());
            if (init) {
                countProducts = db.countData(conn);
            }
            Integer moreItems = 0;
            try {
                if (rs != null) {
                    // ResultSet contains data
                    while (rs.next()) {
                        Product p = new Product(rs.getInt(1),
                                rs.getString(2),
                                rs.getFloat(3),
                                rs.getInt(4),
                                rs.getString(5),
                                rs.getInt(6));
                        productsList.add(p);
                        if (!init) {
                            moreItems++;
                        }
                    }
                    if (moreItems > 0) {
                        isloading = false;
                    } else {
                        if (!init) {
                            // Notify no more data
                            mHandler.sendEmptyMessage(2);
                        }
                    }
                    // Notify change adapter and Text Info List
                    mHandler.sendEmptyMessage(0);
                } else {
                    // Notify error load data
                    mHandler.sendEmptyMessage(1);
                }
                if (!init) {
                    // Notify remove footer listview
                    mHandler.sendEmptyMessage(4);
                }

                // init is false for more load products according limit
                init = false;

                System.out.println("new offset " + productsList.size());
                System.out.println("list of products " + productsList.size());

            } catch (SQLException e) {
                //Toast.makeText(ListViewProductsJDBC.this, "Error en load Data", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } finally {
                db.closeResultSet(rs);
                db.closeConnection(conn);

            }

        }
    };

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // Change adapter and Text Info List
                    adp.notifyDataSetChanged();
                    tvInfoList.setText("Displaying " + productsList.size() + " of " + countProducts + " products");
                    break;
                case 1:
                    showMissage("Error en load Data");
                    break;
                case 2:
                    showMissage("No more data");
                    break;
                case 3:
                    // Add footer listview
                    lvProducts.addFooterView(ftListView, null, false);
                    break;
                case 4:
                    // Remove footer listview
                    lvProducts.removeFooterView(ftListView);
                    break;
                default:
                    break;
            }
        }
    };



    /*******************************************************************************/
    ///// 2.- Implementation of data loading Thread - comunication UI > runOnUiThread
    /*******************************************************************************/

    private class ThreadIULoadData extends Thread {
            @Override
            public void run() {
                if (!init) {
                    // Comunitacion UIThread (UI Interface)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Add panel footer list view
                            lvProducts.addFooterView(ftListView, null, false);
                        }
                    });
                    //Thread sleep 3s for view progress bar listview footer
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                ResultSet rs = null;
                db = new GestorJDBC();
                conn = db.connetion();
                rs = db.loadData(conn, LIMIT, productsList.size());
                if (init) {
                    countProducts = db.countData(conn);
                }
                Integer moreItems = 0;
                try {
                    if (rs != null) {
                        // ResultSet contains data
                        while (rs.next()) {
                            Product p = new Product(rs.getInt(1),
                                    rs.getString(2),
                                    rs.getFloat(3),
                                    rs.getInt(4),
                                    rs.getString(5),
                                    rs.getInt(6));
                            productsList.add(p);
                            if (!init) {
                                moreItems++;
                            }
                        }
                        if (moreItems > 0) {
                            isloading = false;
                        } else {
                            if (!init) {
                                // Comunitacion UIThread (UI Interface)
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showMissage("No more data");
                                    }
                                });
                            }
                        }
                        // Comunitacion UIThread (UI Interface)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Notify adapter
                                adp.notifyDataSetChanged();
                                // Text Info List
                                tvInfoList.setText("Displaying " + productsList.size() + " of " + countProducts + " products");
                            }
                        });
                    } else {
                        // Comunitacion UIThread (UI Interface)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // ResultSet is null
                                showMissage("Error en load Data");
                            }
                        });
                    }

                    if (!init) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Remove panel footer listview
                                lvProducts.removeFooterView(ftListView);
                            }
                        });

                    }

                    // init is false for more load products according limit
                    init = false;

                    System.out.println("new offset " + productsList.size());
                    System.out.println("list of products " + productsList.size());

                } catch (SQLException e) {
                    //Toast.makeText(ListViewProductsJDBC.this, "Error en load Data", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } finally {
                    db.closeResultSet(rs);
                    db.closeConnection(conn);
                }
            }
    };


    /****************************************************/
    ///// 3.- Implementation AsyncTask load data products
    /***************************************************/

    private class AsyncTaskLoadData extends AsyncTask<Void, Void, ResultSet>  {

        @Override
        protected void onPreExecute() {
            if (!init) {
                // Add panel footer list view
                lvProducts.addFooterView(ftListView, null, false);
            }
        }

        @Override
        protected ResultSet doInBackground(Void... voids) {
            if (!init){
                //Thread sleep 3s for view progress bar listview footer
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ResultSet rs = null;
            db = new GestorJDBC();
            conn = db.connetion();
            rs = db.loadData(conn, LIMIT, productsList.size());
            if (init) {
                countProducts = db.countData(conn);
            }
            return rs;
        }

        @Override
        protected void onPostExecute(ResultSet rs) {
            Integer moreItems = 0;

            try {
                if (rs != null) {
                    // ResultSet contains data
                    while (rs.next()) {
                        Product p = new Product(rs.getInt(1),
                                rs.getString(2),
                                rs.getFloat(3),
                                rs.getInt(4),
                                rs.getString(5),
                                rs.getInt(6));
                        productsList.add(p);
                        if (!init) {
                            moreItems++;
                        }
                    }
                    if (moreItems > 0) {
                        isloading = false;
                    } else {
                        if (!init) {
                            showMissage("No more data");
                        }
                    }
                    // Notify adapter
                    adp.notifyDataSetChanged();
                    // Text Info List
                    tvInfoList.setText("Displaying " + productsList.size() + " of " + countProducts + " products");
                } else {
                    // ResultSet is null
                    showMissage("Error en load Data");
                }

                if (!init) {
                    // Remove panel footer listview
                    lvProducts.removeFooterView(ftListView);
                }

                // init is false for more load products according limit
                init = false;

                System.out.println("new offset " + productsList.size());
                System.out.println("list of products " + productsList.size());

            } catch (SQLException e) {
                //Toast.makeText(ListViewProductsJDBC.this, "Error en load Data", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } finally {
                db.closeResultSet(rs);
                db.closeConnection(conn);
            }
        }
    };


};





