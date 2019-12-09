package com.example.mp_final;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Random;

public class Level02Activity extends LevelBase {

    private static final int    TOTAL_MAX = 1;
    private static final float  MAX_SPEED = 100.0f;

    private CheckBox[] _checks = new CheckBox[4];
    private TextView _levelStatus;

    private int _noteWidth;
    private int _noteHeight;

    private float _noteSpeed;

    private ConstraintLayout _noteArea;
    private ArrayList<Level_02_Note> _notes;

    private int _totalNoteCount;
    private int _currentNoteCount;
    private boolean _isGameOver;
    private boolean _isGameClear;

    class Level_02_Note extends View
    {
        private Paint _paint;

        private float _cx, _cy;
        private int _width, _height;

        private boolean _isActive;

        public Level_02_Note(Context context, int x, int y)
        {
            super(context);

            _totalNoteCount++;
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
            _width = _noteWidth;
            _height = _noteHeight;

            _isActive = true;
        }

        private void Update()
        {
            _cy += _noteSpeed;

            /*
            충돌 처리 부분
            */

            if (!_isActive)
            {
                setVisibility(View.GONE);
                RemoveNote(this);
            }
        }

        private void Render(Canvas canvas)
        {
            canvas.drawRect(_cx - _width / 2, _cy - _height / 2, _cx + _width / 2, _cy + _height / 2, _paint);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            postInvalidate();
            Update();
            Render(canvas);
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
                    if (_totalNoteCount < TOTAL_MAX)
                        GenerateNote();
                }
            }
            finally {
                _noteSpeed += 10.0f;
                if (_noteSpeed > MAX_SPEED)
                    _noteSpeed = MAX_SPEED;

                _handle.postDelayed(_generator, 500);
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

        _noteSpeed = 10.0f;
        if (_notes == null)
            _notes = new ArrayList<>();
        _notes.clear();
        _noteArea = findViewById(R.id.level_02_note_area);
        _noteArea.removeAllViews();

        _totalNoteCount = 0;
        _currentNoteCount = 0;

        _isGameClear = false;
        _isGameOver = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level02);

        _level = 2;

        _checks[0] = findViewById(R.id.checkbox_0);
        _checks[1] = findViewById(R.id.checkbox_1);
        _checks[2] = findViewById(R.id.checkbox_2);
        _checks[3] = findViewById(R.id.checkbox_3);

        _levelStatus = findViewById(R.id.level_status);
        _handle = new Handler();
        GameReset();
        StartRepeat();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
        {
            _noteWidth = _checks[0].getWidth() - 10;
            _noteHeight = _checks[0].getHeight() - 10;
        }
    }

    private void GenerateNote()
    {
        Random rnd = new Random();
        int index = rnd.nextInt(_checks.length);
        int x = _checks[0].getLeft() + _noteWidth / 2 + _noteWidth * index;
        Level_02_Note newNote = new Level_02_Note(this, x, -_noteHeight);
        _noteArea.addView(newNote);
        _notes.add(newNote);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.checkbox_0:
            case R.id.checkbox_1:
            case R.id.checkbox_2:
            case R.id.checkbox_3:
                for (int i = 0; i < _checks.length; ++i)
                {
                    if (_checks[i].getId() != v.getId())
                        _checks[i].setChecked(false);
                }
                break;
        }
    }

    public void RemoveNote(Level_02_Note note)
    {
        for (Level_02_Note check : _notes)
        {
            if (check == note)
            {
                _notes.remove(note);
                return;
            }
        }
    }
}
