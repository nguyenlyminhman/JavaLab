package com.lab.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/deadlock")
public class DeadLockController {

    @Autowired
    private DeadlockExample deadlockExample;

    @GetMapping("/lock")
    public String runLock () {
        deadlockExample.deadlockRunner();
        return "locked";
    }

    }