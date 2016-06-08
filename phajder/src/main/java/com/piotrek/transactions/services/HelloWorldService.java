package com.piotrek.transactions.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Simple class for testing.
 *
 * @author Piotrek
 */
@Path("/")
public class HelloWorldService {

    /**
     * Simple Hello World plain text.
     * @return HttpResponse
     */
    @GET
    @Path("/hello")
    public Response getHelloWorldMessage() {
        String output = "Hello world!";
        return Response.status(200).entity(output).build();
    }
}
