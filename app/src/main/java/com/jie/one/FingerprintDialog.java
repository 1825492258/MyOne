package com.jie.one;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FingerprintDialog extends Dialog {

    private Vibrator mVibrator;
    private ImageView fingerImageView;
    private TextView hintTv;
    private TextView btnCancel;
    private FingerprintHelper mFingerprintHelper;
    private Context context;
    // private DisplayMetrics dm; // 屏幕分辨率

    public FingerprintDialog(Context context) {
        super(context);
        this.context = context;
        //     dm = context.getResources().getDisplayMetrics();
        mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        mFingerprintHelper = new FingerprintHelper(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fingerprint);
        btnCancel = findViewById(R.id.btn_cancel);
        hintTv = findViewById(R.id.hint_tv);
        fingerImageView = findViewById(R.id.fingerprint_image);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        startAnthenticate();
        Window window = this.getWindow();
        assert window != null;
        {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }


    private void startAnthenticate() {
        hintTv.setText("尝试指纹识别");
        fingerImageView.setImageResource(R.mipmap.ic_finger_normal);
        mFingerprintHelper.anthenticate(new FingerprintManagerCompat.AuthenticationCallback() {

            //在识别指纹成功时调用。
            @Override
            public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                Log.i("jiejie", "指纹成功----" + result.toString());
                startVibrate();
                hintTv.setText("指纹识别成功");
                fingerImageView.setImageResource(R.mipmap.ic_finger_success);
                hintTv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                hintTv.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 1000);
            }

            //当遇到不可恢复的错误并且操作完成时调用。
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
                super.onAuthenticationError(errMsgId, errString);
                Log.i("jiejie", "指纹异常---" + errMsgId + "  " + errString);
                hintTv.setText(errString);
                if (errMsgId != 5) {
                    startVibrate();
                }
                if (errMsgId == 7) { // 指纹异常，尝试次数过多，请稍后重试
                    Toast.makeText(context,errString,Toast.LENGTH_SHORT).show();
                    hintTv.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }
            }

            //在认证期间遇到可恢复的错误时调用。
            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                super.onAuthenticationHelp(helpMsgId, helpString);
                Log.i("jiejie", "认证错误---" + helpMsgId + "   " + helpString);
                hintTv.setText(helpString);
                startVibrate();
            }

            //当指纹有效但未被识别时调用。
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.i("jiejie", "指纹识别失败");
                startVibrate();
                hintTv.setText("识别失败，请重试");
            }
        });
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mFingerprintHelper.cancel();
    }

    private void startVibrate() {
        mVibrator.vibrate(500);
    }
}
