package com.example.mp_final;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class FInalActivity extends Activity {

    private static String FULL_MESSAGE;
    private static final long ANIM_SPEED = 200;

    private int _currentLength;
    private String _currentMessage;

    private TextView _message;
    private Handler _handle;
    private Thread _textAnim = new Thread()
    {
        @Override
        public void run()
        {
            if (_currentLength < FULL_MESSAGE.length())
            {
                _currentMessage += FULL_MESSAGE.charAt(_currentLength);
                _currentLength++;
                _message.setText(_currentMessage);
                _handle.postDelayed(this, ANIM_SPEED);
            }
            else
            {
                _handle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplication(), StartActivity.class));
                        finish();
                    }
                }, 2000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        SharedPreferences file = getSharedPreferences(getString(R.string.gameData), MODE_PRIVATE);
        SharedPreferences.Editor edit = file.edit();

        edit.putBoolean(getString(R.string.all_clear), true);

        edit.commit();

        FULL_MESSAGE = getString(R.string.final_message);

        _message = findViewById(R.id.final_message);
        _currentMessage = "";
        _currentLength = 0;

        _handle = new Handler();
        _handle.postDelayed(_textAnim, ANIM_SPEED);
    }
}
