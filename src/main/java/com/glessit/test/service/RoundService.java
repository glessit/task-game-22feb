package com.glessit.test.service;

import com.glessit.test.domain.RoundContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class RoundService {

    public static final String IN_ROUND_PAY = "IN_ROUND_PAY";
    public static final Integer IN_ROUND_PAY_VALUE = 1;

    public static final String SESSION_BALANCE = "BALANCE";
    public static final Integer SESSION_BALANCE_VALUE = 1000;

    private final RoundRunner roundRunner;
    private final SessionHolder sessionHolder;

    @Autowired
    public RoundService(
            RoundRunner roundRunner,
            SessionHolder sessionHolder) {
        this.roundRunner = roundRunner;
        this.sessionHolder = sessionHolder;
    }

    public void subscribe(HttpSession session) {

        ReentrantLock sessionHolderLock = sessionHolder.getLock();
        sessionHolderLock.lock();

        RoundContext round = roundRunner.getCurrentRound();

        if (!isNull(round)) {

            if (sessionHolder.isNewSession(session, round)) {

                sessionHolder.add(session, round);
                addInitialBalance(round, session);
                inRoundPayment(round, session);

                log.info("Session  with (ID) {} was added. Balance: {}", session.getId(), getBalance(round, session));
            } else {
                log.info("Session already exist in round {}", round.getId());
                log.info("Balance: {}", session.getAttribute(SESSION_BALANCE));
            }
            sessionHolderLock.unlock();
        } else {
            sessionHolderLock.unlock();
            throw new RuntimeException("Can't obtain round!");
        }
    }

    private void inRoundPayment(RoundContext round, HttpSession session) {
        HttpSession roundSession = sessionHolder.getSessionByRound(round, session);
        Integer currentBalance = (Integer) roundSession.getAttribute(SESSION_BALANCE);
        currentBalance = currentBalance - 1;
        roundSession.setAttribute(SESSION_BALANCE, currentBalance);
    }

    private void addInitialBalance(RoundContext round, HttpSession session) {
        HttpSession roundSession = sessionHolder.getSessionByRound(round, session);
        roundSession.setAttribute(SESSION_BALANCE, SESSION_BALANCE_VALUE);
    }

    public final Integer getBalance(RoundContext round, HttpSession session) {
        return (Integer) sessionHolder.getSessionByRound(round, session).getAttribute(SESSION_BALANCE);
    }
}

