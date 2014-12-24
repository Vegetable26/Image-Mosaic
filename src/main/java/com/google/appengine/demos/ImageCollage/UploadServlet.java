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

public class UploadServlet extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        long start = System.currentTimeMillis();

        //get the blob
        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("userPic");
        ImagesService imgService = ImagesServiceFactory.getImagesService();
        //get the collage
        int depth = Integer.parseInt(req.getParameter("depth"));
        int threshold = Integer.parseInt(req.getParameter("threshold"));
        int inputFactor = Integer.parseInt(req.getParameter("inputFactor"));

        System.out.println("made the collage object");
        CollageMaster master = new CollageMaster();
        Image collage = master.getCollage(imgService, blobKey, depth, threshold, inputFactor);
        String url = imgService.getServingUrl(ServingUrlOptions.Builder.withBlobKey(toBlobstore(collage))) + "=s1600";
        String returnURL = new Gson().toJson(new URLAndAttribute(url, master.getAttributionTable(), master.getX(), master.getY()));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        blobstoreService.delete(blobKey);
        resp.getWriter().write(returnURL);
        long end = System.currentTimeMillis();
        System.out.println("Execution:" + (end - start));
    }


    public static BlobKey toBlobstore(Image uploadMe) throws FileNotFoundException,     FinalizationException, LockException, IOException {
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
            return fileService.getBlobKey(file);
        }
        catch (Exception e){ e.printStackTrace();}
        return null;
    }

    public class URLAndAttribute{
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