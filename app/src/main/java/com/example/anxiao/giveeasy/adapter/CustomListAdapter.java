package com.example.anxiao.giveeasy.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anxiao.giveeasy.R;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.anxiao.giveeasy.app.AppController;
import com.example.anxiao.giveeasy.model.charity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anxiao on 12/29/15.
 */
public class CustomListAdapter extends BaseAdapter implements Filterable {

    public Context context;
    private LayoutInflater inflater;
    private List<charity> charityItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    //sort
    private List<charity> mOrigionalValues;
    private List<charity> mFilterValues;
    private Filter mFilter;

    public CustomListAdapter(Context context1, List<charity> charityItems) {
        this.context = context1;
        this.charityItems = charityItems;
        this.inflater = LayoutInflater.from(context);


        mOrigionalValues = new ArrayList<charity>(charityItems);
        mFilterValues = new ArrayList<charity>(charityItems);
        mFilterValues = charityItems;
    }

    @Override
    public int getCount() {
//        return charityItems.size();
        return mFilterValues.size();
    }

    @Override
    public Object getItem(int location) {
//        return charityItems.get(location);
        return mFilterValues.get(location);
    }

    @Override
    public long getItemId(int position) {
//        return position;
        return mFilterValues.get(position).getCharityID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.list_item, parent, false);

            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.charityname);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.charitydes);
            holder.txtCurrency = (TextView) convertView.findViewById(R.id.currency);
            holder.logo = (NetworkImageView) convertView.findViewById(R.id.charitylogoimg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
//                NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.charitylogoimg);
//                TextView name = (TextView) convertView.findViewById(R.id.charityname);
//                TextView description = (TextView) convertView.findViewById(R.id.charitydes);
//                TextView currencylabel = (TextView) convertView.findViewById(R.id.currency);

//            holder.logo = (NetworkImageView) convertView.findViewById(R.id.charitylogoimg);
//            holder.txtTitle = (TextView) convertView.findViewById(R.id.charityname);
//            holder.txtDesc = (TextView) convertView.findViewById(R.id.charitydes);
//            holder.txtCurrency = (TextView) convertView.findViewById(R.id.currency);


        // getting charity data for the row
        charity m = charityItems.get(position);

        // logo image
        holder.logo.setImageUrl(m.getThumbnailUrl(), imageLoader);
        // name
        holder.txtTitle.setText(m.getTitle());
        // description
        holder.txtDesc.setText(m.getDesc());
        //currency
        holder.txtCurrency.setText(m.getlCurrency());


        return convertView;
    }

    public void add(charity object) {
        mOrigionalValues.add(object);
        this.notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new CustomFilter();
        }
        return mFilter;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    //for search
    //Class for return View of Row in ListView
    private class ViewHolder {
        TextView txtTitle;
        TextView txtDesc;
        TextView txtCurrency;
        NetworkImageView logo;
    }

    // Class for Filter charities ListView
    private class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0) {
                ArrayList<charity> list = new ArrayList<>(mOrigionalValues);
                results.values = list;
                results.count = list.size();
            } else {
                ArrayList<charity> newValues = new ArrayList<>();
                for(int i = 0; i < mOrigionalValues.size(); i++) {
                    charity item = mOrigionalValues.get(i);
                    if(item.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        newValues.add(item);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            mFilterValues = (List<charity>) results.values;
            Log.d("CustomArrayAdapter", String.valueOf(results.values));
            Log.d("CustomArrayAdapter", String.valueOf(results.count));
            notifyDataSetChanged();
        }
    }




}





