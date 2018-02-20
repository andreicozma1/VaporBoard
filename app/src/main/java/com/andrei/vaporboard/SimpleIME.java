package com.andrei.vaporboard;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SentenceSuggestionsInfo;
import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SimpleIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener, SpellCheckerSession.SpellCheckerSessionListener {

    public KeyboardView kv;
    public Keyboard keyboard;
    List<Keyboard.Key> keys;
    private Integer caps = 1;
    InputConnection ic;

    String[] splitStr;

    String lastUsedPredictedWord;
    String wordBeforeAutoCorrection;

    char lastChar;
    SharedPreferences mSettings;
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        playClick(primaryCode);

        if(ic.getTextBeforeCursor(1, 0).length() == 0){
            System.out.println("Start WORD");
        }

        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                if(ic.getTextBeforeCursor(5, 0).length() == 0 || Character.isUpperCase(lastChar)){
                    setShiftUPUnlocked();
                }
                if(lastUsedPredictedWord != null){
                    ic.deleteSurroundingText(splitStr[splitStr.length-1].length(), 0);
                    ic.commitText(wordBeforeAutoCorrection, 0);
                    lastUsedPredictedWord = null;

                }
                lastCorrections.clear();
                break;
            case Keyboard.KEYCODE_SHIFT:
                switch(caps){
                    case 0:
                        setShiftUPUnlocked();
                        break;
                    case 1:
                        setShiftUPLocked();
                        break;
                    case 2:
                        setShiftDown();
                        break;
                }
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

                break;
            case 3:
                System.out.println("Clicked on SYM key");
                if (showsymb == 2){
                    showsymb = 1;
                }else if (showsymb == 1){
                    showsymb = 0;
                } else if (showsymb == 0){
                    showsymb = 1;
            }
                setInputView(onCreateInputView());
                break;
            case 1:
                if (SystemClock.elapsedRealtime() - starttime < 500){
                    InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                    imeManager.showInputMethodPicker();
                }

                break;
            case 2:
                System.out.println("Clicked on SYM2 key");
                showsymb = 2;
                setInputView(onCreateInputView());
                break;
            case 160:
                if (String.valueOf(lastChar).contentEquals(".")){
                    setShiftUPUnlocked();
                }

                wordBeforeAutoCorrection = splitStr[splitStr.length-1].toString().trim();

                if(lastCorrections.size() > 0 && !lastCorrections.contains(splitStr[splitStr.length-1].toString().trim())){
                    try{
                        ic.deleteSurroundingText(splitStr[splitStr.length-1].length(), 0);
                        ic.commitText(lastCorrections.get(0), 0);
                        lastUsedPredictedWord = lastCorrections.get(0);
                    } catch (Exception ex){
                    }
                }
                lastCorrections.clear();
                lastUsedPredictedWord = null;
                ic.commitText(" ",1);

                break;
            case 57344:

                break;
            case 57345:
                int c1 = 0;
                while(ic.getTextBeforeCursor(1,0).toString().contentEquals(" ")){
                    c1++;
                    ic.deleteSurroundingText(1,0);
                }

                if (lastUsedPredictedWord != null){
                    ic.deleteSurroundingText(lastUsedPredictedWord.length(), 0);
                } else{
                    ic.deleteSurroundingText(splitStr[splitStr.length-1].length(), 0);
                }

                ic.commitText(keys.get(1).label.toString(),1);

                while(c1 != 0){
                    ic.commitText(" ", 0);
                    c1--;
                }

                lastUsedPredictedWord = keys.get(1).label.toString();
                break;
            case 57346:
                int c2 = 0;
                while(ic.getTextBeforeCursor(1,0).toString().contentEquals(" ")){
                    c2++;
                    ic.deleteSurroundingText(1,0);
                }

                if (lastUsedPredictedWord != null){
                    ic.deleteSurroundingText(lastUsedPredictedWord.length(), 0);
                } else{
                    ic.deleteSurroundingText(splitStr[splitStr.length-1].length(), 0);
                }

                ic.commitText(keys.get(2).label.toString(),1);
                while(c2 != 0){
                    ic.commitText(" ", 0);
                    c2--;
                }
                lastUsedPredictedWord = keys.get(1).label.toString();
                break;
            case 57347:
                int c3 = 0;
                while(ic.getTextBeforeCursor(1,0).toString().contentEquals(" ")){
                    c3++;
                    ic.deleteSurroundingText(1,0);
                }

                if (lastUsedPredictedWord != null){
                    ic.deleteSurroundingText(lastUsedPredictedWord.length(), 0);
                } else{
                    ic.deleteSurroundingText(splitStr[splitStr.length-1].length(), 0);
                }

                ic.commitText(keys.get(3).label.toString(),1);
                while(c3 != 0){
                    ic.commitText(" ", 0);
                    c3--;
                }
                lastUsedPredictedWord = keys.get(1).label.toString();
                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && (caps == 2)){
                    code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code),1);
                } else if(Character.isLetter(code) && caps == 1){
                    code = Character.toUpperCase(code);
                    ic.commitText(String.valueOf(code),1);
                    setShiftDown();
                }else{
                    ic.commitText(String.valueOf(code),1);
                }
                lastChar = ic.getTextBeforeCursor(1,0).charAt(0);
        }
        lastUsedPredictedWord = null;
        String str = ic.getTextBeforeCursor(5000, 0).toString();
        splitStr = str.split("\\s+");
        System.out.println(splitStr.length);
        fetchSuggestionsFor(splitStr[splitStr.length - 1]);
    }

    private void fetchSuggestionsFor(String input){
        try{
            TextServicesManager tsm =
                    (TextServicesManager) getSystemService(TEXT_SERVICES_MANAGER_SERVICE);

            SpellCheckerSession session =
                    tsm.newSpellCheckerSession(null, Locale.ENGLISH, this, true);
            session.getSentenceSuggestions(
                    new TextInfo[]{ new TextInfo(input) },
                    5
            );
        } catch (Exception ex){

        }

    }
    void setShiftDown(){
        try{
            System.out.println("Set to lowercase");
            keys.get(keyboard.getShiftKeyIndex()).icon = getResources().getDrawable(R.drawable.shift_lock);
            keyboard.setShifted(false);
            caps = 0;
            kv.invalidateAllKeys();
        } catch(Exception ex){
            ex.printStackTrace();
        }

    }
    void setShiftUPUnlocked(){
        try{
        System.out.println("Set to uppercase then lowercase");
        keys.get(keyboard.getShiftKeyIndex()).icon = getResources().getDrawable(R.drawable.shift_caps_2);
        keyboard.setShifted(true);
        caps = 1;
        kv.invalidateAllKeys();
    } catch(Exception ex){
        ex.printStackTrace();
    }
    }
    void setShiftUPLocked(){
        try{
        System.out.println("Set to uppercase locked");
        keys.get(keyboard.getShiftKeyIndex()).icon = getResources().getDrawable(R.drawable.caps_lock);
        keyboard.setShifted(true);
        caps = 2;
        kv.invalidateAllKeys();
    } catch(Exception ex){
        ex.printStackTrace();
        }
    }

    float starttime;
    Boolean isPressing = false;

    @Override
    public void onPress(int primaryCode) {
        switch(primaryCode){
            case 1:
                System.out.println("Key pressed");
                starttime = SystemClock.elapsedRealtime();
                isPressing = true;
                break;
        }
    }

    @Override
    public void onRelease(int primaryCode) {
        switch(primaryCode){
            case 1:
                if (SystemClock.elapsedRealtime() - starttime > 500){
                    System.out.println("Key long pressed");

                }
                isPressing=false;
                break;
        }
    }

    @Override
    public void onText(CharSequence text) {
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }


    Integer showsymb = 1;
    Boolean showsymb2 = false;

    @Override
    public void swipeUp() {
    }
    @Override
    public View onCreateInputView() {
        ic = getCurrentInputConnection();

        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.layout, null);
        mSettings = getApplicationContext().getSharedPreferences("vaporKbSettings", 0);
        switch (showsymb){
            case 0:
                keyboard = new Keyboard(this, R.xml.symbols);
                break;
            case 1:
                keyboard = new Keyboard(this, R.xml.qwerty);
                break;
            case 2:
                if (showsymb2 == false){
                    keyboard = new Keyboard(this, R.xml.symbols2);
                    showsymb2 = true;
                } else{
                    keyboard = new Keyboard(this, R.xml.symbols);
                    showsymb2 = false;
                }

                break;
        }
        if(mSettings.contains("backgroundcolor")){

            kv.setBackgroundColor(mSettings.getInt("backgroundcolor",0));
        }
        try{
            if(ic.getTextBeforeCursor(5, 0).length() < 1){
                System.out.println("Setting shifted");
            }
        } catch (Exception ex){
        }
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        keys = keyboard.getKeys();
        kv.invalidateAllKeys();

        return kv;
    }

    @Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);


        SharedPreferences.Editor editor = mSettings.edit();
        System.out.println("Starting Input View");
        showsymb = 1;
        setInputView(onCreateInputView());
        for(Keyboard.Key k : keys){
            k.height = mSettings.getInt("keyheight", 0) + 140;
        }


        setShiftUPUnlocked();
    }


    public void setBackgroundColor(){
    }

    private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onGetSuggestions(SuggestionsInfo[] suggestionsInfos) {
    }


    List<String> lastCorrections = new ArrayList<>();

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] sentenceSuggestionsInfos) {
        try{
            System.out.println("Suggested 1: " + sentenceSuggestionsInfos[0].getSuggestionsInfoAt(0).getSuggestionAt(0));
            keys.get(1).label = sentenceSuggestionsInfos[0].getSuggestionsInfoAt(0).getSuggestionAt(1);
            keys.get(2).label = sentenceSuggestionsInfos[0].getSuggestionsInfoAt(0).getSuggestionAt(0);
            keys.get(3).label = sentenceSuggestionsInfos[0].getSuggestionsInfoAt(0).getSuggestionAt(2);
            kv.invalidateAllKeys();
            lastCorrections.add(0, sentenceSuggestionsInfos[0].getSuggestionsInfoAt(0).getSuggestionAt(0));
            lastCorrections.add(1, sentenceSuggestionsInfos[0].getSuggestionsInfoAt(0).getSuggestionAt(1));
            lastCorrections.add(2, sentenceSuggestionsInfos[0].getSuggestionsInfoAt(0).getSuggestionAt(2));
        } catch (Exception ex){

        }
    }
}