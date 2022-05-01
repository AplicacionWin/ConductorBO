package com.nikola.driver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.nikola.driver.ui.fragment.AddMoneyBottomSheet;
import com.nikola.driver.utils.customText.CustomBoldRegularTextView;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletAcivity extends AppCompatActivity implements NewWalletAdapter.TransactionInterface {

    NewWalletAdapter walletAdapter;
    ArrayList<WalletPayments> paymentsList = new ArrayList<>();
    @BindView(R.id.addMoney)
    LinearLayout addMoney;
    @BindView(R.id.transaction)
    LinearLayout transaction;
    @BindView(R.id.redeem)
    LinearLayout redeem;
    @BindView(R.id.empty)
    ImageView empty;
    @BindView(R.id.recentTransactionRecycler)
    RecyclerView recentTransactionRecycler;

    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.remaining)
    CustomBoldRegularTextView remaining;
    @BindView(R.id.total)
    CustomBoldRegularTextView total;
    @BindView(R.id.toolbar)
    CustomRegularTextView toolbar;
    @BindView(R.id.viewMore)
    CustomRegularTextView viewMore;
    @BindView(R.id.noData)
    CustomRegularTextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_acivity);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        setUpAdapter();
        Glide.with(getApplicationContext()).load(R.drawable.box).into(empty);
        toolbar.setOnClickListener(view -> onBackPressed());
    }

    private void setUpAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        walletAdapter = new NewWalletAdapter(this, paymentsList, false, this);
        recentTransactionRecycler.setLayoutManager(linearLayoutManager);
        recentTransactionRecycler.setAdapter(walletAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        recentTransactionRecycler.setLayoutAnimation(animation);
        recentTransactionRecycler.scheduleLayoutAnimation();
        recentTransactionRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWalletInfo();
    }

    @OnClick({R.id.addMoney, R.id.redeem, R.id.transaction, R.id.viewMore})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.addMoney:
                AddMoneyBottomSheet addMoneyBottomSheet = new AddMoneyBottomSheet();
                addMoneyBottomSheet.setIsRedeem(false, remaining.getText().toString());
                addMoneyBottomSheet.show(getSupportFragmentManager(), addMoneyBottomSheet.getTag());
                break;
            case R.id.redeem:
                Intent redeemIntent = new Intent(getApplicationContext(), RedeemActivity.class);
                startActivity(redeemIntent);
                break;
            case R.id.transaction:
            case R.id.viewMore:
                Intent transactionIntent = new Intent(getApplicationContext(), TransactionListActivity.class);
                transactionIntent.putExtra(APIConstants.Params.IS_REDEEM, false);
                startActivity(transactionIntent);
                break;
        }
    }

    protected void getWalletInfo() {
        UiUtils.showLoadingDialog(WalletAcivity.this);
        Call<String> call = apiInterface.getWalletData(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                paymentsList.clear();
                JSONObject walletResponse = null;
                try {
                    walletResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (walletResponse != null) {
                    if (walletResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        JSONObject data = walletResponse.optJSONObject(APIConstants.Params.DATA);
                        JSONObject wallet = data.optJSONObject(APIConstants.Params.WALLET);
                        remaining.setText(wallet.optString(APIConstants.Params.REMAINING_FORMATTED));
                        total.setText(wallet.optString(APIConstants.Params.TOTAL_FORMATTED));
                        JSONArray paymentsArray = data.optJSONArray(APIConstants.Params.PAYMENTS);
                        for (int i = 0; i < paymentsArray.length(); i++) {
                            JSONObject paymentsObject = paymentsArray.optJSONObject(i);
                            WalletPayments payments = new WalletPayments();
                            payments.setTitle(paymentsObject.optString(APIConstants.Params.TITLE));
                            payments.setDescription(paymentsObject.optString(APIConstants.Params.DESCRIPTION));
                            payments.setUniqueId(paymentsObject.optString(APIConstants.Params.UNIQUE_ID));
                            payments.setWallet_amount_symbol(paymentsObject.optString(APIConstants.Params.AMOUNT_SYMBOL));
                            payments.setWallet_image(paymentsObject.optString(APIConstants.Params.IMAGE));
                            payments.setAmount(paymentsObject.optString(APIConstants.Params.TOTAL_FORMATTED));
                            paymentsList.add(payments);
                        }
                        walletAdapter.notifyDataSetChanged();
                        noData.setVisibility(paymentsList.isEmpty() ? View.VISIBLE : View.GONE);
                        empty.setVisibility(paymentsList.isEmpty() ? View.VISIBLE : View.GONE);
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

    }

    @Override
    public void onLoadMoreTransactions(int skip) {

    }
}
