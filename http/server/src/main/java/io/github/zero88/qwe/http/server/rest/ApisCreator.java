package io.github.zero88.qwe.http.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.qwe.http.server.HttpLogSystem.ApisLogSystem;
import io.github.zero88.qwe.http.server.RouterCreator;

public interface ApisCreator extends ApisLogSystem {

    default Logger log() {
        return LoggerFactory.getLogger(RouterCreator.class);
    }

}
