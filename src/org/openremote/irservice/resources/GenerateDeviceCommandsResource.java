package org.openremote.irservice.resources;

import org.openremote.irservice.domain.GenerateDeviceCommandsAction;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import flexjson.JSONDeserializer;

public class GenerateDeviceCommandsResource extends ServerResource {
  
  @Post()
  public Representation accept(Representation entity) throws Exception {
      Representation rep = null;
      if (entity != null) {
          if (MediaType.APPLICATION_JSON.equals(entity.getMediaType(), true)) {
            GenerateDeviceCommandsAction action = new JSONDeserializer<GenerateDeviceCommandsAction>().use( null, GenerateDeviceCommandsAction.class ).deserialize( entity.getText());
            
            
            

            
            
            
            
          }
      }
      return rep;
  }

  
  
  /*
  public List<DeviceCommand> saveCommands(GlobalCache globalCache,
          IRTrans irTrans, List<IRCommandInfo> selectedFunctions)
          throws IrFileParserException {

       CodeSetInfo csi = selectedFunctions.get(0).getCodeSet();
       CodeSet cs = new CodeSet(csi.getDeviceInfo().getBrandInfo()
             .getBrandName(), csi.getDeviceInfo().getModelName());
       currentIRCommands = prontoFileParser.getCodeSets(cs.getDevice())
             .get(csi.getIndex()).getIRCommands();
       List<DeviceCommand> deviceCommands = new ArrayList<DeviceCommand>();
       for (IRCommandInfo irCommandInfo : selectedFunctions) {
          Protocol protocol = new Protocol();
          if (globalCache != null) {
             protocol.setType("TCP/IP");
             
             ProtocolAttr ipAttr = new ProtocolAttr();
             ipAttr.setName("ipAddress");
             ipAttr.setValue(globalCache.getIpAddress());
             ipAttr.setProtocol(protocol);
             protocol.getAttributes().add(ipAttr);
             
             ProtocolAttr portAttr = new ProtocolAttr();
             portAttr.setName("port");
             portAttr.setValue(globalCache.getTcpPort());
             portAttr.setProtocol(protocol);
             protocol.getAttributes().add(portAttr);
          } else if (irTrans != null) {
             protocol.setType("UDP");
             
             ProtocolAttr ipAttr = new ProtocolAttr();
             ipAttr.setName("ipAddress");
             ipAttr.setValue(irTrans.getIpAdress());
             ipAttr.setProtocol(protocol);
             protocol.getAttributes().add(ipAttr);
             
             ProtocolAttr portAttr = new ProtocolAttr();
             portAttr.setName("port");
             portAttr.setValue(irTrans.getUdpPort());
             portAttr.setProtocol(protocol);
             protocol.getAttributes().add(portAttr);
          }
          prepareSaveCommands(protocol, irCommandInfo, device,
                deviceCommands, irTrans, globalCache);
       }
       if (deviceCommands.size() > 0) {
          for (DeviceCommand command : deviceCommands) {
             genericDAO.save(command);
          }
          return deviceCommands;
       } else {
          throw new IrFileParserException("No commands to save");
       }
    }

    private void prepareSaveCommands(Protocol protocol,
          IRCommandInfo irCommandInfo,
          org.openremote.modeler.domain.Device device,
          List<DeviceCommand> deviceCommands, IRTrans irTrans,
          GlobalCache globalCache) throws IrFileParserException {
       boolean toggle;
       ProtocolAttr commandAttr = new ProtocolAttr();
       commandAttr.setName("command");
       IRCode currentCom = null;
       String codeString = "";
       for (IRCommand commands : currentIRCommands) {
          if (commands.getName().equals(irCommandInfo.getName())
                && commands.getOriginalCodeString().equals(
                      irCommandInfo.getOriginalCodeString())) {
             currentCom = commands.getCode();
             break;
          }
       }
       currentIRCommands.remove(currentCom);
       if (currentCom != null) {
          if (currentCom.requiresToggle()) {
             toggle = true;
             if (irTrans != null) {
                try {
                   codeString = new RawIRCodeRepresentationHandler()
                         .getRepresentationFromCode(
                               currentCom.getRawCode(toggle))
                         .getStringRepresentation();

                } catch (InvalidIRCodeException e) {
                   throw new IrFileParserException("error in parsing code :"
                         + irCommandInfo.getName());
                }
                commandAttr.setValue("sndccf " + codeString + ",l"
                      + irTrans.getIrLed());
             } else if (globalCache != null) {
                
                codeString = new GCIRCodeRepresentationHandler()
                      .getRepresentationFromCode(currentCom.getRawCode(toggle))
                      .getStringRepresentation();
                commandAttr.setValue("sendir," + globalCache.getConnector()
                      + ",1," + codeString);
             }
             commandAttr.setProtocol(protocol);
             protocol.getAttributes().add(commandAttr);

             DeviceCommand deviceCommand = new DeviceCommand();
             deviceCommand.setDevice(device);
             deviceCommand.setProtocol(protocol);
             deviceCommand.setName(irCommandInfo.getName() + "_ToggleOn");

             protocol.setDeviceCommand(deviceCommand);
             device.getDeviceCommands().add(deviceCommand);
             deviceCommands.add(deviceCommand);
             toggle = false;
             commandAttr = new ProtocolAttr();
             commandAttr.setName("command");
             if (irTrans != null) {
                protocol = new Protocol();
                protocol.setType("UDP");
                
                ProtocolAttr ipAttr = new ProtocolAttr();
                ipAttr.setName("ipAddress");
                ipAttr.setValue(irTrans.getIpAdress());
                ipAttr.setProtocol(protocol);
                protocol.getAttributes().add(ipAttr);
                
                ProtocolAttr portAttr = new ProtocolAttr();
                portAttr.setName("port");
                portAttr.setValue(irTrans.getUdpPort());
                portAttr.setProtocol(protocol);
                protocol.getAttributes().add(portAttr);
                try {
                   codeString = new RawIRCodeRepresentationHandler()
                         .getRepresentationFromCode(
                               currentCom.getRawCode(toggle))
                         .getStringRepresentation();
                } catch (InvalidIRCodeException e) {
                   throw new IrFileParserException("error in parsing code :"
                         + irCommandInfo.getName());
                }
                commandAttr.setValue("sndccf " + codeString + ",l"
                      + irTrans.getIrLed());
             } else if (globalCache != null) {
                protocol = new Protocol();
                protocol.setType("TCP/IP");
                
                ProtocolAttr ipAttr = new ProtocolAttr();
                ipAttr.setName("ipAddress");
                ipAttr.setValue(globalCache.getIpAddress());
                ipAttr.setProtocol(protocol);
                protocol.getAttributes().add(ipAttr);
                
                ProtocolAttr portAttr = new ProtocolAttr();
                portAttr.setName("port");
                portAttr.setValue(globalCache.getTcpPort());
                portAttr.setProtocol(protocol);
                protocol.getAttributes().add(portAttr);
                codeString = new GCIRCodeRepresentationHandler()
                      .getRepresentationFromCode(currentCom.getRawCode(toggle))
                      .getStringRepresentation();
                commandAttr.setValue("sendir," + globalCache.getConnector()
                      + ",1," + codeString);
             }

             commandAttr.setProtocol(protocol);
             protocol.getAttributes().add(commandAttr);

             deviceCommand = new DeviceCommand();
             deviceCommand.setDevice(device);
             deviceCommand.setProtocol(protocol);
             deviceCommand.setName(irCommandInfo.getName() + "_ToggleOff");

             protocol.setDeviceCommand(deviceCommand);
             device.getDeviceCommands().add(deviceCommand);
             deviceCommands.add(deviceCommand);
          } else {//no toggle
             toggle = true;
             if (irTrans != null) {
                try {
                   codeString = new RawIRCodeRepresentationHandler()
                         .getRepresentationFromCode(
                               currentCom.getRawCode(toggle))
                         .getStringRepresentation();
                } catch (InvalidIRCodeException e) {
                   throw new IrFileParserException("error in parsing code :"
                         + irCommandInfo.getName());
                }
                commandAttr.setValue("sndccf " + codeString + ",l"
                      + irTrans.getIrLed());
             } else if (globalCache != null)  {
                codeString = new GCIRCodeRepresentationHandler()
                      .getRepresentationFromCode(currentCom.getRawCode(toggle))
                      .getStringRepresentation();
                commandAttr.setValue("sendir," + globalCache.getConnector()
                      + ",1," + codeString);
             }
             commandAttr.setProtocol(protocol);
             protocol.getAttributes().add(commandAttr);

             DeviceCommand deviceCommand = new DeviceCommand();
             deviceCommand.setDevice(device);
             deviceCommand.setProtocol(protocol);
             deviceCommand.setName(irCommandInfo.getName());

             protocol.setDeviceCommand(deviceCommand);
             device.getDeviceCommands().add(deviceCommand);
             deviceCommands.add(deviceCommand);
          }
       }

    }
    */

}
