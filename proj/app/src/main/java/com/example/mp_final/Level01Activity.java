package com.example.mp_final;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class Level01Activity extends LevelBase {

    private static final int    MARGIN = 200;
    private static final int    TOTAL_MAX = 3;
    private static final int    GAMEOVER_COUNT = 3;
    private static final float  DELAY_MIN = 0.2f;
    private static final float  DELAY_DECREASE = 0.05f;

    private int _totalCircleCount;
    private int _currentCircleCount;
    private boolean _isGameOver;
    private boolean _isGameClear;
    private ConstraintLayout _circleArea;

    private TextView _levelStatus;

    private float _delayTime;

    class Level_01_circle extends RelativeLayout
    {
        private ImageView _circle;
        private RelativeLayout _instance;

        public Level_01_circle(Context context) {
            super(context);

            _instance = this;
            init(context);
        }

        private void init(Context c)
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.level_01_circle, this, true);

            _circle = (ImageView)this.findViewById(R.id.circle_image);
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_bigger);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    _currentCircleCount++;
                    _totalCircleCount++;

                    if (_currentCircleCount > GAMEOVER_COUNT)
                        _isGameOver = true;
                }
                @Override
                public void onAnimationEnd(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            _circle.startAnimation(anim);

            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    _currentCircleCount--;
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

                    if (_currentCircleCount <= 0 && _totalCircleCount >= TOTAL_MAX)
                        _isGameClear = true;
                }
            });
        }
    }

    private Handler _handle;
    private Thread _generator = new Thread() {
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
                            try {
                                _generator.join();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                return;
                            }

                            _handle.removeCallbacks(_gameStarter);
                            _handle.removeCallbacks(_generator);

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
                            try {
                                _generator.join();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                return;
                            }

                            _handle.removeCallbacks(_gameStarter);
                            _handle.removeCallbacks(_generator);

                            StartLevel(Level02Activity.class);
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

    private GameStarter _gameStarter;
    private void StartRepeat()
    {
        _gameStarter = new GameStarter(_generator, _levelStatus, _handle);
        _handle.postDelayed(_gameStarter, 1000);
    }

    private void GameReset()
    {
        _levelStatus.setText(3 + "");
        _circleArea.removeAllViews();

        _currentCircleCount = 0;
        _totalCircleCount = 0;

        _isGameOver = false;
        _isGameClear = false;

        _delayTime = 1.0f;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level01);

        _level = 1;

        _circleArea = findViewById(R.id.level_01_circle_area);

        _levelStatus = findViewById(R.id.level_status);
        _handle = new Handler();
        GameReset();
        StartRepeat();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        _handle.removeCallbacks(_gameStarter);
        _handle.removeCallbacks(_generator);
    }

    public void GenerateCircle()
    {
        Level_01_circle newCircle = new Level_01_circle(this);

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
