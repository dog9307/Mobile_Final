package com.example.mp_final;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Level06Activity extends LevelBase {

    private static final int    ONEDAY = 24 * 3600;

    private TextView _hint;
    private TextView _usageCount;
    private Button _goNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level06);

        _hint = findViewById(R.id.hint);
        _usageCount = findViewById(R.id.usage_count);
        _goNext = findViewById(R.id.button_go_next);

        _hint.setVisibility(View.GONE);
        _usageCount.setVisibility(View.GONE);
        _goNext.setVisibility(View.GONE);

        if (!CheckPermission())
        {
            Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        _generator = new Thread() {
            @Override
            public void run() {
                try
                {
                    _hint.setVisibility(View.VISIBLE);
                    _usageCount.setVisibility(View.VISIBLE);
                    _goNext.setVisibility(View.VISIBLE);
                }
                finally {
                }
            }
        };
        GameInit(6);
    }

    private boolean CheckPermission()
    {
        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,android.os.Process.myUid(), getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        GetUsage();
    }

    private void GetUsage()
    {
        UsageStatsManager um = (UsageStatsManager)getSystemService(USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();

        int count = 0;
        UsageEvents usageEvents = um.queryEvents(time - ONEDAY * 1000, time);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            if (event.getPackageName().equals(getPackageName())) {
                if (event.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED)
                    count++;
            }
        }

        _usageCount.setText(count + "");
    }

    @Override
    protected void GameReset()
    {
        super.GameReset();

        GetUsage();
    }

    private void CheckGame()
    {
        _hint.setVisibility(View.GONE);
        _usageCount.setVisibility(View.GONE);
        _goNext.setVisibility(View.GONE);

        int count = Integer.parseInt(_usageCount.getText().toString());

        if (count % 10 != 0)
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
        else
        {
            _levelStatus.setVisibility(View.VISIBLE);
            _levelStatus.setText("Game Clear!");
            _handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    StartLevel(Level07Activity.class);
                }
            }, 2000);
        }
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_go_next:
                CheckGame();
                break;
        }
    }
}
