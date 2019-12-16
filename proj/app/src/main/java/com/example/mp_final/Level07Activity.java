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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Random;

public class Level07Activity extends LevelBase {

    private static final int    BRIGHTNESS_MIN  = 50;
    private static final int    BRIGHTNESS_MAX  = 200;
    private static final float  ANIM_START_SEC  = 1.0f;
    private static final float  ANIM_MIN_SEC    = 0.1f;
    private static final int    SHUFFLE_MAX     = 20;

    private int _boxWidth, _boxHeight;
    private float _duration;

    private int _currentShuffleCount;

    private ConstraintLayout _boxArea;
    private ArrayList<Level_07_Box> _boxes;
    private Point[] _points;
    private Level_07_Enemy _enemy;

    private Button[] _buttons = new Button[4];
    private TextView[] _boxNums = new TextView[4];

    class Level_07_Enemy extends View
    {
        private Paint _paint;

        private int _cx, _cy;
        private int _radius;

        private int _selectIndex;

        public boolean CheckPosition(int index)
        {
            return (_points[index].x == _cx &&
                _points[index].y == _cy);
        }

        public Level_07_Enemy(Context con)
        {
            super(con);
        }

        public void SetTarget(int select)
        {
            _selectIndex = select;
            Init();
        }

        public void SetPosition(int cx, int cy)
        {
            _cx = cx;
            _cy = cy;
        }

        public void SetColor(int color)
        {
            _paint.setColor(color);
        }

        private void Init()
        {
            _paint = new Paint();
            _paint.setAntiAlias(false);
            _paint.setStyle(Paint.Style.FILL_AND_STROKE);
            int brightness = 0x44;
            try {
                brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);

                brightness = (brightness > BRIGHTNESS_MIN) ? brightness : BRIGHTNESS_MIN;
                brightness = (brightness < BRIGHTNESS_MAX) ? brightness : BRIGHTNESS_MAX;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                SetColor(Color.argb(255, brightness, brightness, brightness));
            }

            SetPosition(_boxes.get(_selectIndex).GetX(), _boxes.get(_selectIndex).GetY());

            _radius = _boxWidth / 4;
        }

