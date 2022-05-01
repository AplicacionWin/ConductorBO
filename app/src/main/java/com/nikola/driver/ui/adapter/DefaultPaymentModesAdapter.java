package com.nikola.driver.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.model.PaymentMode;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefaultPaymentModesAdapter extends RecyclerView.Adapter<DefaultPaymentModesAdapter.PaymentModeViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<PaymentMode> paymentModes;
    private APIInterface apiInterface;
    private PrefUtils prefUtils;


    public DefaultPaymentModesAdapter(Context context, ArrayList<PaymentMode> paymentModes) {
        this.paymentModes = paymentModes;
        this.context = context;
        this.prefUtils = PrefUtils.getInstance(context);
        this.apiInterface = APIClient.getClient().create(APIInterface.class);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public void onBindViewHolder(@NonNull PaymentModeViewHolder viewHolder, int position) {
        PaymentMode paymentMode = paymentModes.get(position);
        viewHolder.paymentModeName.setText(paymentMode.getName());
        Glide.with(context)
                .load(paymentMode.getImage())
                .error(R.drawable.ic_creditcard)
                .into(viewHolder.paymentModePhoto);
        viewHolder.paymentModeSelected.setImageResource(
                paymentMode.isDefault() ?
                        R.drawable.toggle_on
                        : R.drawable.toggle_off);
        viewHolder.root.setOnClickListener(v -> setDefaultPaymentMode(paymentMode));
    }

    @Override
    public int getItemCount() {
        return paymentModes.size();
    }

    @NonNull
    @Override
    public PaymentModeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.item_payment_mode, viewGroup, false);
        return new PaymentModeViewHolder(view);
    }

    private void setDefaultPaymentMode(PaymentMode paymentMode) {
        UiUtils.showLoadingDialog(context);
        Call<String> call = apiInterface.changeDefaultPaymentMode(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                paymentMode.getName()
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject resp = null;
                try {
                    resp = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (resp != null) {
                    if (resp.optBoolean(APIConstants.Constants.SUCCESS)) {
                        for (PaymentMode mode : paymentModes) {
                            mode.setDefault(false);
                        }
                        paymentMode.setDefault(true);
                        notifyDataSetChanged();
                        UiUtils.showShortToast(context, resp.optString(APIConstants.Params.MESSAGE));
                    } else {
                        UiUtils.showShortToast(context, resp.optString(APIConstants.Params.ERROR_MESSAGE));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(context)) {
                    UiUtils.showShortToast(context, context.getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    public String getChosenPaymentMode() {
        for (PaymentMode mode : paymentModes) {
            if (mode.isDefault())
                return mode.getName();
        }
        return null;
    }

    class PaymentModeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.paymentModeName)
        TextView paymentModeName;
        @BindView(R.id.paymentModeSelected)
        ImageView paymentModeSelected;
        @BindView(R.id.paymentModePhoto)
        ImageView paymentModePhoto;
        @BindView(R.id.paymentModeRoot)
        ViewGroup root;

        PaymentModeViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
