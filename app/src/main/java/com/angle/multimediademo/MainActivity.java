package com.angle.multimediademo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.angle.multimediademo.FirstEdition.CustomCameraActivity;
import com.angle.multimediademo.SecondEdition.SecondCustomCameraActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mSystemCamera;
    private Button mCustomCamera;
    private Button mVideo;
    private RxPermissions mRxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSystemCamera = findViewById(R.id.systemCamera);
        mCustomCamera = findViewById(R.id.customCamera);
        mVideo = findViewById(R.id.video);

        initData();
        initListener();
    }

    private void initData() {
        mRxPermissions = new RxPermissions(this);
    }

    private void initListener() {
        mSystemCamera.setOnClickListener(this);
        mCustomCamera.setOnClickListener(this);
        mVideo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.systemCamera:
                break;
            case R.id.customCamera:
                mRxPermissions.request(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean isSuccess) {
                                if (isSuccess) {
                                    //这里绑定极光推送看看是不是应该在登陆的时候去绑定
                                    Intent intent = new Intent(MainActivity.this, SecondCustomCameraActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                break;
            case R.id.video:
                break;
            default:
        }
    }
}
