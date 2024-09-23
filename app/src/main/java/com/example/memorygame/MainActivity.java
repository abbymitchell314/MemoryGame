package com.example.memorygame;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memorygame.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mMainLayout;
    private int mNumMatched = 0;
    private int mScore = 0; // Instance variable
    private ImageView[] mButtons;
    private ImageView mLastButton;

    private static final String NUMMATCHED_KEY = "nummatched";
    private static final String SCORE_KEY = "score";
    private static final String LAST_INDEX_KEY = "lastindex";
    private static final String BUTTON_STATE_KEY = "buttonstate";
    private static final String MATCHED_BUTTONS_KEY = "matchedbuttons"; // Key for matched buttons

    private int[] mDrawables = {
            R.drawable.baseline_agriculture_24,
            R.drawable.baseline_airplanemode_active_24,
            R.drawable.baseline_all_inclusive_24,
            R.drawable.baseline_architecture_24,
            R.drawable.baseline_assistant_photo_24,
            R.drawable.baseline_attach_file_24,
            R.drawable.baseline_attach_money_24,
            R.drawable.baseline_emoji_emotions_24
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainLayout = ActivityMainBinding.inflate(getLayoutInflater());
        mButtons = new ImageView[] {
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
            TileState[] buttonStates = (TileState[]) savedInstanceState.getSerializable(BUTTON_STATE_KEY);
            boolean[] matchedButtons = savedInstanceState.getBooleanArray(MATCHED_BUTTONS_KEY);

            if (buttonStates != null) {
                for (int i = 0; i < mButtons.length; i++) {
                    mButtons[i].setTag(buttonStates[i]);
                    if (matchedButtons != null && matchedButtons[i]) {
                        mButtons[i].setImageResource(buttonStates[i].resourceid);
                    } else {
                        mButtons[i].setImageDrawable(null); // Hide image for unmatched buttons
                    }
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

        for (ImageView button : mButtons) {
            button.setOnClickListener(view -> buttonClick(button));
        }
    }

    private void showScore() {
        if (mNumMatched == 8)
            mMainLayout.done.setText(getText(R.string.completed) + ":" + mScore);
        else
            mMainLayout.done.setText(getText(R.string.score) + ":" + mScore);
    }

    private void buttonClick(ImageView b) {
        int val = ((TileState) b.getTag()).resourceid;
        if (!(b.getDrawable() == null))
            return;

        mScore++;
        setButton(b, true); // Display the image on the button
        if (mLastButton == null) {
            mLastButton = b;
        } else {
            if (((TileState) mLastButton.getTag()).resourceid == val) {
                mNumMatched++;
                mLastButton = null;
            } else {
                setButton(mLastButton, false); // Turn the last button back
                mLastButton = b;
            }
        }
        showScore();
    }

    private void init() {
        mNumMatched = 0;
        mScore = 0;
        mLastButton = null;
        for (ImageView button : mButtons) {
            button.setImageDrawable(null); // Hide images
            button.setTag(new TileState(0, false)); // Set all to default
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 2; j++) {
                int x;
                TileState t;
                do {
                    x = (int) (Math.random() * 16);
                    t = (TileState) mButtons[x].getTag();
                } while (t.resourceid != 0);
                t.resourceid = mDrawables[i];
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
        TileState[] buttonStates = new TileState[16];
        boolean[] matchedButtons = new boolean[16];
        int lastIndex = -1;
        for (int i = 0; i < mButtons.length; i++) {
            buttonStates[i] = (TileState) mButtons[i].getTag();
            matchedButtons[i] = mButtons[i].getDrawable() != null;
            if (mLastButton == mButtons[i]) {
                lastIndex = i;
            }
        }
        outState.putInt(LAST_INDEX_KEY, lastIndex);
        outState.putSerializable(BUTTON_STATE_KEY, buttonStates);
        outState.putBooleanArray(MATCHED_BUTTONS_KEY, matchedButtons); // Save matched buttons array
    }

    private void setButton(final ImageView button, final boolean turned) {
        final TileState t = (TileState) button.getTag();
        final int from;
        final int to;
        if (!turned) {
            from = 0;
            to = 180;
            t.turned = false;
        } else {
            from = 180;
            to = 0;
            t.turned = true;
        }
        button.setRotationY(from);
        button.animate().rotationY((from + to) / 2f).setDuration(100).withEndAction(() -> {
            if (turned)
                button.setImageResource(t.resourceid); // Show the image
            else
                button.setImageDrawable(null); // Hide the image
            button.animate().rotationY(to).setDuration(100).withEndAction(() ->
                    button.setRotationY(0)
            );
        });
    }


}
