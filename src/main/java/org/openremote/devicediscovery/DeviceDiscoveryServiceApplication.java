package org.openremote.devicediscovery;

import org.openremote.devicediscovery.resources.DeviceDiscoveryCommandsResource;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class DeviceDiscoveryServiceApplication extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> s = new HashSet<>();
    s.add(DeviceDiscoveryCommandsResource.class);
    return s;
  }

}
