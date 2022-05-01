package com.nikola.driver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.currentPassword)
    EditText currentPassword;
    @BindView(R.id.newPassword)
    EditText newPassword;
    @BindView(R.id.newPasswordConfirm)
    EditText newPasswordConfirm;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        toolbar.setTitle(getString(R.string.change_password_text));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @OnClick(R.id.changePasswordButton)
    protected void changePasswordClicked() {
        if (validateFields()) {
            changePassword(currentPassword.getText().toString()
                    , newPassword.getText().toString()
                    , newPasswordConfirm.getText().toString());
        }
    }

    private boolean validateFields() {
        String curPass = currentPassword.getText().toString();
        String newPass = newPassword.getText().toString();
        String newPassConfirm = newPasswordConfirm.getText().toString();
        if (curPass.trim().length() == 0
                || newPass.trim().length() == 0
                || newPassConfirm.trim().length() == 0) {
            UiUtils.showShortToast(this, getString(R.string.password_cant_be_empty));
            return false;
        }
        if (curPass.trim().length() < 6
                || newPass.trim().length() < 6
                || newPassConfirm.trim().length() < 6) {
            UiUtils.showShortToast(this, getString(R.string.minimum_six_characters));
            return false;
        }
        if (!newPass.equals(newPassConfirm)) {
            UiUtils.showShortToast(this, getString(R.string.passwords_dont_match));
            return false;
        }
        return true;
    }

    private void changePassword(String oldPass, String newPass, String newPassConfirm) {
        UiUtils.showLoadingDialog(this);
        PrefUtils prefUtils = PrefUtils.getInstance(this);
        Call<String> call = apiInterface.changePassword(prefUtils.getIntValue(PrefKeys.ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , oldPass
                , newPass
                , newPassConfirm);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject changePasswordResponse = null;
                try {
                    changePasswordResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (changePasswordResponse != null) {
                    if (changePasswordResponse.optString(APIConstants.Constants.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.showShortToast(ChangePasswordActivity.this, changePasswordResponse.optString(APIConstants.Params.MESSAGE));
                        startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                        finish();
                    } else {
                        UiUtils.showShortToast(ChangePasswordActivity.this, changePasswordResponse.optString(APIConstants.Params.ERROR));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
