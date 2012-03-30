package org.openremote.irservice.resources.pronto;

import org.openremote.irservice.service.ProntoService;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import flexjson.JSONSerializer;

public class IRCommandsResource extends ServerResource {
  
  private ProntoService prontoService;

  @Get("json")
  public Representation getBrands() {    
    return new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(prontoService.getIRCommands((String)getRequest().getAttributes().get("prontoFileHandle"),
            (String)getRequest().getAttributes().get("brandName"), (String)getRequest().getAttributes().get("deviceName"), Integer.parseInt((String)getRequest().getAttributes().get("index")))));
  }

  public void setProntoService(ProntoService prontoService) {
    this.prontoService = prontoService;
  }

}
