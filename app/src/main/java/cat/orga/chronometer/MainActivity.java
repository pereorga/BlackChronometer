package cat.orga.chronometer;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.MotionEvent;
import android.widget.Chronometer;
import android.os.SystemClock;
import com.github.amlcurran.showcaseview.ShowcaseView;

public class MainActivity extends AppCompatActivity {

    long timeWhenStopped;
    boolean isChronometerRunning;

    protected void onCreate(Bundle savedInstanceState) {

        Chronometer c;
        isChronometerRunning = false;
        timeWhenStopped = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstRun", false)) {

            new ShowcaseView.Builder(this)
                .setContentTitle(R.string.instructions_title)
                .setContentText(R.string.instructions_text)
                .hideOnTouchOutside()
                .build();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstRun", true);
            editor.commit();
        }

        if (savedInstanceState != null) {
            isChronometerRunning = savedInstanceState.getBoolean("is_chronometer_running", false);
            timeWhenStopped = savedInstanceState.getLong("time_when_stopped", 0);
            c = (Chronometer) findViewById(R.id.chronometer);
            if (isChronometerRunning) {
                c.setBase(SystemClock.elapsedRealtime() + savedInstanceState.getLong("time", 0));
                c.start();
            } else if (timeWhenStopped != 0) {
                c.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        Chronometer c;
        if (isChronometerRunning) {
            c = (Chronometer) findViewById(R.id.chronometer);
            outState.putBoolean("is_chronometer_running", true);
            outState.putLong("time", c.getBase() - SystemClock.elapsedRealtime());
        }
        outState.putLong("time_when_stopped", timeWhenStopped);
        super.onSaveInstanceState(outState);
    }

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            Chronometer c;
            c = (Chronometer) findViewById(R.id.chronometer);
            c.setBase(SystemClock.elapsedRealtime());
            c.stop();
            isChronometerRunning = false;
            timeWhenStopped = 0;
        }
    };

    public boolean onTouchEvent(MotionEvent e) {

        Chronometer c;
        if (e.getAction() == MotionEvent.ACTION_DOWN) {

            // Skip top bar.
            if (e.getY() > 50) {
                handler.postDelayed(mLongPressed, 500);
                c = (Chronometer) findViewById(R.id.chronometer);
                c.setTextColor(Color.rgb(218, 67, 54));

                if (!isChronometerRunning) {
                    c.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    c.start();
                    isChronometerRunning = true;
                } else {
                    timeWhenStopped = c.getBase() - SystemClock.elapsedRealtime();
                    c.stop();
                    isChronometerRunning = false;
                }
            }
        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            c = (Chronometer) findViewById(R.id.chronometer);
            c.setTextColor(Color.rgb(255, 255, 255));
            handler.removeCallbacks(mLongPressed);
        }

        return super.onTouchEvent(e);
    }
}
