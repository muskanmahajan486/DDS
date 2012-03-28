package org.openremote.irservice.resources.pronto;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.openremote.irservice.service.ProntoService;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

public class ProntoFileResource extends ServerResource {
  
  private ProntoService prontoService;
  
  /**
   * Provides a simple Web form.
   * 
   * @return The String representation of an HTML page.
   */
  @Get("html")
  public String browse() {
      StringBuilder sb = new StringBuilder("<html><body>");
      sb.append("<form method=\"post\" ");
      sb.append("action=\"");
//      sb.append(getReference());
      sb.append("/irservice/rest/ProntoFile");
      sb.append("\" ");
      sb.append("enctype=\"multipart/form-data\">");
      sb.append("<input name=\"fileToUpload\" type=\"file\"/>");
      sb.append("<input type=\"submit\"/>");
      sb.append("</form>");
      sb.append("</body></html>");
      return sb.toString();
  }

  /**
   * Accepts and processes a representation posted to the resource.
   */
  @Post()
  public Representation accept(Representation entity) throws Exception {
      Representation rep = null;
      if (entity != null) {
          if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
              // The Apache FileUpload project parses HTTP requests which
              // conform to RFC 1867, "Form-based File Upload in HTML". That
              // is, if an HTTP request is submitted using the POST method,
              // and with a content type of "multipart/form-data", then
              // FileUpload can parse that request, and get all uploaded files
              // as FileItem.

              // 1/ Create a factory for disk-based file items
              DiskFileItemFactory factory = new DiskFileItemFactory();
              factory.setSizeThreshold(1024 * 1024 * 10); // TODO

              // 2/ Create a new file upload handler based on the Restlet
              // FileUpload extension that will parse Restlet requests and
              // generates FileItems.
              RestletFileUpload upload = new RestletFileUpload(factory);
              List<FileItem> items;

              // 3/ Request is parsed by the handler which generates a list of FileItems
              items = upload.parseRequest(getRequest());

              String fileName = "";
              // Process only the uploaded item called "fileToUpload" and save it on disk
              boolean found = false;
              for (final Iterator<FileItem> it = items.iterator(); it.hasNext() && !found;) {
                  FileItem fi = it.next();
                  if (fi.getFieldName().equals("fileToUpload")) {
                      found = true;
                      File file = File.createTempFile("Pronto_", ".xcf", new File("/tmp"));
                      fileName = file.getName();
                      fi.write(file);
                  }
              }
              
              System.out.println(">" + fileName  + "<");
              // Once handled, the content of the uploaded file is sent
              // back to the client.
              if (found) {
                  // Create a new representation based on disk file.
                  // The content is arbitrarily sent as plain text.
                  rep = new StringRepresentation(fileName.substring("Pronto_".length(), fileName.indexOf(".xcf")), MediaType.TEXT_HTML);
              } else {
                  // Some problem occurs, sent back a simple line of text.
                  rep = new StringRepresentation("File Upload Error : no file uploaded", MediaType.TEXT_HTML); // TODO: check if better way to handle error
              }
          }
      } else {
          // POST request with no entity.
          setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
      }
      System.out.println("pronto service >" + prontoService + "<");
      
      return rep;
  }

  public void setProntoService(ProntoService prontoService) {
    this.prontoService = prontoService;
  }

}
