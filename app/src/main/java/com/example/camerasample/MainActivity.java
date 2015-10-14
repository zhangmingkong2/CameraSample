package com.example.camerasample;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.hardware.Camera;
import android.media.CameraProfile;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {
    public static MainActivity mactivity;
    Window window;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mactivity=this;
       /* window=getWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        window.setAttributes(params);
        */
        setContentView(R.layout.activity_main);
        CameraPreview cameraPreview=new CameraPreview(this);
        this.setContentView(cameraPreview, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();

        if(id==R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}