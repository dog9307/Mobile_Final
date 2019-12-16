package com.example.mp_final;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Random;

public class Level03Activity extends LevelBase {

    private static final int    TOTAL_MAX       = 3;
    private static final int    SPEED_MIN       = 5;
    private static final int    SPEED_MAX       = 20;
    private static final int    PLAYER_SPEED    = 20;
    private static final float  CLEAR_TIME      = 3.0f;

    private float _circleRadius;
    private int _totalCircleCount;
    private ConstraintLayout _circleArea;

    private ArrayList<Level_03_Circle> _circles;

    private TextView _time;
    private float _currentTime;

    class Level03_Player extends View
    {
        private Paint _paint;
        private float _cx, _cy;
        private float _radius;

        private float _destX;
        private float _destY;

        public void SetDest(float dx, float dy) { _destX = dx; _destY = dy; }
        public float GetX() { return _cx; }
        public float GetY() { return _cy; }
        public float GetRadius() { return _radius; }

        private float _speed;

        public Level03_Player(Context con)
        {
            super(con);

            Init();
        }

        private void Init()
        {
            _paint = new Paint();
            _paint.setAntiAlias(false);
            _paint.setStyle(Paint.Style.FILL_AND_STROKE);
            _paint.setColor(Color.RED);

            _cx = _circleArea.getWidth() / 2;
            _cy = _circleArea.getHeight() / 2;
            _destX = _cx;
            _destY = _cy;

            _radius = 50.0f;

            _speed = PLAYER_SPEED;

            _circleRadius = _radius * 0.8f;

            _circleArea.addView(this);
        }

        private void Update()
        {
            if (_isGameClear || _isGameOver) return;

            float dirX = _destX - _cx;
            float dirY = _destY - _cy;

            float mag = (float)Math.sqrt(dirX * dirX + dirY * dirY);
            if (mag < _speed)
                return;

            dirX /= mag;
            dirY /= mag;

            _cx += dirX * _speed;
            _cy += dirY * _speed;
        }

        private void Render(Canvas can)
        {
            can.drawOval(_cx - _radius, _cy - _radius, _cx + _radius, _cy + _radius, _paint);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            postInvalidate();
            Update();
            Render(canvas);
        }
    }
    private Level03_Player _player;


    class Level_03_Circle extends View
    {
        private Paint _paint;

        private float _cx, _cy;
        private float _radius;
        private float _speed;
        private float _angle;

        private boolean _isActive;

        public Level_03_Circle(Context context, int x, int y)
        {
            super(context);

            _totalCircleCount++;
            Init(x, y);
        }

        private void Init(int cx, int cy)
        {
            _paint = new Paint();
            _paint.setAntiAlias(false);
            _paint.setStyle(Paint.Style.FILL_AND_STROKE);
            _paint.setColor(Color.DKGRAY);

            _cx = cx;
            _cy = cy;
            _radius = _circleRadius;

            Random rnd = new Random();
            _angle = rnd.nextInt(360);

            _speed = rnd.nextInt(SPEED_MAX - SPEED_MIN + 1) + SPEED_MIN;

            _isActive = true;
        }

        private void Update()
        {
            if (_isGameClear || _isGameOver) return;

            _cx += Math.cos(_angle * (Math.PI / 180.0f)) * _speed;
            _cy -= Math.sin(_angle * (Math.PI / 180.0f)) * _speed;

            _cx += _circleArea.getWidth();
            _cx %= _circleArea.getWidth();

            _cy += _circleArea.getHeight();
            _cy %= _circleArea.getHeight();

            float disX = _player.GetX() - _cx;
            float disY = _player.GetY() - _cy;
            float distance = (float)Math.sqrt(disX * disX + disY * disY);
            if (distance < (_radius + _player.GetRadius()) * 0.8f)
                _isGameOver = true;
        }

        private void Render(Canvas canvas)
        {
            canvas.drawOval(_cx - _radius, _cy - _radius, _cx + _radius, _cy + _radius, _paint);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            postInvalidate();
            Update();
            Render(canvas);
        }
    }

    @Override
    protected void GameReset()
    {
        super.GameReset();

        _circleArea = findViewById(R.id.level_03_circle_area);
        _circleArea.removeAllViews();
        _circleArea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (_isGameClear || _isGameOver) return false;

                _player.SetDest(event.getX(), event.getY());

                return true;
            }
        });

        if (_circles == null)
            _circles = new ArrayList<>();
        _circles.clear();

        _totalCircleCount = 0;

        _currentTime = CLEAR_TIME;
        _time = findViewById(R.id.level_03_time);
        _time.setVisibility(View.GONE);
    }

    private Thread _timer = new Thread()
    {
        @Override
        public void run()
        {
            if (_isGameClear || _isGameOver) return;
            _time.setVisibility(View.VISIBLE);
            _currentTime -= 0.01f;
            if (_currentTime <= 0.0f)
            {
                _currentTime = 0.0f;
                _isGameClear = true;
                return;
            }
            _time.setText(String.format("%.2f", _currentTime));
            _handle.postDelayed(_timer, 10);
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
        {
            GameReset();
            _player = new Level03_Player(this);
        }
    }

    @Override
    protected void StartRepeat()
    {
        super.StartRepeat();
        _handle.postDelayed(_timer, 4000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level03);

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
                                StartLevel(Level04Activity.class);
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
                    _handle.postDelayed(_generator, 1000);
                }
            }
        };
        GameInit(3);
    }

    private void GenerateCircle()
    {
        Random rnd = new Random();
        int x, y;
        int min, max;
        float length;
        do {
            min = 0;
            max = _circleArea.getWidth();
            x = rnd.nextInt(max - min + 1) + min;

            min = 0;
            max = _circleArea.getHeight();
            y = rnd.nextInt(max - min + 1) + min;

            float disX = _player.GetX() - x;
            float disY = _player.GetY() - y;
            length = (float)Math.sqrt(disX * disX + disY * disY);
        } while (length < (_player.GetRadius() + _circleRadius) * 1.2f);

        Level_03_Circle newCircle = new Level_03_Circle(this, x, y);

        _circleArea.addView(newCircle);
        _circles.add(newCircle);
    }
}
