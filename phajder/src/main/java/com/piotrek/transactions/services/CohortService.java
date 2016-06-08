package com.piotrek.transactions.services;

import com.piotrek.transactions.core.State;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Random;

/**
 * Provides Cohort services.
 * Answers on coordinator questions about transaction.
 *
 * @author Piotrek
 */
@Path("/cohort")
public class CohortService {
    private static final String TEMP_FILE = "temp_result.txt";
    private static final String BASE_FILE = "result.txt";
    private static final String STATE_FILE = "curr_state.txt";

    public static final String PERFORM_ACTION = "/action";
    public static final String GLOBAL_STATE = "/global";

    /**
     * Method provided for testing.
     *
     * @return HttpResponse Just a simple plain text
     */
    @GET
    @Path("/hello")
    public Response sayHello() {
        return Response.status(200).entity("Hello, I'm cohort.").build();
    }

    @GET
    @Path(PERFORM_ACTION)
    public Response performAction() {
        Response response;
        try {
            boolean result = doAction();
            State state = result ? State.COMMIT : State.ROLLBACK;
            persistCurrentState(state.toString());
            response = Response.status(200).entity(state.toString()).build();
        } catch (IOException e) {
            response = Response.status(500).build();
        }
        return response;
    }

    /**
     * Provides global state application.
     * If global state is GLOBAL_COMMIT - persists changes.
     *
     * @param state global state of system
     * @return HttpResponse
     */
    @POST
    @Path(GLOBAL_STATE)
    public Response globalStateAction(@FormParam("state") String state) {
        Response response;
        try {
            if (state.equals(State.COMMIT.toString()))
                persistChanges();
            response = Response.status(200).build();
        } catch (IOException e) {
            response = Response.status(500).build();
        }
        return response;
    }

    /**
     * Action to do on every cohort.
     *
     * @return false only when random returns 0, because you cannot divide by 0
     * @throws IOException
     */
    private boolean doAction() throws IOException {
        Random random = new Random();
        int a, b;
        double divide = 0.0;
        for (int i = 0; i < 1000; i++) {
            a = random.nextInt();
            b = random.nextInt();
            if (b != 0) divide += a / b;
            else return false;
        }
        saveFile(TEMP_FILE, divide);
        return true;
    }

    /**
     * Persists data to file.
     *
     * @param filename File which has to be overwritten
     * @param number   double number to persist
     * @throws IOException
     */
    private void saveFile(String filename, double number) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(filename));
        dos.writeDouble(number);
        dos.close();
    }

    /**
     * Reads double value from a given as param file.
     *
     * @param filename File to be read
     * @return value saved in file
     * @throws IOException
     */
    private double readFile(String filename) throws IOException {
        DataInputStream dis = new DataInputStream(new FileInputStream(filename));
        double result = dis.readDouble();
        dis.close();
        return result;
    }

    /**
     * Persists current state to a file - required for machine reconstruction.
     * It is a form of state implementation in REST services.
     *
     * @param state state to be persisted
     * @throws IOException
     */
    private void persistCurrentState(String state) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(STATE_FILE));
        dos.writeChars(state);
        dos.close();
    }

    /**
     * Overwrites global file with value from temporary file.
     *
     * @throws IOException
     */
    private void persistChanges() throws IOException {
        double value = readFile(TEMP_FILE);
        saveFile(BASE_FILE, value);
    }
}
