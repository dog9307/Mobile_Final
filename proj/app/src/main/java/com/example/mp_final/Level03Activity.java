package com.example.mp_final;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Level03Activity extends LevelBase {

    @Override
    protected void GameReset()
    {
        super.GameReset();
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

                                StartLevel(StartActivity.class);
                            }
                        }, 2000);
                    }
                    else
                    {
                    }
                }
                finally {
                    _handle.postDelayed(_generator, 1000);
                }
            }
        };
        GameInit(3);
    }
}
