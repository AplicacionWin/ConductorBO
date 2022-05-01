package com.nikola.driver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.NotificationSendDriverPosition;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONObject;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusAvailabilityActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.statusChangeText)
    CustomRegularTextView statusChangeText;
    @BindView(R.id.availableImage)
    ImageView availableImage;
    @BindView(R.id.activity_status_availability)
    LinearLayout activityStatusAvailability;
    private String switchStatus = "";
    APIInterface apiInterface;
    PrefUtils prefUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_status_availability);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(this);
        checkAvailabilityStatus();

        statusChangeText.setOnClickListener(view -> {
            updateAvailabilityStatus(statusChangeText.getText().toString().equalsIgnoreCase("Go Online") ? 1 : 0);
        });
        if(!prefUtils.getBoolanValue(PrefKeys.IS_LOGGED_IN,false))
        {
            Intent restartActivity = new Intent(StatusAvailabilityActivity.this, SplashActivity.class);
            restartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(restartActivity);
            StatusAvailabilityActivity.this.finish();
        }
        statusChangeText.setOnClickListener(view -> {
            updateAvailabilityStatus(statusChangeText.getText().toString().equalsIgnoreCase("Go Online") ? 1 : 0);
        });
        back.setOnClickListener(view ->{
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
            StatusAvailabilityActivity.this.finish();
        });
    }


    @Override
    public void onBackPressed(){
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        StatusAvailabilityActivity.this.finish();
    }

    private void updateAvailabilityStatus(int status) {
        UiUtils.showLoadingDialog(StatusAvailabilityActivity.this);
        Call<String> call = apiInterface.updateAvailability(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                status);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (jsonObject.optString(Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        JSONObject data = jsonObject.optJSONObject(Params.DATA);
                        boolean isAvailable = data.optInt(Params.IS_AVAILABLE) == 1;
                        if(isAvailable) {
                            Const.SEND_POSITION_DRIVER = true;
                            Objects.requireNonNull(getApplicationContext()).startService(new Intent(getApplicationContext(), NotificationSendDriverPosition.class));
                        }
                        else {
                            Const.SEND_POSITION_DRIVER = false;
                            Objects.requireNonNull(getApplicationContext()).stopService(new Intent(getApplicationContext(), NotificationSendDriverPosition.class));

                        }
                        prefUtils.setValue(PrefKeys.AVAILABLE_STATUS, data.optInt(Params.IS_AVAILABLE) == 1);
                        statusChangeText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), isAvailable ? R.color.red : R.color.green));
                        statusChangeText.setText(isAvailable ? "Go Offline" : "Go Online");
                        Glide.with(getApplicationContext()).load(isAvailable ? R.drawable.online_taxi : R.drawable.taxi_offline).into(availableImage);
                    } else {
                        UiUtils.hideLoadingDialog();
                        UiUtils.showShortToast(StatusAvailabilityActivity.this, jsonObject.optString(Params.ERROR));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    private void checkAvailabilityStatus() {
        UiUtils.showLoadingDialog(StatusAvailabilityActivity.this);
        Call<String> call = apiInterface.checkAvailability(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if (jsonObject.optString(Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        JSONObject data = jsonObject.optJSONObject(Params.DATA);
                        prefUtils.setValue(PrefKeys.AVAILABLE_STATUS, data.optInt(Params.IS_AVAILABLE) == 1);
                        boolean isAvailable = data.optInt(Params.IS_AVAILABLE) == 1;
                        if(isAvailable) {
                            Const.SEND_POSITION_DRIVER = true;
                            Objects.requireNonNull(getApplicationContext()).startService(new Intent(getApplicationContext(), NotificationSendDriverPosition.class));
                        }
                        else {
                            Const.SEND_POSITION_DRIVER = false;
                            Objects.requireNonNull(getApplicationContext()).stopService(new Intent(getApplicationContext(), NotificationSendDriverPosition.class));

                        }
                        statusChangeText.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), isAvailable ? R.color.red : R.color.green));
                        statusChangeText.setText(isAvailable ? "Go Offline" : "Go Online");
                        Glide.with(getApplicationContext()).load(isAvailable ? R.drawable.online_taxi : R.drawable.taxi_offline).into(availableImage);
                    } else {
                        UiUtils.hideLoadingDialog();
                        UiUtils.showShortToast(StatusAvailabilityActivity.this, jsonObject.optString(Params.ERROR));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }
}
