package com.nikola.driver.ui.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.model.HistorialRecarga;
import com.nikola.driver.network.model.UserSettings;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.activity.ChangePasswordActivity;
import com.nikola.driver.ui.activity.EarningsActivity;
import com.nikola.driver.ui.activity.GetStartedActivity;
import com.nikola.driver.ui.activity.HelpwebActivity;
import com.nikola.driver.ui.activity.HistorialRecargaActivity;
import com.nikola.driver.ui.activity.HistoryActivity;
import com.nikola.driver.ui.activity.ListaPlanillaActivity;
import com.nikola.driver.ui.activity.MainActivity;
import com.nikola.driver.ui.activity.PanicContactActivity;
import com.nikola.driver.ui.activity.PaymentsActivity;
import com.nikola.driver.ui.activity.PlanillaViajeActivity;
import com.nikola.driver.ui.activity.ProfileActivity;
import com.nikola.driver.ui.activity.RegistrarRecargaActivity;
import com.nikola.driver.ui.activity.SaldoActivity;
import com.nikola.driver.ui.activity.StatusAvailabilityActivity;
import com.nikola.driver.ui.activity.UploadDocActivity;
import com.nikola.driver.ui.activity.WalletAcivity;
import com.nikola.driver.ui.adapter.UserSettingsAdapter;
import com.nikola.driver.utils.Const;
import com.nikola.driver.utils.customText.CustomRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefHelper;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 12/28/2016.
 */
public class NavigationDrawableFragment extends Fragment implements AdapterView.OnItemClickListener {

