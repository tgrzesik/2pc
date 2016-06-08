package com.piotrek.transactions.core;

import com.piotrek.transactions.client.RestClient;
import com.piotrek.transactions.client.RestClientImpl;
import com.piotrek.transactions.util.SystemProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Objective representation for Coordinator.
 * Holds all required data to perform commit (or rollback) action.
 *
 * @author Piotrek
 */
public class Coordinator {
    /**
     * All participants defined in system.
     */
    private Participant[] participants;

    /**
     * Global state of system.
     */
    private State globalState;

    /**
     * REST client, used to communicate with cohorts
     */
    private RestClient client;

    /**
     * Initializes array of participants, their IPs and holds their state in local memory.
     */
    public Coordinator() {
        List<Participant> list = new ArrayList<>();
        String ipAddress;
        int counter = 1;
        while (true) {
            ipAddress = SystemProperties.getProperty(SystemProperties.COHORT_IP + counter++, false);
            if (ipAddress == null) break;
            list.add(new Participant(ipAddress));
        }
        participants = list.toArray(new Participant[list.size()]);
        client = new RestClientImpl();
    }

    /**
     * Collect votes from cohorts. Required to perform global commit.
     */
    public void getVotes() {
        State state;
        for (Participant participant : participants) {
            state = client.getMachineState(participant.getIpAddress());
            participant.setState(state);
        }
    }

    /**
     * Processing all votes and deciding about global state.
     */
    public void processVoting() {
        for (Participant participant : participants) {
            if (participant.getState() != State.COMMIT)
                globalState = State.ROLLBACK;
        }
        globalState = State.COMMIT;
    }

    /**
     * Distributes global state to all cohorts.
     * Is is required to perform commit on any machine.
     */
    public void sendGlobalState() {
        for (Participant participant : participants) {
            client.sendGlobalState(participant.getIpAddress(), globalState);
        }
        System.out.println("Global status persisted on all machines!");
    }

    /**
     * {@link Coordinator#globalState}
     */
    public State getGlobalState() {
        return globalState;
    }
}
