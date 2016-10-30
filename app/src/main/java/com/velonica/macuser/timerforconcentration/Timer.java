package com.velonica.macuser.timerforconcentration;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import icepick.Icepick;
import icepick.State;

public class Timer extends AppCompatActivity {
    TextView txtTimer;
    TextView txtInterval;

    @State
    String preserveTextTimer;
    @State
    String preserveTextInterval;

    Button btnStart;
    Button btnEnd;

    @State
    long timerValue;
    @State
    long intervalValue;

    timerCountDownTimer timerCountDownTimer = null;
    intervalCountDownTimer intervalCountDownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        txtTimer = (TextView) findViewById(R.id.timerTextView);
        txtInterval = (TextView) findViewById(R.id.intervalTextView);

        btnStart = (Button) findViewById(R.id.startButton);
        btnEnd = (Button) findViewById(R.id.endButton);

        Intent intent = getIntent();
        int timerMinute = 0;
        int timerSecond = 0;
        int intervalMinute = 0;
        int intervalSecond = 0;

        if (intent.hasExtra("timerMinute")) {
            timerMinute = intent.getIntExtra("timerMinute", 0);
        }
        if (intent.hasExtra("timerSecond")) {
            timerSecond = intent.getIntExtra("timerSecond", 0);
        }
        if (intent.hasExtra("intervalMinute")) {
            intervalMinute = intent.getIntExtra("intervalMinute", 0);
        }
        if (intent.hasExtra("intervalSecond")) {
            intervalSecond = intent.getIntExtra("intervalSecond", 0);
        }

        // MainActivityからTimerActivityを起動した場合
        if (timerMinute != 0 || intervalMinute != 0 || timerSecond != 0 || intervalSecond != 0) {
            String showTimerText = getTimerString(timerMinute, timerSecond);
            String showIntervalText = getTimerString(intervalMinute, intervalSecond);

            timerValue = timerMinute * 60 * 1000 + timerSecond * 1000;
            intervalValue = intervalMinute * 60 * 1000 + intervalSecond * 1000;

            txtTimer.setText(showTimerText);
            txtInterval.setText(showIntervalText);

            timerCountDownTimer = new timerCountDownTimer(timerValue, 1000);

        } else {     //ライフサイクルによって、onStop or onPauseが呼ばれた場合
            txtTimer.setText(preserveTextTimer);
            txtInterval.setText(preserveTextInterval);

            long timerValue = getTimerTime(preserveTextTimer);
            long intervalValue = getTimerTime(preserveTextInterval);

            if (timerValue != this.timerValue) {
                timerCountDownTimer = new timerCountDownTimer(timerValue, 1000);
                timerCountDownTimer.start();
                btnStart.setText("停止");
            } else if (intervalValue != this.intervalValue) {
                intervalCountDownTimer = new intervalCountDownTimer(intervalValue, 1000);
                intervalCountDownTimer.start();
                btnStart.setText("停止");
            }
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnStart.getText().toString().equals("開始")) {
                    btnStart.setText("停止");
                    if (timerCountDownTimer != null) {
                        timerCountDownTimer.start();
                    } else if (intervalCountDownTimer != null) {
                        intervalCountDownTimer.start();
                    }

                } else if (btnStart.getText().toString().equals("停止")) {
                    btnStart.setText("開始");
                    if (timerCountDownTimer != null) {
                        timerCountDownTimer.stop();
                    } else if (intervalCountDownTimer != null) {
                        intervalCountDownTimer.stop();
                    }

                }

            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        preserveTextTimer = txtTimer.getText().toString();
        preserveTextInterval = txtInterval.getText().toString();

        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();

        getIntent().removeExtra("timerMinute");
        getIntent().removeExtra("timerSecond");
        getIntent().removeExtra("intervalMinute");
        getIntent().removeExtra("intervalSecond");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerCountDownTimer != null) {
            timerCountDownTimer.cancel();
            timerCountDownTimer = null;
        } else if (intervalCountDownTimer != null) {
            intervalCountDownTimer.cancel();
            intervalCountDownTimer = null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Timer.this.startActivity(homeIntent);
            return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }



    private String getTimerString(int minute, int second) {
        String minuteString = null;
        if (minute < 10) {
            StringBuilder sbMinuteString = new StringBuilder();
            sbMinuteString.append("0");
            sbMinuteString.append(String.valueOf(minute));
            minuteString = sbMinuteString.toString();
        } else {
            minuteString = String.valueOf(minute);
        }

        String secondString = null;
        if (second < 10) {
            StringBuilder sbSecondString = new StringBuilder();
            sbSecondString.append("0");
            sbSecondString.append(String.valueOf(second));
            secondString = sbSecondString.toString();
        } else {
            secondString = String.valueOf(second);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(minuteString);
        sb.append(":");
        sb.append(secondString);

        return sb.toString();
    }

    private long getTimerTime(String textViewString) {
        int index = textViewString.indexOf(":");
        String minute = textViewString.substring(0, index);
        String second = textViewString.substring(index + 1);

        if (minute.indexOf("0") == 0) {
            minute = minute.substring(1, 2);
        }
        if (second.indexOf("0") == 0) {
            second = second.substring(1, 2);
        }

        int hourTime = Integer.valueOf(minute);
        int minuteTime = Integer.valueOf(second);

        return hourTime * 60 * 1000 + minuteTime * 1000;
    }

    private abstract class myCountDownTimer extends CountDownTimer {
        private long stopNumber;

        myCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        private void setStopNumber(long number) {
            this.stopNumber = number;
        }

        public long getStopNumber() {
            return this.stopNumber;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            setStopNumber(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] patterns = {200, 300, 200, 300, 200, 300, 200, 1500};
            vibrator.vibrate(patterns, -1);
        }

        protected abstract void stop();
    }

    private class timerCountDownTimer extends myCountDownTimer {
        timerCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFuture) {
            super.onTick(millisUntilFuture);
            txtTimer.setText(getTimerString((int) millisUntilFuture / 1000 / 60, (int) millisUntilFuture / 1000 % 60));
        }

        @Override
        public void onFinish() {
            super.onFinish();
            txtTimer.setText(R.string.end);
            intervalCountDownTimer = new intervalCountDownTimer(intervalValue, 1000);
            intervalCountDownTimer.start();
            timerCountDownTimer = null;
        }

        @Override
        public void stop() {
            timerCountDownTimer.cancel();
            timerCountDownTimer = new timerCountDownTimer(timerCountDownTimer.getStopNumber(), 1000);
        }
    }

    private class intervalCountDownTimer extends myCountDownTimer {
        intervalCountDownTimer(long millisInFuture, long countDonwInterval) {
            super(millisInFuture, countDonwInterval);
        }

        @Override
        public void onTick(long millisUntilFuture) {
            super.onTick(millisUntilFuture);
            txtInterval.setText(getTimerString((int) millisUntilFuture / 1000 / 60, (int) millisUntilFuture / 1000 % 60));
        }

        @Override
        public void onFinish() {
            super.onFinish();
            txtInterval.setText(R.string.end);
            timerCountDownTimer = new timerCountDownTimer(timerValue, 1000);
            timerCountDownTimer.start();
            intervalCountDownTimer = null;
        }

        @Override
        public void stop() {
            intervalCountDownTimer.cancel();
            intervalCountDownTimer = new intervalCountDownTimer(intervalCountDownTimer.getStopNumber(), 1000);
        }
    }
}
