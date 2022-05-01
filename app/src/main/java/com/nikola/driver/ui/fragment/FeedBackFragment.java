package com.nikola.driver.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.model.RequestDetails;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.network.newnetwork.ParserUtils;
import com.nikola.driver.ui.activity.MainActivity;
import com.nikola.driver.utils.AndyUtils;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.customText.CustomBoldRegularTextView;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 1/12/2017.
 */

public class FeedBackFragment extends Fragment {

    @BindView(R.id.tv_feedback_total_fare)
    CustomBoldRegularTextView tvFeedbackTotalFare;
    @BindView(R.id.iv_feedback_vehicle)
    CircleImageView ivFeedbackVehicle;
    @BindView(R.id.iv_feedback_user)
    CircleImageView ivFeedbackUser;
    @BindView(R.id.iv_feedback_location)
    CircleImageView ivFeedbackLocation;
    @BindView(R.id.tv_feedback_time)
    CustomBoldRegularTextView tvFeedbackTime;
    @BindView(R.id.tv_feedback_distance)
    CustomBoldRegularTextView tvFeedbackDistance;
    @BindView(R.id.layout_distance)
    LinearLayout layoutDistance;
    @BindView(R.id.tv_payment_type)
    CustomRegularTextView tvPaymentType;
    @BindView(R.id.tv_no_tolls)
    CustomRegularTextView tvNoTolls;
    @BindView(R.id.toll_layout)
    LinearLayout tollLayout;
    @BindView(R.id.feedback_rating_bar)
    SimpleRatingBar feedbackRatingBar;
    @BindView(R.id.bn_feedback_submit)
    CustomRegularTextView bnFeedbackSubmit;
    private Bundle bundle;
    private RequestDetails requestDetails;
    private int ratingStar = 0;
    private MainActivity activity;
    private Handler reqHandler;
    private boolean ishown = false;
    private String payment_mode, status = "";
    Unbinder unbinder;
    APIInterface apiInterface;
    int requestId;
    PrefUtils prefUtils;
    Runnable runnable = new Runnable() {
        public void run() {
            if (requestDetails != null) {
                checkRequestStatus();
                reqHandler.postDelayed(this, 5000);
            }
        }
    };

