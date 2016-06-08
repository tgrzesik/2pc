package com.piotrek.transactions.util;

/**
 * Used to define machine role
 *
 * @author Piotrek
 */
public enum Role {
    COHORT("cohort"), COORDINATOR("coordinator");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public boolean compare(String role) {
        return this.name.equals(role);
    }

    @Override
    public String toString() {
        return name;
    }
}
