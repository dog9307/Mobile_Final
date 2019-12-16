package com.example.mp_final;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SelectStageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_stage);
    }

    public void onClick(View v)
    {
        Class<?> cls = null;
        switch (v.getId())
        {
            case R.id.stage_1:
                cls = Level01Activity.class;
                break;

            case R.id.stage_2:
                cls = Level02Activity.class;
                break;

            case R.id.stage_3:
                cls = Level03Activity.class;
                break;

            case R.id.stage_4:
                cls = Level04Activity.class;
                break;

            case R.id.stage_5:
                cls = Level05Activity.class;
                break;

            case R.id.stage_6:
                cls = Level06Activity.class;
                break;

            case R.id.stage_7:
                cls = Level07Activity.class;
                break;

            case R.id.button_back:
                cls = StartActivity.class;
                break;
        }

        if (cls != null)
        {
            startActivity(new Intent(getApplication(), cls));
            finish();
        }
    }
}
