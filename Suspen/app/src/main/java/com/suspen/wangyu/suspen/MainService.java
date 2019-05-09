package com.suspen.wangyu.suspen;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.suspen.wangyu.constants.Constants;
import com.suspen.wangyu.model.SizeEntity;
import com.suspen.wangyu.util.AutoTouch;
import com.suspen.wangyu.util.LogoSuspend;

/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/4/2
 */
public class MainService extends Service {
    private LogoSuspend logoSuspend;

    private AutoTouch autoTouch;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (logoSuspend == null) {
            logoSuspend = new LogoSuspend(this);
        }
        if(autoTouch == null){
            autoTouch = new AutoTouch();
        }
        Constants.sizeEntity = new SizeEntity();
        Constants.sizeEntity.setHeight(100);
        Constants.sizeEntity.setWidth(100);
        logoSuspend.showSuspend(Constants.sizeEntity, false);//从缓存中提取上一次显示的位置
        logoSuspend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //处理单击事件
                System.out.print(Constants.sizeEntity.getWidth()+":"+Constants.sizeEntity.getHeight());
//                autoTouch.autoClickPos(logoSuspend.height,logoSuspend.width,100,100);
                autoTouch.autoClickPos(logoSuspend.height,logoSuspend.width,Constants.sizeEntity.getWidth(),Constants.sizeEntity.getHeight()-100);
            }
        });
    }
}
