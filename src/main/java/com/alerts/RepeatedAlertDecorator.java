package com.alerts;

import java.util.Timer;
import java.util.TimerTask;


public class RepeatedAlertDecorator extends AlertDecorator{

    private int repeatCount;
    private long intervalMillis;

    public RepeatedAlertDecorator(AlertInterface alert, int repeatCount, long intervalMillis) {
        super(alert);
        this.repeatCount = repeatCount;
        this.intervalMillis = intervalMillis;
    }


    public void checkConditionRepeatedly(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            private int currentCount = 0;

            @Override
            public void run() {
                if (currentCount < repeatCount) {
                    System.out.println("Re-checking alert condition: " + decoratedAlert.getCondition());
                    currentCount++;
                } else {
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, intervalMillis);
    }


    @Override
    public String getMessage() {
        return "[REPEATED " + repeatCount + "x] " + decoratedAlert.getMessage();
    }

}
