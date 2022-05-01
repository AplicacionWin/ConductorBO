package com.nikola.driver.ui.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.animation.keyframe.MaskKeyframeAnimation;
import com.bumptech.glide.Glide;
import com.nikola.driver.R;
import com.nikola.driver.network.model.Uploads;
import com.nikola.driver.network.newnetwork.APIClient;
import com.nikola.driver.network.newnetwork.APIConstants.Constants;
import com.nikola.driver.network.newnetwork.APIConstants.Params;
import com.nikola.driver.network.newnetwork.APIInterface;
import com.nikola.driver.network.newnetwork.NetworkUtils;
import com.nikola.driver.ui.activity.UploadDocActivity;
import com.nikola.driver.utils.Commonutils;
import com.nikola.driver.utils.newutils.UiUtils;
import com.nikola.driver.utils.newutils.sharedpref.PrefKeys;
import com.nikola.driver.utils.newutils.sharedpref.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nikola.driver.ui.activity.ProfileActivity.STORAGE_PERMISSION_REQUEST;

/**
 * Created by user on 1/26/2017.
 */

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.typesViewHolder> implements EasyPermissions.PermissionCallbacks {

    private static final int RC_LOCATION_PERM = 100 ;
    private UploadDocActivity mContext;
    private List<Uploads> itemsuploadList;
    private File cameraFile;
    private String filepath = "";
    private Uri uri = null;
    private String document_id = "";
    APIInterface apiInterface;
    PrefUtils prefUtils;
    ShowDocuments showDocuments;
    Bitmap photo = null;
    private static  final int RC_STORAGE_PERM = 125;

    public UploadAdapter(UploadDocActivity context, List<Uploads> itemsuploadList, ShowDocuments showDocuments) {
        this.mContext = context;
        this.itemsuploadList = itemsuploadList;
        this.showDocuments = showDocuments;
    }

    @Override
    public UploadAdapter.typesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.upload_item, null);
        UploadAdapter.typesViewHolder holder = new UploadAdapter.typesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final UploadAdapter.typesViewHolder holder, int position) {
        final Uploads upload_itme = itemsuploadList.get(position);
        if (upload_itme != null) {
            holder.tv_upload_name.setText(upload_itme.getUpload_name());
            Glide.with(mContext).load(upload_itme.getUpload_img())
                    .placeholder(R.drawable.docs)
                    .thumbnail(0.5f)
                    .into(holder.iv_upload);

            Glide.with(mContext)
                    .load(upload_itme.getUpload_img().equalsIgnoreCase("") ? upload_itme.getPreviewImage() : upload_itme.getUpload_img())
                    .placeholder(R.drawable.docs)
                    .thumbnail(0.5f)
                    .into(holder.iv_upload);
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            holder.itemView.startAnimation(animation);
        }
        holder.iv_upload.setOnClickListener(view -> {

            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(mContext)
                            .setIcon(R.mipmap.ic_launcher)
                            .setTitle(mContext.getString(R.string.need_storage_permission_title))
                            .setMessage(mContext.getString(R.string.need_storage_permission_desc))
                            .setPositiveButton(mContext.getString(R.string.grant), ((dialog, which)
                                    -> EasyPermissions.requestPermissions(mContext,mContext.getString(R.string.needs_multimedia_permission),
                                    RC_LOCATION_PERM, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)))
                            .setNegativeButton(mContext.getString(R.string.cancel), null)
                            .create().show();
            } else {
                if (!upload_itme.isDocumentUploaded())
                    showPictureDialog();
                else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.alreadyUploaded), Toast.LENGTH_SHORT).show();
                    viewUploadedDoc(upload_itme.getUpload_img());
                }
            }

            document_id = upload_itme.getUpload_id();
        });
    }


    @Override
    public int getItemCount() {
        return itemsuploadList.size();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(mContext, perms)) {
            new AppSettingsDialog.Builder(mContext).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mContext.onRequestPermissionsResult(requestCode,permissions,grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, mContext);
    }

    public class typesViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_upload;
        private TextView tv_upload_name;

        public typesViewHolder(View itemView) {
            super(itemView);
            iv_upload = itemView.findViewById(R.id.iv_upload);
            tv_upload_name = itemView.findViewById(R.id.tv_upload_name);

        }
    }

    private void showPictureDialog() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle(mContext.getResources().getString(R.string.txt_slct_option));
            String[] items = {mContext.getResources().getString(R.string.txt_gellery), mContext.getResources().getString(R.string.txt_cameray)};
            dialog.setItems(items, (dialog1, which) -> {
                // TODO Auto-generated method stub
                switch (which) {
                    case 0:
                        choosePhotoFromGallary();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            });
            dialog.show();
        } else {
            EasyPermissions.requestPermissions(mContext,mContext.getString(R.string.needs_location_permission),
                    RC_LOCATION_PERM, Manifest.permission.CAMERA);
        }
    }

    private void choosePhotoFromGallary() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            ((Activity) mContext).startActivityForResult(i, 201);
        } catch (Exception e) {
            e.printStackTrace();
            Commonutils.showtoast(mContext.getString(R.string.gallery_not_found), mContext);
        }
    }

    private void takePhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            EasyPermissions.requestPermissions(mContext,mContext.getString(R.string.needs_multimedia_permission),
                    RC_LOCATION_PERM, Manifest.permission.CAMERA);
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, STORAGE_PERMISSION_REQUEST);
        } else {
            try{
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                File photo = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), cal.getTimeInMillis() + ".jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                uri = Uri.fromFile(photo);
                mContext.startActivityForResult(intent, 101);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != mContext.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case 101:
                if (uri != null) {
                    filepath = getRealPathFromURI(uri);
                    photo = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(mContext, photo);

                    // CALL THIS METHOD TO GET THE ACTUAL PATH

//                    Bitmap compressedImageFile = Compressor.getDefault(mContext).compressToBitmap(new File(filepath));
//                    Uri uri2 = getImageUri(mContext, compressedImageFile);
//                    filepath = getRealPathFromURI(uri2);
                    uploadfile(document_id, tempUri);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.txt_img_error),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case 201:
                if (data != null) {
                    uri = data.getData();
                    if (uri != null) {
                        filepath = getRealPathFromURI(uri);
//                        Bitmap compressedImageFile = Compressor.getDefault(mContext).compressToBitmap(new File(filepath));
//                        Uri uri3 = getImageUri(mContext, compressedImageFile);
//                        filepath = getRealPathFromURI(uri3);
                        uploadfile(document_id, uri);
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.txt_img_error), Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void uploadfile(String document_id, Uri filepath) {
        apiInterface = APIClient.getClient().create(APIInterface.class);
        prefUtils = PrefUtils.getInstance(mContext);

        MultipartBody.Part multipartBody = null;
        if (filepath != null) {
            String path = getRealPathFromURI(filepath);
            File file = new File(path);
            // create RequestBody instance tempFrom file
            String mimeType = mContext.getContentResolver().getType(filepath);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse(mimeType == null ? "multipart/form-data" : mimeType),
                            file);
            // MultipartBody.Part is used to send also the actual file name
            multipartBody = MultipartBody.Part.createFormData(Params.DOCUMENT_URL, file.getName(), requestFile);
        }

        Call<String> call = apiInterface.uploadDoc(prefUtils.getIntValue(PrefKeys.ID, 0)
                , prefUtils.getStringValue(PrefKeys.SESSION_TOKEN, "")
                , multipartBody
                , document_id);
        UiUtils.showLoadingDialog(mContext);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null) {
                    try {
                        JSONObject uplaodResponse = new JSONObject(response.body());
                        if (uplaodResponse.optString(Params.SUCCESS).equals(Constants.TRUE)) {
                            UiUtils.hideLoadingDialog();
                            UiUtils.showShortToast(mContext, uplaodResponse.optString(Params.MESSAGE));
                            showDocuments.callDocumentsList();
                        } else {
                            UiUtils.hideLoadingDialog();
                            Commonutils.showtoast(mContext.getResources().getString(R.string.txt_upload_fail), mContext);
                        }
                    } catch (JSONException e) {
                        UiUtils.hideLoadingDialog();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (NetworkUtils.isNetworkConnected(mContext)) {
                    UiUtils.hideLoadingDialog();
                    UiUtils.showShortToast(mContext, mContext.getString(R.string.may_be_your_is_lost));
                }
            }
        });

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = mContext.getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public interface ShowDocuments {
        void callDocumentsList();
    }

    public void viewUploadedDoc(String imageUrl) {
        Dialog dialog = new Dialog(mContext, R.style.AppTheme);
        dialog.setContentView(R.layout.dialog_document_image);
        ImageView documentImage = dialog.findViewById(R.id.documentImage);
        ImageView back = dialog.findViewById(R.id.back);
        back.setOnClickListener(view -> dialog.cancel());
        Log.e("Image Url ", imageUrl);
        Glide.with(mContext).load(imageUrl)
                .placeholder(R.raw.place_holder_gif)
                .thumbnail(.4f)
                .into(documentImage);
        dialog.show();
    }

}
