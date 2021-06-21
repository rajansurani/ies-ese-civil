package com.upscrks.iesesecivil.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.ads.AdView;
import com.upscrks.iesesecivil.Application.Helper;
import com.upscrks.iesesecivil.R;
import com.upscrks.iesesecivil.Utils.AdsUtils;
import com.upscrks.iesesecivil.Utils.FilesUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PDFViewerActivity extends BaseActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;

    @BindView(R.id.heading)
    TextView tvHeading;

    @BindView(R.id.pdfViewer)
    PDFView mPDFView;

    ProgressDialog mProgressDialog;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        ButterKnife.bind(this);
        tvHeading.setText(getIntent().getStringExtra("title"));

        if (!Helper.checkPermission(this)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            loadPDF();
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Hold Tight!! Opening PDF...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (!(isDestroyed() || isFinishing()))
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
    }

    private void loadPDF() {
        if (FilesUtils.isPdfExists(getIntent().getStringExtra("title"), this)) {
            loadPDFFromStorage();
        } else {
            downloadPDF();
        }
    }

    private void downloadPDF() {
        showProgressDialog();
        mDataAccess.getNotesPdf(getIntent().getStringExtra("pdfUrl"), pdf -> {
            String filePath = FilesUtils.savePdf(getIntent().getStringExtra("title"), pdf, this);
            if (filePath != null) {
                Toast.makeText(this, "File Saved", Toast.LENGTH_SHORT).show();
                loadPDFFromStorage();
            }
            hideProgressDialog();
        });
    }

    private void loadPDFFromStorage() {
        mPDFView.fromUri(FilesUtils.getFile(getIntent().getStringExtra("title"), this)).load();
        displayAd();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
                loadPDF();
            } else {
                Toast.makeText(this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @OnClick(R.id.back)
    public void OnClickBack() {
        finish();
    }

    private void displayAd() {
        AdsUtils.loadBannerAd(PDFViewerActivity.this);
    }
}