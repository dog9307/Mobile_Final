package com.example.mp_final;

import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class GameStarter extends Thread {

    private Thread _generator;
    private TextView _levelStatus;
    private Handler _handle;

    GameStarter (Thread gene, TextView ls, Handler h)
    {
        _generator = gene;
        _levelStatus = ls;
        _handle = h;
    }

    @Override
    public void run() {
        if (_levelStatus.getText().toString().equals("3"))
        {
            _levelStatus.setText(2 + "");
            _handle.postDelayed(this, 1000);
        }
        else if (_levelStatus.getText().toString().equals("2"))
        {
            _levelStatus.setText(1 + "");
            _handle.postDelayed(this, 1000);
        }
        else if (_levelStatus.getText().toString().equals("1"))
        {
            _levelStatus.setText("Start!");
            _handle.postDelayed(this, 1000);
        }
        else
        {
            _levelStatus.setVisibility(View.GONE);
            if (_generator != null)
                _handle.postDelayed(_generator, 1000);
        }
    }

}
