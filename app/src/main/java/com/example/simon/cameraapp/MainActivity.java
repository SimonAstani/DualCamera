package com.example.simon.cameraapp;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements OnClickListener {
    String[] ImagePath;
    private ImageView btnChangeCam;
    private ImageView btnSave;
    private ImageView btnTakePic;
    private int currentCameraId;
    private boolean isFirst;
    private Camera mCamera;
    private CameraPreviews mCameraPreview;
    PictureCallback mPicture;
    private LinearLayout mllFirst;
    private LinearLayout mllSecond;
    private boolean isPlay = false;

    /* renamed from: com.aquasoltools.dualhdcamera.MainActivity.1 */
    class takePicture implements PictureCallback {
        takePicture() {
        }

        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("Image Path", "Path is : ");
            File pictureFile = MainActivity.getOutputMediaFile();
            if (pictureFile != null) {
                try {
                    Log.d("Image Path", "Path is : " + pictureFile.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    if (MainActivity.this.isFirst) {
                        MainActivity.this.ImagePath[0] = pictureFile.getAbsolutePath();
                    } else {
                        MainActivity.this.ImagePath[1] = pictureFile.getAbsolutePath();
                    }
                    MainActivity.this.stopCameraPreview(camera);
                } catch (FileNotFoundException e) {
                } catch (IOException e2) {
                }
            }
        }
    }



    public MainActivity() {
        this.isFirst = true;
        this.mPicture = new takePicture();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onResume() {
        if (this.mCamera == null) {
            bindView();
            init();
            addListner();
            this.mllFirst.removeAllViews();
            this.mllSecond.removeAllViews();
            this.mCamera = getCameraInstance(this.currentCameraId);
            this.mCameraPreview = new CameraPreviews(this, this.mCamera);
            this.mCameraPreview.refreshDrawableState();
            this.mllFirst.addView(this.mCameraPreview);
        }
        super.onResume();
    }

    private void init() {
        this.currentCameraId = 0;
        this.ImagePath = new String[2];
        this.ImagePath[0] = "null";
        this.ImagePath[1] = "null";
    }


    protected void onPause() {
        super.onPause();
        if (this.mCamera != null) {
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    @SuppressLint({"NewApi"})
    private Camera getCameraInstance(int mCamId) {
        Camera camera = null;
        try {
            camera = Camera.open(mCamId);
        } catch (Exception e) {
        }
        return camera;
    }

    private void stopCameraPreview(Camera mCamera) {
        if (mCamera != null) {
            this.mCamera.release();
            this.mCamera = null;
            this.mCamera = getCameraInstance(this.currentCameraId);
            this.mCameraPreview = new CameraPreviews(this, this.mCamera);
            if (this.isFirst) {
                this.mllSecond.removeAllViews();
                this.mllSecond.addView(this.mCameraPreview);
            }
        }
        this.isFirst = !this.isFirst;
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MyCameraApp");
        if (mediaStorageDir.exists() || mediaStorageDir.mkdirs()) {
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg");
            Log.d("Image Path", "Path is : " + mediaFile.getAbsolutePath());
            return mediaFile;
        }
        Log.d("MyCameraApp", "failed to create directory");
        return null;
    }

    private void bindView() {
        this.btnSave = (ImageView) findViewById(R.id.btnSavePic);
        this.btnChangeCam = (ImageView) findViewById(R.id.btnChangeCam);
        this.btnTakePic = (ImageView) findViewById(R.id.btnTakePic);
        this.mllFirst = (LinearLayout) findViewById(R.id.backcamera_preview);
        this.mllSecond = (LinearLayout) findViewById(R.id.frontcamera_preview);
    }

    private void addListner() {
        this.btnSave.setOnClickListener(this);
        this.btnChangeCam.setOnClickListener(this);
        this.btnTakePic.setOnClickListener(this);
    }

    @SuppressLint({"NewApi"})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSavePic:
                String imagePath = meargeTwoBitmap();
                if (!imagePath.equals("null")) {
                    Intent intent = new Intent(this, ImageList.class);
                    intent.putExtra("ImagePath", imagePath);
                    startActivity(intent);
                }
                break;
            case R.id.btnTakePic:
                if (isPlay){

                }
                if (this.mCamera != null) {
                    this.mCamera.takePicture(null, null, this.mPicture);
                }
                break;
            case R.id.btnChangeCam:
                if (Camera.getNumberOfCameras() != 1) {
                    if (0 < Camera.getNumberOfCameras()) {
                        Camera.getCameraInfo(0, new CameraInfo());
                        if (this.currentCameraId == 1) {
                            this.currentCameraId = 0;
                        } else if (this.currentCameraId == 0) {
                            this.currentCameraId = 1;
                        }
                    }
                    if (this.mCamera != null) {
                        this.mCamera.release();
                        this.mCamera = null;
                        this.mCamera = getCameraInstance(this.currentCameraId);
                        this.mCameraPreview = new CameraPreviews(this, this.mCamera);
                        if (this.isFirst) {
                            this.mllFirst.removeAllViews();
                            this.mllFirst.addView(this.mCameraPreview);
                            return;
                        }
                        this.mllSecond.removeAllViews();
                        this.mllSecond.addView(this.mCameraPreview);
                    }
                }
                break;
            default:
        }
    }

    private String meargeTwoBitmap() {
        FileNotFoundException e;
        FileOutputStream fileOutputStream;
        Exception e2;
        Options opts = new Options();
        if (!this.ImagePath[0].equals("null")) {
            Bitmap resultBitmap;
            Bitmap bmp1;
            if (this.ImagePath[1].equals("null")) {
                bmp1 = BitmapFactory.decodeFile(this.ImagePath[0], opts);
                resultBitmap = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), Config.ARGB_8888);
                new Canvas(resultBitmap).drawBitmap(bmp1, 0.0f, 0.0f, null);
            } else {
                bmp1 = BitmapFactory.decodeFile(this.ImagePath[0], opts);
                resultBitmap = Bitmap.createBitmap(bmp1.getWidth() * 2, bmp1.getHeight(), Config.ARGB_8888);
                Canvas mCanvas = new Canvas(resultBitmap);
                mCanvas.drawBitmap(bmp1, 0.0f, 0.0f, null);
                mCanvas.drawBitmap(BitmapFactory.decodeFile(this.ImagePath[1], opts), (float) bmp1.getWidth(), 0.0f, null);
            }
            try {
                File outputFile = getOutputMediaFile();
                FileOutputStream fos = new FileOutputStream(outputFile);
                try {
                    resultBitmap.compress(CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    if (!this.ImagePath[0].equals("null")) {
                        if (!this.ImagePath[1].equals("null")) {
                            new File(this.ImagePath[1]).delete();
                        }
                        new File(this.ImagePath[0]).delete();
                    }
                    return outputFile.getAbsolutePath();
                } catch (FileNotFoundException e3) {
                    e = e3;
                    fileOutputStream = fos;
                    e.printStackTrace();
                    return "null";
                } catch (Exception e4) {
                    e2 = e4;
                    fileOutputStream = fos;
                    e2.printStackTrace();
                    return "null";
                }
            } catch (FileNotFoundException e5) {
                e = e5;
                e.printStackTrace();
                return "null";
            } catch (Exception e6) {
                e2 = e6;
                e2.printStackTrace();
                return "null";
            }
        }
        return "null";
    }
}