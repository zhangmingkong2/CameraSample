package com.example.camerasample;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder mSurfaceHolder;
    Camera mCamera;

    public CameraPreview(Context context){
        super(context);
        mSurfaceHolder=getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        try {
            int openCameraType=Camera.CameraInfo.CAMERA_FACING_BACK;
            if(openCameraType<=Camera.getNumberOfCameras()){
                mCamera=Camera.open(openCameraType);
                mCamera.setPreviewDisplay(holder);
            }else{
                Log.d("CameraSample","cannot bind camera.");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
        setPreviewSize(width,height);
        mCamera.startPreview();
    }
    protected void setPreviewSize(int width,int height){
        Camera.Parameters params=mCamera.getParameters();
        List<Camera.Size> supported=params.getSupportedPreviewSizes();
        if(supported!=null){
            for(Camera.Size size:supported){
                if(size.width<=width&&size.height<=height){
                    params.setPreviewSize(size.width,size.height);
                    mCamera.setParameters(params);
                    break;
                }
            }
        }
    }
    private Camera.ShutterCallback mShutterListener =
            new Camera.ShutterCallback() {
                public void onShutter() {
                    // TODO Auto-generated method stub
                }
            };
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mCamera != null) {
                mCamera.takePicture(mShutterListener, null, mPictureCallback);
            }
        }
        return true;
    }

    /*
      private Camera.PictureCallback mPictureListener =
              new Camera.PictureCallback() {
                  public void onPictureTaken(byte[] data, Camera camera) {
                      if (data != null) {
                          try {
                              File imageFile = new File("pic0.jpg");
                              FileOutputStream outStream = new FileOutputStream(imageFile);
                              outStream.write(data);
                              outStream.close();
                          } catch (Exception e) {
                              e.printStackTrace();
                          }

                          camera.startPreview();
                      }
                  }
              };

      */



    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] imageData, Camera c) {
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        uploadPhoto(imageData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            mCamera.startPreview();
        }


    };
        private void uploadPhoto(byte[] imageData) {
            Log.e("Callback TAG", "Here in jpeg Callback");
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            if (imageData != null) {
                try {
                    int length = -1;
                    URL url = new URL("http://127.0.0.1:8080/TestServices/upload");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
          /* 允许Input、Output，不使用Cache */
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setUseCaches(false);
          /* 设置传送的method=POST */
                    con.setRequestMethod("POST");
          /* setRequestProperty */
                    con.setRequestProperty("Connection", "Keep-Alive");
                    con.setRequestProperty("Charset", "UTF-8");
                    con.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded" + boundary);
          /* 设置DataOutputStream */
                    DataOutputStream ds =
                            new DataOutputStream(con.getOutputStream());
                    ds.writeBytes(twoHyphens + boundary + end);
                    ds.writeBytes("Content-Disposition: form-data; " +
                            "name=\"file1\";filename=\"" +
                            "image.jpg" + "\"" + end);
                    ds.writeBytes(end);
          /* 取得文件的FileInputStream */
                    //  FileInputStream fStream =new FileInputStream(uploadFile);
          /* 设置每次写入1024bytes */
                    // int bufferSize =1024;
                    //   byte[] buffer =new byte[bufferSize];
                    // int length =-1;
          /* 从文件读取数据至缓冲区 */
                    // while((length = fStream.read(imageData)) !=-1)
                    // {
            /* 将资料写入DataOutputStream中 */
                    ds.write(imageData, 0, length);
                    // }
                    ds.writeBytes(end);
                    ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
          /* close streams */
                    //  fStream.close();
                    ds.flush();
          /* 取得Response内容 */
                    InputStream is = con.getInputStream();
                    int ch;
                    StringBuffer b = new StringBuffer();
                    while ((ch = is.read()) != -1) {
                        b.append((char) ch);
                    }
          /* 将Response显示于Dialog */
                    showDialog("上传成功" + b.toString().trim());
          /* 关闭DataOutputStream */
                    ds.close();
                } catch (Exception e) {
                    showDialog("上传失败" + e);
                }
                //outputStream = mActivity.openFileOutput("pic01.jpg", Context.MODE_PRIVATE);
                //outputStream.write(imageData);
                // Removed the finish call you had here


            }
        }


    private void showDialog(String mess)
    {  final Activity mActivity = (Activity)this.getContext();
        new AlertDialog.Builder(mActivity).setTitle("Message")
                .setMessage(mess)
                .setNegativeButton("确定",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    }
                })
                .show();
    }
    }



