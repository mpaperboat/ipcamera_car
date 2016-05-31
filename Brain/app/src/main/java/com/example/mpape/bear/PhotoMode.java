package com.example.mpape.bear;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.ListIterator;

public class PhotoMode extends Activity implements SurfaceHolder.Callback,GestureDetector.OnGestureListener {
    private static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private Camera camera = null;
    private ListIterator<Bitmap> it;
    private GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//取消标题栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);//全屏
        setContentView(R.layout.activity_photo_mode);
        //mGestureDetector=new GestureDetector(this.surfaceview,this);
        mGestureDetector = new GestureDetector(this, this);

        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PhotoMode.this, KeyMode.class);
                startActivity(intent);
            }
        });
        context = this;
        surfaceview = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!((MyApplication)getApplication()).phts.isEmpty()){
            paint(((MyApplication)getApplication()).phts.getFirst());
            it=((MyApplication)getApplication()).phts.listIterator();
            it.next();
        }else{
            it=null;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        surfaceview.setVisibility(View.GONE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        surfaceview.setVisibility(View.VISIBLE);

    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }
    public void paint(Bitmap m){
        Canvas c=surfaceholder.lockCanvas();
        if(c!=null){
            synchronized (surfaceholder) {
                Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                c.drawBitmap(m,null,tmp,new Paint());
                // System.out.println("draw one img!");
                // pdate();
            }
            surfaceholder.unlockCanvasAndPost(c);
        }
        //surfaceview
        System.out.println("wfk");
    }
    private String getActionName(int action) {
        String name = "";
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                name = "ACTION_DOWN";
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                name = "ACTION_MOVE";
                break;
            }
            case MotionEvent.ACTION_UP: {
                name = "ACTION_UP";
                break;
            }
            default:
                break;
        }
        return name;
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i(getClass().getName(), "onSingleTapUp-----" + getActionName(e.getAction()));
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i(getClass().getName(), "onLongPress-----" + getActionName(e.getAction()));
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i(getClass().getName(),
                "onScroll-----" + getActionName(e2.getAction()) + ",(" + e1.getX() + "," + e1.getY() + ") ,("
                        + e2.getX() + "," + e2.getY() + ")");
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        System.out.println("zkw"+(e1.getX()-e2.getX()));
        //大于设定的最小滑动距离并且在水平/竖直方向速度绝对值大于设定的最小速度，则执行相应方法
        if(e1.getX()-e2.getX() > 20 && Math.abs(velocityX) > 10){
            System.out.println(""+(e1.getX()-e2.getX())+","+velocityX);
            //在此处实现跳转
            //Intent intent  = new Intent(MainActivity.this,nextActivity.class);
            //startActivity(intent);

            //Toast.makeText(this, "turn left", Toast.LENGTH_SHORT).show();
            if(it!=null&&it.hasPrevious())
                paint(it.previous());

        }else if(e2.getX() - e1.getX() > 20 && Math.abs(velocityX) > 10){
            //Toast.makeText(this, "turn right", Toast.LENGTH_SHORT).show();
            if(it!=null&&it.hasNext()){
                paint(it.next());

            }

        }else if(e1.getY()-e2.getY() > 20 && Math.abs(velocityY) > 10){
           // Toast.makeText(MainActivity.this, "turn up", Toast.LENGTH_SHORT).show();


        }else if(e2.getY()-e1.getY() > 20 && Math.abs(velocityY) > 10){
            //Toast.makeText(MainActivity.this, "turn down", Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    @Override
    public void onShowPress(MotionEvent e) {
        Log.i(getClass().getName(), "onShowPress-----" + getActionName(e.getAction()));
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(getClass().getName(), "onDown-----" + getActionName(e.getAction()));
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.d(TAG, "+ onTouchEvent(event:" + event + ")");
        mGestureDetector.onTouchEvent(event);
       // Log.d(TAG, "- onTouchEvent()");
        return true;
    }

}