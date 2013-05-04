package org.openremote.devicediscovery.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.openremote.devicediscovery.GenericDAO;
import org.openremote.devicediscovery.domain.Account;
import org.openremote.devicediscovery.domain.DiscoveredDevice;
import org.openremote.devicediscovery.domain.DiscoveredDeviceAttr;
import org.openremote.devicediscovery.domain.User;
import org.openremote.rest.GenericResourceResultWithErrorMessage;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;
import org.springframework.security.core.context.SecurityContextHolder;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class DeviceDiscoveryCommandsResource extends ServerResource
{

  private GenericDAO dao;
  private static Logger log = Logger.getLogger(DeviceDiscoveryCommandsResource.class);

  /**
   * Return a list of all or one DiscoveredDevice.<p>
   * You can filter devices when adding request parameter:<br>
   * used=true,false<br>
   * type=typeName<br>
   * protocol=protocolName<br>
   * <p>
   * REST Url: /rest/discoveredDevices -> return all discovered devices<br>
   * REST Url: /rest/discoveredDevices/{deviceOid} -> return the discovered device with given OID
   * REST Url: /rest/discoveredDevices?used=true&type=Switch&protocol=knx -> return all discovered that match the filter criteria
   * 
   * @return a List of DiscoveredDevices
   */
  @Get("json")
  public Representation loadDevices()
  {
    GenericResourceResultWithErrorMessage result = null;
    try
    {
      String oid = (String) getRequest().getAttributes().get("deviceOid");
      Form queryParams = getQuery();
      String username = SecurityContextHolder.getContext().getAuthentication().getName();
      User user = dao.getByNonIdField(User.class, "username", username);
      Account account = user.getAccount();
      DetachedCriteria search = DetachedCriteria.forClass(DiscoveredDevice.class);
      search.add(Restrictions.eq("account", account));
      if (oid != null)
      {
        long id = Long.parseLong(oid);
        search.add(Restrictions.eq("oid", id));
      }
      if (queryParams.getFirstValue("used", true) != null)
      {
        Boolean used = Boolean.valueOf(queryParams.getFirstValue("used", true));
        search.add(Restrictions.eq("used", used));
      }
      if (queryParams.getFirstValue("type", true) != null)
      {
        String type = queryParams.getFirstValue("type", true);
        search.add(Restrictions.eq("type", type));
      }
      if (queryParams.getFirstValue("protocol", true) != null)
      {
        String protocol = queryParams.getFirstValue("protocol", true);
        search.add(Restrictions.eq("protocol", protocol));
      }
      List<DiscoveredDevice> devices = dao.findByDetachedCriteria(search);
      result = new GenericResourceResultWithErrorMessage(null, devices);
    } catch (Exception e)
    {
      result = new GenericResourceResultWithErrorMessage(e.getMessage(), null);
    }
    Representation rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(result));
    return rep;
  }

  /**
   * Add the given devices to the database
   * POST data has to contain a list of devices as JSON string
   * REST POST Url:/rest/discoveredDevices
   * @param data
   * @return a list of OID's of the saved devices
   */
  @SuppressWarnings("unchecked")
  @Post("json:json")
  public Representation saveDevices(Representation data)
  {
    log.debug("save discovered devices - start");
    Representation rep = null;
    GenericResourceResultWithErrorMessage result = null;
    if (data != null) {
      if (MediaType.APPLICATION_JSON.equals(data.getMediaType(), true)) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = dao.getByNonIdField(User.class, "username", username);
        Account account = user.getAccount();
        List<Long> newOIDList = new ArrayList<Long>();
        try {
          String jsonData = data.getText();
          log.debug("received json data with devices: " + jsonData);
          List<DiscoveredDevice> dtos = new JSONDeserializer<List<DiscoveredDevice>>().use(null, ArrayList.class).use("values", DiscoveredDevice.class).deserialize(jsonData);
          for (DiscoveredDevice discoveredDevice : dtos)
          {
            discoveredDevice.setAccount(account);
            for (DiscoveredDeviceAttr discoveredDeviceAttr : discoveredDevice.getDeviceAttrs())
            {
              discoveredDeviceAttr.setDiscoveredDevice(discoveredDevice);
            }
            
            log.debug("check device if device exists: " + discoveredDevice);
            DetachedCriteria search = DetachedCriteria.forClass(DiscoveredDevice.class);
            search.add(Restrictions.eq("account", account));
            search.add(Restrictions.eq("protocol", discoveredDevice.getProtocol()));
            search.add(Restrictions.eq("type", discoveredDevice.getType()));
            search.add(Restrictions.eq("name", discoveredDevice.getName()));
            search.add(Restrictions.eq("model", discoveredDevice.getModel()));
            List<DiscoveredDevice> devices = dao.findByDetachedCriteria(search);
            if (devices.isEmpty()) { //Only add if device is not available already
              dao.save(discoveredDevice);
              newOIDList.add(discoveredDevice.getOid());
              log.debug("device saved with oid: " + discoveredDevice.getOid());
            } else {
              log.debug("device already exists");
            }
          }
          result = new GenericResourceResultWithErrorMessage(null, newOIDList);
        } catch (Exception e) {
          log.error("could not save discovered devices", e);
          result = new GenericResourceResultWithErrorMessage(e.getMessage(), null);
        }
        rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(result));
      }
    }
    try {log.debug("return json result: " + rep.getText());} catch (IOException ignored){}
    log.debug("save discovered devices - end");
    return rep;
  }

  /**
   * Update the device with the given id
   * PUT data has to contain device as JSON string
   * REST PUT Url:/rest/discoveredDevices
   * @param data
   * @return the updated device
   */
  @Put("json:json")
  public Representation updateDevice(Representation data)
  {
    Representation rep = null;
    GenericResourceResultWithErrorMessage result = null;
    if (data != null) {
      if (MediaType.APPLICATION_JSON.equals(data.getMediaType(), true)) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = dao.getByNonIdField(User.class, "username", username);
        Account account = user.getAccount();
        try {
          String jsonData = data.getText();
          DiscoveredDevice device = new JSONDeserializer<DiscoveredDevice>().use(null, DiscoveredDevice.class).deserialize(jsonData);
          device.setAccount(account);
          for (DiscoveredDeviceAttr discoveredDeviceAttr : device.getDeviceAttrs())
          {
            discoveredDeviceAttr.setDiscoveredDevice(device);
          }
          DiscoveredDevice d = (DiscoveredDevice)dao.merge(device);
          result = new GenericResourceResultWithErrorMessage(null, d);
        } catch (Exception e) {
          result = new GenericResourceResultWithErrorMessage(e.getMessage(), null);
        }
        rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(result));
      }
    }
    return rep;
  }

  /**
   * Delete the device with the given id
   * @return
   */
  @Delete("json")
  public Representation deleteDevice()
  {
    Representation rep = null;
    GenericResourceResultWithErrorMessage result = null;
    String oid = (String) getRequest().getAttributes().get("deviceOid");
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    User user = dao.getByNonIdField(User.class, "username", username);
    Account account = user.getAccount();
    DetachedCriteria search = DetachedCriteria.forClass(DiscoveredDevice.class);
    search.add(Restrictions.eq("account", account));
    if (oid != null)
    {
      try
      {
        long id = Long.parseLong(oid);
        search.add(Restrictions.eq("oid", id));
        DiscoveredDevice deviceToDelete = dao.findOneByDetachedCriteria(search);
        dao.delete(deviceToDelete);
        result = new GenericResourceResultWithErrorMessage(null, null);
      } catch (Exception e)
      {
        result = new GenericResourceResultWithErrorMessage(e.getMessage(), null);
      }
    } else {
      result = new GenericResourceResultWithErrorMessage("No deviceOid found in URL", null);
    }
    rep = new JsonRepresentation(new JSONSerializer().exclude("*.class").deepSerialize(result));
    return rep;
  }

  public GenericDAO getDao()
  {
    return dao;
  }

  public void setDao(GenericDAO dao)
  {
    this.dao = dao;
  }

}
