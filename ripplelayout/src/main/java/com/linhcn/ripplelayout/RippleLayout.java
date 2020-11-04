package com.linhcn.ripplelayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class RippleLayout extends RelativeLayout {

    private static final float DEFAULT_SCALE = 6.0f;
    private static final int DEFAULT_RIPPLE_COUNT = 6;
    private static final int DEFAULT_DURATION_TIME = 5000;
    private static final int DEFAULT_FILL_TYPE = 0;
    private static final int DEFAULT_RIPPLE_SHADOW = 0;

    private final ArrayList<RippleView> rippleViewList = new ArrayList<>();
    private AnimatorSet animatorSet; // using to mix all Animators
    private ArrayList<Animator> animatorList;
    private Paint paint;
    private LayoutParams rippleParams;
    private boolean isAnimationRunning = false;
    private boolean isRippleShadow = false;

    private float rippleStrokeWidth;
    private float rippleRadius;
    private int rippleColor;
    private int rippleDurationTime;
    private int rippleNumber;
    private int rippleDelay; // delay between each ripples
    private float rippleScale; // max ripple scale
    private int rippleType; // is Paint.Style
    private int rippleShadow;

    public RippleLayout(Context context) {
        super(context);
    }

    public RippleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RippleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        if (isInEditMode())
            return;

        if (null == attrs) {
            throw new IllegalArgumentException("Attributes shouldn't be empty!");
        }

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleLayout);
        rippleStrokeWidth = typedArray.getDimension(R.styleable.RippleLayout_rl_strokeWidth, getResources().getDimension(R.dimen.rippleStrokeWith));
        rippleRadius = typedArray.getDimension(R.styleable.RippleLayout_rl_radius, getResources().getDimension(R.dimen.rippleRadius));
        rippleScale = typedArray.getFloat(R.styleable.RippleLayout_rl_scale, DEFAULT_SCALE);
        rippleDurationTime = typedArray.getInt(R.styleable.RippleLayout_rl_duration, DEFAULT_DURATION_TIME);
        rippleNumber = typedArray.getInt(R.styleable.RippleLayout_rl_rippleAmount, DEFAULT_RIPPLE_COUNT);
        rippleType = typedArray.getInt(R.styleable.RippleLayout_rl_type, DEFAULT_FILL_TYPE);
        rippleShadow = typedArray.getInt(R.styleable.RippleLayout_rl_shadow, DEFAULT_RIPPLE_SHADOW);
        rippleColor = typedArray.getColor(R.styleable.RippleLayout_rl_color, getResources().getColor(android.R.color.background_light));
        typedArray.recycle();

        if (rippleType == DEFAULT_FILL_TYPE) {
            rippleStrokeWidth = 0;
        }

        isRippleShadow = rippleShadow != DEFAULT_RIPPLE_SHADOW;
        paint = initPaintRippleView(rippleType, rippleColor, isRippleShadow, rippleShadow);
        rippleParams = initParamRippleView(rippleRadius, rippleStrokeWidth);

        rippleDelay = rippleDurationTime / rippleNumber;
        animatorSet = initAnimatorSetRippleView(rippleParams, rippleDurationTime, rippleDelay);
    }

    private Paint initPaintRippleView(final int rippleType, final int rippleColor,
                                      final boolean isRippleShadow, final int rippleShadow) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(rippleType == DEFAULT_FILL_TYPE ? Paint.Style.FILL : Paint.Style.STROKE);
        paint.setColor(rippleColor);
        if (isRippleShadow)
            paint.setShadowLayer(rippleShadow, 0.0f, 0.0f, Color.GRAY);
        return paint;
    }

    private LayoutParams initParamRippleView(final float rippleRadius,
                                             final float rippleStrokeWidth) {
        int w = (int) (2 * (rippleRadius + rippleStrokeWidth));
        LayoutParams rippleParams = new LayoutParams(w, w);
        rippleParams.addRule(CENTER_IN_PARENT, TRUE);
        return rippleParams;
    }

    private AnimatorSet initAnimatorSetRippleView(final LayoutParams rippleParams,
                                                  final int rippleDurationTime,
                                                  final int rippleDelay) {

        animatorList = new ArrayList<>();
        for (int i = 0; i < rippleNumber; i++) {
            // layout
            RippleView rippleView = new RippleView(getContext());
            addView(rippleView, rippleParams);
            rippleViewList.add(rippleView);
            // scaleX
            final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
            scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleXAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(scaleXAnimator);
            // scaleY
            final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
            scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
            scaleYAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(scaleYAnimator);
            // alpha
            final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f);
            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i * rippleDelay);
            animatorList.add(alphaAnimator);
        }

        // combine the animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(rippleDurationTime);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(animatorList);
        return animatorSet;
    }

    private class RippleView extends View {

        public RippleView(Context context) {
            super(context);
            this.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int cx = (Math.min(getWidth(), getHeight())) / 2;
            int radius = cx - (isRippleShadow ? rippleShadow : DEFAULT_RIPPLE_SHADOW);
            canvas.drawCircle(cx, cx, radius, paint);
        }
    }

    public void startRippleAnimation() {
        if (!isRippleAnimationRunning()) {
            for (RippleView rippleView : rippleViewList) {
                rippleView.setVisibility(VISIBLE);
            }
            animatorSet.start();
            isAnimationRunning = true;
        }
    }

    public void stopRippleAnimation() {
        if (isRippleAnimationRunning()) {
            animatorSet.end();
            isAnimationRunning = false;
        }
    }

    public void setSpeed(int speed) {
        if (speed <= 0) return;

        rippleDurationTime = speed;
        animatorSet.setDuration(rippleDurationTime);

        // update the animations start delay duration
        int index = 0;
        for (int i = 0; i < animatorList.size(); i++) {
            int delay = rippleDurationTime / rippleNumber;
            if (i != 0 && i % 3 == 0) {
                index++;
            }
            animatorList.get(i).setStartDelay(delay * index);
        }

        isAnimationRunning = false;
        startRippleAnimation();
    }

    public boolean isRippleAnimationRunning() {
        return isAnimationRunning;
    }

    public int getDurationTime() {
        return rippleDurationTime;
    }
}
