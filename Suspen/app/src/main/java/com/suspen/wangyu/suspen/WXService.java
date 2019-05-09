package com.suspen.wangyu.suspen;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/4/2
 * 微信版本 7.0.3
 *
 */

//后退键
//performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//Home键
//performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
//模拟左滑
//performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT);
public class WXService extends AccessibilityService {
    private List<AccessibilityNodeInfo> parentsList = new ArrayList<>();
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String className2 = event.getClassName().toString();
        System.out.println(eventType);
        System.out.println(className2);
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED://64
                handleNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED://32
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED://2048
                String className = event.getClassName().toString();
                if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                    System.out.println("点击红包");
                    getPacket();
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")||
                        className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI")) {
                    System.out.println("开红包");
                    openPacket(event.getSource(),"com.tencent.mm:id/cyf");
                } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                    System.out.println("退出红包");
                    openPacket(event.getSource(),"com.tencent.mm:id/ka");
                }else{
                    openPacket(event.getSource(),"com.tencent.mm:id/cyf");
                }

                break;
            default:System.out.println(event.getClassName().toString());
        }
    }

    /**
     * 处理通知栏信息
     *
     * 如果是微信红包的提示信息,则模拟点击
     *
     * @param event
     */
    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                //如果微信红包的提示信息,则模拟点击进入相应的聊天窗口
                if (content.contains("[微信红包]")) {
                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                        Notification notification = (Notification) event.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    /**
     * 模拟点击,拆开红包
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void openPacket(AccessibilityNodeInfo node,String id) {
        AccessibilityNodeInfo nodeInfo = node;
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(id);
//            if(list.size() == 0){
//                performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
//                startActivityForPackage(getApplicationContext(),"com.tencent.mm");
//            }
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    /**
     * @param
     * @描述 通过包名启动其他应用，假如应用已经启动了在后台运行，则会将应用切到前台
     * @作者 tll
     * @时间 2017/2/7 17:40
     */
    public static void startActivityForPackage(Context context, String packName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packName);
        context.startActivity(intent);
    }
    /**
     * 模拟点击,打开抢红包界面
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getPacket() {
//        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
//        parentsList.clear();
//        recycle(rootNode,"微信红包","已领取");
////        node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
////        AccessibilityNodeInfo parent = node.getParent();
//        for (AccessibilityNodeInfo parent : parentsList) {
//            while (parent != null) {
//                if (parent.isClickable()) {
//                    parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    break;
//                }
//                parent = parent.getParent();
//            }
//        }


        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        List<AccessibilityNodeInfo> redBagList = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aq5");
        for(AccessibilityNodeInfo redBag : redBagList){
            if(redBag.getParent()!=null
                    &&redBag.getParent().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aq6").size() == 0){
                while (redBag != null) {
                    if (redBag.isClickable()) {
                        redBag.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        break;
                    }
                    redBag = redBag.getParent();
                }
            }
        }
    }

    /**
     * 递归查找当前聊天窗口中的红包信息
     *
     * 聊天窗口中的红包都存在"领取红包"一词,因此可根据该词查找红包
     *
     * @param node
     */
    public void recycle(AccessibilityNodeInfo node,String text,String textSkip) {
        if (node.getChildCount() == 0) {
            if (node.getText() != null) {
                if (text.equals(node.getText().toString())) {
                    parentsList.add(node);
                }
                if(textSkip!= null &&!textSkip.trim().equals("")&&textSkip.equals(node.getText().toString())){
                    parentsList.remove(node);
                    return;
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (node.getChild(i) != null) {
                    recycle(node.getChild(i),text,textSkip);
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

}
