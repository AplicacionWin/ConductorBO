package com.nikola.driver.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.utils.customText.CustomRegularEditView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaldoActivity extends AppCompatActivity {

    @BindView(R.id.saldo_back)
    ImageButton back;

    @BindView(R.id.recargas)
    CustomRegularEditView recargas;

    @BindView(R.id.descuentos)
    CustomRegularEditView descuentos;

    APIInterface apiInterface;
    PrefUtils prefUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo);
        ButterKnife.bind(this);

        prefUtils = PrefUtils.getInstance(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        consultarSaldo();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void consultarSaldo() {
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.consultarSaldo(
                prefUtils.getIntValue(PrefKeys.ID, 0)
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject recargaResponse = null;
                try {
                    recargaResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (recargaResponse != null) {
                    if (recargaResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        JSONObject data = recargaResponse.optJSONObject(APIConstants.Params.DATA);

                        recargas.setText(getText(R.string.recargas)+" " + data.optString(APIConstants.Params.RECARGAS));
                        descuentos.setText(getText(R.string.descuentos)+" " + data.optString(APIConstants.Params.DESCUENTOS));

                    } else {
                        UiUtils.showShortToast(SaldoActivity.this, recargaResponse.optString(APIConstants.Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }
}