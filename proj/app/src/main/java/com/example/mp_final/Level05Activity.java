package com.example.mp_final;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class Level05Activity extends LevelBase {

    private static final int    MARGIN          = 200;
    private static final int    TOTAL_MAX       = 50;
    private static final float  DELAY_MIN       = 0.2f;
    private static final float  DELAY_DECREASE  = 0.05f;
    private static final float  CLEAR_TIME      = 3.0f;

    private int _totalCircleCount;
    private float _delayTime;

    private float _currentTime;

    private ConstraintLayout _circleArea;

    class Level_05_Circle extends RelativeLayout
    {
        private ImageView _circle;
        private RelativeLayout _instance;

        public Level_05_Circle(Context context) {
            super(context);

            _instance = this;
            init(context);
        }

        private void init(Context c)
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.level_01_circle, this, true);

            _totalCircleCount++;
            _circle = (ImageView)this.findViewById(R.id.circle_image);
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_bigger);
            _circle.startAnimation(anim);

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_smaller);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {}
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            _circleArea.removeView(_instance);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {}
                    });
                    _circle.startAnimation(anim);

                    _isGameOver = true;
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level05);

        _circleArea = findViewById(R.id.level_05_circle_area);

        _generator = new Thread() {
            @Override
            public void run() {
                try
                {
                    if (_isGameOver)
                    {
                        _levelStatus.setVisibility(View.VISIBLE);
                        _levelStatus.setText("Game Over");
                        _handle.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LevelFail();
                                StartLevel(StartActivity.class);
                            }
                        }, 2000);
                    }
                    else if (_isGameClear)
                    {
                        _levelStatus.setVisibility(View.VISIBLE);
                        _levelStatus.setText("Game Clear!");
                        _handle.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                StartLevel(Level06Activity.class);
                            }
                        }, 2000);
                    }
                    else
                    {
                        if (_totalCircleCount < TOTAL_MAX)
                            GenerateCircle();
                    }
                }
                finally {
                    _delayTime -= DELAY_DECREASE;
                    if (_delayTime < DELAY_MIN)
                        _delayTime = DELAY_MIN;
                    _handle.postDelayed(_generator, (long)(_delayTime * 1000));
                }
            }
        };
        GameInit(5);
    }

    private Thread _timer = new Thread()
    {
        @Override
        public void run()
        {
            _currentTime -= 0.01f;
            if (_currentTime <= 0.0f)
            {
                _currentTime = 0.0f;
                _isGameClear = true;
                return;
            }
            _handle.postDelayed(_timer, 10);
        }
    };

    @Override
    protected void StartRepeat()
    {
        super.StartRepeat();
        _handle.postDelayed(_timer, 4000);
    }

    @Override
    protected void GameReset()
    {
        super.GameReset();

        _circleArea.removeAllViews();

        _totalCircleCount = 0;

        _delayTime = 1.0f;

        _currentTime = CLEAR_TIME;
    }

    public void GenerateCircle()
    {
        if (_isGameClear || _isGameOver) return;

        Level_05_Circle newCircle = new Level_05_Circle(this);

        Random rnd = new Random();
        int x, y;
        int min, max;
        min = MARGIN;
        max = _circleArea.getWidth() - MARGIN;
        x = rnd.nextInt(max - min + 1) + min;

        min = MARGIN;
        max = _circleArea.getHeight() - MARGIN;
        y = rnd.nextInt(max - min + 1) + min;

        newCircle.setX(x);
        newCircle.setY(y);

        _circleArea.addView(newCircle);
    }
}
