
package com.example.highfreqlock;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class HighFreqLock extends Activity {
    private volatile boolean running = true;
    private WindowManager windowManager;
    private ImageView floatingFan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
            return;
        }

        floatingFan = new ImageView(this);
        floatingFan.setImageResource(android.R.drawable.presence_online);

        RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        floatingFan.startAnimation(rotate);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.END;
        params.x = 50;
        params.y = 50;
        windowManager.addView(floatingFan, params);

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            new Thread(() -> {
                while (running) {
                    double x = Math.sin(System.nanoTime());
                }
            }).start();
        }

        Handler handler = new Handler();
        Runnable refresher = new Runnable() {
            public void run() {
                if (!running) return;
                for (int i = 0; i < 4; i++) {
                    new Thread(() -> {
                        for (int j = 0; j < 1000000; j++) {
                            double x = Math.tan(System.nanoTime());
                        }
                    }).start();
                }
                handler.postDelayed(this, 10 * 60 * 1000);
            }
        };
        handler.post(refresher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        if (floatingFan != null) {
            windowManager.removeView(floatingFan);
        }
    }
}
