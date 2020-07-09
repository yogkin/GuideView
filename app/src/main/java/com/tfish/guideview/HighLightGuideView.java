package com.tfish.guideview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by zafa on 2017/6/6.
 */

public class HighLightGuideView extends View {

    public static final String TAG = HighLightGuideView.class.getSimpleName();
    //高亮类型：矩形、圆形、椭圆形
    public static final int VIEWSTYLE_RECT = 0;
    public static final int VIEWSTYLE_CIRCLE = 1;
    public static final int VIEWSTYLE_OVAL = 2;
    //画笔类型，圆滑、默认
    public static final int MASKBLURSTYLE_SOLID = 0;
    public static final int MASKBLURSTYLE_NORMAL = 1;

    private View rootView;
    private Bitmap fgBitmap;// 前景
    private Canvas mCanvas;// 绘制蒙版层的画布
    private Paint mPaint;// 绘制蒙版层画笔
    private int screenW, screenH;// 屏幕宽高
    private OnDismissListener onDismissListener;//关闭监听
    private Activity activity;

    /*******************可配置属性*****************************/
    private boolean touchOutsideCancel = true;//外部点击是否可关闭
    private int highLightStyle = VIEWSTYLE_RECT;//高亮类型默认圆形
    public int maskblurstyle = MASKBLURSTYLE_SOLID;//画笔类型默认
    private ArrayList<Bitmap> tipBitmaps;//显示图片
    private ArrayList<View> targetViews;//高亮目标view
    private int maskColor = 0x99000000;// 蒙版层颜色

    private Path mPath;

    private static final int GUIDE_HOMEPAGE_FIRST = 10;
    private static final int GUIDE_HOMEPAGE_SECOND = 12;
    private static final int GUIDE_HOMEPAGE_THIRD = 13;

    private int guideType;
    private float triangleWidth;
    private float triangleHeight;

    private HighLightGuideView(Activity activity, int guideType) {
        super(activity);
        this.activity = activity;
        this.guideType = guideType;
        // 计算参数
        cal(activity);
        // 初始化对象
        init();
    }

    private static HighLightGuideView builder(Activity activity, int guideType) {
        return new HighLightGuideView(activity, guideType);
    }

    public static HighLightGuideView buildHomePageFirst(Activity activity, View view, View view2, View view3) {
        return HighLightGuideView.builder(activity, GUIDE_HOMEPAGE_FIRST)
                .addHighLightGuidView(view, view2, view3)
                .addTipBitMaps(R.mipmap.guidance_07, R.mipmap.guidance_03, R.mipmap.guidance_11, R.mipmap.guidance_17, R.mipmap.guidance_15, R.mipmap.guidance_22);
    }


