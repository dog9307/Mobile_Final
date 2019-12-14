package com.example.mp_final;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Random;

public class Level07Activity extends LevelBase {

    private static final int    BRIGHTNESS_MIN  = 10;
    private static final int    BRIGHTNESS_MAX  = 255;
    private static final float  ANIM_START_SEC  = 1.0f;
    private static final float  ANIM_MIN_SEC    = 0.1f;
    private static final int    SHUFFLE_MAX     = 20;

    private float _boxWidth, _boxHeight;
    private float _duration;

    private int _currentShuffleCount;

    private ConstraintLayout _boxArea;
    private ArrayList<Level_07_Box> _boxes;

    class Level_07_Box extends View
    {
        private Paint _paint;

        public Level_07_Box(Context con, float x, float y)
        {
            super(con);

            Init(x, y);
        }

        public void SetPosition(float x, float y)
        {
            setTranslationX(x);
            setTranslationY(y);
        }

        public void SetColor(int color)
        {
            _paint.setColor(color);
        }

        private void Init(float x, float y)
        {
            _paint = new Paint();
            _paint.setAntiAlias(false);
            _paint.setStyle(Paint.Style.FILL_AND_STROKE);
            int brightness = 0x44;
            try {
                brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                _paint.setColor(Color.argb(255, brightness, brightness, brightness));
            }

            SetPosition(x, y);
        }

        private void Update()
        {
        }

        private void Render(Canvas canvas)
        {
            canvas.drawRect(getX(), getY(), getX() + _boxWidth, getY() + _boxHeight, _paint);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            postInvalidate();
            Update();
            Render(canvas);
        }
    }

    class Level_07_Changer {
        private Level_07_Box _box1;
        private Level_07_Box _box2;

        Level_07_Changer(Level_07_Box b1, Level_07_Box b2)
        {
            _box1 = b1;
            _box2 = b2;

            StartAnimation(b1, b2, false);
            StartAnimation(b2, b1, true);
        }

        private void StartAnimation(Level_07_Box from, Level_07_Box to, boolean isEndEvent)
        {
            float deltaX = to.getX() - from.getX();
            float deltaY = to.getY() - from.getY();
            TranslateAnimation anim = new TranslateAnimation(
                    0, deltaX,
                    0, deltaY);
            anim.setDuration((long)(_duration * 1000));
            anim.setFillAfter(true);
            anim.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), android.R.anim.accelerate_decelerate_interpolator));

            if (isEndEvent)
            {
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        _currentShuffleCount++;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        float x, y;
                        x = _box1.getX();
                        y = _box1.getY();

                        _box1.SetPosition(_box2.getX(), _box2.getY());
                        _box2.SetPosition(x, y);

                        if (_currentShuffleCount < SHUFFLE_MAX)
                        {
                            //_duration -= 0.1f;
                            if (_duration < ANIM_MIN_SEC)
                                _duration = ANIM_MIN_SEC;

                            Shuffle();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }

            from.startAnimation(anim);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level07);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        _boxWidth = point.x / 5;
        _boxHeight = point.y / 5;

        _generator = new Thread() {
            @Override
            public void run() {
                try
                {
                    Shuffle();
                }
                finally {
                }
            }
        };
        GameInit(7);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (hasFocus)
        {
            GameReset();
        }
    }

    @Override
    protected void GameInit(int level)
    {
        _level = level;
        _levelStatus = findViewById(R.id.level_status);
        _handle = new Handler();
        StartRepeat();
    }

    @Override
    protected void GameReset()
    {
        super.GameReset();

        _boxArea = findViewById(R.id.level_07_box_area);
        _boxArea.removeAllViews();

        if (_boxes == null)
            _boxes = new ArrayList<>();
        _boxes.clear();

        Level_07_Box temp;
        temp = new Level_07_Box(getApplicationContext(), 100, 100);
        temp.SetColor(Color.RED);
        _boxArea.addView(temp);
        _boxes.add(temp);

        temp = new Level_07_Box(getApplicationContext(), 100 + _boxWidth, 100);
        temp.SetColor(Color.GREEN);
        _boxArea.addView(temp);
        _boxes.add(temp);

        temp = new Level_07_Box(getApplicationContext(), 100, 100 + _boxHeight);
        temp.SetColor(Color.BLUE);
        _boxArea.addView(temp);
        _boxes.add(temp);

        temp = new Level_07_Box(getApplicationContext(), 100 + _boxWidth, 100 + _boxHeight);
        temp.SetColor(Color.BLACK);
        _boxArea.addView(temp);
        _boxes.add(temp);

        _duration = ANIM_START_SEC;

        _currentShuffleCount = 0;
    }

    private void Shuffle()
    {
        Random rnd = new Random();
        int from = rnd.nextInt(_boxes.size());
        int to;
        do {
            to = rnd.nextInt(_boxes.size());
        } while (to == from);

        Level_07_Changer changer = new Level_07_Changer(_boxes.get(from), _boxes.get(to));
    }
}
