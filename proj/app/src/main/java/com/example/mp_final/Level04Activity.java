package com.example.mp_final;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Level04Activity extends LevelBase {

    private ConstraintLayout _circleArea;

    class Level_04_Circle extends View
    {
        private Paint _paint;

        private float _cx, _cy;
        private float _radius;

        private Animation _smaller;
        private boolean _isAnimStarted;

        public Level_04_Circle(Context con)
        {
            super(con);

            Init();
        }

        private void HitDamager(float damage)
        {
            _radius -= damage;
            if (_radius < 0.0f)
            {
                _radius = 0.0f;
                _isGameClear = true;
            }
        }

        private void Init()
        {
            _paint = new Paint();
            _paint.setAntiAlias(false);
            _paint.setStyle(Paint.Style.FILL_AND_STROKE);
            _paint.setColor(Color.DKGRAY);

            _cx = _circleArea.getWidth() / 2;
            _cy = _circleArea.getHeight() / 2;

            _radius = 100.0f;

            _isAnimStarted = false;
            _smaller = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_smaller);

            this.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (_isGameClear || _isGameOver) return false;

                        switch (event.getAction())
                        {
                            case MotionEvent.ACTION_DOWN:
                                HitDamager(5.0f);
                                break;
                        }

                        return true;
                    }
                }
            );
        }

        public void StartAnim()
        {
            if (_isAnimStarted) return;

            _isAnimStarted = true;
            startAnimation(_smaller);
        }

        public void Bigger()
        {
            if (_isGameOver || _isGameClear) return;

            _radius++;
            if (_radius >= _circleArea.getWidth() / 2)
            {
                _radius = _circleArea.getWidth() / 2;
                _isGameOver = true;
            }
        }

        private void Render(Canvas canvas)
        {
            canvas.drawOval(_cx - _radius, _cy - _radius, _cx + _radius, _cy + _radius, _paint);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            postInvalidate();
            Render(canvas);
        }
    }
    private Level_04_Circle _enemy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level04);

        _generator = new Thread() {
            @Override
            public void run() {
                try
                {
                    if (_isGameOver)
                    {
                        _enemy.StartAnim();

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
                                StartLevel(StartActivity.class);
                            }
                        }, 2000);
                    }
                    else
                        _enemy.Bigger();
                }
                finally {
                    _handle.postDelayed(_generator, (long)((1.0f / 60.0f) * 1000));
                }
            }
        };
        GameInit(4);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
        {
            GameReset();
        }
    }

    @Override
    protected void GameReset()
    {
        super.GameReset();

        _circleArea = findViewById(R.id.level_03_circle_area);
        _circleArea.removeAllViews();

        _enemy = new Level_04_Circle(this);
        _circleArea.addView(_enemy);
    }
}
