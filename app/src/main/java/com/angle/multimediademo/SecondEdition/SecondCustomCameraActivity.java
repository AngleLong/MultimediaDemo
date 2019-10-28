package com.angle.multimediademo.SecondEdition;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.TextureView;

import com.angle.multimediademo.CameraHelper;
import com.angle.multimediademo.R;
import com.angle.multimediademo.Utils.NewToastUtils;
import com.angle.multimediademo.view.CircleButtonView;

public class SecondCustomCameraActivity extends AppCompatActivity {

    private CircleButtonView mCircleBtn;
    private TextureView mTextureView;
    private CameraHelper mCameraHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_custom_camera);

        mCircleBtn = findViewById(R.id.circleBtn);
        mTextureView = findViewById(R.id.textureView);

        initData();
        initListener();
    }

    private void initData() {
        mCameraHelper = new CameraHelper(mTextureView, this);
    }

    private void initListener() {
        mCircleBtn.setOnLongClickListener(new CircleButtonView.OnLongClickListener() {
            @Override
            public void onLongClick() {
                mCameraHelper.startVideo();
            }

            @Override
            public void onNoMinRecord(int currentTime) {
                NewToastUtils.show(SecondCustomCameraActivity.this, "时间不足");
            }

            @Override
            public void onRecordFinishedListener() {
                mCameraHelper.stopVideo();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraHelper.onPause();
    }
}
