package com.suspen.wangyu.suspen;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.frame.wangyu.retrofitframe.WTApplicationContextUtil;
import com.frame.wangyu.retrofitframe.common.ProgressSubscriber;
import com.frame.wangyu.retrofitframe.common.SubscriberOnNextListener;
import com.frame.wangyu.retrofitframe.model.tulin.response.TuLingResponse;
import com.suspen.wangyu.AccessibilityUtil;

import java.util.Date;
import java.util.List;

import static com.frame.wangyu.retrofitframe.model.tulin.RetrofitModel.*;

/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/4/2
 * 微信版本 7.0.3
 */
//后退键
//performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//Home键
//performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
//模拟左滑
//performGlobalAction(AccessibilityService.GESTURE_SWIPE_LEFT);
public class ChatService extends AccessibilityService {
    private static final String TAG  = "聊天机器人";
    private String notifyText = "";
    private final String userName = "Anyin";
    private final String chatViewClass = "android.widget.RelativeLayout";
    private final String imageViewClass = "android.widget.ImageView";
    private final String listViewClass = "android.widget.ListView";

    private boolean notifySend = false;//只有通知栏消息才会自动回复
    private final  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sendChatData(msg.obj.toString());
        }
    };
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        Log.i(TAG,eventType+"");
        switch (eventType) {
            //监听到微信通知栏消息
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED://64
                notifySend = true;
                handleNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED://2048
                if(!notifySend)break;
                String className = event.getClassName().toString();
                if (className.equals(listViewClass)) {
                    Log.i(TAG,"聊天记录检索");
                    boolean isNeedSend = getLastRelativeImageView(event.getSource());
                    if(isNeedSend){
                        notifySend = false;
                        getTextByRobot(notifyText);
                    }
                }
                break;
            default:System.out.println(event.getClassName().toString());
        }
    }

    /**
     * 获取机器人回复消息
     *
     * @param notifyText
     * @return
     */
    private void getTextByRobot( String notifyText) {
        getInstance().aiTuLing(notifyText, new ProgressSubscriber(new SubscriberOnNextListener<TuLingResponse>() {
            public void onNext(TuLingResponse tuLingResponse) {
                Message message = Message.obtain();
                message.obj = tuLingResponse.text;
                mHandler.sendMessage(message);
            }

            public void onError(Throwable e) {
                Message message = Message.obtain();
                message.obj = "我现在不舒服，晚点再回你好吗";
                mHandler.sendMessage(message);
            }
        }, WTApplicationContextUtil.mContext));
    }

    /**
     * 自动发消息
     * @param responseText
     */
    private void sendChatData( String responseText) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> editNodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/amb");
        if(editNodeInfoList != null && editNodeInfoList.size() >0){
            AccessibilityUtil.getInstance().pastContent(this,editNodeInfoList.get(0),responseText);
            List<AccessibilityNodeInfo> buttonNodeInfoList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ami");
            if(buttonNodeInfoList != null && buttonNodeInfoList.size()>0){
                buttonNodeInfoList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            }
        }
    }

    /**
     * 最后消息是否为对面发出
     * @param nodeInfo
     * @return
     */
    private boolean getLastRelativeImageView(AccessibilityNodeInfo nodeInfo) {
        try {
            int listCount = nodeInfo.getChildCount();
            for (int i = listCount - 1; i < listCount; i--) {
                AccessibilityNodeInfo accessibilityNodeInfo = nodeInfo.getChild(i);
                if (accessibilityNodeInfo != null &&
                        chatViewClass.equals(accessibilityNodeInfo.getClassName())) {
                    int chatViewCount = accessibilityNodeInfo.getChildCount();
                    for (int j = 0; j < chatViewCount; j++) {
                        if (accessibilityNodeInfo.getChild(j) != null &&
                                imageViewClass.equals(accessibilityNodeInfo.getChild(j).getClassName()) &&
                                (userName + "头像").equals(accessibilityNodeInfo.getChild(j).getContentDescription())) {
                            return true;
                        }

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
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
                if (content.startsWith(userName+":")) {
                    if (event.getParcelableData() != null && event.getParcelableData() instanceof Notification) {
                        Notification notification = (Notification) event.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            notifyText = content.substring(userName.length()+1,content.length());
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onInterrupt() {
        Toast.makeText(this, "自动聊天机器人已关闭", Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, "自动聊天机器人已开启", Toast.LENGTH_SHORT)
                .show();
    }

}
