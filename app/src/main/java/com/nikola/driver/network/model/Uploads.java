package com.nikola.driver.network.model;

/**
 * Created by user on 1/26/2017.
 */

public class Uploads {
    private String upload_id;
    private String upload_name;
    private String upload_img;
    private String previewImage;
    private boolean isDocumentUploaded;

    public boolean isDocumentUploaded() {
        return isDocumentUploaded;
    }

    public void setDocumentUploaded(boolean documentUploaded) {
        isDocumentUploaded = documentUploaded;
    }

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public String getUpload_id() {
        return upload_id;
    }

    public void setUpload_id(String upload_id) {
        this.upload_id = upload_id;
    }

    public String getUpload_name() {
        return upload_name;
    }

    public void setUpload_name(String upload_name) {
        this.upload_name = upload_name;
    }

    public String getUpload_img() {
        return upload_img;
    }

    public void setUpload_img(String upload_img) {
        this.upload_img = upload_img;
    }
}
