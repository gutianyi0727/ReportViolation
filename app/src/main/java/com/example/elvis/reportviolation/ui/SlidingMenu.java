package com.example.elvis.reportviolation.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.nineoldandroids.view.ViewHelper;


/**
 * Created by Administrator on 2018/4/5.
 */

public class SlidingMenu extends HorizontalScrollView {

    private VelocityTracker velocityTracker;
    private float velocity = 0.0f;
    //屏幕宽度
    private int mScreenWidth;
    //菜单右边距  dp
    private int mMenuRightPadding = 50;
    //菜单宽度
    private int mMenuWidth;
    private int mHalfMenuWidth;

    private int x, y; //点击事件的落点

    private ViewGroup mMenu;//菜单布局
    private ViewGroup mContent;//内容布局

    private Boolean once = false;
    private Boolean isOpen = false;

    public SlidingMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        //获取屏幕的宽度 px
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;

        velocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //显示设置的一个宽度
        if (!once){
            LinearLayout wrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) wrapper.getChildAt(0);
            mContent = (ViewGroup) wrapper.getChildAt(1);
            // dp to px
            mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMenuRightPadding, mContent.getResources().getDisplayMetrics());

            mMenuWidth = mScreenWidth - mMenuRightPadding;
            mHalfMenuWidth = mMenuWidth / 3*2;
            mMenu.getLayoutParams().width = mMenuWidth;
            mContent.getLayoutParams().width = mScreenWidth;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            //隐藏菜单
            this.scrollTo(mMenuWidth,0);
            once = true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        velocityTracker.addMovement(ev);
        velocityTracker.computeCurrentVelocity(1);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                x = (int) ev.getX();
                y = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                velocity = velocityTracker.getXVelocity();
                break;
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                int upx = (int) ev.getX();
                int upy = (int) ev.getY();
                Log.d("pianyiliang------", String.valueOf(velocity));
                if (isOpen && upx==x && upy ==y && x>mMenuWidth){
                    this.smoothScrollTo(mMenuWidth,0);
                    isOpen = false;
                }else if ( velocity < -1.0f ) {
                    this.smoothScrollTo(mMenuWidth,0);
                    isOpen = false;
                } else if( velocity > 1.0f ){
                    this.smoothScrollTo(0,0);
                    isOpen = true;
                } else {
                    if (scrollX > mHalfMenuWidth){
                        this.smoothScrollTo(mMenuWidth,0);
                        isOpen = false;
                    }else {
                        this.smoothScrollTo(0,0);
                        isOpen = true;
                    }
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        Log.d("aaaaaaa------", String.valueOf(x));
        if (isOpen && x > mMenuWidth){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        velocityTracker.recycle();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt); //l是屏幕原点到view原点的距离
        float scale = l * 1.0f / mMenuWidth;     //隐藏到显示 1~0
        float leftScale = 1 - 0.3f * scale;      //0.7~1
        float rightScale = 0.6f + scale * 0.4f;  //1~0.6

        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
        ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.6f);

        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent, rightScale);
    }


    public void showMenu(){
        if (!isOpen){
            this.smoothScrollTo(0,0);
            isOpen = true;
        }
    }

    public void hideMenu(){
        if (isOpen){
            this.smoothScrollTo(mMenuWidth,0);
            isOpen = false;
        }
    }

    public void toggleMenu(){
        if (isOpen){
            this.smoothScrollTo(mMenuWidth,0);
            isOpen = false;
        }else {
            this.smoothScrollTo(0,0);
            isOpen = true;
        }
    }

    public boolean isOpen(){
        return isOpen;
    }
}
