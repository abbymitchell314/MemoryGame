package com.example.memorygame;

import android.animation.TimeInterpolator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.DialogFragment;

import com.example.memorygame.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MatchingGame";
    private static final String SCORE = "SCORE";
    private static final String LAST = "LAST";
    private static final String MATCHED = "MATCH";
    private static final String TILEVALS = "TILEVALS";
    private static final String TURNED = "TURNED";

    private static final int NTILES = 256;
    private static final int NCOLS = 4;
    private int mLastTileIndex = -1;

    private GridView mTiles;
    private TextView mDone;
    private int mTileValues[] = new int[NTILES];
    private boolean mTurned[] = new boolean[NTILES];
    private int mNumMatched = 0;
    private TileAdapter mTileAdapter;
    private int mScore = 0;
    private final TimeInterpolator mFlipInterpolator = new AccelerateDecelerateInterpolator();

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

    private ScaleGestureDetector mScaleGestureDetector;

    public class TileAdapter extends BaseAdapter {
        class ViewHolder {
            int position;
            ImageView image;
        }

        @Override
        public int getCount() {
            return NTILES;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder vh;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.tile, viewGroup, false);
                vh = new ViewHolder();
                vh.image = convertView.findViewById(R.id.tilebtn);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            convertView.setMinimumHeight(mTiles.getWidth() / mTiles.getNumColumns());

            vh.image.setRotation(0);
            if (mTurned[i]) {
                vh.image.setImageResource(mDrawables[mTileValues[i]]);
            } else {
                vh.image.setImageDrawable(null);
            }
            vh.position = i;

            return convertView;
        }
    }

    public static class SureDialog extends DialogFragment {
        @Override
        public AlertDialog onCreateDialog(Bundle savedInstanceState) {
            MainActivity activity = (MainActivity) getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(R.string.sure)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> activity.init())
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_restart) {
            new SureDialog().show(getSupportFragmentManager(), null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onSaveInstanceState()");
        savedInstanceState.putInt(SCORE, mScore);
        savedInstanceState.putInt(LAST, mLastTileIndex);
        savedInstanceState.putInt(MATCHED, mNumMatched);
        savedInstanceState.putIntArray(TILEVALS, mTileValues);
        savedInstanceState.putBooleanArray(TURNED, mTurned);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void showscore() {
        Log.i(TAG, "score=" + mScore);
        String score = getText(R.string.app_name).toString() + " ";
        if (mNumMatched == 8) {
            score += getText(R.string.complete).toString() + ": " + mScore;
        } else {
            score += getText(R.string.score).toString() + ": " + mScore;
        }
        ActionBar ab = getSupportActionBar();
        if (ab != null) ab.setTitle(score);
    }

    private void setTile(final int v, final int n, final boolean turnback, int delay) {
        int from = 0;
        int to = 0;
        final TileAdapter.ViewHolder vh;

        View layout = mTiles.getChildAt(v - mTiles.getFirstVisiblePosition());
        if (layout != null) {
            vh = (TileAdapter.ViewHolder) layout.getTag();
        } else {
            mTurned[v] = false;
            return;
        }

        // Ensure correct drawable is shown when the tile is flipped
        if (!mTurned[v]) {
            from = -180;
            to = 0;
        } else {
            mTurned[v] = true;
            if (vh.position != v) {
                if (turnback)
                    mTurned[v] = false;
                return;
            }

            if (n == -1) {
                vh.image.setImageResource(mDrawables[mTileValues[v]]);
            } else {
                vh.image.setImageDrawable(null);
            }
        }

        vh.image.setRotationY(from);

        vh.image.animate().rotationY((from + to) / 2f).setDuration(200).setInterpolator(mFlipInterpolator).setStartDelay(delay);

        if (vh.position != v) {
            if (turnback)
                mTurned[v] = false;
            return;
        }
        if (n == -1) {
            vh.image.setImageDrawable(null);
        } else {
            vh.image.setImageResource(mDrawables[n]);
        }

        vh.image.animate().rotationY(to).setStartDelay(0).setInterpolator(mFlipInterpolator).setDuration(200).withEndAction(() -> {
            if (turnback) {
                setTile(v, -1, false, 400);
            }
        });
    }

    private void setTile(final int v, final int n) {
        setTile(v, n, false, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate:" + mScore);

        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        // Initialize GridView and Adapter
        mTiles = findViewById(R.id.gridview);
        mTiles.setNumColumns(NCOLS);

        mTileAdapter = new TileAdapter();
        mTiles.setAdapter(mTileAdapter);

        // Restore state if available
        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(SCORE, 0);
            mLastTileIndex = savedInstanceState.getInt(LAST, -1);
            mNumMatched = savedInstanceState.getInt(MATCHED, 0);
            mTileValues = savedInstanceState.getIntArray(TILEVALS);
            mTurned = savedInstanceState.getBooleanArray(TURNED);

            if (mTileValues == null) mTileValues = new int[NTILES];
            if (mTurned == null) mTurned = new boolean[NTILES];

            // Notify the adapter to update the view with restored data
            mTileAdapter.notifyDataSetChanged();
            showscore();
        } else {
            // If no saved state, initialize a new game
            init();
        }

        // Set up click listener for grid items
        mTiles.setOnItemClickListener((adapterView, view, v, l) -> {
            if (mTurned[v]) {
                return; // Do nothing if the tile is already turned
            }

            mScore++;
            if (mLastTileIndex == -1) {
                // First tile clicked, turn it
                mLastTileIndex = v;
                setTile(v, mTileValues[v]);
            } else {
                // Second tile clicked, check if it matches the first
                if (mTileValues[mLastTileIndex] == mTileValues[v]) {
                    // Tiles match, keep them turned
                    setTile(v, mTileValues[v]);
                    mNumMatched++;
                    mLastTileIndex = -1; // Reset for the next turn
                } else {
                    // Tiles don't match, turn them back after a delay
                    setTile(v, mTileValues[v], true, 0);
                    setTile(mLastTileIndex, -1);
                    mLastTileIndex = -1; // Reset for the next turn
                }
            }
            showscore(); // Update the score display
        });
    }

        /*// Initialize ScaleGestureDetector for pinch-to-zoom functionality
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            private float mCols = NCOLS;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mCols = mCols / detector.getScaleFactor();
                mCols = Math.max(1, Math.min(mCols, 8));
                mTiles.setNumColumns((int) mCols);

                for (int i = 0; i < mTiles.getChildCount(); i++) {
                    if (mTiles.getChildAt(i) != null) {
                        mTiles.getChildAt(i).setMinimumHeight(mTiles.getWidth() / (int) mCols);
                    }
                }
                mTiles.invalidate();
                return true;
            }
        });

        // Set up OnTouchListener for GridView
        mTiles.setOnTouchListener((view, motionEvent) -> {
            boolean isScaleGesture = mScaleGestureDetector.onTouchEvent(motionEvent);
            // Return true if scaling to prevent other actions, false otherwise
            return isScaleGesture;
        });

        init();
    }
*/
    private void init() {
        // Reset game state
        mNumMatched = 0;
        mScore = 0;
        mLastTileIndex = -1;

        // Total number of unique pairs needed (each drawable appears twice)
        int numPairs = NTILES / 2;
        int[] tempValues = new int[NTILES];

        // Initialize tempValues with pairs of drawable indices
        for (int i = 0; i < numPairs; i++) {
            tempValues[2 * i] = i % mDrawables.length;      // First occurrence of drawable index
            tempValues[2 * i + 1] = i % mDrawables.length;  // Second occurrence of drawable index
        }

        // Assign tempValues to mTileValues in random positions
        boolean[] filledPositions = new boolean[NTILES];
        for (int i = 0; i < NTILES; i++) {
            int randomIndex;
            do {
                randomIndex = (int) (Math.random() * NTILES);
            } while (filledPositions[randomIndex]); // Find an empty position

            mTileValues[randomIndex] = tempValues[i];
            filledPositions[randomIndex] = true; // Mark this position as filled
        }

        // Set all tiles as not turned
        for (int i = 0; i < NTILES; i++) {
            mTurned[i] = false;
        }

        // Notify the adapter to refresh the GridView
        mTileAdapter.notifyDataSetChanged();

        // Update the score display
        showscore();
    }

}
