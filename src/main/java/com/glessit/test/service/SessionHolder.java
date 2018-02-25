package com.glessit.test.service;

import com.glessit.test.domain.RoundContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class SessionHolder {

    @Getter
    private volatile Map<Long, Set<HttpSession>> sessions = new HashMap<>();
    @Getter
    private ReentrantLock lock = new ReentrantLock();

    public synchronized Boolean isNewSession(HttpSession httpSession, RoundContext round) {
        return sessions.isEmpty() || !sessions.get(round.getId()).contains(httpSession);
    }

    public synchronized void add(HttpSession session, RoundContext round) {
        sessions.get(round.getId()).add(session);
    }

    public final synchronized void createSessionStorage(RoundContext round) {
        log.info("Allocate sessions container. Round {}", round.getId());
        sessions.put(round.getId(), new HashSet<>());
    }

    public void clearSessionStorage(RoundContext round) {
        if (!lock.isLocked()) {
            if (sessions.get(round.getId()).isEmpty()) {
                log.info("Remove round info. RoundID: {}", round.getId());
                sessions.remove(round.getId());
            }
        }
    }

    public HttpSession getSessionByRound(RoundContext round, HttpSession session) {
        return sessions.get(round.getId())
                .stream()
                .filter(s -> s.getId().equals(session.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't obtain session from round"));
    }

    public void up(RoundContext round) {
        // ToDo: get all sessions from round and add by 2 point per each
        // sessions.get(currentRound.getId()).forEach(session -> {});
    }
}
