package com.example.robertogarcia.model;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.robertogarcia.listviewdinamic.R;

import java.util.List;

/**
 * Created by Roberto on 08/07/2017.
 */

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private List<Product> productsList;

    public ProductAdapter(Context context, List<Product> productsList) {
        super();
        this.context = context;
        this.productsList = productsList;
    }

    // Add list to adapter products
    public void addListItemAdapter(List<Product> list){
        productsList.addAll(list);
    }

    // Return number items from the list
    @Override
    public int getCount() {
        return productsList.size();
    }

    // Return item from the list
    @Override
    public Object getItem(int position) {
        return productsList.get(position);
    }

    // Return position from item the list
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Return the inflated view with the data
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.template_listview,  null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = (Product) getItem(position);
        holder.title.setText(product.getName());
        holder.description.setText(product.getDescription());
        holder.price.setText(String.valueOf(product.getPrice()));
        holder.amount.setText(String.valueOf(product.getAmount()));

        return convertView;

    }

    // Pattern ViewHolder for performence listview
    private class ViewHolder {
        private TextView title;
        private TextView description;
        private TextView price;
        private TextView amount;

        public ViewHolder(View view){
            title = (TextView) view.findViewById(R.id.tvTitle);
            description = (TextView) view.findViewById(R.id.tvDescription);
            price = (TextView) view.findViewById(R.id.tvPrice);
            amount = (TextView) view.findViewById(R.id.tvAmount);

        }

    }

}






