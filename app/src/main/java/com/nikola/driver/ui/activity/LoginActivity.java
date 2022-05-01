package com.nikola.driver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.nikola.driver.R;
import com.nikola.driver.fcm.FCMIntentService;
import com.nikola.driver.network.model.SocialMediaProfile;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.fragment.ForgotpassFragment;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.PreferenceHelper;
import com.nikola.driver.utils.customText.CustomRegularEditView;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.AppUtils;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefHelper;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONObject;

import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 1/4/2017.
 */

public class LoginActivity extends AppCompatActivity {
    public static String currentfragment = "";
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.email)
    CustomRegularEditView email;
    @BindView(R.id.password)
    CustomRegularEditView password;
    @BindView(R.id.forgotPassword)
    CustomRegularTextView forgotPassword;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.socialLogin)
    CustomRegularTextView socialLogin;
    @BindView(R.id.signUp)
    CustomRegularTextView signUp;
    @BindView(R.id.inputPassword)
    TextInputLayout inputPassword;
    private String loginType = Const.MANUAL;
    private String sFirstName, sLastName, sEmailId, sPassword, sUserName, sSocial_unique_id, pictureUrl;
    private CallbackManager callbackManager;
    private String sPictureUrl;
    private String sLoginUserId, sLoginPassword;
    private String filePath = "";
    private SocialMediaProfile mediaProfile;
    private int mFragmentId = 0;
    private String mFragmentTag = null, newToken;
    private boolean isclicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(this);

            inputPassword.setHintAnimationEnabled(false);
            inputPassword.setHint("");
            password.setHint(getString(R.string.password));

            if(prefUtils.getStringValue(PrefKeys.LANGUAGES,"").equalsIgnoreCase("es"))
            {
                Spannable wordtoSpan = new SpannableString(getString(R.string.do_not_have_an_account_signup));
                wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), 22, 32, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                signUp.setText(wordtoSpan);
            }
            else if(prefUtils.getStringValue(PrefKeys.LANGUAGES,"").equalsIgnoreCase("en"))
            {
                Spannable wordtoSpan = new SpannableString(getString(R.string.do_not_have_an_account_signup));
                wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), 24, 30, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                signUp.setText(wordtoSpan);
            }
        try {
            startService(new Intent(this, FCMIntentService.class));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        /*
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
                Log.e("newToken",newToken);

            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        currentfragment = "";
    }

    private boolean validateFields() {
        if (email.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(this, getString(R.string.email_cant_be_empty));
            email.requestFocus();
            return false;
        }
        if (password.getText().toString().trim().length() == 0) {
            UiUtils.showShortToast(this, getString(R.string.password_cant_be_empty));
            password.requestFocus();
            return false;
        }
        /*
        if (!AppUtils.isValidEmail(email.getText().toString())) {
            UiUtils.showShortToast(this, getString(R.string.enter_valid_email));
            return false;
        }*/
        if (password.getText().toString().length() < 6) {
            UiUtils.showShortToast(this, getString(R.string.minimum_six_characters));
            return false;
        }
        return true;
    }


    public void startActivityForResult(Intent intent, int requestCode, String fragmentTag) {
        mFragmentTag = fragmentTag;
        mFragmentId = 0;
        super.startActivityForResult(intent, requestCode);
    }


    private void UserLogin(String logintype) {
        UiUtils.showLoadingDialog(this);
        Call<String> call = apiInterface.loginUser(email.getText().toString()
                , password.getText().toString()
                , logintype
                , Constants.ANDROID
                , prefUtils.getStringValue(PrefKeys.FCM_TOKEN, "")
                , TimeZone.getDefault().getID());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject loginResponse = null;
                try {
                    loginResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (loginResponse != null) {
                    if (loginResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        JSONObject data = loginResponse.optJSONObject(Params.DATA);
                        loginUserInDevice(data, logintype);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else {
                        UiUtils.showShortToast(LoginActivity.this, loginResponse.optString(Params.ERROR));
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

    private void loginUserInDevice(JSONObject data, String manualLogin) {
        if (data.optInt("is_verified") == 1) {
            PrefHelper.setUserLoggedIn(this, data.optInt(Params.PROVIDER_ID)
                    , data.optString(Params.NAME)
                    , data.optString(Params.FIRSTNAME)
                    , data.optString(Params.LAST_NAME)
                    , data.optString(Params.EMAIL)
                    , data.optString(Params.PICTURE)
                    , data.optString(Params.PHONE)
                    , data.optString(Params.DESCRIPTION)
                    , data.optString(Params.TOKEN)
                    , manualLogin
                    , data.optInt(Params.PROVIDER_STATUS)
                    , data.optString(Params.TIME_ZONE)
                    , data.optString(Params.COUNTRY)
                    , data.optInt(Params.CURRENCEY)
                    , data.optString(Params.PLATE_NUMBER)
                    , data.optString(Params.COLOR)
                    , data.optString(Params.GENDER)
            );
            new PreferenceHelper(this).putUserId(String.valueOf(data.optInt(Params.PROVIDER_ID)));
            Intent toHome = new Intent(this, MainActivity.class);
            toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toHome);
            this.finish();
        } else {
            Intent toHome = new Intent(this, LoginActivity.class);
            toHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(toHome);
            this.finish();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Activity Res", "" + requestCode);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = null;
        if (mFragmentId > 0) {
            fragment = getSupportFragmentManager().findFragmentById(
                    mFragmentId);
        } else if (mFragmentTag != null
                && !mFragmentTag.equalsIgnoreCase("")) {
            fragment = getSupportFragmentManager().findFragmentByTag(
                    mFragmentTag);
        }
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
      super.onBackPressed();
    }

    @OnClick({R.id.signUp, R.id.login, R.id.forgotPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login:
                if (validateFields()) {
                    UserLogin(Constants.MANUAL_LOGIN);
                }
                break;
            case R.id.forgotPassword:
                Intent forgotintent = new Intent(getApplicationContext(), ForgotpassFragment.class);
                startActivity(forgotintent);
                break;
            case R.id.signUp:
                Intent signUp = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(signUp);
                break;
        }
    }
}
