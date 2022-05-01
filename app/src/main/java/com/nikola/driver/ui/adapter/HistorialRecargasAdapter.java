package com.nikola.driver.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.model.HistorialRecarga;
import com.nikola.driver.network.model.History;
import com.nikola.driver.ui.activity.HistorialRecargaActivity;
import com.nikola.driver.ui.activity.HistoryActivity;
import com.nikola.driver.ui.fragment.ScheduleDetailBottomSheet;
import com.nikola.driver.utils.customText.CustomBoldRegularTextView;
import com.nikola.driver.utils.customText.CustomLightTextView;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 1/20/2017.
 */

public class HistorialRecargasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HistorialRecargaActivity mContext;
    private List<HistorialRecarga> itemsHistorialRecargaList;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat inputformat;
    private TripsInterface tripsInterface;

    public HistorialRecargasAdapter(HistorialRecargaActivity context, List<HistorialRecarga> itemsHistorialRecargaList, TripsInterface tripsInterface) {
        mContext = context;
        inputformat = new SimpleDateFormat("yyyy-MM-dd");
        this.tripsInterface = tripsInterface;
        this.itemsHistorialRecargaList = itemsHistorialRecargaList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.historial_recarga_item, null);
        HistoryViewHolder holder = new HistoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof HistoryViewHolder) {
            HistoryViewHolder historyViewHolder = (HistoryViewHolder) viewHolder;
            HistorialRecarga history_itme = itemsHistorialRecargaList.get(position);
            if (history_itme != null) {
                if(!"".equalsIgnoreCase(history_itme.getFechaAprobacion()) && null!=history_itme.getFechaAprobacion())
                {
                    historyViewHolder.historial_fechaAprobacion.setVisibility(View.VISIBLE);
                    historyViewHolder.historial_fechaAprobacion.setText("Fecha aprobación: "+history_itme.getFechaAprobacion().substring(0,history_itme.getFechaAprobacion().indexOf("T")));
                }
                else
                    historyViewHolder.historial_fechaAprobacion.setVisibility(View.GONE);
                historyViewHolder.historial_fecha.setText("Fecha recarga:" +history_itme.getFecha());
                historyViewHolder.historial_estado.setText("Estado: "+history_itme.getEstado());
                historyViewHolder.historial_monto.setText("Monto recarga: "+history_itme.getMonto());
                historyViewHolder.historial_numTransferencia.setText("Num. Transferencia:" + history_itme.getNumero());
            }
        }
    }


    @Override
    public int getItemCount() {
        return itemsHistorialRecargaList.size();
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.historial_estado)
        CustomBoldRegularTextView historial_estado;

        @BindView(R.id.historial_fecha)
        CustomBoldRegularTextView historial_fecha;

        @BindView(R.id.historial_monto)
        CustomBoldRegularTextView historial_monto;

        @BindView(R.id.historial_fechaAprobacion)
        CustomBoldRegularTextView historial_fechaAprobacion;

        @BindView(R.id.historial_numTransferencia)
        CustomBoldRegularTextView historial_numTransferencia;


        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void showLoading() {
        if (tripsInterface != null)
            tripsInterface.onLoadMoreTrips(itemsHistorialRecargaList.size());
    }

    public interface TripsInterface {
        void onLoadMoreTrips(int skip);
    }
}