    /**
     * 计算参数
     *
     * @param context 上下文环境引用
     */
    private void cal(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // 获取屏幕尺寸数组
        Point sizePoint = new Point(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
        // 获取屏幕宽高
        screenW = sizePoint.x;
        screenH = sizePoint.y;
        Log.d(TAG, "屏宽: " + screenW + "--屏高: " + screenH);
    }

    /**
     * 初始化对象
     */
    private void init() {
        tipBitmaps = new ArrayList<>();
        targetViews = new ArrayList<>();
        rootView = ((Activity) getContext()).findViewById(android.R.id.content);
        // 实例化画笔并开启其抗锯齿和抗抖动
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        // 设置画笔透明度为0是关键！
        mPaint.setARGB(0, 255, 0, 0);
        // 设置混合模式为DST_IN
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // 生成前景图Bitmap
        fgBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_4444);
        // 将其注入画布
        mCanvas = new Canvas(fgBitmap);
        // 绘制前景画布颜色
        mCanvas.drawColor(maskColor);

        triangleWidth = ViewUtils.dip2px(getContext(), 22);
        triangleHeight = ViewUtils.dip2px(getContext(), 10);
        Log.d(TAG, "三角形宽: " + triangleWidth + "--三角形高: " + triangleHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(fgBitmap, 0, 0, null);
        switch (guideType) {
            case GUIDE_HOMEPAGE_FIRST:
                drawHomepage(canvas);
                break;
            default:
                return;
        }
    }

    private void drawHomepage(Canvas canvas) {
        mPath = new Path();

        //画搜索引导图
        View view1 = targetViews.get(0);
        canvas.drawBitmap(tipBitmaps.get(0), null, new Rect(view1.getLeft(), view1.getTop(), view1.getRight(), view1.getBottom()), null);
        canvas.drawBitmap(tipBitmaps.get(1), view1.getLeft() + view1.getWidth() / 3f, view1.getTop() + view1.getHeight() / 1.5f, null);

        //画圈子
        View view2 = targetViews.get(1);
        canvas.drawBitmap(tipBitmaps.get(2), null, new Rect(view2.getLeft(), view2.getTop(), view2.getRight(), view2.getBottom()), null);
        canvas.drawBitmap(tipBitmaps.get(3), view2.getLeft() - ViewUtils.dip2px(getContext(), 70), view2.getTop() + view2.getHeight() / 1.3f, null);

        //画我的
        View view3 = targetViews.get(2);
        canvas.drawBitmap(tipBitmaps.get(4), null, new Rect(view3.getLeft(), view3.getTop(), view3.getRight(), view3.getBottom()), null);
        canvas.drawBitmap(tipBitmaps.get(5), view3.getLeft()-ViewUtils.dip2px(getContext(), 130), view3.getTop()-view3.getHeight()-ViewUtils.dip2px(getContext(), 10) , null);


        //绘制图片
    }

    private static final String FRAGMENT_CON = "NoSaveStateFrameLayout";


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP://
                if (touchOutsideCancel) {
                    this.setVisibility(View.GONE);
                    //移除view
                    if (rootView != null) {
                        ((ViewGroup) rootView).removeView(this);
                    }
                    //返回监听
                    if (this.onDismissListener != null) {
                        onDismissListener.onDismiss();
                    }
                    return true;
                }
                break;
        }
        return true;
    }

    public void dismiss() {
        this.setVisibility(View.GONE);
        //移除view
        if (rootView != null) {
            ((ViewGroup) rootView).removeView(this);
        }
        //返回监听
        if (this.onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    /********************builder模式设置属性******************************/

    /**
     * 绘制前景画布颜色
     *
     * @param bgColor
     */
    public HighLightGuideView setMaskColor(int bgColor) {
        try {
            this.maskColor = ContextCompat.getColor(getContext(), bgColor);
            // 重新绘制前景画布
            mCanvas.drawColor(maskColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置高亮显示类型
     *
     * @param style
     */
    public HighLightGuideView setHighLightStyle(int style) {
        this.highLightStyle = style;
        return this;
    }

    /**
     * 设置高亮画笔类型
     *
     * @param maskblurstyle
     */
    public HighLightGuideView setMaskblurstyle(int maskblurstyle) {
        this.maskblurstyle = maskblurstyle;
        return this;
    }


    /**
     * 设置需要高亮的View
     *
     * @param views
     * @return
     */
    public HighLightGuideView addHighLightGuidView(View... views) {
        try {
            Collections.addAll(targetViews, views);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置提示图片
     *
     * @param resArray
     */
    public HighLightGuideView addTipBitMaps(int... resArray) {
        try {
            for (int res : resArray) {
                tipBitmaps.add(BitmapFactory.decodeResource(getResources(), res));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 设置外部是否关闭，默认关闭
     *
     * @param cancel
     */
    public HighLightGuideView setTouchOutsideDismiss(boolean cancel) {
        this.touchOutsideCancel = cancel;
        return this;
    }

    /**
     * 设置关闭监听
     *
     * @param listener
     */
    public HighLightGuideView setOnDismissListener(OnDismissListener listener) {
        this.onDismissListener = listener;
        return this;
    }

    /**
     * 清空画布
     */
    public HighLightGuideView clearBg() {
        if (mCanvas != null) {
            Paint paint = new Paint();
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawPaint(paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }
        // 将其注入画布
        mCanvas = new Canvas(fgBitmap);
        // 绘制前景画布
        mCanvas.drawColor(maskColor);
        return this;
    }

    /**
     * 显示
     */
    public void show() {
        if (rootView != null) {
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            Log.d(TAG, "view个数位: " + ((ViewGroup) rootView).getChildCount());
            ((ViewGroup) rootView).addView(this, ((ViewGroup) rootView).getChildCount(), lp);
        }
    }

}
