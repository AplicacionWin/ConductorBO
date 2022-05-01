package com.nikola.driver.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.nikola.driver.R;
import com.nikola.driver.network.model.History;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.network.newnetwork.ParserUtils;
import com.nikola.driver.ui.adapter.HistoryAdapter;
import com.nikola.driver.utils.RecyclerLongPressClickListener;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 1/20/2017.
 */

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.TripsInterface {
    @BindView(R.id.ride_lv)
    RecyclerView rideLv;
    @BindView(R.id.emptyLayout)
    LinearLayout emptyLayout;
    @BindView(R.id.emptyIcon)
    ImageView emptyIcon;
    private ArrayList<History> historylst = new ArrayList<>();
    private HistoryAdapter historyAdapter;
    APIInterface apiInterface;
    PrefUtils prefUtils;

    private RecyclerView.OnScrollListener tripScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager llmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (llmanager.findLastCompletelyVisibleItemPosition() == (historyAdapter.getItemCount() - 1)) {
                historyAdapter.showLoading();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_history);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        ButterKnife.bind(this);
        getHistoryList(0);
        Glide.with(getApplicationContext()).load(R.drawable.box).into(emptyIcon);
    }

    private void setUpAdapter() {
        historyAdapter = new HistoryAdapter(this, historylst, this);
        rideLv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rideLv.setAdapter(historyAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        rideLv.setLayoutAnimation(animation);
        rideLv.scheduleLayoutAnimation();
        rideLv.setHasFixedSize(true);
        historyAdapter.notifyDataSetChanged();
    }

    protected void getHistoryList(int skip) {
        if(skip == 0) {
            UiUtils.showLoadingDialog(this);
            historylst.clear();
        }
        Call<String> call = apiInterface.history(prefUtils.getIntValue(PrefKeys.ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , skip);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(skip == 0) {
                    UiUtils.hideLoadingDialog();
                    historylst.clear();
                }
                JSONObject historyResponse = null;
                try {
                    historyResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (historyResponse != null) {
                    if (historyResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        JSONArray hisArray = historyResponse.optJSONArray(APIConstants.Params.DATA);
                        historylst = ParserUtils.ParseHistoryArrayList(hisArray);
                        setUpAdapter();
                        if (!historylst.isEmpty())
                            rideLv.removeOnScrollListener(tripScrollListener);
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), historyResponse.optString(APIConstants.Params.ERROR));
                    }
                }
                emptyLayout.setVisibility(historylst.isEmpty() ? View.VISIBLE : View.GONE);
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
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.history_back)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onLoadMoreTrips(int skip) {
        getHistoryList(skip);
    }
}
