package org.openremote.irservice;

import org.openremote.irservice.resources.pronto.ProntoFileResource;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class IRServiceApplication extends Application {

  
    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
  /*
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a new instance of HelloWorldResource.
        Router router = new Router(getContext());

        // Defines only one route
        router.attach("/hello", ProntoFileResource.class);

        return router;
    }
    */

}
