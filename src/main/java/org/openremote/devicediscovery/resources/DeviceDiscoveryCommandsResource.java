/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2016, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.devicediscovery.resources;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.log4j.Logger;
import org.openremote.beehive.EntityTransactionFilter;
import org.openremote.devicediscovery.domain.Account;
import org.openremote.devicediscovery.domain.DiscoveredDevice;
import org.openremote.devicediscovery.domain.DiscoveredDeviceAttr;
import org.openremote.devicediscovery.domain.User;
import org.openremote.rest.GenericResourceResultWithErrorMessage;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class DeviceDiscoveryCommandsResource
{

  private static Logger log = Logger.getLogger(DeviceDiscoveryCommandsResource.class);

  /**
   * Return a list of one DiscoveredDevice.<p>
   * <p>
   * REST Url: /rest/discoveredDevices/{deviceOid} -> return the discovered device with given OID
   *
   * @return a List of DiscoveredDevices
   */
  @Path("discoveredDevices/{deviceOid}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response loadDevices(@Context HttpServletRequest request,
                              @Context SecurityContext securityContext,
                              @PathParam("deviceOid") String oid)
  {
    GenericResourceResultWithErrorMessage result = null;
    try
    {
      String username = securityContext.getUserPrincipal().getName();
      User user = getUserByName(getEntityManager(request), username);
      Account account = user.getAccount();

      CriteriaBuilder cb = getEntityManager(request).getCriteriaBuilder();
      CriteriaQuery<DiscoveredDevice> cq = cb.createQuery(DiscoveredDevice.class);
      Root<DiscoveredDevice> dd = cq.from(DiscoveredDevice.class);

      Predicate criteria = cb.equal(dd.get("account"), account);
      if (oid != null) {
        long id = Long.parseLong(oid);
        criteria = cb.and(criteria, cb.equal(dd.get("oid"), id));
      }

      cq.select(dd).where(criteria);

      List<DiscoveredDevice> devices = getEntityManager(request).createQuery(cq).getResultList();
      result = new GenericResourceResultWithErrorMessage(null, devices);
    } catch (Exception e)
    {
      result = new GenericResourceResultWithErrorMessage(e.getMessage(), null);
    }
    return Response.ok(new JSONSerializer().exclude("*.class").deepSerialize(result)).build();
  }

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
  @Path("discoveredDevices")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response loadDevices(@Context HttpServletRequest request,
                              @Context SecurityContext securityContext,
                              @QueryParam("used") Boolean used,
                              @QueryParam("type") String type,
                              @QueryParam("protocol") String protocol)
  {
    GenericResourceResultWithErrorMessage result = null;
    try
    {
      String username = securityContext.getUserPrincipal().getName();
      User user = getUserByName(getEntityManager(request), username);
      Account account = user.getAccount();

      CriteriaBuilder cb = getEntityManager(request).getCriteriaBuilder();
      CriteriaQuery<DiscoveredDevice> cq = cb.createQuery(DiscoveredDevice.class);
      Root<DiscoveredDevice> dd = cq.from(DiscoveredDevice.class);

      Predicate criteria = cb.equal(dd.get("account"), account);
      if (used != null)
      {
        criteria = cb.and(criteria, cb.equal(dd.get("used"), used));
      }
      if (type != null)
      {
        criteria = cb.and(criteria, cb.equal(dd.get("type"), type));
      }
      if (protocol != null)
      {
        criteria = cb.and(criteria, cb.equal(dd.get("protocol"), protocol));
      }

      cq.select(dd).where(criteria);

      List<DiscoveredDevice> devices = getEntityManager(request).createQuery(cq).getResultList();
      result = new GenericResourceResultWithErrorMessage(null, devices);
    } catch (Exception e)
    {
      result = new GenericResourceResultWithErrorMessage(e.getMessage(), null);
    }
    return Response.ok(new JSONSerializer().exclude("*.class").deepSerialize(result)).build();
  }

  /**
   * Add the given devices to the database
   * POST data has to contain a list of devices as JSON string
   * REST POST Url:/rest/discoveredDevices
   * @param jsonData
   * @return a list of OID's of the saved devices
   */
  @Path("discoveredDevices")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response saveDevices(@Context HttpServletRequest request,
                              @Context SecurityContext securityContext, String jsonData)
  {
    log.debug("save discovered devices - start");
    Response rep = null;
    GenericResourceResultWithErrorMessage result = null;

    String username = securityContext.getUserPrincipal().getName();

    User user = getUserByName(getEntityManager(request), username);
    Account account = user.getAccount();
    List<Long> newOIDList = new ArrayList<Long>();
    try {
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

        CriteriaBuilder cb = getEntityManager(request).getCriteriaBuilder();
        CriteriaQuery<DiscoveredDevice> cq = cb.createQuery(DiscoveredDevice.class);
        Root<DiscoveredDevice> dd = cq.from(DiscoveredDevice.class);

        Predicate criteria = cb.equal(dd.get("account"), account);
        criteria = cb.and(criteria, cb.equal(dd.get("protocol"), discoveredDevice.getProtocol()));
        criteria = cb.and(criteria, cb.equal(dd.get("type"), discoveredDevice.getType()));
        criteria = cb.and(criteria, cb.equal(dd.get("name"), discoveredDevice.getName()));
        criteria = cb.and(criteria, cb.equal(dd.get("model"), discoveredDevice.getModel()));

        cq.select(dd).where(criteria);

        List<DiscoveredDevice> devices = getEntityManager(request).createQuery(cq).getResultList();

        if (devices.isEmpty()) { //Only add if device is not available already
          getEntityManager(request).persist(discoveredDevice);

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
    rep = Response.ok(new JSONSerializer().exclude("*.class").deepSerialize(result)).build();

    log.debug("return json result: " + rep.getEntity());
    log.debug("save discovered devices - end");
    return rep;
  }

  /**
   * Update the device with the given id
   * PUT data has to contain device as JSON string
   * REST PUT Url:/rest/discoveredDevices
   * @param jsonData
   * @return the updated device
   */
  @Path("discoveredDevices/{deviceOid}")
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response updateDevice(@Context HttpServletRequest request,
                               @Context SecurityContext securityContext,
                               @PathParam("deviceOid") String oid, String jsonData)
  {
    Response rep = null;
    GenericResourceResultWithErrorMessage result = null;

    String username = securityContext.getUserPrincipal().getName();
    User user = getUserByName(getEntityManager(request), username);
    Account account = user.getAccount();
    try {
      DiscoveredDevice device = new JSONDeserializer<DiscoveredDevice>().use(null, DiscoveredDevice.class).deserialize(jsonData);
      device.setAccount(account);
      for (DiscoveredDeviceAttr discoveredDeviceAttr : device.getDeviceAttrs())
      {
        discoveredDeviceAttr.setDiscoveredDevice(device);
      }

      DiscoveredDevice d =  getEntityManager(request).merge(device);

      result = new GenericResourceResultWithErrorMessage(null, d);
    } catch (Exception e) {
      result = new GenericResourceResultWithErrorMessage(e.getMessage(), null);
    }
    rep = Response.ok(new JSONSerializer().exclude("*.class").deepSerialize(result)).build();
    return rep;
  }

  /**
   * Delete the device with the given id
   * @return
   */
  @Path("discoveredDevices/{deviceOid}")
  @DELETE
  public Response deleteDevice(@Context HttpServletRequest request,
                               @Context SecurityContext securityContext, @PathParam("deviceOid") String oid)
  {
    Response rep = null;
    GenericResourceResultWithErrorMessage result = null;

    String username = securityContext.getUserPrincipal().getName();
    User user = getUserByName(getEntityManager(request), username);
    Account account = user.getAccount();

    if (oid != null) {
      CriteriaBuilder cb = getEntityManager(request).getCriteriaBuilder();
      CriteriaQuery<DiscoveredDevice> cq = cb.createQuery(DiscoveredDevice.class);
      Root<DiscoveredDevice> dd = cq.from(DiscoveredDevice.class);
      Predicate criteria = cb.equal(dd.get("account"), account);

      long id = Long.parseLong(oid);
      criteria = cb.and(criteria, cb.equal(dd.get("oid"), id));

      cq.select(dd).where(criteria);
      DiscoveredDevice deviceToDelete = getEntityManager(request).createQuery(cq).getSingleResult();
      getEntityManager(request).remove(deviceToDelete);

      result = new GenericResourceResultWithErrorMessage(null, null);
    } else {
      result = new GenericResourceResultWithErrorMessage("No deviceOid found in URL", null);
    }
    rep = Response.ok(new JSONSerializer().exclude("*.class").deepSerialize(result)).build();
    return rep;
  }

  private User getUserByName(EntityManager em, String name)
  {
    CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
    CriteriaQuery<User> userQuery = criteriaBuilder.createQuery(User.class);
    Root<User> userRoot = userQuery.from(User.class);
    userQuery.select(userRoot);
    userQuery.where(criteriaBuilder.equal(userRoot.get("username"), name));
    return em.createQuery(userQuery).getSingleResult();
  }

  private EntityManager getEntityManager(HttpServletRequest request)
  {
    return (EntityManager)request.getAttribute(EntityTransactionFilter.PERSISTENCE_ENTITY_MANAGER_LOOKUP);
  }

}