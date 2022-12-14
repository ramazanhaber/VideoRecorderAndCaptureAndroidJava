package com.odev.onkamerakayit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
/*
* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    Uri photoUri = Uri.fromFile(getOutputPhotoFile());
    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
    intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
    startActivityForResult(intent, CAMERA_PHOTO_REQUEST_CODE);
* */
    // kaynak https://youtu.be/_igp9Apumvg

    private static int CAMERA_PERMISSIONS_CODE = 100;
    private static int VIDEO_RECORD_CODE = 101;
    private static int IMAGE_CAPTURE_CODE = 102;

    private Uri videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordButton =
                (Button) findViewById(R.id.recordButton);


        Button captureButton =
                (Button) findViewById(R.id.captureButton);


        if (isCameraPresentInPhone()) {
            Toast.makeText(this, "Camera is detected", Toast.LENGTH_SHORT).show();
            getCameraPermission();
        } else {
            Toast.makeText(this, "Kamera izni verilmemiş", Toast.LENGTH_SHORT).show();
        }


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
            }
        });

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
    }


    private boolean isCameraPresentInPhone() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    private void getCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_CODE);
        } else {

        }
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //        File outPutFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Util.SD_CARD_PATH);
//        if (!outPutFile.exists()) {
//            outPutFile.mkdirs();
//        }
//        capturedImageUri = Uri.fromFile(File.createTempFile("packagename" + System.currentTimeMillis(), ".jpg", outPutFile));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, VIDEO_RECORD_CODE);
    }

    String fileUri = "";


    Uri capturedImageUri;


    private static boolean isFrontCameraIntent(int intentCameraId) {
        return (intentCameraId == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    private static boolean isBackCameraIntent(int intentCameraId) {
        return (intentCameraId == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    private void recordVideoFront() throws IOException {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        camera.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);

        camera.putExtra( "android.intent.extras.LENS_FACING_FRONT", 1 );
        camera.putExtra( "android.intent.extras.CAMERA_FACING", 1 );
        camera.putExtra( "android.intent.extra.USE_FRONT_CAMERA", true );
        startActivityForResult(camera, VIDEO_RECORD_CODE);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAPTURE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_RECORD_CODE) {
            if (resultCode == RESULT_OK) {
                videoPath = data.getData();
                Toast.makeText(this, "Video Kaydedildi Path : " + videoPath, Toast.LENGTH_SHORT).show();
                Log.i("videopat", videoPath.toString());
                Log.i("file", fileUri);

//                videoShare(videoPath.toString());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video iptal edildi...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hata", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IMAGE_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {
                videoPath = data.getData();
                Toast.makeText(this, "Resim Kaydedildi Path : " + videoPath, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Resim iptal edildi...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hata", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void videoShare(String filePath) {
        try {
            File videoFile = new File(filePath);
            Uri videoURI = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                    ? FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName(), videoFile)
                    : Uri.fromFile(videoFile);
            ShareCompat.IntentBuilder.from(MainActivity.this)
                    .setStream(videoURI)
                    .setType("video/mp4")
                    .setChooserTitle("Share video...")
                    .startChooser();
        } catch (Exception ex) {
            Toast.makeText(this, "HATA " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}