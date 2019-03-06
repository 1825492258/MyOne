package com.jie.one;

import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

public class FingerprintHelper {
    private FingerprintManagerCompat fingerprintManager;
    private CancellationSignal mCancellationSignal = null;

    public FingerprintHelper(Context ctx) {
        fingerprintManager = FingerprintManagerCompat.from(ctx);
    }

    /**
     * 确定是否至少有一个指纹登记过
     *
     * @return 如果至少有一个指纹登记，则为true，否则为false
     */
    public boolean hasEnrolledFingerprints() {
        try {
            // 有些厂商api23之前的版本可能没有做好兼容，这个方法内部会崩溃（redmi note2, redmi note3等）
            return fingerprintManager.hasEnrolledFingerprints();
        } catch (SecurityException e) {
            return false;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 确定指纹硬件是否存在并且功能正常。
     *
     * @return 如果硬件存在且功能正确，则为true，否则为false。
     */
    public boolean isHardwareDetected() {
        try {
            return fingerprintManager.isHardwareDetected();
        } catch (SecurityException e) {
            return false;
        } catch (Throwable e) {
            return false;
        }
    }

    public void anthenticate(FingerprintManagerCompat.AuthenticationCallback callback) {
        mCancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(null, 0, mCancellationSignal, callback, null);
    }

    public void cancel() {
        if (mCancellationSignal != null)
            mCancellationSignal.cancel();
    }
}
