package com.google.appengine.demos.ImageCollage;


import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.images.*;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import com.google.appengine.api.files.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.lang.Runnable;
import com.google.appengine.api.ThreadManager;
import java.util.concurrent.ThreadFactory;

/*
UploadServlet: this class's doPost method is called when a user submits the form
to make a collage. It writes the url for the collage and the attribution mapping for
each thumbnail into the collage to the response.
 */

public class MakeMosaicServlet extends HttpServlet {
    //create a BlobstoreService so that we can 1) get the user's photo; 2) serve the collage
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] status = CheckLogInStatus.getStatus().split(" ");
        try {
            req.setAttribute("isApproved", status[0]);
            req.setAttribute("log", status[1]);
            req.setAttribute("action", blobstoreService.createUploadUrl("/make_mosaic"));
            req.getRequestDispatcher("MakeCollage.jsp").forward(req, resp);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        long start = System.currentTimeMillis();

        //get the blob for the submitted image
        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("userPic");
        //get the rest of the parameters
        int depth = Integer.parseInt(req.getParameter("depth"));
        int threshold = Integer.parseInt(req.getParameter("threshold"));
        int inputFactor = 3;
        boolean smartSizing = false;
        String smartSizingString = req.getParameter("smartSizing");
        if (smartSizingString.compareTo("true") == 0){
            smartSizing = true;
        }
        System.out.println("smart sizing is" + smartSizing);
        //begin making the collage
        CollageMaster master = new CollageMaster();
        ImagesService imgService = ImagesServiceFactory.getImagesService();
        Image collage = master.getCollage(imgService, blobKey, depth, threshold, inputFactor, smartSizing);
        //get the url for collage by adding it to the blobstore
        String url = imgService.getServingUrl(ServingUrlOptions.Builder.withBlobKey(toBlobstore(collage))) + "=s1600";
        //now get the url and attribute wrapped together in an object in JSON format
        String urlAndAttribute = new Gson().toJson(new URLAndAttribute(url, master.getAttributionTable(), master.getX(), master.getY()));
        //write the urlAndAttribute to the response
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        blobstoreService.delete(blobKey);
        resp.getWriter().write(urlAndAttribute);
        long end = System.currentTimeMillis();
        //System.out.println("Execution:" + (end - start));
    }

    /*
    inputs: Image uploadMe, the image that is to be uploaded to the blobstore
    returns the BlobKey for uploadMe
     */
    public static BlobKey toBlobstore(Image uploadMe){
        try {
            // Get a file service
            FileService fileService = FileServiceFactory.getFileService();

            // Create a new Blob file with mime-type "image/png"
            AppEngineFile file = fileService.createNewBlobFile("image/jpeg");// png

            // Open a channel to write to it
            boolean lock = true;
            FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);

            // This time we write to the channel directly
            writeChannel.write(ByteBuffer.wrap
                    (uploadMe.getImageData()));

            // Now finalize
            writeChannel.closeFinally();
            //return the blobKey for the file
            return fileService.getBlobKey(file);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*
    URLAndAttribute wraps up the pertinent data for a completed collage:
        -String url, the url of the collage image
        -List<Collage.AttributionCell> attributionTable, which maps a pixel area to the url
            of the thumbnail that is in the pixel area
        -int width, the width of the collage
        -int height, the height of the collage
     */
    class URLAndAttribute{
        String url;
        List<Collage.AttributionCell> attributionTable;
        int width;
        int height;
        public URLAndAttribute(String url, List<Collage.AttributionCell> attributes, int width, int height){
            this.url = url;
            this.attributionTable = attributes;
            this.width = width;
            this.height = height;
        }
    }
}