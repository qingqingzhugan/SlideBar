package com.qqzg.slidebar.Service;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import com.qqzg.slidebar.R;


public class SlideService extends AccessibilityService {
    public SlideService() {
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    View leftView;

    int touchSlop;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("service", "onCreate");
        createFloatView();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getApplication());
        touchSlop =10;
        createFloatView();
    }

    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wmParams.flags=WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT|Gravity.BOTTOM;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x =0;
        wmParams.y =(mWindowManager.getDefaultDisplay().getHeight()-1000)/2;

        //设置悬浮窗口长宽数据
        wmParams.width = (int) (getApplication().getResources().getDisplayMetrics().density * 10);
        wmParams.height = 1000;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        leftView = inflater.inflate(R.layout.slide_bar, null);
        leftView.setOnTouchListener(new LeftOnTouchListener());
        mWindowManager.addView(leftView, wmParams);
        //浮动窗口按钮
        leftView.setOnTouchListener(new LeftOnTouchListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (leftView != null) {
            mWindowManager.removeView(leftView);
        }
        leftView=null;
    }

    private class LeftOnTouchListener implements View.OnTouchListener {
        private float startX;
        private float startY;

        private boolean isMove = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    isMove = Math.abs(event.getY() - startY) > touchSlop;
                    break;
                case MotionEvent.ACTION_UP:
                    if(isMove){
                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                        isMove=false;
                        return true;
                    }
                    break;
            }
            return false;
        }
    }
}
