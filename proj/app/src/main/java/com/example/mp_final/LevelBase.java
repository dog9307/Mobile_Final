package com.example.mp_final;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;

public class LevelBase extends Activity {

    protected int _level = 0;

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
    }

    protected void LevelFail()
    {
        SharedPreferences file = getSharedPreferences("LastLevel", MODE_PRIVATE);
        SharedPreferences.Editor edit = file.edit();

        edit.putInt("last", _level);

        edit.commit();
    }

    protected void StartLevel(Class<?> cls)
    {
        startActivity(new Intent(getApplication(), cls));
        finish();
    }
}
