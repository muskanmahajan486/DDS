package org.openremote.devicediscovery.resources;

import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class DeviceDiscoveryCommandsResource extends ServerResource {
  
  @Get()
  public Representation loadDevices() throws Exception {
      Representation rep = null;

      rep = new JsonRepresentation("hallo=1");
      return rep;
  }

  @Post()
  public Representation putDevices(Representation data) throws Exception {
      Representation rep = null;

      rep = new JsonRepresentation("hallo=2");
      return rep;
  }
  
  
}
