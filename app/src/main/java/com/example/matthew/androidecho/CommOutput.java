package com.example.matthew.androidecho;

import android.app.Activity;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Matthew on 8/16/2017.
 */

public class CommOutput {
    TextView TextOutput;
    Activity Act;

    public CommOutput(TextView aOutput){
        TextOutput = aOutput;
        Act = new Activity();
    }

    public void Send(String msg){
        final String szMsg = msg;
        Act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextOutput.append(szMsg + "\n");
            }
        });
    }
}
