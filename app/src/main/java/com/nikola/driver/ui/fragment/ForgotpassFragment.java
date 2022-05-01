package com.nikola.driver.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.nikola.driver.R;
import com.nikola.driver.network.APIManager.AsyncTaskCompleteListener;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.activity.GetStartedActivity;
import com.nikola.driver.ui.activity.LoginActivity;
import com.nikola.driver.utils.Commonutils;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.customText.CustomRegularEditView;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.AppUtils;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefHelper;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 1/5/2017.
 */

public class ForgotpassFragment extends AppCompatActivity {

    Unbinder unbinder;
    @BindView(R.id.btn_forgot_cancel)
    ImageButton btnForgotCancel;
    @BindView(R.id.et_email_forgot)
    CustomRegularEditView etEmailForgot;
    @BindView(R.id.input_layout_email_forgot)
    TextInputLayout inputLayoutEmailForgot;
    @BindView(R.id.forgot_pass_btn)
    CustomRegularTextView forgotPassBtn;
    APIInterface apiInterface;
    PrefUtils prefutils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefutils = PrefUtils.getInstance(this);
    }

    private void RequestPassword() {
        UiUtils.showLoadingDialog(ForgotpassFragment.this);
        Call<String> call = apiInterface.forgotPassword(etEmailForgot.getText().toString());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject forgotPasswordResponse = null;
                try {
                    forgotPasswordResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (forgotPasswordResponse != null) {
                    if (forgotPasswordResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        UiUtils.showShortToast(ForgotpassFragment.this, forgotPasswordResponse.optString(APIConstants.Params.MESSAGE));
                        startActivity(new Intent(ForgotpassFragment.this, GetStartedActivity.class));
                        ForgotpassFragment.this.finish();
                    } else {
                        UiUtils.hideLoadingDialog();

                        UiUtils.showShortToast(ForgotpassFragment.this, forgotPasswordResponse.optString(APIConstants.Params.ERROR));

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

    @OnClick({R.id.btn_forgot_cancel, R.id.forgot_pass_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_forgot_cancel:
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                break;
            case R.id.forgot_pass_btn:
                if (validateField()){
                    RequestPassword();
                }
                break;
        }
    }

    private boolean validateField() {
        if (etEmailForgot.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(getApplicationContext(), getString(R.string.email_cant_be_empty));
            inputLayoutEmailForgot.setError(getResources().getString(R.string.txt_email_error));
            etEmailForgot.requestFocus();
            return false;
        }
        if (!AppUtils.isValidEmail(etEmailForgot.getText().toString())) {
            UiUtils.showShortToast(getApplicationContext(), getString(R.string.enter_valid_email));
            return false;
        }
        return true;
    }
}
