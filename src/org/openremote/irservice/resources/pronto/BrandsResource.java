package org.openremote.irservice.resources.pronto;

import org.openremote.irservice.service.ProntoService;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import flexjson.JSONSerializer;

public class BrandsResource extends ServerResource {

  private ProntoService prontoService;

  @Get("json")
  public Representation getBrands() {
    return new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(prontoService.getBrands((String)getRequest().getAttributes().get("prontoFileHandle"))));
  }

  public void setProntoService(ProntoService prontoService) {
    this.prontoService = prontoService;
  }

}
