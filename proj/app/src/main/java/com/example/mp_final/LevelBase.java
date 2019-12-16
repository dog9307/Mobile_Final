package com.example.mp_final;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.TextView;

public class LevelBase extends Activity {

    protected int _level = 0;
    protected TextView _levelStatus;

    protected boolean _isGameClear = false;
    protected boolean _isGameOver = false;

    protected boolean _isAllClear = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if( event.getAction() == KeyEvent.ACTION_DOWN )
        {
            if( keyCode == KeyEvent.KEYCODE_BACK )
                return false;
        }
        return super.onKeyDown( keyCode, event );
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        LevelFail();

        if (_gameStarter != null)
            _handle.removeCallbacks(_gameStarter);
        if (_generator != null)
            _handle.removeCallbacks(_generator);
    }

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);

        SharedPreferences file = getSharedPreferences(getString(R.string.gameData), MODE_PRIVATE);

        _isAllClear = file.getBoolean(getString(R.string.all_clear), false);
    }

    protected void LevelFail()
    {
        SharedPreferences file = getSharedPreferences(getString(R.string.gameData), MODE_PRIVATE);
        SharedPreferences.Editor edit = file.edit();

        edit.putInt(getString(R.string.last_level), _level);

        edit.commit();
    }

    protected void StartLevel(Class<?> cls)
    {
        if (_isAllClear)
            cls = SelectStageActivity.class;

        startActivity(new Intent(getApplication(), cls));
        finish();
    }

    protected Handler _handle;
    protected Thread _generator;

    protected GameStarter _gameStarter;
    protected void StartRepeat()
    {
        _gameStarter = new GameStarter(_generator, _levelStatus, _handle);
        _handle.postDelayed(_gameStarter, 1000);
    }

    protected void GameReset()
    {
        _levelStatus.setText(3 + "");

        _isGameClear = false;
        _isGameOver = false;
    }

    protected void GameInit(int level)
    {
        _level = level;
        _levelStatus = findViewById(R.id.level_status);
        _handle = new Handler();
        GameReset();
        StartRepeat();
    }
}
