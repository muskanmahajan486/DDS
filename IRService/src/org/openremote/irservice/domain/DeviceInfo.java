package org.openremote.irservice.domain;

import java.io.Serializable;

/**
 * allows to share xcfFileParser.device necessary information with the client
 * side
 * 
 * @author wbalcaen
 * 
 */
public class DeviceInfo implements Serializable {

  private BrandInfo brandInfo;
  private String modelName;

  private static final long serialVersionUID = 1L;

  public DeviceInfo() {
  }

  public DeviceInfo(BrandInfo brand, String modelName) {
    setBrandInfo(brand);
    setModelName(modelName);
  }

  public BrandInfo getBrandInfo() {
    return brandInfo;
  }

  public void setBrandInfo(BrandInfo brandInfo) {
    this.brandInfo = brandInfo;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

}
