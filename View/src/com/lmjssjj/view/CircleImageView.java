package com.lmjssjj.view;
 
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;


public class CircleImageView extends ImageView {

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();
    private final RectF mBackgroundRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();
    private final Paint mBackgroudPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private BitmapDrawable mBitmapDrawable;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mTintColor;

    private float mDrawableRadius;
    private float mBorderRadius;
    private float mBackgroidRadius;

    private boolean mReady;
    private boolean mSetupPending;
    private boolean mIsBusiness;
    
    private boolean hasLineBorder ;
    private Paint mLineBorderPaint;
    private int mLineBorderColor = Color.WHITE;
    private int mLineBorderWidth = 1;

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(SCALE_TYPE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);
        hasLineBorder = a.getBoolean(R.styleable.CircleImageView_hasLineBorder,false);
        mLineBorderColor = a.getColor(R.styleable.CircleImageView_lineBorderColor,0xFFF0F0F0);
        mLineBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_lineBorderWidth,1);
        a.recycle();

        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        if(mTintColor != 0){
        	canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius - mLineBorderWidth, mBackgroudPaint);
        }
        
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius - mLineBorderWidth, mBitmapPaint);
        if (mBorderWidth != 0) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
        }

        if(hasLineBorder){
            initLineBorderPaint();
            canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2 - mLineBorderWidth * 1f/2,mLineBorderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }
    
    public void setIsBusiness(boolean isBusiness) {
        mIsBusiness = isBusiness;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

//    @Override
//    public void setImageDrawable(Drawable drawable) {
//        super.setImageDrawable(drawable);
//        mBitmap = getBitmapFromDrawable(drawable);
//        setup();
//    }
    
    @Override
    public void setImageDrawable(Drawable drawable) {
        // There is no way to avoid all this casting. Blending modes aren't equally
        // supported for all drawable types.
        final BitmapDrawable bitmapDrawable;
        if (drawable == null || drawable instanceof BitmapDrawable) {
            bitmapDrawable = (BitmapDrawable) drawable;
        } else if (drawable instanceof LetterTileDrawable) {
            if (!mIsBusiness) {
                bitmapDrawable = (BitmapDrawable) getResources().getDrawable(
                        R.drawable.person_white_540dp);
            } else {
                bitmapDrawable = (BitmapDrawable) getResources().getDrawable(
                        R.drawable.generic_business_white_540dp);
            }
        } else {
            throw new IllegalArgumentException("Does not support this type of drawable");
        }

//        mOriginalDrawable = drawable;
        mBitmapDrawable = bitmapDrawable;
//        setTint(mTintColor);
        mBitmap = getBitmapFromDrawable(bitmapDrawable);
        setup();
        super.setImageDrawable(bitmapDrawable);
    }
    
    public void setTint(int color) {
        if (mBitmapDrawable == null || mBitmapDrawable.getBitmap() == null
                || mBitmapDrawable.getBitmap().hasAlpha()) {
//            setBackgroundColor(color);
        	initBackgroud(color);
        } else {
            setBackground(null);
        }
        mTintColor = color;
        postInvalidate();
    }
    
    private void initBackgroud(int color){
    	mBackgroudPaint.setStyle(Paint.Style.FILL);
    	mBackgroudPaint.setAntiAlias(true);
    	mBackgroudPaint.setColor(color);
    	mBackgroundRect.set(0, 0, getWidth(), getHeight());
        mBackgroidRadius = Math.min((mBorderRect.height() - mLineBorderWidth) / 2, (mBorderRect.width() - mLineBorderWidth) / 2);
        invalidate();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null) {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();

        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);

        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);

        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {//将图片缩小为控件大小
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.set(null);

        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    private void initLineBorderPaint(){
        if(mLineBorderPaint == null){
            mLineBorderPaint = new Paint();
            mLineBorderPaint.setStyle(Paint.Style.STROKE);
            mLineBorderPaint.setColor(mLineBorderColor);
            mLineBorderPaint.setStrokeWidth(mLineBorderWidth);
            mLineBorderPaint.setAntiAlias(true);
        }
    }

    public void hasLineBorder(boolean has){
        this.hasLineBorder = has;
        invalidate();
    }

}