package io.zero88.qwe.http.server.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zero88.qwe.http.server.HttpLogSystem.ApisLogSystem;
import io.zero88.qwe.http.server.RouterCreator;

public interface ApisCreator extends ApisLogSystem {

    default Logger log() {
        return LoggerFactory.getLogger(RouterCreator.class);
    }

}
