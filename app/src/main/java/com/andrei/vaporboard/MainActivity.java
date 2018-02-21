package com.andrei.vaporboard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class MainActivity extends Activity {

    SeekBar keyHeightSeekBar;
    SharedPreferences mSettings;
    TextView keyHeightText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final MainActivity context = this;

        Button enableBttn = (Button) findViewById(R.id.enableBttn);
        enableBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                startActivity(in);
            }
        });

        Button switchPrevBttn = (Button) findViewById(R.id.switchPrevBttn);
        switchPrevBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Button setKeyBttn = (Button) findViewById(R.id.setKeyBttn);
        setKeyBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                imeManager.showInputMethodPicker();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "VAPE NATION", Snackbar.LENGTH_LONG)
                        .setAction("K", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
            }
        });
        mSettings = getApplicationContext().getSharedPreferences("vaporKbSettings", 0);
        final SharedPreferences.Editor sharedPreferences = mSettings.edit();
        keyHeightText = (TextView) findViewById(R.id.keyHeightText);
        keyHeightSeekBar = (SeekBar) findViewById(R.id.keyHeightSeekbar);
        sharedPreferences.putInt("keyheight", keyHeightSeekBar.getProgress());
        sharedPreferences.commit();
        keyHeightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sharedPreferences.putInt("keyheight", i);
                sharedPreferences.commit();
                Integer height = mSettings.getInt("keyheight", 0);
                System.out.println(height);
                keyHeightText.setText(height.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        keyboardHeightSeek = (SeekBar) findViewById(R.id.keyboardHeightSeekbar);
        keyboardHeightText = (TextView) findViewById(R.id.keyboardHeightText);
        sharedPreferences.putInt("keyboardheight", keyboardHeightSeek.getProgress());
        sharedPreferences.commit();
        keyboardHeightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sharedPreferences.putInt("keyboardheight", i);
                sharedPreferences.commit();
                Integer keyboardheight = mSettings.getInt("keyboardheight", 0);
                System.out.println("Keyboard Height " + keyboardheight);
                keyboardHeightText.setText(keyboardheight.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Button bgColorPicker = (Button) findViewById(R.id.bgColorPicker);
        bgColorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogBuilder
                        .with(context)
                        .setTitle("Choose color")
                        .initialColor(backgroundColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                backgroundColor = selectedColor;
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                backgroundColor = selectedColor;
                                sharedPreferences.putInt("backgroundcolor", selectedColor);
                                sharedPreferences.commit();
                                System.out.println(selectedColor);
                                SimpleIME sm = new SimpleIME();
                                sm.setBackgroundColor();
                                hideKeyboard();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .lightnessSliderOnly()
                        .build()
                        .show();
            }
        });
    }


    Integer backgroundColor = Color.parseColor("#ffcccc");

    SeekBar keyboardHeightSeek;
    TextView keyboardHeightText;


    @Override
    protected void onResume() {
        super.onResume();
        if (mSettings.contains("keyheight")){
            keyHeightSeekBar.setProgress(mSettings.getInt("keyheight", 0));
        }
        if (mSettings.contains("keyboardheight")){
            keyboardHeightSeek.setProgress(mSettings.getInt("keyboardheight", 0));
        }


    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        imm.showSoftInput(view, 0);
    }


}
