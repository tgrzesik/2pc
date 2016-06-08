package com.piotrek.transactions;

import com.piotrek.transactions.core.Coordinator;

import java.io.*;

/**
 * Main class for testing.
 *
 * @author Piotrek
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Coordinator coordinator = new Coordinator();
        long start = System.currentTimeMillis();
        coordinator.getVotes();
        coordinator.processVoting();
        coordinator.sendGlobalState();
        long time = System.currentTimeMillis() - start;
        System.out.println(coordinator.getGlobalState());
        System.out.println("Time: " + time % 1000);
    }
}
