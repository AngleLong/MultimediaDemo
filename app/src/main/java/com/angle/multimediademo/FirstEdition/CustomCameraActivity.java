package com.angle.multimediademo.FirstEdition;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.angle.multimediademo.Utils.CameraUtils;
import com.angle.multimediademo.Utils.FileUtils;
import com.angle.multimediademo.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.IOException;
import java.util.List;

/**
 * 自定义相机
 * https://juejin.im/entry/5bcec29cf265da0a96250da5
 */
public class CustomCameraActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, View.OnClickListener {

    private static final String TAG = CustomCameraActivity.class.getSimpleName();

    private TextureView mTextureView;
    private Camera mCamera;
    private int mScreenOrientation;
    private String mPathName = "video.mp4";

    /**
     * 传感器正常方向
     */
    private static final int SENSOR_ORIENTATION_DEFAULT_DEGREES = 90;
    /**
     * 传感器反方向
     */
    private static final int SENSOR_ORIENTATION_INVERSE_DEGREES = 270;

    /**
     * 默认的校正度数
     */
    private static final SparseIntArray DEFAULT_ORIENTATIONS = new SparseIntArray();
    /**
     * 反向的矫正度数
     */
    private static final SparseIntArray INVERSE_ORIENTATIONS = new SparseIntArray();

    static {
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_0, 90);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_90, 0);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_180, 270);
        DEFAULT_ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    static {
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_0, 270);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_90, 180);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_180, 90);
        INVERSE_ORIENTATIONS.append(Surface.ROTATION_270, 0);
    }


    private SurfaceTexture mSurface;
    private MediaRecorder mMediaRecorder;
    private Camera.Size mVideoSize;
    private Button mStartVideo;
    private Button mStopVideo;
    private RxPermissions mRxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera);

        mTextureView = findViewById(R.id.textureView);

        mStartVideo = findViewById(R.id.startVideo);
        mStopVideo = findViewById(R.id.stopVideo);

        initListener();
    }

    private void initListener() {
        mRxPermissions = new RxPermissions(this);
        mStartVideo.setOnClickListener(this);
        mStopVideo.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTextureView.isAvailable()) {
            // TODO: 2019-10-22 这里打开相机
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            //设置监听
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    /**
     * 开启Camera并设置一些参数
     *
     * @param cameraDirection 相机方向
     */
    public void openCamera(int cameraDirection) {

        //首先应该释放相机资源,因为这里可能有别的程序在使用相机资源
        releaseCamera();
        Log.e(TAG, "释放相机资源");

        if (!CameraUtils.isSupport(cameraDirection)) {
            Log.e(TAG, "不支持的摄像头");
        }

        mCamera = Camera.open(cameraDirection);
        Log.e(TAG, "开启相机");

        //获取屏幕方向
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraDirection, cameraInfo);
        mScreenOrientation = cameraInfo.orientation;

        //设置相机参数
        Camera.Parameters parameters = mCamera.getParameters();

        logPreviewSize(parameters.getSupportedPreviewSizes());

        //获取视频的size
        mVideoSize = parameters.getPreferredPreviewSizeForVideo();
        Log.e(TAG, "视频首选尺寸====>宽:" + mVideoSize.width + "高:" + mVideoSize.height);

        //是否支持单次对焦
        boolean autoFocus = CameraUtils.isSupportFocus(parameters, Camera.Parameters.FOCUS_MODE_AUTO);
        if (autoFocus) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }

        //是否支持多次对焦
        boolean continuousFocus = CameraUtils.isSupportFocus(parameters, Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (continuousFocus) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //设置预览尺寸
        parameters.setPreviewSize(mVideoSize.width, mVideoSize.height);

        //设置Parameters
        mCamera.setParameters(parameters);

        //获取屏幕方向,并设定视频方向
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        mCamera.setDisplayOrientation(DEFAULT_ORIENTATIONS.get(rotation));

        openPreview();
    }

    /**
     * 开始预览
     */
    private void openPreview() {
        //设置实时预览
        Log.e(TAG, "开启预览");
        if (mSurface == null) {
            mSurface = mTextureView.getSurfaceTexture();
        }
        try {
            //设置预览的SurfaceView
            mCamera.setPreviewTexture(mSurface);
            //开启预览
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印所有的预览尺寸
     *
     * @param cameraSize 预览尺寸集合
     */
    private void logPreviewSize(List<Camera.Size> cameraSize) {

        for (int i = 0; i < cameraSize.size(); i++) {
            Camera.Size size = cameraSize.get(i);
            Log.e(TAG, "所支持的尺寸---宽:" + size.width + "高" + size.height);
        }
    }

    /**
     * 开始录制视频
     *
     * @param path 录制视频保存处
     */
    public void startVideo(String path) {
        if (!mTextureView.isAvailable()) {
            Log.e(TAG, "startVideo中,挂载丢失");
            return;
        }

        initMediaRecorder(path);

        //准备并开始录制
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "开始录制异常" + e.toString());
        }
    }

    /**
     * 初始化相机录制参数
     *
     * @param path 录像地址
     */
    private void initMediaRecorder(String path) {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }

        if (mCamera != null) {
            mCamera.unlock();
        }

        mMediaRecorder.setCamera(mCamera);
        //设置音频来源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置视频来源
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        //设置帧率 有些手机会崩溃
        mMediaRecorder.setVideoFrameRate(60);
        //设置输出格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置视频的编码格式
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        // 设置音频的编码格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        // 设置输出
        mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        //设置路径
        if (TextUtils.isEmpty(path)) {
            path = FileUtils.getVideoFilePath(this, mPathName);
        }
        Log.e(TAG, "文件路径" + path);
        mMediaRecorder.setOutputFile(path);

        //设置尺寸
        mMediaRecorder.setVideoSize(mVideoSize.width, mVideoSize.height);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        Log.e(TAG, "setUpMediaRecorder:===> " + rotation);
        switch (mScreenOrientation) {
            case SENSOR_ORIENTATION_DEFAULT_DEGREES:
                Log.e(TAG, "正向校正" + mScreenOrientation);
                mMediaRecorder.setOrientationHint(DEFAULT_ORIENTATIONS.get(rotation));
                break;
            case SENSOR_ORIENTATION_INVERSE_DEGREES:
                Log.e(TAG, "反向校正" + mScreenOrientation);
                mMediaRecorder.setOrientationHint(INVERSE_ORIENTATIONS.get(rotation));
                break;
            default:
        }
    }


    /**
     * 停止录制
     */
    public void stopVideo() {

        releaseMediaRecorder();

        if (mCamera != null) {
            mCamera.lock();
        }

        openPreview();
    }

    /**
     * 释放相机资源
     */
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
        }
    }


    //----------------------------mTextureView的监听----------------------------//
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        // TODO: 2019-10-22 这里打开相机
        mSurface = surfaceTexture;
        openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
    //----------------------------mTextureView的监听----------------------------//


    @Override
    protected void onPause() {
        super.onPause();
        stopVideo();
        releaseCamera();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startVideo:
                startVideo("");
                break;
            case R.id.stopVideo:
                stopVideo();
                break;
            default:
        }
    }
}
