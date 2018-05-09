/*==============================================================================

name:       AudioServlet.java

purpose:    Audio servlet.

==============================================================================*/
                                       // package ----------------------------//
package com.pumaj.servlets;
                                       // imports ----------------------------//
import com.google.api.client.http.MultipartContent;
import com.google.appengine.api.datastore.*;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.channels.Channels;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.sun.media.sound.WaveFileWriter;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import static java.lang.Boolean.TRUE;


// AudioServlet ======================//
public class AudioServlet extends HttpServlet
{

private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
        .initialRetryDelayMillis(10)
        .retryMaxAttempts(10)
        .totalRetryPeriodMillis(15000)
        .build());

// private final GcsService gcsSevice = GcsServiceFactory.createGcsService();


public void doGet(
   HttpServletRequest  req, 
   HttpServletResponse rsp)
   throws ServletException, IOException
{
    Object response = "No";
    doResponse(req, rsp, response);
}
public void doPost (
            HttpServletRequest req,
            HttpServletResponse rsp)
            throws ServletException, IOException
{
    Object response = "Ok";
    boolean isMultipart = ServletFileUpload.isMultipartContent(req);
    if(isMultipart != TRUE){
        response = new Exception();
    } else{
        try {
            response = saveFile(req);
        } catch (FileUploadException | IOException e) {
            response = e;
        }
    }

    doResponse(req,rsp,response);
}

public String saveFile(HttpServletRequest req) throws IOException, FileUploadException, ServletException {
    /*String test = IOUtils.toString(req.getReader());
    int filenameIndex = test.indexOf("filename");
    String testString = test.substring(filenameIndex,test.length()-1);
    int quoteIndex = testString.indexOf(".wav");
    String the_name = testString.substring(10,quoteIndex+4);

    HttpServletRequest other_req = req; */

    DiskFileItemFactory factory = new DiskFileItemFactory();
    ServletContext servletContext = this.getServletConfig().getServletContext();
    File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
    factory.setRepository(repository);

    // Create a new file upload handler
    ServletFileUpload upload = new ServletFileUpload(factory);

    // Parse the request
    List<FileItem> items = upload.parseRequest(req);

    if(items.size() != 1){
        throw new ServletException("There is not one file");
    }

    //Trying something new to get file name

    InputStream inputStream = items.get(0).getInputStream();
      try
      {
         parseFile(inputStream);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }


    try {
    	//get file name and add to end of zipURL
        String zipURL = doTransform("https://storage.googleapis.com/audiowavelet.appspot.com/pipe.wav");
        return zipURL;
    } catch (UnsupportedAudioFileException e) {
        e.printStackTrace();
    }
    return "";

}
private void parseFile(InputStream inputStream) throws IOException
{
   String body = null;
   StringBuilder stringBuilder = new StringBuilder();
   BufferedReader bufferedReader = null;
   if (inputStream != null) {
         bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
         char[] charBuffer = new char[128];
            int bytesRead = -1;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } else {
            stringBuilder.append("");
        }



    body = stringBuilder.toString();
    int filenameIndex = body.indexOf("filename");
    String testString = body.substring(filenameIndex,body.length()-1);
    int quoteIndex = testString.indexOf(".wav");
    String the_name = testString.substring(10,quoteIndex+4);


    GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
    //GcsFilename fileName = getFileName(req);

    GcsFilename fileName = new GcsFilename("audiowavelet.appspot.com", the_name);
    GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName, instance);
    copy(inputStream, Channels.newOutputStream(outputChannel));
}


private void copy(InputStream input, OutputStream output) throws IOException {
    try {
        byte[] buffer = new byte[2 * 1024 * 1024];
        int bytesRead = input.read(buffer);
        while (bytesRead != -1) {
            output.write(buffer, 0, bytesRead);
            bytesRead = input.read(buffer);
        }
    } finally {
        input.close();
        output.close();
    }
}

public String doTransform(String path) throws IOException, UnsupportedAudioFileException {
    Wave wave = new Wave(path);

    wave.wavelet = 39;

    wave.compress(1500);
    String transformedFileName = path.substring(56, path.indexOf(".wav")) + ".zip";
    // wave.decompress();


    GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
    GcsFilename fileName = new GcsFilename("audiowavelet.appspot.com",transformedFileName);

    GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName,instance);

   ZipOutputStream the_bot = wave.toZipStream(Channels.newOutputStream(outputChannel));
   int test = 0;
   String zipURL = "https://storage.googleapis.com/audiowavelet.appspot.com/" + transformedFileName;
   return zipURL;

}


public void doOptions(
   HttpServletRequest  req,
   HttpServletResponse rsp)
   throws ServletException, IOException
{
   doResponse(req, rsp, "OK");
}
/*------------------------------------------------------------------------------

@name       doResponse - generate response
                                                                              */
                                                                             /**
            Write the response back to the requestor.

@return     void

@param      req         servlet request
@param      rsp         servlet response
@param      response    response value

                                                                      */
//------------------------------------------------------------------------------
public void doResponse(
   HttpServletRequest  req, 
   HttpServletResponse rsp,
   Object              response)
   throws ServletException, IOException 
{
                                       // enable cors                         //
   rsp.addHeader("Access-Control-Allow-Origin",  "http://localhost:4200");
   rsp.addHeader("Access-Control-Max-Age",       "1800");
   rsp.addHeader("Access-Control-Allow-Credentials", "true");

                                       // support all requested headers       //
   for (Enumeration<String> ctlReqHeaders =
        req.getHeaders("Access-control-request-headers");
        ctlReqHeaders.hasMoreElements();)
   {
      rsp.addHeader("Access-Control-Allow-Headers", ctlReqHeaders.nextElement());
   }
   if (response instanceof Throwable)
   {
      rsp.sendError(
         HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.toString());
   }
   else
   {
      rsp.getWriter().print(response);
   }
}


/*------------------------------------------------------------------------------

@name       getRequestParams - get request parameters
                                                                              */
                                                                             /**
            Get request parameters.

@return     request parameters.

                                                                              */
//------------------------------------------------------------------------------
public Map<String,Object> getRequestParams(
   HttpServletRequest  req)
{
   Map<String,String[]> paramsRaw = req.getParameterMap();
   Map<String,Object>   params    = new HashMap<String,Object>();

   for (String key : paramsRaw.keySet())
   {
      params.put(key, paramsRaw.get(key)[0]);
   }

   return(params);
}
}//====================================// end class ComServiceSimpleServlet ------------//
