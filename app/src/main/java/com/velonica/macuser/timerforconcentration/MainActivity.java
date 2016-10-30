package com.velonica.macuser.timerforconcentration;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final NumberPicker timerMinute = (NumberPicker) findViewById(R.id.timerNumberPickerMinute);
        final NumberPicker timerSecond = (NumberPicker) findViewById(R.id.timerNumberPickerSecond);
        final NumberPicker intervalMinute = (NumberPicker) findViewById(R.id.intervalNumberPickerMinute);
        final NumberPicker intervalSecond = (NumberPicker) findViewById(R.id.intervalNumberPickerSecond);

        Button btnSetting = (Button) findViewById(R.id.SettingButton);

        timerMinute.setMaxValue(90);
        timerMinute.setMinValue(0);
        timerSecond.setMaxValue(59);
        timerSecond.setMinValue(0);

        intervalMinute.setMaxValue(60);
        intervalMinute.setMinValue(0);
        intervalSecond.setMaxValue(59);
        intervalSecond.setMinValue(0);

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int timerMinuteNumber = timerMinute.getValue();
                int timerSecondNumber = timerSecond.getValue();
                int intervalMinuteNumber = intervalMinute.getValue();
                int intervalSecondNumber = intervalSecond.getValue();

                if ((timerMinuteNumber == 0 && timerSecondNumber == 0) || (intervalMinuteNumber == 0 && intervalSecondNumber == 0)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("注意")
                            .setIcon(android.R.drawable.presence_busy);
                    if ((timerMinuteNumber == 0 && timerSecondNumber == 0) && (intervalMinuteNumber == 0 && intervalSecondNumber == 0)) {
                        builder.setMessage("時間が設定されていません。\n\n時間を設定してください");
                    } else if (timerMinuteNumber != 0 || timerSecondNumber != 0) {
                        builder.setMessage("インターバルの時間が\n正しく設定されていません。\n\nインターバルの時間を\n正しく設定してください");
                    } else if (intervalMinuteNumber != 0 || intervalSecondNumber != 0) {
                        builder.setMessage("集中タイマーの時間が\n正しく設定されていません。\n\n集中タイマーの時間を\n正しく設定してください");
                    }

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                } else {
                    Intent intent = new Intent(MainActivity.this, Timer.class);
                    intent.putExtra("timerMinute", timerMinuteNumber);
                    intent.putExtra("timerSecond", timerSecondNumber);
                    intent.putExtra("intervalMinute", intervalMinuteNumber);
                    intent.putExtra("intervalSecond", intervalSecondNumber);
                    startActivity(intent);
                }

            }
        });
    }
}
