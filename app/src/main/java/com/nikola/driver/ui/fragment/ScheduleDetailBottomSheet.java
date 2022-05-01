package com.nikola.driver.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.activity.ChatActivity;
import com.nikola.driver.ui.activity.MainActivity;
import com.nikola.driver.utils.customText.CustomBoldRegularTextView;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleDetailBottomSheet extends DialogFragment {
    Unbinder unbinder;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    String requestId;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tripTime)
    CustomRegularTextView tripTime;
    @BindView(R.id.requestUniqueId)
    CustomRegularTextView requestUniqueId;
    @BindView(R.id.amount)
    CustomRegularTextView amount;
    @BindView(R.id.mapImage)
    ImageView mapImage;
    @BindView(R.id.providerImage)
    CircleImageView providerImage;
    @BindView(R.id.providerName)
    CustomRegularTextView providerName;
    @BindView(R.id.rating)
    SimpleRatingBar rating;
    @BindView(R.id.serviceImage)
    CircleImageView serviceImage;
    @BindView(R.id.serviceName)
    CustomRegularTextView serviceName;
    @BindView(R.id.modelName)
    CustomRegularTextView modelName;
    @BindView(R.id.sourceAddress)
    CustomRegularTextView sourceAddress;
    @BindView(R.id.destAddress)
    CustomRegularTextView destAddress;
    @BindView(R.id.ridefare)
    CustomRegularTextView ridefare;
    @BindView(R.id.serviceFee)
    CustomRegularTextView serviceFee;
    @BindView(R.id.cancellationFee)
    CustomRegularTextView cancellationFee;
    @BindView(R.id.discount)
    CustomRegularTextView discount;
    @BindView(R.id.total)
    CustomBoldRegularTextView total;
    @BindView(R.id.chat)
    CustomRegularTextView chat;
    @BindView(R.id.tack)
    CustomRegularTextView track;
    @BindView(R.id.cancel)
    CustomRegularTextView cancel;
    @BindView(R.id.taxes)
    CustomRegularTextView taxes;
    @BindView(R.id.providerLayout)
    RelativeLayout providerLayout;
    @BindView(R.id.invoiceLayout)
    LinearLayout invoiceLayout;
    @BindView(R.id.rootLayout)
    ScrollView rootLayout;
    @BindView(R.id.loader)
    ImageView loader;
    @BindView(R.id.payment_Mode)
    CustomRegularTextView payment_mode;
    @BindView(R.id.buttonLayout)
    LinearLayout buttonLayout;
    @BindView(R.id.topLine)
    View topLine;

    double latitude, longitude;

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.history_details_view, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        Glide.with(getActivity()).load(R.raw.car).into(loader);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getActivity());
        back.setOnClickListener(view -> dialog.dismiss());
        getSingleResponse(requestId);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
               dismiss();
            }
        };
    }

    private void getSingleResponse(String requestId) {
        loader.setVisibility(View.VISIBLE);
        rootLayout.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
        Call<String> call = apiInterface.getRequestsView(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                requestId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                rootLayout.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.VISIBLE);
                JSONObject singleResponse = null;
                try {
                    singleResponse = new JSONObject(response.body());
                    if (singleResponse != null) {
                        if (singleResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                            JSONObject data = singleResponse.optJSONObject(Params.DATA);
                            tripTime.setText(data.optString(Params.REQUEST_CREATED_TIME));
                            requestUniqueId.setText("#" + data.optString(Params.REQUEST_UNIQUE_ID));
                            sourceAddress.setText(data.optString(Params.S_ADDRESS));
                            destAddress.setText(data.optString(APIConstants.Params.D_ADDRESS).isEmpty() ? getResources().getString(R.string.not_available) : data.optString(APIConstants.Params.D_ADDRESS));
                            latitude = data.optDouble(Params.LATITUDE);
                            longitude = data.optDouble(Params.LONGITUDE);
                            longitude = data.optDouble(Params.LONGITUDE);

                            Glide.with(getActivity())
                                    .load(data.optString(Params.TYPE_PICTURE))
                                    .into(serviceImage);

                            Glide.with(getActivity())
                                    .load(data.optString(Params.REQUEST_MAP_IMAGE))
                                    .into(mapImage);

                            serviceName.setText(data.optString(Params.SERVICE_TYPE_NAME));
                            modelName.setText(data.optString(Params.SERVICE_MODEL));
                            providerName.setText(data.optString(Params.PROVIDER_NAME));
                            Glide.with(getActivity())
                                    .load(data.optString(Params.PROVIDER_PICTURE))
                                    .into(providerImage);

//                            try {
//                                JSONObject providerDetails = data.getJSONObject(Params.PROVIDER_DETAILS);
//                                if (providerDetails != null) {
//                                    providerName.setText(providerDetails.optString(Params.PROVIDER_NAME));
//                                    Glide.with(getActivity())
//                                            .load(data.optString(Params.PROVIDER_PICTURE))
//                                            .into(providerImage);
//                                } else {
//                                    providerLayout.setVisibility(View.GONE);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                providerLayout.setVisibility(View.GONE);
//                            }
                            /*try {
                                JSONObject providerDetails = data.getJSONObject(Params.PROVIDER_DETAILS);
                                if (providerDetails != null) {
                                    providerName.setText(providerDetails.optString(Params.PROVIDER_NAME));
                                    Glide.with(getActivity())
                                            .load(data.optString(Params.PROVIDER_PICTURE))
                                            .into(providerImage);
                                } else {
                                    providerLayout.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                providerLayout.setVisibility(View.GONE);
                            }*/
                            try {
                                JSONObject invoiceDetails = data.getJSONObject(Params.INVOICE_DETAILS);
                                ridefare.setText(invoiceDetails.optString(Params.RIDE_FARE));
                                serviceFee.setText(invoiceDetails.optString(Params.SERVICE_FARE_FORMATTED));
                                cancellationFee.setText(invoiceDetails.optString(Params.CANCELLATION_FARE_FORMATTED));
                                discount.setText(invoiceDetails.optString(Params.DISCOUNT));
                                total.setText(invoiceDetails.optString(Params.TOTAL_FORMATTED));
                                payment_mode.setText(invoiceDetails.optString(Params.PAYMENT_MODE));
                                taxes.setText(String.format("Includes %s taxes", invoiceDetails.optString(Params.TAX_FARE_FORMATTED)));
                                rating.setRating(data.optInt(Params.RATING));
                                amount.setText(invoiceDetails.optString(Params.PROVIDER_EARNINGS_FORMATTED));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            JSONObject requestBtnStatus = data.optJSONObject(Params.REQUEST_BUTTON_STATUS);
                            track.setVisibility(requestBtnStatus.optInt(Params.TRACK_STATUS) == 0 ? View.GONE : View.VISIBLE);
                            chat.setVisibility(requestBtnStatus.optInt(Params.MESSAGE_BTN_STATUS) == 0 ? View.GONE : View.VISIBLE);
                            cancel.setVisibility(requestBtnStatus.optInt(Params.CANCEL_BUTTON_STATUS) == 0 ? View.GONE : View.VISIBLE);
                            invoiceLayout.setVisibility(requestBtnStatus.optInt(Params.INVOICE_BUTTON_STATUS) == 0 ? View.GONE : View.VISIBLE);

                            if(cancel.getVisibility() == View.GONE && track.getVisibility() == View.GONE && chat.getVisibility() == View.GONE) {
                                topLine.setVisibility(View.GONE);
                            }
                            if(data.optString(Params.STATUS_TEXT).equalsIgnoreCase("Ongoing")){
                                buttonLayout.setVisibility(View.VISIBLE);
                            } else {
                                buttonLayout.setVisibility(View.GONE);
                            }
                        } else {
                            UiUtils.showShortToast(getActivity(), singleResponse.optString(Params.ERROR_MESSAGE));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.chat, R.id.tack, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chat:
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                startActivity(chatIntent);
                break;
            case R.id.tack:
                Intent ongoinIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(ongoinIntent);
                break;
            case R.id.cancel:
                final iOSDialog cancelDialog = new iOSDialog(getActivity());
                cancelDialog.setTitle(getResources().getString(R.string.txt_cancel_ride));
                cancelDialog.setSubtitle(getResources().getString(R.string.cancel_txt));
                cancelDialog.setNegativeLabel(getResources().getString(R.string.txt_no));
                cancelDialog.setPositiveLabel(getResources().getString(R.string.txt_yes));
                cancelDialog.setBoldPositiveLabel(false);
                cancelDialog.setNegativeListener(view1 -> cancelDialog.dismiss());
                cancelDialog.setPositiveListener(view12 -> {
                    //geCancelReason();
                    cancelRide();
                    cancelDialog.dismiss();
                });
                cancelDialog.show();
                break;
        }
    }

    private void cancelRide() {
        Call<String> call = apiInterface.postCancelTrip(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , Integer.parseInt(requestId));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject postCancelResponse = null;
                try {
                    postCancelResponse = new JSONObject(response.body());
                    if (postCancelResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }

            }
        });
    }

    private void requestPermission(int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(intent, requestCode);
    }
}
