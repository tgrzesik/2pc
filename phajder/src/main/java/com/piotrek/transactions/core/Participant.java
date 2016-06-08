package com.piotrek.transactions.core;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Abstract model of transaction participant.
 *
 * @author Piotrek
 */
public class Participant {

    /**
     * Machine IP address.
     * Required for communication between coordinator and cohort.
     */
    private String ipAddress;

    /**
     * Current state of machine.
     * In general determines global state of system.
     */
    private State state;

    /**
     * Participant of transaction constructor.
     *
     * @param ipAddress cohorts IP address
     */
    public Participant(String ipAddress) {
        if (ipAddress != null) {
            this.ipAddress = ipAddress;
        } else {
            try {
                this.ipAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                System.err.println("Unknown host exception thrown.");
                System.exit(1);

            }
        }
    }

    /**
     * {@link Participant#ipAddress}
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * {@link Participant#state}
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * {@link Participant#state}
     */
    public State getState() {
        return state;
    }
}
