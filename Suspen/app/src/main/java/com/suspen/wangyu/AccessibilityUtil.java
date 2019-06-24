package com.suspen.wangyu;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.frame.wangyu.retrofitframe.model.tulin.RetrofitModel;


public class AccessibilityUtil {
    private static class SingletonInstance {
        private static final AccessibilityUtil INSTANCE = new AccessibilityUtil();

        private SingletonInstance() {
        }
    }

    private AccessibilityUtil() {
    }

    public static AccessibilityUtil getInstance() {
        return AccessibilityUtil.SingletonInstance.INSTANCE;
    }

    /**
     * 将内容粘贴到对应的控件上
     * @param accessibilityService
     * @param nodeInfo
     * @param content
     */
    public  void pastContent(AccessibilityService accessibilityService, AccessibilityNodeInfo nodeInfo, String content) {
        Bundle arguments = new Bundle();
        arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
        arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN, true);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, arguments);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        ClipData clip = ClipData.newPlainText("label", content);
        ClipboardManager clipboardManager = (ClipboardManager) accessibilityService.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(clip);
        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
    }
}
