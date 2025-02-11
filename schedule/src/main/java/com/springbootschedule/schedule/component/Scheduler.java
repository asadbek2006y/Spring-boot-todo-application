package com.springbootschedule.schedule.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {
    
    @Scheduled(cron = "0 * 19 * * ?")
    public void scheduleTask() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd-MM-yyyy HH:mm:ss.SSS"
        );

        String strDate = dateFormat.format(new Date());

        System.out.println(
            "Cron job scheduler: Job running at - " + strDate
        );
    }

}
