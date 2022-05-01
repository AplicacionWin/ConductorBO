package com.nikola.driver.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.utils.Const;
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

public class PanicContactActivity extends AppCompatActivity {

    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.panic_back)
    ImageButton panic_back;
    @BindView(R.id.savePanicContact)
    Button savePanicContact;
    @BindView(R.id.panicContactNumber)
    CustomRegularEditView panicContactNumber;
    boolean enable = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_contact_activity);
        ButterKnife.bind(this);
        prefUtils = PrefUtils.getInstance(PanicContactActivity.this);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        panicContactNumber.setEnabled(enable);
        panicContactNumber.setBackground(getDrawable(R.drawable.drawable_edittext_background_dark));
        savePanicContact.setText(getText(R.string.btn_edit));
        panic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        savePanicContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(enable){
                    if(panicContactNumber.getText().toString().length()==10)
                    {
                        sendPanicMessage();
                        enable= false;
                        panicContactNumber.setEnabled(false);
                        savePanicContact.setText(R.string.btn_edit);
                        panicContactNumber.setBackground(getDrawable(R.drawable.drawable_edittext_background_dark));
                    }
                    else
                    {
                        UiUtils.showShortToast(PanicContactActivity.this, getString(R.string.invalid));
                    }
                }
                else
                {
                    panicContactNumber.setEnabled(true);
                    savePanicContact.setText(R.string.save_contact);
                    enable=true;
                    panicContactNumber.setBackground(getDrawable(R.drawable.drawable_edittext_background));
                }
            }
        });
        getPanicMessage();
    }

    protected void sendPanicMessage() {
        UiUtils.showLoadingDialog(PanicContactActivity.this);

        Call<String> call = apiInterface.setPanicContact(prefUtils.getIntValue(PrefKeys.ID, 0)
                ,panicContactNumber.getText().toString()
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject savePanicContact = null;
                try {
                    savePanicContact = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (savePanicContact != null) {
                    if (savePanicContact.optString(Const.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        UiUtils.showShortToast(PanicContactActivity.this, savePanicContact.optString(APIConstants.Params.MESSAGE));
                    } else {
                        UiUtils.showShortToast(PanicContactActivity.this, savePanicContact.optString(APIConstants.Params.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(PanicContactActivity.this)) {
                    UiUtils.showShortToast(PanicContactActivity.this, getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    protected void getPanicMessage() {
        UiUtils.showLoadingDialog(PanicContactActivity.this);

        Call<String> call = apiInterface.getPanicContact(prefUtils.getIntValue(PrefKeys.ID, 0)
        );
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject getPanicContact = null;
                try {
                    getPanicContact = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (savePanicContact != null) {
                    if (getPanicContact.optString(Const.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        panicContactNumber.setText(getPanicContact.optString(APIConstants.Constants.DATA));
                    } else {
                        UiUtils.showShortToast(PanicContactActivity.this, getPanicContact.optString(APIConstants.Constants.ERROR));
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(PanicContactActivity.this)) {
                    UiUtils.showShortToast(PanicContactActivity.this, getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
