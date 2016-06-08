package com.piotrek.transactions;

import com.piotrek.transactions.services.CohortService;
import com.piotrek.transactions.services.HelloWorldService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines REST services.
 *
 * @author Piotrek
 */
public class RestApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> set = new HashSet<>();
        set.add(HelloWorldService.class);
        set.add(CohortService.class);

        return set;
    }
}
