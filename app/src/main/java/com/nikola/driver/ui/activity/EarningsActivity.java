package com.nikola.driver.ui.activity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.gson.JsonObject;
import com.nikola.driver.R;
import com.nikola.driver.network.model.Earnings;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.network.newnetwork.ParserUtils;
import com.nikola.driver.utils.customText.CustomLightTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarningsActivity extends AppCompatActivity {
    ArrayList<BarEntry> BARENTRY = new ArrayList<>();
    ArrayList<String> BarEntryLabels = new ArrayList<>();
    BarData BARDATA;
    @BindView(R.id.weeklyChart)
    BarChart weeklyChart;
    @BindView(R.id.earnings_back)
    ImageButton earningsBack;
    @BindView(R.id.toolbar_history)
    Toolbar toolbarHistory;
    @BindView(R.id.todayEarnings)
    CustomLightTextView todayEarnings;
    @BindView(R.id.todayEarningsAmt)
    CustomLightTextView todayEarningsAmt;
    @BindView(R.id.earningsAmtOV)
    CustomLightTextView earningsAmtOV;
    @BindView(R.id.totalEarnings)
    CustomLightTextView totalEarnings;
    @BindView(R.id.totalTrips)
    CustomLightTextView totalTrips;
    @BindView(R.id.monthlyEarningsAmt)
    CustomLightTextView monthlyEarningAmt;
    @BindView(R.id.completedTrips)
    CustomLightTextView completedTrips;
    private ArrayList<Earnings> earningslst = new ArrayList<>();
    BarDataSet Bardataset;

    APIInterface apiInterface;
    PrefUtils prefUtils;
    ParserUtils parserUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(this);
        getEarnings();
        getEarningsOV();
    }


    private void getEarnings() {

        Call<String> call = apiInterface.getDashboardData(prefUtils.getIntValue(PrefKeys.ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    try {
                        JSONObject earningsObj = new JSONObject(response.body());
                        if (earningsObj.optString(Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                            JSONObject data = earningsObj.optJSONObject(Params.DATA);
                            earningslst.clear();
                            todayEarningsAmt.setText(data.optString(Params.TODAY_EARNINGS_FORMATTED));
                            totalTrips.setText(data.optString(Params.TOTAL_REQUESTS));
                            totalEarnings.setText(data.optString(Params.TOTAL_EARNINGS_FORMATTED));
                            completedTrips.setText(data.optString(Params.COMPLETED_TRIPS));
                            monthlyEarningAmt.setText(data.optString(Params.CURRENT_MONTH_FORMATTED));
                            JSONArray earnArray = data.getJSONArray(Params.LAST_X_DAYS_REQUESTS);
                            if (earnArray.length() > 0) {
                                JSONObject obj1 = earnArray.getJSONObject(0);
                                //                               earningsToday.setText(earningsObj.optString(Params.CURRENCEY) + obj1.optString(Params.TOTAL));
//                                totalTrips.setText(obj1.optString("trips") + " Trips");
                                for (int i = 1; i < earnArray.length(); i++) {
                                    JSONObject obj = earnArray.getJSONObject(i);
                                    BARENTRY.add(new BarEntry(Float.parseFloat(obj.getString(Params.TOTAL)), i - 1));
                                    BarEntryLabels.add(obj.getString(Params.DATE));
                                }
                                setUpDataBar();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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

    private void getEarningsOV() {

        Call<String> call = apiInterface.getEarningsData(prefUtils.getIntValue(PrefKeys.ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    try {
                        JSONObject earningsObj = new JSONObject(response.body());
                        if (earningsObj.optString(Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                            JSONObject data = earningsObj.getJSONObject(Params.DATA);
                            String code = data.getString(Params.STATUS);
                            if(code.equalsIgnoreCase("200"))
                            {
                                JSONObject data1 = data.getJSONObject(Params.DATA);
                                JSONObject posts = data1.getJSONObject(Params.POSTS);
                                JSONArray currencies = posts.getJSONArray(Params.CURRENCIES);
                                earningsAmtOV.setText(currencies.getJSONObject(0).optString(Params.AMOUNT_FORMATTED));
                            }
                            else
                            {
                                earningsAmtOV.setText("$0.00");
                            }
                        }
                        else
                        {
                            earningsAmtOV.setText("$0.00");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        earningsAmtOV.setText("$0.00");
                    }
                }
                else
                {
                    earningsAmtOV.setText("$0.00");
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

    private void setUpDataBar() {
        Bardataset = new BarDataSet(BARENTRY, "This Week Earnings");
        BARDATA = new BarData(BarEntryLabels, Bardataset);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        weeklyChart.setData(BARDATA);
        weeklyChart.animateY(3000);
        weeklyChart.setDescription("");
    }

    @OnClick(R.id.earnings_back)
    public void onViewClicked() {
        onBackPressed();
    }
}
