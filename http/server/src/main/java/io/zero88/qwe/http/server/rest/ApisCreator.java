package io.zero88.qwe.http.server.rest;

import io.zero88.qwe.http.server.HttpSystem.ApisSystem;
import io.zero88.qwe.http.server.RouterConfig;
import io.zero88.qwe.http.server.RouterCreator;

public interface ApisCreator<T extends RouterConfig> extends RouterCreator<T>, ApisSystem {

}
