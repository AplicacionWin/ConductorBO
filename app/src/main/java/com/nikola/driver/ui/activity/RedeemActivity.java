package com.nikola.driver.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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

public class RedeemActivity extends AppCompatActivity implements NewWalletAdapter.TransactionInterface {

    NewWalletAdapter walletAdapter;
    ArrayList<WalletPayments> paymentsList = new ArrayList<>();
    @BindView(R.id.recentTransactionRecycler)
    RecyclerView recentTransactionRecycler;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.remainingAmount)
    CustomBoldRegularTextView remainingAmount;
    @BindView(R.id.totalAmount)
    CustomBoldRegularTextView totalAmount;
    @BindView(R.id.redeemedAmount)
    CustomBoldRegularTextView redeemedAmount;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sendBtn)
    CardView sendBtn;
    @BindView(R.id.viewMore)
    CustomRegularTextView viewMore;
    @BindView(R.id.noData)
    CustomRegularTextView noData;
    @BindView(R.id.empty)
    ImageView emptyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        getRedeemsFromBackend();
        setUpAdapter();
        Glide.with(getApplicationContext()).load(R.drawable.box).into(emptyData);
    }

    private void setUpAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        walletAdapter = new NewWalletAdapter(this, paymentsList, true, this);
        recentTransactionRecycler.setLayoutManager(linearLayoutManager);
        recentTransactionRecycler.setAdapter(walletAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        recentTransactionRecycler.setLayoutAnimation(animation);
        recentTransactionRecycler.scheduleLayoutAnimation();
        recentTransactionRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                viewMore.setVisibility(linearLayoutManager.findLastCompletelyVisibleItemPosition() == (walletAdapter.getItemCount() - 1) ? View.VISIBLE : View.GONE);
            }
        });
    }

    protected void getRedeemsFromBackend() {
        UiUtils.showLoadingDialog(RedeemActivity.this);
        Call<String> call = apiInterface.getRedeemsList(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                paymentsList.clear();
                JSONObject redeemsResponse = null;
                try {
                    redeemsResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (redeemsResponse != null) {
                    if (redeemsResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        JSONObject data = redeemsResponse.optJSONObject(APIConstants.Params.DATA);
                        JSONObject wallet = data.optJSONObject(APIConstants.Params.WALLET);
                        remainingAmount.setText(wallet.optString(APIConstants.Params.REMAINING_FORMATTED));
                        totalAmount.setText(wallet.optString(APIConstants.Params.TOTAL_FORMATTED));
                        sendBtn.setVisibility(!wallet.optString(APIConstants.Params.REMAINING_FORMATTED).equals("$0.00") ? View.VISIBLE : View.INVISIBLE);

                        redeemedAmount.setText(wallet.optString(APIConstants.Params.USED_FORMATTED));
                        JSONArray paymentsArray = data.optJSONArray(APIConstants.Params.PAYMENTS);
                        for (int i = 0; i < paymentsArray.length(); i++) {
                            JSONObject paymentsObject = paymentsArray.optJSONObject(i);
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
                        emptyData.setVisibility(paymentsList.isEmpty() ?View.VISIBLE : View.GONE);
                        noData.setVisibility(paymentsList.isEmpty() ? View.VISIBLE : View.GONE);
                        recentTransactionRecycler.setVisibility(paymentsList.isEmpty() ? View.GONE : View.VISIBLE);
                        viewMore.setVisibility(paymentsList.isEmpty() ? View.GONE : View.VISIBLE);
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), redeemsResponse.optString(APIConstants.Params.ERROR));
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

    @Override
    public void onLoadMoreTransactions(int skip) {

    }

    protected void cancelRedeemRequest(String redeemRequestId) {
        UiUtils.showLoadingDialog(RedeemActivity.this);
        Call<String> call = apiInterface.cancelRedeemRequest(prefUtils.getIntValue(PrefKeys.ID, 0),
                prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                redeemRequestId);
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
                        UiUtils.showShortToast(RedeemActivity.this, cancelRequestResponse.optString(APIConstants.Params.MESSAGE));
                        getRedeemsFromBackend();
                    }
                } else {
                    UiUtils.showShortToast(RedeemActivity.this, cancelRequestResponse.optString(APIConstants.Params.ERROR));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(RedeemActivity.this)) {
                    UiUtils.showShortToast(RedeemActivity.this, getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    @OnClick(R.id.sendBtn)
    public void onViewClicked() {
        AddMoneyBottomSheet addMoneyBottomSheet = new AddMoneyBottomSheet();
        addMoneyBottomSheet.setIsRedeem(true, remainingAmount.getText().toString());
        addMoneyBottomSheet.show(getSupportFragmentManager(), addMoneyBottomSheet.getTag());
    }

    @OnClick(R.id.viewMore)
    public void setViewMore() {
        Intent transactionIntent = new Intent(getApplicationContext(), TransactionListActivity.class);
        transactionIntent.putExtra(APIConstants.Params.IS_REDEEM, true);
        startActivity(transactionIntent);
    }
}
