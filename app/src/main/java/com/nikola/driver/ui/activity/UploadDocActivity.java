package com.nikola.driver.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikola.driver.R;
import com.nikola.driver.network.model.Uploads;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.adapter.UploadAdapter;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.storage.StorageManager.ACTION_MANAGE_STORAGE;

/**
 * Created by user on 1/26/2017.
 */

public class UploadDocActivity extends AppCompatActivity implements UploadAdapter.ShowDocuments , EasyPermissions.PermissionCallbacks {
    @BindView(R.id.upload_lv)
    RecyclerView uploadLv;
    @BindView(R.id.upload_progress_bar)
    ProgressBar uploadProgressBar;
    private ArrayList<Uploads> uploadlst = new ArrayList<>();
    private UploadAdapter uploadAdapter;
    APIInterface apiInterface;
    PrefUtils prefUtils;
    @BindView(R.id.noData)
    TextView noData;
    private static final int RC_LOCATION_PERM = 100 ;
    private static final long NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 10L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_documents);
        ButterKnife.bind(this);
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(this);
        getPermssion();
        setResult(RESULT_OK);
        getDocumentsList();
        setUploadAdapter();

    }

    private void getPermssion() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            EasyPermissions.requestPermissions(this,this.getString(R.string.needs_multimedia_permission),
                    RC_LOCATION_PERM, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA);
        }

    }

    private void setUploadAdapter() {
        uploadAdapter = new UploadAdapter(UploadDocActivity.this, uploadlst, this);
        uploadLv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        uploadLv.setAdapter(uploadAdapter);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, getResources().getIdentifier("layout_animation_from_left", "anim", getPackageName()));
        uploadLv.setLayoutAnimation(animation);
        uploadLv.scheduleLayoutAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != uploadAdapter) {
            uploadAdapter.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getPermssion();
    }

    private void getDocumentsList() {
        Call<String> call = apiInterface.getDoc(prefUtils.getIntValue(PrefKeys.ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, ""));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    try {
                        JSONObject job = new JSONObject(response.body());
                        if (job.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                            uploadlst.clear();
                            uploadProgressBar.setVisibility(View.GONE);
                            JSONArray jarray = job.getJSONArray(Params.DATA);
                            if (jarray.length() > 0) {
                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject docjob = jarray.getJSONObject(i);
                                    Uploads upload = new Uploads();
                                    upload.setUpload_id(docjob.getString(Params.DOCUMENT_ID));
                                    upload.setUpload_name(docjob.getString(Params.DOCUMENT_NAME));
                                    upload.setUpload_img(docjob.getString(Params.DOCUMENT_URL));
                                    upload.setPreviewImage(docjob.getString(Params.PREVIEW));
                                    upload.setDocumentUploaded(docjob.getInt(Params.IS_DOCUMENT_UPLOADED) == 1);
                                    uploadlst.add(upload);
                                }
                                setUploadAdapter();
                                if (uploadlst.isEmpty()) {
                                    noData.setVisibility(View.VISIBLE);
                                    uploadLv.setVisibility(View.GONE);
                                } else {
                                    noData.setVisibility(View.GONE);
                                    uploadLv.setVisibility(View.VISIBLE);
                                }
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

    @OnClick(R.id.upload_back)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void callDocumentsList() {
        getDocumentsList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }
}
