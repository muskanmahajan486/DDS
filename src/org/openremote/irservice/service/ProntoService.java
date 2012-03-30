package org.openremote.irservice.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.openremote.irservice.domain.BrandInfo;
import org.openremote.irservice.domain.CodeSetInfo;
import org.openremote.irservice.domain.DeviceInfo;
import org.openremote.irservice.domain.IRCommandInfo;
import org.xml.sax.SAXException;

import com.tinsys.ir.database.Brand;
import com.tinsys.ir.database.CodeSet;
import com.tinsys.ir.database.Device;
import com.tinsys.ir.database.IRCommand;
import com.tinsys.ir.representations.IRCodeRepresentationFactory;
import com.tinsys.ir.representations.pronto.NecIRCodeRepresentationHandler;
import com.tinsys.ir.representations.pronto.RC5IRCodeRepresentationHandler;
import com.tinsys.ir.representations.pronto.RC5xIRCodeRepresentationHandler;
import com.tinsys.ir.representations.pronto.RawIRCodeRepresentationHandler;
import com.tinsys.pronto.irfiles.ProntoFileParser;

public class ProntoService {

  private ProntoFileParser prontoFileParser;
  
  public String uploadProntoConfigurationFile() {
    return null;
  }
  
  public ArrayList<String> getBrands(String prontoFileHandle) {
    
    try {
      prontoFileParser.parseFile(new ZipFile(new File("/tmp/Pronto_" + prontoFileHandle + ".xcf"))); // TODO review name construction
    } catch (ZipException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    System.out.println("File parsed");
    ArrayList<String> brandInfo = new ArrayList<String>();
     List<Brand> brands = prontoFileParser.getBrands();
     for (Brand brand : brands) {
       System.out.println("Brand name " + brand.getBrandName());
        brandInfo.add(brand.getBrandName());
     }
     
     return brandInfo;
 }
  
  public ArrayList<String> getDevices(String prontoFileHandle, String brandName) {
    try {
      prontoFileParser.parseFile(new ZipFile(new File("/tmp/Pronto_" + prontoFileHandle + ".xcf"))); // TODO review name construction
    } catch (ZipException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ArrayList<String> deviceInfo = new ArrayList<String>();
    Brand b = new Brand(brandName);
    List<Device> devices = prontoFileParser.getDevices(b);
    for (Device device : devices) {
       deviceInfo.add(device.getModelName());
    }
    return deviceInfo;
  }

  public ArrayList<CodeSetInfo> getCodeSets(String prontoFileHandle, String brandName, String deviceName) {
    try {
      prontoFileParser.parseFile(new ZipFile(new File("/tmp/Pronto_" + prontoFileHandle + ".xcf"))); // TODO review name construction
    } catch (ZipException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ArrayList<CodeSetInfo> codeSetInfo = new ArrayList<CodeSetInfo>();

    Device d = new Device(new Brand(brandName), deviceName);
    DeviceInfo di = new DeviceInfo(new BrandInfo(brandName), deviceName);

    List<CodeSet> codeSets = prontoFileParser.getCodeSets(d);
    int index = 0;
    for (CodeSet codeSet : codeSets) {
       codeSetInfo.add(new CodeSetInfo(di, codeSet.getDescription(), codeSet.getCategory(), index));
       index++;
    }
    return codeSetInfo;
  }

  public ArrayList<IRCommandInfo> getIRCommands(String prontoFileHandle, String brandName, String deviceName, int index) {
    try {
      prontoFileParser.parseFile(new ZipFile(new File("/tmp/Pronto_" + prontoFileHandle + ".xcf"))); // TODO review name construction
    } catch (ZipException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (SAXException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ArrayList<IRCommandInfo> iRCommandInfo = new ArrayList<IRCommandInfo>();
    Device d = new Device(new Brand(brandName), deviceName);
    List<IRCommand> iRcommands = prontoFileParser.getCodeSets(d).get(index).getIRCommands();

    for (IRCommand irCommand : iRcommands) {
        IRCommandInfo iRCI;
        if (irCommand.getCode() != null) {
           iRCI = new IRCommandInfo(irCommand.getName(), irCommand.getCode()
                 .toString(), irCommand.getOriginalCodeString(),
                 irCommand.getComment(), null); // TODO: do we really need codeset as last param
        } else {
           iRCI = new IRCommandInfo(irCommand.getName(), null,
                 irCommand.getOriginalCodeString(), irCommand.getComment(), null); // TODO: do we really need codeset as last param
        }
        iRCommandInfo.add(iRCI);
     }
     return iRCommandInfo;
  }

  public void setProntoFileParser(ProntoFileParser prontoFileParser) {
    this.prontoFileParser = prontoFileParser;
    
    
    // TODO: have all this defined in Spring too
    IRCodeRepresentationFactory factory = new IRCodeRepresentationFactory();
    new RC5IRCodeRepresentationHandler().registerWithFactory(factory);
    new RC5xIRCodeRepresentationHandler().registerWithFactory(factory);
    new RawIRCodeRepresentationHandler().registerWithFactory(factory);
    new NecIRCodeRepresentationHandler().registerWithFactory(factory);
    prontoFileParser.setFactory(factory);

  }
  
}
