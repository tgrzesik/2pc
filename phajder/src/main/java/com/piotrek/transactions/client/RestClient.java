package com.piotrek.transactions.client;

import com.piotrek.transactions.core.State;

/**
 * Defines all required methods needed to perform commit in system.
 *
 * @author Piotrek
 */
public interface RestClient {
    State getMachineState(String ip);

    void sendGlobalState(String ip, State state);
}
