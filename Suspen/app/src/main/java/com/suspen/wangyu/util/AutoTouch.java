package com.suspen.wangyu.util;

import android.app.Activity;

import java.io.IOException;

/**
 * @Author wangyu
 * @Description: Copyright yiYuan Networks 上海义援网络科技有限公司. All rights reserved.
 * @Date 2019/4/2
 */
public class AutoTouch {
    public int width = 0;
    public int height = 0;

    /**
     * 传入在屏幕中的比例位置，坐标左上角为基准
     * @param ratioX 需要点击的x坐标在屏幕中的比例位置
     * @param ratioY 需要点击的y坐标在屏幕中的比例位置
     */
    public void autoClickRatio(int height2,int width2, final double ratioX, final double ratioY) {
        this.width = width2;
        this.height = height2;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 线程睡眠0.3s
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 生成点击坐标
                int x = (int) (width * ratioX);
                int y = (int) (height * ratioY);

                // 利用ProcessBuilder执行shell命令
                String[] order = { "input", "tap", "" + x, "" + y };
                try {
                    new ProcessBuilder(order).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 传入在屏幕中的坐标，坐标左上角为基准
     * @param x 需要点击的x坐标
     * @param y 需要点击的x坐标
     */
    public void autoClickPos(int height2,int width2, final double x, final double y) {
        this.width = width2;
        this.height = height2;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 线程睡眠0.3s
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 利用ProcessBuilder执行shell命令
                String[] order = { "input", "tap", "" + x, "" + y };
                try {
                    new ProcessBuilder(order).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}