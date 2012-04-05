package org.openremote.irservice.domain;

import java.util.ArrayList;

import org.openremote.ir.domain.GlobalCache;
import org.openremote.ir.domain.IRCommandInfo;
import org.openremote.ir.domain.IRTrans;

public class GenerateDeviceCommandsAction {

  private GlobalCache globalCache;
  private IRTrans irTrans;
  private ArrayList<IRCommandInfo> commands;
  
  public GlobalCache getGlobalCache() {
    return globalCache;
  }
  
  public void setGlobalCache(GlobalCache globalCache) {
    this.globalCache = globalCache;
  }
  
  public IRTrans getIrTrans() {
    return irTrans;
  }
  
  public void setIrTrans(IRTrans irTrans) {
    this.irTrans = irTrans;
  }
  
  public ArrayList<IRCommandInfo> getCommands() {
    return commands;
  }
  
  public void setCommands(ArrayList<IRCommandInfo> commands) {
    this.commands = commands;
  }

}
