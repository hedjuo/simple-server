package com.github.hedjuo.server.service;

import com.github.hedjuo.server.annotations.Action;
import com.github.hedjuo.server.annotations.Service;

import java.util.Date;

@Service(name = "date-service")
public class DateService {

    public DateService() {
    }

    @Action(name = "sleep")
    public void sleep(Long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Action(name = "now")
    public Date getCurrentDate() {
        return new Date();
    }
}
