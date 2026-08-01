package com.game4men.aigroove.game.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.game4men.aigroove.common.entity.Log;
import com.game4men.aigroove.common.entity.Log.LogLevel;
import com.game4men.aigroove.common.entity.User;
import com.game4men.aigroove.common.repository.LogRepository;

@Service
public class LogService {
    @Autowired
    private LogRepository repos;

    public void createLog(int level, User user, String msg){
        Log log = new Log();
        if (user != null){
            log.setUser(user);
        }
        log.setLogLevel(LogLevel.values()[level]);
        log.setLogTime(LocalDateTime.now());
        log.setMessage(msg);

        repos.save(log);
    }
}
