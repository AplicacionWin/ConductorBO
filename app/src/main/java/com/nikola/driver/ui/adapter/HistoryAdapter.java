package com.nikola.driver.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.nikola.driver.R;
import com.nikola.driver.network.model.History;
import com.nikola.driver.ui.activity.HistoryActivity;
import com.nikola.driver.ui.fragment.ScheduleDetailBottomSheet;
import com.nikola.driver.utils.customText.CustomBoldRegularTextView;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 1/20/2017.
 */

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HistoryActivity mContext;
    private List<History> itemshistroyList;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat inputformat;
    private TripsInterface tripsInterface;

    public HistoryAdapter(HistoryActivity context, List<History> itemshistroyList, TripsInterface tripsInterface) {
        mContext = context;
        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm a");
        inputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.tripsInterface = tripsInterface;
        this.itemshistroyList = itemshistroyList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_item, null);
        HistoryViewHolder holder = new HistoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HistoryViewHolder) {
            HistoryViewHolder historyViewHolder = (HistoryViewHolder) viewHolder;
            History history_itme = itemshistroyList.get(position);
            if (history_itme != null) {
                historyViewHolder.type.setText(history_itme.getHistory_type() + " #" + history_itme.getRequestUniqueId());
                if(!"".equalsIgnoreCase(history_itme.getCancelReason()) && null!=history_itme.getCancelReason())
                {
                    historyViewHolder.cancelReason.setVisibility(View.VISIBLE);
                    historyViewHolder.cancelReason.setText(history_itme.getCancelReason());
                }
                else
                    historyViewHolder.cancelReason.setVisibility(View.GONE);

                historyViewHolder.sourceAddress.setText(history_itme.getHistory_Sadd());
                if (!history_itme.getHistory_Dadd().equals("")) {
                    historyViewHolder.destAddress.setText(history_itme.getHistory_Dadd());
                } else {
                    historyViewHolder.destAddress.setText(mContext.getResources().getString(R.string.not_available));
                }
                historyViewHolder.amount.setText(String.format("%s %s", history_itme.getCurrency_unit(), history_itme.getProviderCommission()));
                historyViewHolder.trip_date.setText(history_itme.getRequestCreatedTime());

                Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
                historyViewHolder.itemView.startAnimation(animation);
                Glide.with(mContext).load(history_itme.getMap_img()).into(historyViewHolder.providerImage);
                Glide.with(mContext).load(R.raw.trip_ongoing).into(historyViewHolder.status);


                switch (history_itme.getRequest_ico_status()) {
                    case 1:
                        Glide.with(mContext).load(R.drawable.bicycle).into(historyViewHolder.status);
                        historyViewHolder.yourEarnings.setVisibility(View.GONE);
                        break;
                    case 2:
                        Glide.with(mContext).load(R.drawable.schedule).into(historyViewHolder.status);
                        historyViewHolder.yourEarnings.setVisibility(View.GONE);
                        break;
                    case 3:
                        Glide.with(mContext).load(R.drawable.flag).into(historyViewHolder.status);
                        break;
                    case 4:
                        historyViewHolder.status.setVisibility(View.GONE);
                        historyViewHolder.yourEarnings.setVisibility(View.GONE);
                        historyViewHolder.statusText.setVisibility(View.GONE);
                        break;
                }
                historyViewHolder.statusText.setText(history_itme.getRequest_icon_status_text());

                if(history_itme.getRequest_ico_status() == 1) {
                    historyViewHolder.amount.setText(history_itme.getRequest_icon_status_text());
                }
                else if(history_itme.getRequest_ico_status() == 4) {
                    historyViewHolder.statusText.setText("");
                    historyViewHolder.amount.setText(history_itme.getRequest_icon_status_text());
                }
                historyViewHolder.itemView.setOnClickListener(view -> {
                    ScheduleDetailBottomSheet scheduleDetailBottomSheet = new ScheduleDetailBottomSheet();
                    scheduleDetailBottomSheet.setRequestId(history_itme.getHistory_id());
                    scheduleDetailBottomSheet.show(mContext.getSupportFragmentManager(), scheduleDetailBottomSheet.getTag());
                });
            }
        }
    }


    @Override
    public int getItemCount() {
        return itemshistroyList.size();
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tripTime)
        TextView trip_date;
        @BindView(R.id.sourceAddress)
        TextView sourceAddress;
        @BindView(R.id.destAddress)
        TextView destAddress;
        @BindView(R.id.type)
        TextView type;
        @BindView(R.id.cancelReason)
        TextView cancelReason;
        @BindView(R.id.amount)
        TextView amount;
        @BindView(R.id.providerImage)
        CircleImageView providerImage;
        @BindView(R.id.status)
        CircleImageView status;
        @BindView(R.id.statusText)
        TextView statusText;
        @BindView(R.id.yourEarnings)
        TextView yourEarnings;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void showLoading() {
        if (tripsInterface != null)
            tripsInterface.onLoadMoreTrips(itemshistroyList.size());
    }

    public interface TripsInterface {
        void onLoadMoreTrips(int skip);
    }
}


