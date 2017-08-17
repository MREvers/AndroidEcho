package com.example.matthew.androidecho;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;
import java.lang.Integer;

public class MainActivity extends AppCompatActivity {
    CommManager commManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tb = (TextView) findViewById(R.id.my_chat_box);

        commManager = new CommManager(tb);
        commManager.Begin();

        // Initialize the EditText behavior
        initEditTextOperation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initEditTextOperation(){
        EditText editText = (EditText) findViewById(R.id.my_text_input);

        // The main activity EditText object needs to have input type "text",
        // otherwise, the IME_ACTION_SEND will never fire.
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = actionId == EditorInfo.IME_ACTION_SEND;

                TextView IPView = (TextView) findViewById(R.id.my_ip_input);
                TextView PortView = (TextView) findViewById(R.id.my_port_input);

                String IP = IPView.getText().toString();
                try{
                    Integer Port = Integer.parseInt(PortView.getText().toString());
                    if (Port < Short.MAX_VALUE*2 - 1){
                        commManager.SendMessage(IP, Port, v.getText().toString());
                    }
                }catch (NumberFormatException e){
                    Snackbar.make(v, "Port Not a Number", Snackbar.LENGTH_LONG)
                            .setAction("PNaN", null).show();
                }

                v.setText("");
                return handled;
            }
        });
    }
}
