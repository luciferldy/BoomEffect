package io.liandy.boomeffect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lian_ on 2017/11/20.
 */

public class ExplosionField extends View {

    private static final String LOG_TAG = ExplosionField.class.getSimpleName();
    int part = 300;
    ArrayList<Dust> dustL = new ArrayList<>();
    Paint mPaint;
    int count;
    private int height;
    private int width;
    private float gravity = 0.2f;
    private int speed = 20;
    private boolean hasWind = true;

    public ExplosionField(Context context) {
        super(context);
        init();
    }

    public ExplosionField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Dust d: dustL) {
            if (d == null) {
                Log.d("ExplosionFiled", "dust list isnot init.");
                continue;
            }
            int alpha = (int) (255 * d.alpha);
            mPaint.setAlpha(alpha);
            if (d.directionTip < 0) {
                // x轴反向
                canvas.drawCircle(d.x - d.transX, d.y + d.transY, d.radius, mPaint);
            } else {
                canvas.drawCircle(d.x + d.transX, d.y + d.transY, d.radius, mPaint);
            }
        }
    }

    // 爆裂
    public void explode() {
        // 初始化
        dustL.clear();
        Random random = new Random();
        for (int i = 0; i < part; i++) {
            Dust d = new Dust();
            d.direction = random.nextFloat() * 120 + 30;  // 逆时针 30~ 150
            d.direction = (float) (d.direction * Math.PI / 180);  // 角度转弧度
            // 是否小于0，方向标记
            if (Math.cos(d.direction) < 0) {
                d.directionTip = -1;
            } else {
                d.directionTip = 1;
            }
            // 初始速度，每个单位时间 0~20 px
            d.speed = random.nextFloat() * speed * 0.5f + speed * 0.5f;
            d.speedX = Math.abs((float) Math.cos(d.direction) * d.speed);
            d.speedY = -(float) Math.sin(d.direction) * d.speed;
            // 爆裂点换做一个区域
            // x: -10~10 y: -10~10
            float tmpX = random.nextFloat() * 20 - 10;
            float tmpY = random.nextFloat() * 20 - 10;
            d.x = getWidth()/2 + tmpX;
            d.y = getHeight()/2 + tmpY;

            Log.d(LOG_TAG, String.format("d.direction %f, " +
                    "d.speed %f," +
                    "d.speedX %f," +
                    "d.speedY: %f", d.direction, d.speed, d.speedX, d.speedY));
            // 完全不透明
            d.alpha = 1;
            d.gravity = gravity;
            dustL.add(d);
        }

        // 共有 100 个单位时间
        ValueAnimator animator = ValueAnimator.ofInt(0, 60).setDuration(1500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            int prev = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                count++;
                int value = (int) animation.getAnimatedValue();
                // value 相同渲染没有意义
                if (value == prev) {
                    return;
                }
                Log.d(LOG_TAG, String.format("Animation value: %d ", value));
                for (Dust d: dustL) {
                    if (hasWind) {
                        if (d.speedX - d.wind * value > 0) {
                            d.transX = d.speedX * value - 0.5f * d.wind * value * value;
                        }
                    } else {
                        d.transX = d.speedX * value;
                    }

                    Log.d(LOG_TAG, String.format("d.transX: %f ", d.transX));
                    d.transY = d.speedY * value + d.gravity * value * value;
                    d.alpha = 1 - value / 60.0f;
                }
                invalidate();
                prev = value;
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(LOG_TAG, String.format("count: %d", count));
            }

            @Override
            public void onAnimationStart(Animator animation) {
                count = 0;
            }
        });
        animator.start();
    }

    // 抖动
    void shake() {

    }

    public void setPart(int part) {
        this.part = part;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setWind(boolean hasWind) {
        this.hasWind = hasWind;
    }

    /**
     * 每一粒尘埃有一个初始的方向和速度，要模拟重力作用
     */
    class Dust {
        float speed;
        float speedX;
        float speedY;
        float wind = 0.2f;  // 阻力
        float gravity = 0.4f; // 重力加速度
        float x, y;
        float transX = 0, transY = 0;
        float direction;
        int directionTip;
        float alpha;
        float radius = 5;
    }
}
