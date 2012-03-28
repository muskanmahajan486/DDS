package org.openremote.irservice.domain;

import java.io.Serializable;

/**
 * contains informations for led command information in irTrans commands
 * 
 * @author wbalcaen
 * 
 */
public class IRLed implements Serializable {

  private static final long serialVersionUID = 1L;

  private String value;
  private String code;

  public IRLed() {

  }

  public IRLed(String value, String code) {
    setValue(value);
    setCode(code);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

}