    private void checkRequestStatus() {
        Call<String> call = apiInterface.checkRequestStatus(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject checkRequestStatusResponse = null;
                try {
                    checkRequestStatusResponse = new JSONObject(response.body());
                    if (checkRequestStatusResponse != null) {
                        if (checkRequestStatusResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                            requestDetails = ParserUtils.parseRequestStatus(response.body());
                            JSONObject data = checkRequestStatusResponse.optJSONObject(Params.DATA);
                            JSONArray jsonArray = data.getJSONArray(Params.DATA);
                            JSONArray invoicearray = data.getJSONArray(Params.INVOICE);
                            JSONObject stausobj = jsonArray.getJSONObject(0);
                            status = stausobj.getString(Params.STATUS);
                            JSONObject invobj = invoicearray.getJSONObject(0);
                            payment_mode = invobj.getString(Params.PAYMENT_MODE);
                            if (ishown == false && status.equals("8") && payment_mode.equals(Const.CASH) && activity != null && !activity.isFinishing() && activity.currentFragment.equals(Const.FEEDBACK_FRAGMENT)) {
                                ishown = true;
                                showCashPaymentDialog();
                            }
                            setDataOnFeedBackView();
                            requestId = requestDetails.getRequestId();

                        } else {
                            UiUtils.showShortToast(getContext(), checkRequestStatusResponse.optString(Params.ERROR));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getContext());
        reqHandler = new Handler();
        bundle = getArguments();
        if (bundle != null) {
            requestDetails = (RequestDetails) bundle.getSerializable(Constants.REQUEST_DETAIL);
            startCheckingstatusRequests();
        }
    }



    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        feedbackRatingBar.setListener(value -> {
            ratingStar = value;
            AndyUtils.appLog("RatingValue", value + "");
        });
        disableRatingBtn();

        try {
            if (requestDetails.getRequest_type().equals("1") || requestDetails.getRequest_type().equals("2")) {
                tollLayout.setVisibility(View.GONE);
            } else {
                tollLayout.setVisibility(View.VISIBLE);
                tvNoTolls.setText(getResources().getString(R.string.txt_total_no_tolls) + " " + requestDetails.getNo_tolls());
            }

            if (requestDetails.getRequest_type().equals("2") || requestDetails.getRequest_type().equals("3")) {
                layoutDistance.setVisibility(View.GONE);
                ivFeedbackLocation.setVisibility(View.GONE);
            } else {
                layoutDistance.setVisibility(View.VISIBLE);
                ivFeedbackLocation.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public static String getGoogleMapThumbnail(double lati, double longi) {
        String staticMapUrl = "http://maps.google.com/maps/api/staticmap?center=" + lati + "," + longi + "&markers=" + lati + "," + longi + "&zoom=14&size=150x120&sensor=false&key=" + Const.GOOGLE_API_KEY;
        return staticMapUrl;
    }


    private void setDataOnFeedBackView() {
        if (requestDetails != null) {
            Glide.with(activity).load(requestDetails.getClientProfile()).into(ivFeedbackUser);
            Glide.with(activity).load(requestDetails.getTypePicture()).into(ivFeedbackVehicle);
            tvFeedbackTotalFare.setText(requestDetails.getTotal() != null ? requestDetails.getCurrency_unit() + " " + requestDetails.getTotal() : "0.00");
            //tvFeedbackTotalFare.setText(getText(R.string.taximetro));
            tvFeedbackDistance.setText(requestDetails.getDistance_unit() != null ? requestDetails.getDistance() + " " + requestDetails.getDistance_unit() : "0");
            tvFeedbackTime.setText(requestDetails.getDistance_unit() != null  ? requestDetails.getTime() + " " + "mins" : "0");
            tvPaymentType.setText(requestDetails.getPayment_type() != null ? getResources().getString(R.string.txt_payment_type) +  requestDetails.getPayment_type() : requestDetails.getPayment_type());
            try {
                if (!requestDetails.getdLatitude().equals("") && !requestDetails.getdLongitude().equals("")) {
                    Glide.with(activity).load(getGoogleMapThumbnail(Double.valueOf(requestDetails.getdLatitude()), Double.valueOf(requestDetails.getdLongitude()))).centerCrop().into(ivFeedbackLocation);
                }
            enableRatingBtn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startCheckingstatusRequests() {
        startCheckRegTimer();
    }

    private void stopCheckingstatusRequests() {
        if (reqHandler != null) {
            reqHandler.removeCallbacks(runnable);
            Log.d("mahi", "stop handler");
        }
    }

    public void startCheckRegTimer() {
        reqHandler.postDelayed(runnable, 5000);
    }


    @Override
    public void onResume() {
        super.onResume();
        checkRequestStatus();
        activity.currentFragment = Const.FEEDBACK_FRAGMENT;
    }


    private void showCashPaymentDialog() {
        String Mesaage = getResources().getString(R.string.txt_ride_total) + requestDetails.getCurrency_unit() + " " + requestDetails.getTotal();
        AlertDialog.Builder paybuilder = new AlertDialog.Builder(activity);
        paybuilder.setMessage(Mesaage)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.btn_confirm), (dialog, id) -> {
                    dialog.cancel();
                    postCodConfirmation();
                });
        AlertDialog alert = paybuilder.create();
        alert.show();
    }


    private void postCodConfirmation() {
        Call<String> call = apiInterface.codConfirm(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                Params.REQUEST_ID);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.showLoadingDialog(getContext());
                JSONObject codResponse = null;
                try {
                    codResponse = new JSONObject(response.body());
                    if (codResponse != null) {
                        if (codResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                            UiUtils.hideLoadingDialog();
                            UiUtils.showShortToast(getContext(), getString(R.string.txt_confirm_cash));
                        } else {
                            UiUtils.showShortToast(getContext(), codResponse.optString(Params.ERROR));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(getActivity())) {
                    UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }


    private void postUserRated() {
        disableRatingBtn();
        try {
            Call<String> call = apiInterface.rateUser(prefUtils.getIntValue(PrefKeys.ID, 0),
                    prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                    requestId,
                    feedbackRatingBar.getRating());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    JSONObject ratingResponse = null;
                    try {
                        ratingResponse = new JSONObject(response.body());
                        if (ratingResponse != null) {
                            if (ratingResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                                UiUtils.hideLoadingDialog();
                                stopCheckingstatusRequests();
                                //clear the request after rating
                                Intent homeIntent = new Intent(activity, MainActivity.class);
                                startActivity(homeIntent);
                                activity.finish();
                            } else {
                                UiUtils.showShortToast(getActivity(), ratingResponse.optString(Params.ERROR));
                                enableRatingBtn();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (NetworkUtils.isNetworkConnected(getActivity())) {
                        UiUtils.showShortToast(getActivity(), getString(R.string.may_be_your_is_lost));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCheckingstatusRequests();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup mContainer = getActivity().findViewById(R.id.content_frame);
        stopCheckingstatusRequests();
        mContainer.removeAllViews();
    }

    @OnClick(R.id.bn_feedback_submit)
    public void onViewClicked() {
        try {
            if (status.equals("3") && payment_mode.equals(Const.CASH)) {
                UiUtils.showShortToast(activity, getString(R.string.txt_error_payment_confirm));
                return;
            }
            if (ratingStar > 0) {
                postUserRated();
            } else {
                UiUtils.showShortToast(getActivity(), getString(R.string.give_rating));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableRatingBtn() {
        bnFeedbackSubmit.setEnabled(true);
        bnFeedbackSubmit.setBackgroundColor(activity.getResources().getColor(R.color.black));
    }

    public void disableRatingBtn() {
        bnFeedbackSubmit.setEnabled(false);
        bnFeedbackSubmit.setBackgroundColor(activity.getResources().getColor(R.color.dark_grey));
    }

}
