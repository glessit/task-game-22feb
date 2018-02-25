package com.glessit.test.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
@Getter
public class RoundContext {

    private Long id;
    private Integer decision;
    private Random generator = new Random();

    public RoundContext(Long id) {
        this.id = id;
        open();
    }

    private void open() {
//        System.out.println(LocalDateTime.now() + "Round " + id + " has already opened!");

    }

    public void close() {
        //  invoke decision here
        decision = generator.nextInt(1) + 1;
//        System.out.println("DECISION " + decision);
//        System.out.println(LocalDateTime.now() + "Round " + id + " has closed!");
    }

}
