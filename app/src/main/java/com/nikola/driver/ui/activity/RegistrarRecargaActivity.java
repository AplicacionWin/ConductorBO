package com.nikola.driver.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.utils.AndyUtils;
import com.nikola.driver.utils.customText.CustomRegularEditView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrarRecargaActivity extends AppCompatActivity {

    @BindView(R.id.montoRecarga)
    CustomRegularEditView montoRecarga;
    @BindView(R.id.numTransferenciaRecarga)
    CustomRegularEditView numTransferenciaRecarga;
    @BindView(R.id.fechaTransferencia)
    CustomRegularEditView fechaTransferencia;

    @BindView(R.id.recargar)
    Button recargar;

    @BindView(R.id.registrar_back)
    ImageButton back;

    APIInterface apiInterface;
    PrefUtils prefUtils;

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_recarga);
        ButterKnife.bind(this);
        prefUtils = PrefUtils.getInstance(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        recargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validar()){
                    recargarSaldo();
                }
                else
                    UiUtils.showLongToast(RegistrarRecargaActivity.this, getString(R.string.fillFields));
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        
        showDate(year, month+1, day);

        fechaTransferencia.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    AndyUtils.hideKeyBoard(getApplicationContext());
                    setDate();
                }
                else
                    AndyUtils.hideKeyBoard(getApplicationContext());
            }
        });
    }

    public boolean validar()
    {
        String fecha = fechaTransferencia.getText().toString().trim();
        String monto = montoRecarga.getText().toString().trim();
        String idTransfer = numTransferenciaRecarga.getText().toString().trim();

        if(fecha.equalsIgnoreCase("")|| monto.equalsIgnoreCase("")|| idTransfer.equalsIgnoreCase("") )
            return false;
        else
            return true;
    }

    public void recargarSaldo() {
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.recargarSaldo(
                prefUtils.getIntValue(PrefKeys.ID, 0),
                montoRecarga.getText().toString(),
                numTransferenciaRecarga.getText().toString(),
                fechaTransferencia.getText().toString()
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
                        String message = recargaResponse.optString(APIConstants.Params.MESSAGE);
                        UiUtils.showShortToast(RegistrarRecargaActivity.this, message);
                    } else {
                        UiUtils.showShortToast(RegistrarRecargaActivity.this, recargaResponse.optString(APIConstants.Params.ERROR));
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

    @SuppressWarnings("deprecation")
    public void setDate() {
        showDialog(999);
        AndyUtils.hideKeyBoard(getApplicationContext());
        Toast.makeText(getApplicationContext(), getText(R.string.selectDate),
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        fechaTransferencia.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }
}