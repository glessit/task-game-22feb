package com.glessit.test.service;

import com.glessit.test.domain.RoundContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Set;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class RoundRunner {

    private final SessionHolder sessionHolder;

    @Autowired
    public RoundRunner(SessionHolder sessionHolder) {
        this.sessionHolder = sessionHolder;
    }

    @Getter
    private RoundContext currentRound;
    private long roundID = 1L;

    @Scheduled(fixedDelay = 1000)
    public final void startNewRound() {

        log.debug("Current thread name: {}", Thread.currentThread().getName());

        if (isNull(currentRound)) {
            currentRound = startRound(roundID, false);
        } else {
            log.info("Close round, ID {}", roundID);
            currentRound.close();
            Integer result = currentRound.getDecision();
            if (result > 0) {
                // ToDo: add 2 point to balance
                // sessionHolder.up(currentRound);
            }
            sessionHolder.clearSessionStorage(currentRound);
            log.info("Round {} time boundary. Finish time: {}", roundID, LocalDateTime.now());
            log.info("\r\n");

            roundID++;
            currentRound = startRound(roundID, true);
        }
    }

    private RoundContext startRound(final Long id, Boolean copySession) {
        Long copyOfId = new Long(id);
        copyOfId--;
        log.info("Round {} time boundary. Start time: {}", id, LocalDateTime.now());
        log.info("Create new round. RoundID: {}", id);
        RoundContext roundContext = new RoundContext(id);
        sessionHolder.createSessionStorage(roundContext);
        if (copySession) {
            Set<HttpSession> previousSessions = sessionHolder.getSessions().get(copyOfId);
            if (previousSessions != null && !previousSessions.isEmpty()) {
                log.info("Copy {} previous sessions to next round..", previousSessions.size());
                sessionHolder.getSessions().get(id).addAll(previousSessions);
            }
        }
        return roundContext;
    }
}