        private void Update()
        {
            int brightness = 0x44;
            try {
                brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);

                brightness = (brightness > BRIGHTNESS_MIN) ? brightness : BRIGHTNESS_MIN;
                brightness = (brightness < BRIGHTNESS_MAX) ? brightness : BRIGHTNESS_MAX;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                SetColor(Color.argb(255, brightness, brightness, brightness));
            }
            SetPosition(_boxes.get(_selectIndex).GetX(), _boxes.get(_selectIndex).GetY());
        }

        private void Render(Canvas canvas)
        {
            bringToFront();
            canvas.drawOval(_cx - _radius, _cy - _radius, _cx + _radius, _cy + _radius, _paint);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            if (getVisibility() == View.GONE) return;

            postInvalidate();
            Update();
            Render(canvas);
        }
    }

    class Level_07_Box extends View
    {
        private Paint _paint;

        private int _cx, _cy;

        public int GetX() { return _cx; }
        public int GetY() { return _cy; }

        public void SetPosition(int x, int y)
        {
            _cx = x;
            _cy = y;
        }

        public Level_07_Box(Context con, int x, int y)
        {
            super(con);

            Init(x, y);
        }

        public void SetColor(int color)
        {
            _paint.setColor(color);
        }

        private void Init(int x, int y)
        {
            _paint = new Paint();
            _paint.setAntiAlias(false);
            _paint.setStyle(Paint.Style.FILL_AND_STROKE);
            int brightness = 0x44;
            try {
                brightness = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);

                brightness = (brightness > BRIGHTNESS_MIN) ? brightness : BRIGHTNESS_MIN;
                brightness = (brightness < BRIGHTNESS_MAX) ? brightness : BRIGHTNESS_MAX;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                SetColor(Color.argb(255, brightness, brightness, brightness));
            }

            SetPosition(x, y);

        }

        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            setAnimation(null);
        }

        private void Update()
        {
        }

        private void Render(Canvas canvas)
        {
            canvas.drawRect(_cx - _boxWidth / 2, _cy - _boxHeight / 2, _cx + _boxWidth / 2, _cy + _boxHeight / 2, _paint);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            Update();
            Render(canvas);
        }
    }

    class Level_07_Changer {
        private Level_07_Box _box1;
        private Level_07_Box _box2;

        public Level_07_Changer(Level_07_Box b1, Level_07_Box b2)
        {
            _box1 = b1;
            _box2 = b2;

            StartAnimation(b1, b2, false);
            StartAnimation(b2, b1, true);
        }

        private void StartAnimation(Level_07_Box from, Level_07_Box to, boolean isEndEvent)
        {
            float deltaX = to.GetX() - from.GetX();
            float deltaY = to.GetY() - from.GetY();
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

                        int x, y;
                        x = _box1.GetX();
                        y = _box1.GetY();

                        _box1.SetPosition(_box2.GetX(), _box2.GetY());
                        _box2.SetPosition(x, y);

                        if (_currentShuffleCount < SHUFFLE_MAX)
                        {
                            _duration -= 0.1f;
                            if (_duration < ANIM_MIN_SEC)
                                _duration = ANIM_MIN_SEC;

                            Shuffle();
                        }
                        else
                        {
                            _enemy.setVisibility(View.VISIBLE);

                            for (int i = 0; i < _buttons.length; ++i)
                            {
                                _buttons[i].setEnabled(true);
                                _boxNums[i].setVisibility(View.VISIBLE);
                            }
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

        if (_boxWidth > _boxHeight)
            _boxWidth = _boxHeight;
        else
            _boxHeight = _boxWidth;

        _buttons[0] = findViewById(R.id.button_00);
        _buttons[1] = findViewById(R.id.button_01);
        _buttons[2] = findViewById(R.id.button_02);
        _buttons[3] = findViewById(R.id.button_03);

        _boxNums[0] = findViewById(R.id.box_0);
        _boxNums[1] = findViewById(R.id.box_1);
        _boxNums[2] = findViewById(R.id.box_2);
        _boxNums[3] = findViewById(R.id.box_3);

        for (int i = 0; i < _buttons.length; ++i)
            _buttons[i].setEnabled(false);

        _generator = new Thread() {
            @Override
            public void run() {
                StartEnemyAnim();
            }
        };
        GameInit(7);
    }

    private boolean _isResetted = false;
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (hasFocus)
        {
            if (!_isResetted)
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

        _isResetted = true;

        _boxArea = findViewById(R.id.level_07_box_area);
        _boxArea.removeAllViews();

        _enemy = new Level_07_Enemy(getApplicationContext());
        _enemy.setVisibility(View.GONE);
        _boxArea.addView(_enemy);

        if (_boxes == null)
            _boxes = new ArrayList<>();
        _boxes.clear();

        float cx = _boxArea.getWidth() / 2;
        float cy = _boxArea.getHeight() / 2;
        Level_07_Box temp;
        temp = new Level_07_Box(getApplication(), (int)(cx - _boxWidth), (int)(cy - _boxHeight));
        _boxArea.addView(temp);
        _boxes.add(temp);

        temp = new Level_07_Box(getApplication(), (int)(cx + _boxWidth), (int)(cy - _boxHeight));
        _boxArea.addView(temp);
        _boxes.add(temp);

        temp = new Level_07_Box(getApplication(), (int)(cx - _boxWidth), (int)(cy + _boxHeight));
        _boxArea.addView(temp);
        _boxes.add(temp);

        temp = new Level_07_Box(getApplication(), (int)(cx + _boxWidth), (int)(cy + _boxHeight));
        _boxArea.addView(temp);
        _boxes.add(temp);

        _points = new Point[_boxes.size()];
        for (int i = 0; i < _boxes.size(); ++i)
        {
            _points[i] = new Point(_boxes.get(i).GetX(), _boxes.get(i).GetY());
            SetBoxNumPosition(i);
        }

        _duration = ANIM_START_SEC;

        _currentShuffleCount = 0;
    }

    private void SetBoxNumPosition(int index)
    {
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(_boxWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        param.leftMargin = _points[index].x - _boxWidth / 2;
        param.topMargin = _points[index].y;
        _boxNums[index].setLayoutParams(param);
        _boxNums[index].setText((index + 1) + "");
        _boxNums[index].setVisibility(View.GONE);
    }

    private void StartEnemyAnim()
    {
        Random rnd = new Random();
        int index = rnd.nextInt(_boxes.size());

        _enemy.setVisibility(View.VISIBLE);
        _enemy.SetTarget(index);

        TranslateAnimation enemyAnim = new TranslateAnimation(
                0, 0,
                -_boxWidth, 0);
        enemyAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                _enemy.setVisibility(View.GONE);
                Shuffle();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        enemyAnim.setDuration(1000);
        enemyAnim.setFillAfter(true);
        enemyAnim.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), android.R.anim.accelerate_decelerate_interpolator));

        _enemy.startAnimation(enemyAnim);
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

    public void onClick(View v)
    {
        int index = -1;
        switch (v.getId())
        {
            case R.id.button_00:
                index = 0;
                break;

            case R.id.button_01:
                index = 1;
                break;

            case R.id.button_02:
                index = 2;
                break;

            case R.id.button_03:
                index = 3;
                break;
        }

        if (index == -1) return;

        _levelStatus.setVisibility(View.VISIBLE);
        _levelStatus.bringToFront();
        if (_enemy.CheckPosition(index))
        {
            _levelStatus.setText("Game Clear!");
            _handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    StartLevel(FInalActivity.class);
                }
            }, 2000);
        }
        else
        {
            _levelStatus.setText("Game Over");
            _handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LevelFail();
                    StartLevel(StartActivity.class);
                }
            }, 2000);
        }
    }
}
