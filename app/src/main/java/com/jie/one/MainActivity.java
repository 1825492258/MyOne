package com.jie.one;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FingerprintDialog mDialog;
    private FingerprintHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelper = new FingerprintHelper(this);
        findViewById(R.id.btnOne).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOne:
                setPermission();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0x99) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 已经获取指纹权限
                showFringerprintDialog();
                Log.i("jiejie","------------------");
            }
            // 已拒绝取指纹权限
        }
    }

    private void setPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.USE_FINGERPRINT}, 0x99);
            Log.i("jiejie","11111111111111");
        } else {
            Log.i("jiejie","2222222222");
            showFringerprintDialog();
        }
    }

    /**
     * 展示弹窗
     */
    private void showFringerprintDialog() {
        if (mHelper.isHardwareDetected()) {
            if (mHelper.hasEnrolledFingerprints()) {
                mDialog = null;
                mDialog = new FingerprintDialog(this);
                mDialog.show();
            } else {
                Toast.makeText(this, "您未录制任何指纹", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "您的手机不支持指纹识别", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /**
     * 判断是否有指定的权限
     */
    public boolean hasPermission(String... permissions) {
        for (String permisson : permissions) {
            if (ContextCompat.checkSelfPermission(this, permisson)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 申请指定的权限.
     */
    public void requestPermission(int code, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(permissions, code);
        }
    }
}
