package org.openremote.irservice.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;

import org.openremote.irservice.domain.BrandInfo;
import org.xml.sax.SAXException;

import com.tinsys.ir.database.Brand;
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
  
  public ArrayList<BrandInfo> getBrands(String prontoFileHandle) {
    
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
    if (prontoFileParser != null) {
      ArrayList<BrandInfo> brandInfo = new ArrayList<BrandInfo>();
       List<Brand> brands = prontoFileParser.getBrands();
       for (Brand brand : brands) {
         System.out.println("Brand name " + brand.getBrandName());
          brandInfo.add(new BrandInfo(brand.getBrandName()));
       }
       
       return brandInfo;
    } else {
       return null;
    }
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
