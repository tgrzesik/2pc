package com.piotrek.transactions.client;

import com.piotrek.transactions.core.State;
import com.piotrek.transactions.services.CohortService;
import com.piotrek.transactions.util.Role;
import com.piotrek.transactions.util.SystemProperties;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Implementation of REST client in this application case.
 *
 * @author Piotrek
 */
public class RestClientImpl implements RestClient {
    private static final String DEFAULT_PORT = SystemProperties.getProperty(SystemProperties.DEFAULT_PORT, true);

    /**
     * Retrieves certain machine state.
     *
     * @param ip machine IP address
     * @return machine state
     */
    @Override
    public State getMachineState(String ip) {
        WebResource resource = getWebResource(ip, CohortService.PERFORM_ACTION);
        ClientResponse response = resource.accept(MediaType.TEXT_PLAIN_TYPE)
                .get(ClientResponse.class);
        State state;
        if (response.getStatus() != 200) {
            state = State.ROLLBACK;
        } else {
            state = State.getByName(response.getEntity(String.class));
        }
        return state;
    }

    /**
     * Distributes global states between machines.
     *
     * @param ip    machine IP address
     * @param state global state of system
     */
    @Override
    public void sendGlobalState(String ip, State state) {
        WebResource resource = getWebResource(ip, CohortService.GLOBAL_STATE);
        MultivaluedMap<String, String> form = new MultivaluedMapImpl();
        form.add("state", state.toString());
        ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(ClientResponse.class, form);
        if (response.getStatus() != 200) {
            System.out.println("Some errors found!");
        }
    }

    /**
     * This method creates WebResource object. Just for not copying this fragment of code for no reason. :D
     *
     * @param ip     IP address of cohort
     * @param action Service action represented as String vale
     * @return WebResource object
     */
    private WebResource getWebResource(String ip, String action) {
        String url = "http://" + ip + ":" + DEFAULT_PORT + "/Transactions/rest/" + Role.COHORT + action;
        Client client = new Client();
        return client.resource(url);
    }
}
