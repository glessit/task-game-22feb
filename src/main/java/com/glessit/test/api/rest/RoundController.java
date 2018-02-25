package com.glessit.test.api.rest;

import com.glessit.test.service.RoundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@Slf4j
public class RoundController {

    private final HttpSession httpSession;
    private final RoundService roundService;

    @Autowired
    public RoundController(
            HttpSession httpSession,
            RoundService roundService) {

        this.httpSession = httpSession;
        this.roundService = roundService;
    }

    @GetMapping(value = "/subscribe")
    public final SubscribeResult subscribe() {
        roundService.subscribe(httpSession);
        return new SubscribeResult(true);
    }
}
