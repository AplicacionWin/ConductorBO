package com.nikola.driver.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.model.WalletPayments;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.adapter.NewWalletAdapter;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionListActivity extends AppCompatActivity implements NewWalletAdapter.TransactionInterface {

    @BindView(R.id.recentTransactionRecycler)
    RecyclerView recentTransactionRecycler;
    NewWalletAdapter walletAdapter;
    ArrayList<WalletPayments> paymentsList = new ArrayList<>();
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.empty)
    ImageView empty;
    boolean isRedeem;
    @BindView(R.id.noData)
    CustomRegularTextView noData;

    private RecyclerView.OnScrollListener transactionScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager llmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (llmanager.findLastCompletelyVisibleItemPosition() == (walletAdapter.getItemCount() - 1)) {
                walletAdapter.showLoading();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null){
            isRedeem = getIntent().getBooleanExtra(APIConstants.Params.IS_REDEEM, false);
        }
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        apiInterface = APIClient.getClient().create(APIInterface.class);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        toolbar.setTitle("Transactions");
        setUpAdapter();
        getTransactions(0);
        Glide.with(getApplicationContext()).load(R.drawable.box).into(empty);
    }

    private void setUpAdapter() {
        walletAdapter = new NewWalletAdapter(this, paymentsList, isRedeem, this);
        recentTransactionRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recentTransactionRecycler.setAdapter(walletAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        recentTransactionRecycler.setLayoutAnimation(animation);
        recentTransactionRecycler.scheduleLayoutAnimation();
        recentTransactionRecycler.addOnScrollListener(transactionScrollListener);
    }

    protected void getTransactions(int skip) {
        if (skip == 0) {
            UiUtils.showLoadingDialog(TransactionListActivity.this);
            paymentsList.clear();
        }
        Call<String> call;
        if (!isRedeem)
            call = apiInterface.getWalletTransactions(prefUtils.getIntValue(PrefKeys.ID, 0),
                    prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                    skip);
        else
            call = apiInterface.getAllRedeemRequests(prefUtils.getIntValue(PrefKeys.ID, 0),
                    prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                    skip);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject walletResponse = null;
                try {
                    walletResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (walletResponse != null) {
                    if (walletResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        JSONArray data = walletResponse.optJSONArray(APIConstants.Params.DATA);
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject paymentsObject = data.optJSONObject(i);
                            WalletPayments payments = new WalletPayments();
                            payments.setTitle(paymentsObject.optString(APIConstants.Params.TITLE));
                            payments.setRedeemId(paymentsObject.optString(APIConstants.Params.PROVIDER_REDEEM_REQUEST_ID));
                            payments.setDescription(paymentsObject.optString(APIConstants.Params.DESCRIPTION));
                            payments.setUniqueId(paymentsObject.optString(APIConstants.Params.UNIQUE_ID));
                            payments.setWallet_amount_symbol(paymentsObject.optString(APIConstants.Params.AMOUNT_SYMBOL));
                            payments.setWallet_image(paymentsObject.optString(APIConstants.Params.IMAGE));
                            payments.setAmount(paymentsObject.optString(APIConstants.Params.TOTAL_FORMATTED));
                            payments.setCancelButtonStatus(paymentsObject.optInt(APIConstants.Params.CANCEL_BUTTON_STATUS) == 1);
                            paymentsList.add(payments);
                        }
                        walletAdapter.notifyDataSetChanged();
                        empty.setVisibility(paymentsList.isEmpty() ? View.VISIBLE : View.GONE);
                        noData.setVisibility(paymentsList.isEmpty() ? View.VISIBLE : View.GONE);
                        recentTransactionRecycler.setVisibility(paymentsList.isEmpty() ? View.GONE : View.VISIBLE);
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), walletResponse.optString(APIConstants.Params.ERROR));
                    }
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

    @Override
    public void cancelRequest(String redeemId) {
        cancelRedeemRequest(redeemId);
    }

    private void cancelRedeemRequest(String redeemId) {

        UiUtils.showLoadingDialog(TransactionListActivity.this);
        Call<String> call = apiInterface.cancelRedeemRequest(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                redeemId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject cancelRequestResponse = null;
                try {
                    cancelRequestResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (cancelRequestResponse != null) {
                    if (cancelRequestResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        UiUtils.showShortToast(TransactionListActivity.this, cancelRequestResponse.optString(APIConstants.Params.MESSAGE));
                        getTransactions(0);
                    }
                } else {
                    UiUtils.showShortToast(TransactionListActivity.this, cancelRequestResponse.optString(APIConstants.Params.ERROR));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(TransactionListActivity.this)) {
                    UiUtils.showShortToast(TransactionListActivity.this, getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }


    @Override
    public void onLoadMoreTransactions(int skip) {
        getTransactions(skip);
    }
}
