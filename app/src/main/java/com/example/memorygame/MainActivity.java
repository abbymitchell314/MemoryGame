package com.example.memorygame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memorygame.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mMainLayout;
    private int mNumMatched = 0;
    private int mScore = 0; // Changed from static to instance variable
    private Button[] mButtons;
    private Button mLastButton;

    private static final String NUMMATCHED_KEY = "nummatched";
    private static final String SCORE_KEY = "score";
    private static final String LAST_INDEX_KEY = "lastindex";
    private static final String BUTTON_STATE_KEY = "buttonstate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout = ActivityMainBinding.inflate(getLayoutInflater());
        mButtons = new Button[] {
                mMainLayout.button11, mMainLayout.button12, mMainLayout.button13, mMainLayout.button14,
                mMainLayout.button21, mMainLayout.button22, mMainLayout.button23, mMainLayout.button24,
                mMainLayout.button31, mMainLayout.button32, mMainLayout.button33, mMainLayout.button34,
                mMainLayout.button41, mMainLayout.button42, mMainLayout.button43, mMainLayout.button44,
        };
        setContentView(mMainLayout.getRoot());

        // Restore saved state
        if (savedInstanceState != null) {
            mNumMatched = savedInstanceState.getInt(NUMMATCHED_KEY, 0);
            mScore = savedInstanceState.getInt(SCORE_KEY, 0);
            int lastIndex = savedInstanceState.getInt(LAST_INDEX_KEY, -1);
            String[] buttonStates = savedInstanceState.getStringArray(BUTTON_STATE_KEY);
            if (buttonStates != null) {
                for (int i = 0; i < mButtons.length; i++) {
                    mButtons[i].setTag(buttonStates[i]);
                    mButtons[i].setText(buttonStates[i].isEmpty() ? "" : buttonStates[i]);
                }
            }
            if (lastIndex != -1) {
                mLastButton = mButtons[lastIndex];
            }
            showScore();
        } else {
            init();
        }

        mMainLayout.restart.setOnClickListener(view -> init());

        for (Button button : mButtons) {
            button.setOnClickListener(view -> buttonClick((Button) view));
        }
    }

    private void showScore() {
        if (mNumMatched == 8)
            mMainLayout.done.setText(getText(R.string.completed) + ":" + mScore);
        else
            mMainLayout.done.setText(getText(R.string.score) + ":" + mScore);
    }

    private void buttonClick(Button b) {
        String val = (String)b.getTag();
        if (!b.getText().equals(""))
            return;
        mScore++;
        b.setText(val);
        if (mLastButton == null) {
            mLastButton = b;
        } else {
            if (mLastButton.getText().equals(val)) {
                mNumMatched++;
                mLastButton = null;
            } else {
                mLastButton.setText("");
                mLastButton = b;
            }
        }
        showScore();
    }

    private void init() {
        mNumMatched = 0;
        mScore = 0;
        mLastButton = null;
        for (Button button : mButtons) {
            button.setText("");
            button.setTag("");
        }
        for (int i = 1; i < 9; i++) {
            for (int j = 0; j < 2; j++) {
                int x;
                do {
                    x = (int) (Math.random() * 16);
                } while (!"".equals(mButtons[x].getTag()));
                mButtons[x].setTag("" + i);
            }
        }
        showScore();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the game state and score
        outState.putInt(NUMMATCHED_KEY, mNumMatched);
        outState.putInt(SCORE_KEY, mScore);

        // Save the state of each button
        String[] buttonStates = new String[16];
        int lastIndex = -1;
        for (int i = 0; i < mButtons.length; i++) {
            buttonStates[i] = (String) mButtons[i].getTag();
            if (mLastButton == mButtons[i]) {
                lastIndex = i;
            }
        }
        outState.putInt(LAST_INDEX_KEY, lastIndex);
        outState.putStringArray(BUTTON_STATE_KEY, buttonStates);
    }
}
