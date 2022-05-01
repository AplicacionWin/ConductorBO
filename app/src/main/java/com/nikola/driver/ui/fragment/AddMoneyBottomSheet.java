package com.nikola.driver.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.activity.PaymentsActivity;
import com.nikola.driver.ui.activity.RedeemActivity;
import com.nikola.driver.ui.activity.WalletAcivity;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMoneyBottomSheet extends DialogFragment {

    Unbinder unbinder;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.one)
    CardView one;
    @BindView(R.id.two)
    CardView two;
    @BindView(R.id.three)
    CardView three;
    @BindView(R.id.four)
    CardView four;
    @BindView(R.id.five)
    CardView five;
    @BindView(R.id.six)
    CardView six;
    @BindView(R.id.seven)
    CardView seven;
    @BindView(R.id.eight)
    CardView eight;
    @BindView(R.id.nine)
    CardView nine;
    @BindView(R.id.clearAmt)
    CardView clearAmt;
    @BindView(R.id.zero)
    CardView zero;
    @BindView(R.id.dot)
    CardView dot;
    @BindView(R.id.moneyText)
    CustomRegularTextView moneyText;
    @BindView(R.id.addMoney)
    Button addMoney;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    boolean isRedeem;
    String totalAmount;
    @BindView(R.id.balance)
    CustomBoldRegularTextView balance;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.add_money_bottom_sheet, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getActivity());
        toolbar.setNavigationOnClickListener(view -> dismiss());
        balance.setText(totalAmount);
        dot.setEnabled(true);
        addMoney.setText(isRedeem ? getString(R.string.send_redeem_request) : getString(R.string.add_money_to_wallet));
    }

    public void setIsRedeem(boolean isRedeem, String totalAmount) {
        this.isRedeem = isRedeem;
        this.totalAmount = totalAmount;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @OnClick({R.id.one, R.id.two, R.id.three, R.id.four, R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine, R.id.clearAmt, R.id.zero, R.id.dot})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.one:
                moneyText.append("1");
                break;
            case R.id.two:
                moneyText.append("2");
                break;
            case R.id.three:
                moneyText.append("3");
                break;
            case R.id.four:
                moneyText.append("4");
                break;
            case R.id.five:
                moneyText.append("5");
                break;
            case R.id.six:
                moneyText.append("6");
                break;
            case R.id.seven:
                moneyText.append("7");
                break;
            case R.id.eight:
                moneyText.append("8");
                break;
            case R.id.nine:
                moneyText.append("9");
                break;
            case R.id.clearAmt:
                moneyText.setText("");
                break;
            case R.id.zero:
                moneyText.append("0");
                break;
            case R.id.dot:
                moneyText.append(".");
                break;
        }
    }

    protected void addMoneyToWallet() {
        UiUtils.showLoadingDialog(getActivity());
        Call<String> call;
        if (!isRedeem) {
            call = apiInterface.addMoneyToWallet(prefUtils.getIntValue(PrefKeys.ID, 0),
                    prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                    moneyText.getText().toString(),
                    "card");
        } else {
            call = apiInterface.sendMoneyForRedeem(prefUtils.getIntValue(PrefKeys.ID, 0),
                    prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""),
                    moneyText.getText().toString());
        }
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
                        UiUtils.showShortToast(getActivity(), walletResponse.optString(APIConstants.Params.MESSAGE));
                        dismiss();
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), !isRedeem ? WalletAcivity.class : RedeemActivity.class);
                        startActivity(intent);
                    } else {
                        UiUtils.showShortToast(getActivity(), walletResponse.optString(APIConstants.Params.ERROR));
                        if (walletResponse.optInt(APIConstants.Params.ERROR_CODE) == APIConstants.ErrorCodes.REDIRECT_PAYMENTS) {
                            Intent i = new Intent(getActivity(), PaymentsActivity.class);
                            startActivity(i);
                        }
                    }
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

    @OnClick(R.id.addMoney)
    public void onViewClicked() {
        if (validateField())
            addMoneyToWallet();
    }

    private boolean validateField() {
        if (moneyText.getText().toString().equals("")){
            UiUtils.showShortToast(getContext(), getString(R.string.please_enter_amount));
            return  false;
        }
        if (moneyText.getText().toString().equalsIgnoreCase("0.0")|| moneyText.getText().toString().equalsIgnoreCase("0")){
            UiUtils.showShortToast(getContext(), getString(R.string.amount_cant_be_zero));
            return  false;
        }
        if (Float.valueOf(moneyText.getText().toString())< 0){
            UiUtils.showShortToast(getContext(), getString(R.string.amount_cant_be_zero));
            return  false;
        }
        return  true;
    }
}
