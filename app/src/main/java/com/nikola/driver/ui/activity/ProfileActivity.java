package com.nikola.driver.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hbb20.CountryCodePicker;
import com.nikola.driver.R;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.utils.customText.CustomBoldEditView;
import com.nikola.driver.utils.customText.CustomBoldRegularTextView;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefHelper;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by user on 1/7/2017.
 */

public class ProfileActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final int PICK_IMAGE = 100;
    public static final int STORAGE_PERMISSION_REQUEST = 1000;
    @BindView(R.id.profile_back)
    ImageButton profileBack;
    @BindView(R.id.btn_edit_profile)
    CustomBoldRegularTextView btnEditProfile;
    @BindView(R.id.toolbar_profile)
    Toolbar toolbarProfile;
    @BindView(R.id.actionbar_lay)
    RelativeLayout actionbarLay;
    @BindView(R.id.profile_image)
    ImageView profileImage;
    @BindView(R.id.profile_img_lay)
    RelativeLayout profileImgLay;
    @BindView(R.id.et_firstname)
    CustomBoldEditView etFirstname;
    @BindView(R.id.et_lastname)
    CustomBoldEditView etLastname;
    @BindView(R.id.lay_name)
    LinearLayout layName;
    @BindView(R.id.etFirstName)
    CustomBoldEditView etFirstName;
    @BindView(R.id.etLastName)
    CustomBoldEditView etLastName;
    @BindView(R.id.et_profile_email)
    CustomBoldEditView etProfileEmail;
    @BindView(R.id.et_profile_mobile)
    CustomBoldEditView etProfileMobile;
    @BindView(R.id.radio_btn_male)
    RadioButton radioBtnMale;
    @BindView(R.id.radio_btn_female)
    RadioButton radioBtnFemale;
    @BindView(R.id.radio_btn_others)
    RadioButton radioBtnOthers;
    @BindView(R.id.profile_radioGroup)
    RadioGroup profileRadioGroup;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;

    private String filePath = "";
    private File cameraFile;
    private Uri uri = null;
    private RadioButton rd_btn;
    PrefUtils prefUtils;
    APIInterface apiInterface;
    private Uri fileToUpload = null;
    boolean isEditMode;
    private int RC_STORAGE_PERM = 125;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_profile);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(getApplicationContext());
        ButterKnife.bind(this);
        etProfileMobile.setEnabled(false);
        enableAndDisableView(false);
        getProfile();
        etFirstName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        etLastName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        if (prefUtils.getStringValue(PrefKeys.COUNTRY_CODE, "") != null && !prefUtils.getStringValue(PrefKeys.COUNTRY_CODE, "").equalsIgnoreCase("")) {
            ccp.setCountryForPhoneCode(Integer.parseInt(prefUtils.getStringValue(PrefKeys.COUNTRY_CODE, "")));
        }
    }

    private class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        }
    }

    protected void getProfile() {
        UiUtils.showLoadingDialog(ProfileActivity.this);
        Call<String> call = apiInterface.getProfile(prefUtils.getIntValue(PrefKeys.ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject profileResponse = null;
                try {
                    profileResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (profileResponse != null) {
                    if (profileResponse.optString(APIConstants.Params.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        JSONObject data = profileResponse.optJSONObject(APIConstants.Params.DATA);
                        etProfileEmail.setText("");
                        etProfileEmail.append(data.optString(APIConstants.Params.EMAIL));
                        etFirstName.setText("");
                        etFirstName.append(data.optString(APIConstants.Params.FIRSTNAME));
                        etLastName.setText("");
                        etLastName.append(data.optString(APIConstants.Params.LAST_NAME));
                        etProfileMobile.setText("");
                        etProfileMobile.append(data.optString(APIConstants.Params.MOBILE));
                        Glide.with(ProfileActivity.this).load(data.optString(APIConstants.Params.PICTURE)).into(profileImage);
                        if (data.optString(APIConstants.Params.GENDER).equalsIgnoreCase(getString(R.string.txt_male_rb))) {
                            radioBtnMale.setChecked(true);
                        } else {
                            radioBtnFemale.setChecked(true);
                        }
                    } else {
                        UiUtils.showShortToast(ProfileActivity.this, profileResponse.optString(APIConstants.Params.ERROR));
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

    private void setUpProfileData() {
        etProfileEmail.setText("");
        etProfileEmail.append(prefUtils.getStringValue(PrefKeys.EMAIL, ""));
        etFirstName.setText("");
        etFirstName.append(prefUtils.getStringValue(PrefKeys.FIRSTNAME, ""));
        etLastName.setText("");
        etLastName.append(prefUtils.getStringValue(PrefKeys.LAST_NAME, ""));
        etProfileMobile.setText("");
        Log.e("Profile ",prefUtils.getStringValue(PrefKeys.PHONE, "") );
        etProfileMobile.append(prefUtils.getStringValue(PrefKeys.PHONE, ""));
        Glide.with(ProfileActivity.this)
                .load(prefUtils.getStringValue(PrefKeys.PICTURE, ""))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(.1f).into(profileImage);
        if (prefUtils.getStringValue(PrefKeys.GENDER, "").equalsIgnoreCase(getString(R.string.txt_male_rb))) {
            radioBtnMale.setChecked(true);
        } else if (prefUtils.getStringValue(PrefKeys.GENDER, "").equalsIgnoreCase(getString(R.string.others))) {
            radioBtnOthers.setChecked(true);
        } else {
            radioBtnFemale.setChecked(true);
        }
    }

    private void enableAndDisableView(boolean toggle) {
        profileImage.setEnabled(toggle);
        etFirstName.setEnabled(toggle);
        etLastName.setEnabled(toggle);
        etProfileMobile.setEnabled(toggle);
        etProfileEmail.setEnabled(toggle);
        radioBtnFemale.setEnabled(toggle);
        radioBtnMale.setEnabled(toggle);
        radioBtnOthers.setEnabled(toggle);
        ccp.setEnabled(toggle);
    }

    protected void updateUserProfile() {
        UiUtils.showLoadingDialog(ProfileActivity.this);
        MultipartBody.Part multipartBody = null;
        if (fileToUpload != null) {
            String path = getRealPathFromURIPath(fileToUpload, this);
            File file = new File(path);
            // create RequestBody instance tempFrom file
            String mimeType = getContentResolver().getType(fileToUpload);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse(mimeType == null ? "multipart/form-data" : mimeType),
                            file);
            // MultipartBody.Part is used to send also the actual file name
            multipartBody =
                    MultipartBody.Part.createFormData(APIConstants.Params.PICTURE, file.getName(), requestFile);
        }

        String gender = null;

        if (radioBtnFemale.isChecked()) {
            gender = "female";
        } else if (radioBtnMale.isChecked()) {
            gender = "male";
        } else {
            gender = "others";
        }

        String countryCode = ccp.getSelectedCountryCodeWithPlus();
        Call<String> call = apiInterface.updateUserProfile(
                prefUtils.getIntValue(PrefKeys.ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , etFirstName.getText().toString()
                , etLastName.getText().toString()
                , etProfileEmail.getText().toString()
                , multipartBody
                , APIConstants.Constants.ANDRIOD
                , prefUtils.getStringValue(PrefKeys.FCM_TOKEN, "")
                , APIConstants.Constants.MANUAL_LOGIN
                , etProfileMobile.getText().toString()
                , TimeZone.getDefault().getID()
                , gender
                , countryCode);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                UiUtils.hideLoadingDialog();
                JSONObject updateProfileResponse = null;
                try {
                    updateProfileResponse = new JSONObject(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (updateProfileResponse != null) {
                    if (updateProfileResponse.optString(APIConstants.Constants.SUCCESS).equals(APIConstants.Constants.TRUE)) {
                        JSONObject data = updateProfileResponse.optJSONObject(APIConstants.Params.DATA);
                        UiUtils.showShortToast(ProfileActivity.this, updateProfileResponse.optString(APIConstants.Params.MESSAGE));
                        updateUserInDevice(data);
                        enableAndDisableView(false);
                        btnEditProfile.setText(getString(R.string.btn_edit));
                        isEditMode = false;
                    } else {
                        UiUtils.showShortToast(getApplicationContext(), updateProfileResponse.optString(APIConstants.Params.ERROR));
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                String throwback = t.toString();
                if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                    UiUtils.showShortToast(getApplicationContext(), getString(R.string.may_be_your_is_lost));
                }
            }
        });
    }

    private void callImagePicker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            EasyPermissions.requestPermissions(this, getString(R.string.need_storage_permission_desc),
                    RC_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        } else {
            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, PICK_IMAGE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            fileToUpload = data.getData();
            Glide.with(this)
                    .load(fileToUpload)
                    .into(profileImage);
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    public void updateUserInDevice(JSONObject data) {
        PrefHelper.setUserLoggedIn(this, data.optInt(APIConstants.Params.PROVIDER_ID)
                , data.optString(APIConstants.Params.NAME)
                , data.optString(APIConstants.Params.FIRSTNAME)
                , data.optString(APIConstants.Params.LAST_NAME)
                , data.optString(APIConstants.Params.EMAIL)
                , data.optString(APIConstants.Params.PICTURE)
                , data.optString(APIConstants.Params.MOBILE)
                , data.optString(APIConstants.Params.DESCRIPTION)
                , data.optString(APIConstants.Params.TOKEN)
                , "manual"
                , data.optInt(APIConstants.Params.PROVIDER_STATUS)
                , data.optString(APIConstants.Params.TIME_ZONE)
                , data.optString(APIConstants.Params.COUNTRY)
                , data.optInt(APIConstants.Params.CURRENCEY)
                , data.optString(APIConstants.Params.PLATE_NUMBER)
                , data.optString(APIConstants.Params.COLOR)
                , data.optString(APIConstants.Params.GENDER)
        );
        prefUtils.setValue(PrefKeys.COUNTRY_CODE, data.optString(APIConstants.Params.COUNTRY_CODE));
        prefUtils.setValue(PrefKeys.PICTURE, data.optString(APIConstants.Params.PICTURE));
        setUpProfileData();

    }

    @Override
    public void onBackPressed() {
        UiUtils.hideKeyboard(ProfileActivity.this);
        if (isEditMode) {
            enableAndDisableView(isEditMode);
            btnEditProfile.setText(getString(R.string.btn_edit));
            isEditMode = false;
        } else {
            super.onBackPressed();
        }
    }

    @OnClick({R.id.profile_back, R.id.btn_edit_profile, R.id.profile_image})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profile_back:
                onBackPressed();
                break;
            case R.id.btn_edit_profile:
                if (btnEditProfile.getText().toString().equals(getString(R.string.btn_edit))) {
                    enableAndDisableView(true);
                    btnEditProfile.setText(getString(R.string.btn_save));
                    isEditMode = true;
                } else {
                    enableAndDisableView(false);
                    btnEditProfile.setText(getString(R.string.btn_edit));
                    updateUserProfile();
                    isEditMode = false;
                }
                break;
            case R.id.profile_image:
                callImagePicker();
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        startActivityForResult(openGalleryIntent, PICK_IMAGE);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