    Unbinder unbinder;
    public static CircleImageView ivUserIcon;
    @BindView(R.id.tv_user_name)
    CustomRegularTextView tvUserName;
    @BindView(R.id.lv_drawer_user_settings)
    ListView lvDrawerUserSettings;
    private MainActivity activity;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    private int RC_LOCATION_PERM = 124;
    TextView refCodeEarned;
    String shareMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation_drawer_layout, container, false);
        activity = (MainActivity) getActivity();
        unbinder = ButterKnife.bind(this, view);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getContext());
        ivUserIcon = view.findViewById(R.id.iv_user_icon);
        Glide.with(getActivity()).load(prefUtils.getStringValue(PrefKeys.PICTURE, "")).into(ivUserIcon);
        setUpAdapter();
        return view;
    }

    public void setUpAdapter() {
        UserSettingsAdapter settingsAdapter = new UserSettingsAdapter(activity, getUserSettingsList());
        lvDrawerUserSettings.setAdapter(settingsAdapter);
        lvDrawerUserSettings.setOnItemClickListener(this);
        Glide.with(getActivity()).load(prefUtils.getStringValue(PrefKeys.PICTURE, "")).into(ivUserIcon);
        tvUserName.setText(prefUtils.getStringValue(PrefKeys.FIRSTNAME, ""));
        ivUserIcon.setOnClickListener(view1 -> {
            Intent i = new Intent(activity, ProfileActivity.class);
            startActivity(i);
        });
    }


    private List<UserSettings> getUserSettingsList() {
        List<UserSettings> userSettingsList = new ArrayList<>();
        userSettingsList.add(new UserSettings(R.drawable.home_map_marker, getString(R.string.my_home)));
        userSettingsList.add(new UserSettings(R.drawable.settings, getString(R.string.setting)));
        //userSettingsList.add(new UserSettings(R.drawable.credit_card, getString(R.string.my_payment)));

        userSettingsList.add(new UserSettings(R.drawable.time, getString(R.string.ride_history)));
        userSettingsList.add(new UserSettings(R.drawable.profits, getString(R.string.earnings)));
        userSettingsList.add(new UserSettings(R.drawable.lock, getString(R.string.change_password_text)));
        //userSettingsList.add(new UserSettings(R.drawable.wallet, getResources().getString(R.string.wallet)));
        userSettingsList.add(new UserSettings(R.drawable.help_circle, getString(R.string.my_help)));
        //userSettingsList.add(new UserSettings(R.drawable.sale, getString(R.string.referral_title)));
        userSettingsList.add(new UserSettings(R.drawable.ic_sos, getString(R.string.panic_contact)));
        userSettingsList.add(new UserSettings(R.drawable.number_plate, getString(R.string.recargarSaldo)));
        userSettingsList.add(new UserSettings(R.drawable.ic_saldo, getString(R.string.saldo)));
        userSettingsList.add(new UserSettings(R.drawable.ic_historialrecarga, getString(R.string.historialRecarga)));
        userSettingsList.add(new UserSettings(R.drawable.folder_upload, getString(R.string.doc)));
        userSettingsList.add(new UserSettings(R.drawable.ic_power_off, getString(R.string.txt_logout)));
        return userSettingsList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        activity.closeDrawer();
        switch (position) {
            case 0:
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
                break;
            case 1:
                String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
                if (EasyPermissions.hasPermissions(getActivity(), perms)) {
                    if (activity.currentFragment.equals(Const.HOME_MAP_FRAGMENT)) {
                        Intent settingIntent = new Intent(activity, StatusAvailabilityActivity.class);
                        startActivity(settingIntent);
                    } else {
                        UiUtils.showShortToast(activity, getString(R.string.txt_trip_progress));
                    }
                } else {
                    EasyPermissions.requestPermissions(this, getString(R.string.needs_location_permission),
                            RC_LOCATION_PERM, Manifest.permission.ACCESS_FINE_LOCATION);
                }
                break;
            /*case 2:
                startActivity(new Intent(activity, PaymentsActivity.class));
                break;
            */
            case 2:
                Intent intent = new Intent(activity, HistoryActivity.class);
                startActivity(intent);
                break;
            case 3:
                Intent earnings = new Intent(activity, EarningsActivity.class);
                startActivity(earnings);
                break;
            case 4:
                Intent changePassIntent = new Intent(activity, ChangePasswordActivity.class);
                startActivity(changePassIntent);
                break;
            /*case 7:
                startActivity(new Intent(activity, WalletAcivity.class));
                break;*/
            case 5:
                Intent a = new Intent(activity, HelpwebActivity.class);
                startActivity(a);
                break;
            /*case 6:
                showrefferal();
                break;*/
            case 6:
                startActivity(new Intent(activity, PanicContactActivity.class));
                break;
            case 7:
                startActivity(new Intent(activity, RegistrarRecargaActivity.class));
                break;
            case 8:
                startActivity(new Intent(activity, SaldoActivity.class));
                break;
            case 9:
                startActivity(new Intent(activity, HistorialRecargaActivity.class));
                break;
            /*case 7:
                startActivity(new Intent(activity, PlanillaViajeActivity.class));
                break;
            case 8:
                startActivity(new Intent(activity, ListaPlanillaActivity.class));
                break;
*/
            case 10:
                Intent d = new Intent(activity, UploadDocActivity.class);
                startActivity(d);
                break;

            case 11:
                showLogoutDailog();
                break;
        }

    }

    private void showLogoutDailog() {
        final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim_leftright);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout_dialog);
        TextView btn_logout_yes = dialog.findViewById(R.id.btn_logout_yes);
        btn_logout_yes.setOnClickListener(view -> {
            dialog.dismiss();
            logout();
//            updateAvailabilityStatus("0");
        });
        TextView btn_logout_no = dialog.findViewById(R.id.btn_logout_no);
        btn_logout_no.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Glide.with(getActivity()).load(prefUtils.getStringValue(PrefKeys.PICTURE, "")).into(ivUserIcon);
    }


    private void logout() {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.logOutUser(
                prefUtils.getIntValue(PrefKeys.ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                JSONObject logoutResponse = null;
                try {
                    logoutResponse = new JSONObject(response.body());
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (logoutResponse != null)
                    if (logoutResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                        UiUtils.hideLoadingDialog();
                        UiUtils.showShortToast(activity, logoutResponse.optString(Params.MESSAGE));
                        prefUtils.setValue(PrefKeys.IS_LOGGED_IN, false);
                        PrefHelper.setUserLoggedOut(activity);
                        Const.SEND_POSITION_DRIVER = false;
                        Intent i = new Intent(activity, GetStartedActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        activity.finish();
                    } else {
                        UiUtils.showShortToast(activity, logoutResponse.optString(Params.ERROR_MESSAGE));
                        if (logoutResponse.optString(Params.ERROR_CODE).equals(APIConstants.ErrorCodes.INVALID_TOKEN)) {
                            UiUtils.showShortToast(getContext(), getString(R.string.you_have_logged_in_other_device));
                            startActivity(new Intent(activity, GetStartedActivity.class));
                            activity.finish();
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

    private void showrefferal() {
        getReferralCode();
        final Dialog refrel_dialog = new Dialog(activity, R.style.DialogThemeforview);
        refrel_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        refrel_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        refrel_dialog.setCancelable(true);
        refrel_dialog.setContentView(R.layout.refferalcode_layout);
        TextView refCode = refrel_dialog.findViewById(R.id.refCode);
        refCodeEarned = refrel_dialog.findViewById(R.id.txt_referl_earn);
        ImageView copyCode = refrel_dialog.findViewById(R.id.copyCode);
        refCode.setText(prefUtils.getStringValue(PrefKeys.REFERRAL_CODE, ""));
        (refrel_dialog.findViewById(R.id.referral_back)).setOnClickListener(view -> refrel_dialog.dismiss());
        (refrel_dialog.findViewById(R.id.twitter_share)).setOnClickListener(view -> refrel_dialog.dismiss());
        if (prefUtils.getStringValue(PrefKeys.REFERRAL_BONUS, "").isEmpty()) {
            ((TextView) refrel_dialog.findViewById(R.id.txt_referl_earn)).setText("00");
        } else {
            ((TextView) refrel_dialog.findViewById(R.id.txt_referl_earn)).setText(prefUtils.getStringValue(PrefKeys.REFERRAL_BONUS, ""));
        }
        (refrel_dialog.findViewById(R.id.gm_share)).setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });
        copyCode.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", refCode.getText().toString());
            clipboard.setPrimaryClip(clip);
            UiUtils.showShortToast(activity, getString(R.string.copiedCode));
        });
        (refrel_dialog.findViewById(R.id.fb_share)).setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.facebook.orca");
            try {
                startActivity(sendIntent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(activity, getString(R.string.facebook_messager), Toast.LENGTH_LONG).show();
            }
        });
        refrel_dialog.show();
    }

    protected void getReferralCode() {
        UiUtils.showLoadingDialog(activity);
        Call<String> call = apiInterface.getReferralCode(prefUtils.getIntValue(PrefKeys.PROVIDER_ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject referralCodeResponse = null;
                try {
                    referralCodeResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (referralCodeResponse != null) {
                    if (referralCodeResponse.optString(Const.Params.SUCCESS).equals(Constants.TRUE)) {
                        JSONObject data = referralCodeResponse.optJSONObject(Params.DATA);
                        refCodeEarned.setText(data.optString(Params.REFERRER_BONUS_FORMATTED));
                        shareMessage = data.optString(Params.REFERAL_SHARE_MESSAGE);
                    } else {
                        UiUtils.showShortToast(activity, referralCodeResponse.optString(Params.ERROR));
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
}
