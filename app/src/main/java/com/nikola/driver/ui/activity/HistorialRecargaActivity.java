package com.nikola.driver.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.model.HistorialRecarga;
import com.nikola.driver.network.model.History;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.network.newnetwork.ParserUtils;
import com.nikola.driver.ui.adapter.HistorialRecargasAdapter;
import com.nikola.driver.ui.adapter.HistoryAdapter;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistorialRecargaActivity extends AppCompatActivity implements HistorialRecargasAdapter.TripsInterface{

    @BindView(R.id.historial_back)
    ImageButton back;
    @BindView(R.id.historialemptyIcon)
    ImageView emptyIcon;
    @BindView(R.id.historial_lv)
    RecyclerView rideLv;

    @BindView(R.id.historialemptyLayout)
    LinearLayout emptyLayout;

    private ArrayList<HistorialRecarga> historylst = new ArrayList<>();
    private HistorialRecargasAdapter historyAdapter;
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
        setContentView(R.layout.activity_historial_recarga);
        ButterKnife.bind(this);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getHistoryList(0);
        Glide.with(getApplicationContext()).load(R.drawable.box).into(emptyIcon);
    }

    protected void getHistoryList(int skip) {
        if(skip == 0) {
            UiUtils.showLoadingDialog(this);
            historylst.clear();
        }
        Call<String> call = apiInterface.historialRecarga(
                prefUtils.getIntValue(PrefKeys.ID, 0));
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
                        JSONObject data = historyResponse.optJSONObject(APIConstants.Params.DATA);
                        JSONArray hisArray = data.optJSONArray("recargas");
                        historylst = ParserUtils.ParseHistorialArrayList(hisArray);
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

    private void setUpAdapter() {
        historyAdapter = new HistorialRecargasAdapter(this, historylst, this);
        rideLv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rideLv.setAdapter(historyAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        rideLv.setLayoutAnimation(animation);
        rideLv.scheduleLayoutAnimation();
        rideLv.setHasFixedSize(true);
        historyAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMoreTrips(int skip) {
        getHistoryList(skip);
    }
}