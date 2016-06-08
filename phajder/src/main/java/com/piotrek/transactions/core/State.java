package com.piotrek.transactions.core;

/**
 * Enumeration of possible machine states
 *
 * @author Piotrek
 */
public enum State {
    STARTING("starting"), READY("ready"), COMMIT("commit"), ROLLBACK("rollback");
    private String name;

    State(String name) {
        this.name = name;
    }

    public static State getByName(String name) {
        for (State state : values()) {
            if (state.name.equals(name))
                return state;
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
