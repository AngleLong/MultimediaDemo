package com.angle.multimediademo.Utils;

import android.hardware.Camera;

import java.util.List;

public class CameraUtils {

    /**
     * 判断是否支持某个相机
     *
     * @param faceOrBack 前置还是后置
     * @return 传入相应的相机参数
     */
    public static boolean isSupport(int faceOrBack) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            //返回相机信息
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == faceOrBack) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断是否支持对焦模式
     *
     * @return 是否支持对焦模式
     */
    public static boolean isSupportFocus(Camera.Parameters parameters, String focusMode) {
        boolean isSupport = false;
        //获取所支持对焦模式
        List<String> listFocus = parameters.getSupportedFocusModes();
        for (String s : listFocus) {
            //如果存在 返回true
            if (s.equals(focusMode)) {
                isSupport = true;
            }

        }
        return isSupport;
    }
}
