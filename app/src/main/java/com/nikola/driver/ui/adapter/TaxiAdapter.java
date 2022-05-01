package com.nikola.driver.ui.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.model.TaxiTypes;

import java.util.List;

/**
 * Created by user on 10/5/2016.
 */
public class TaxiAdapter extends RecyclerView.Adapter<TaxiAdapter.typesViewHolder> {

    private Activity mContext;
    private List<TaxiTypes> taxiTypesList;
    private int pos=-1;

    public TaxiAdapter(Activity context, List<TaxiTypes> taxiTypesList) {
        mContext = context;
        this.taxiTypesList = taxiTypesList;

    }

    @Override
    public TaxiAdapter.typesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vehicle_type_item, null);
        typesViewHolder holder = new typesViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(TaxiAdapter.typesViewHolder holder, int position) {
        TaxiTypes list_types = taxiTypesList.get(position);
        holder.tv_type_name.setText(list_types.getTaxitype());

        Glide.with(mContext).load(list_types.getTaxiimage()).error(R.mipmap.frontal_taxi_cab).into(holder.type_picutre);

        if (pos == position) {
            holder.select_view.setVisibility(View.VISIBLE);
        } else {
            holder.select_view.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return taxiTypesList.size();
    }

    public class typesViewHolder extends RecyclerView.ViewHolder {
        private ImageView type_picutre;
        private TextView tv_type_name;
        private View select_view;

        public typesViewHolder(View itemView) {
            super(itemView);
            type_picutre = (ImageView) itemView.findViewById(R.id.type_picutre);
            select_view = (View) itemView.findViewById(R.id.select_view);
            tv_type_name = (TextView) itemView.findViewById(R.id.tv_type_name);
        }
    }

    public void ItemClicked(int position) {
        pos = position;
        notifyDataSetChanged();
    }

}
