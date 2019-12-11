package com.example.mp_final;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

    private int _lastLevel;

    private Button _start;
    private Button _restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences file = getSharedPreferences("LastLevel", MODE_PRIVATE);
        _lastLevel = file.getInt("last", -1);

        _start = findViewById(R.id.button_start);
        _restart = findViewById(R.id.button_restart);

        if (_lastLevel == -1)
        {
            _lastLevel = 1;
            _restart.setEnabled(false);
        }
        else
        {
            _start.setText("Level " + _lastLevel);
            _restart.setEnabled(true);
        }
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_start:
                switch (_lastLevel)
                {
                    case 1:
                        StartLevel(Level01Activity.class);
                        break;

                    case 2:
                        StartLevel(Level02Activity.class);
                        break;

                    case 3:
                        StartLevel(Level03Activity.class);
                        break;

                    case 4:
                        StartLevel(Level04Activity.class);
                        break;

                    case 5:
                        StartLevel(Level05Activity.class);
                        break;
                }
                break;

            case R.id.button_restart:
                StartLevel(Level01Activity.class);
                break;

            case R.id.button_quit:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }

    private void StartLevel(Class<?> cls)
    {
        startActivity(new Intent(getApplication(), cls));
        finish();
    }

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
}
