package com.example.memorygame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.memorygame.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mMainLayout;
    private int mNumMatched = 0;
    private static int mScore = 0;
    private Button[] mButtons;
    private Button mLastButton;

    private void showScore() {
        if (mNumMatched == 8)
            mMainLayout.done.setText(getText(R.string.completed) + ":" + mScore);
        else
            mMainLayout.done.setText(getText(R.string.score) + ":" + mScore);
    }

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
    mMainLayout.restart.setOnClickListener(view -> init());

    for (Button button : mButtons) {
        button.setOnClickListener(view -> buttonClick((Button)view));
    }
    if (mScore == 0)
        init();
    showScore();
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
                x = (int)(Math.random() * 16);
            } while (!"".equals(mButtons[x].getTag()));
            mButtons[x].setTag("" + i);
        }
    }
    showScore();
}

}