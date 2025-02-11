package com.springbootschedule.schedule.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AppController {
    
    @GetMapping("/")
    public String home() {
        String str
            = "<html><body><font color=\"green\">"
              + "<h1>WELCOME To GeeksForGeeks</h1>"
              + "</font></body></html>";
        return str;
    }
    
}
