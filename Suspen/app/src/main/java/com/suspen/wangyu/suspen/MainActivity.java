package com.suspen.wangyu.suspen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.frame.wangyu.retrofitframe.WTApplicationContextUtil;
import com.suspen.wangyu.util.AccessibilitySettingUtil;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        WTApplicationContextUtil.initContext(this);
        startService();
    }

    private void startService() {
        if (!AccessibilitySettingUtil.isAccessibilitySettingOn(MainActivity.this, WXService.class.getCanonicalName())) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, WXService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
