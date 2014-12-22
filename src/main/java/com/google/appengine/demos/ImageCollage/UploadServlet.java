package com.google.appengine.demos.ImageCollage;


import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.images.*;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import com.google.appengine.api.files.*;
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
        Image collage = getCollage(imgService, blobKey, depth, threshold, inputFactor);
        String url = imgService.getServingUrl(ServingUrlOptions.Builder.withBlobKey(toBlobstore(collage)));
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(url+"=s1600");
        long end = System.currentTimeMillis();
        System.out.println("Execution:"+(end - start));

    }

    private Image getCollage(ImagesService imgService, BlobKey blobKey, int depth, int threshold, int inputFactor){
        try {
            Crawler crawler = new Crawler();
            crawler.buildIndex();
            byte[] imageData = getData(blobKey);
            Image image = ImagesServiceFactory.makeImage(imageData);

            System.out.println("starting to get the collage");
            ThreadFactory tf = ThreadManager.currentRequestThreadFactory();
            int width = image.getWidth();
            int height = image.getHeight();
            int limit = 1000000;

            if (height*width > limit){  // Scales the image if the current image is too high resolution
                double scalingFactor = Math.pow((double)limit/(height*width),.5);
                Transform scaleTransform = ImagesServiceFactory.makeResize((int)(width*scalingFactor), (int)(height*scalingFactor));
                image = imgService.applyTransform(scaleTransform, image);
                imageData = image.getImageData();
                height = image.getHeight();
                width = image.getWidth();
            }

            ArrayList<RunnableCollage> subCollages = new ArrayList<>();
            ArrayList<Thread> threads = new ArrayList<>();
            int initSplit = 4;
            double prop = 1.0 / initSplit;
            for (int j = 0; j < initSplit; j++) {
                double y = (double)j / initSplit;
                for (int i = 0; i < initSplit; i++) {
                    image = ImagesServiceFactory.makeImage(imageData);
                    System.out.println("on block "+j+", "+i);
                    double x = (double)i / initSplit;
                    Transform crop = ImagesServiceFactory.makeCrop(x, y, x + prop, y + prop);
                    RunnableCollage subCollage = new RunnableCollage(imgService.applyTransform(crop, image),
                            depth - 3, threshold, inputFactor, crawler);
                    System.out.println("applied the crop on the square x:["+x+", "+(x+prop)+"], y:["+y+", "+(y+prop)+"]");
                    //create the object of FutureTask
                    subCollages.add(subCollage);

                    //Create a thread object using the task object created
                    Thread t = tf.newThread(subCollage);

                    //Start the thread as usual
                    t.start();
                    threads.add(t);
                }
            }
            int z = 0;
            for (Thread thread : threads){
                thread.join();
                System.out.println("thread "+ z + " is done");
                z++;
            }
            ArrayList<Composite> composites = new ArrayList<>();
            for (int n = 0; n < initSplit; n++) {
                for (int m = 0; m < initSplit; m++) {
                    composites.add(ImagesServiceFactory.makeComposite(subCollages.get(n * initSplit + m).getCollage(),
                            (int) (m / (double)initSplit * width), (int) (n / (double)initSplit * height), 1f, Composite.Anchor.TOP_LEFT));
                }
            }
            return imgService.composite(composites, width * inputFactor, height * inputFactor, 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private byte[] getData(BlobKey blobKey) {
        InputStream input;
        byte[] oldImageData = null;
        try {
            input = new BlobstoreInputStream(blobKey);
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = input.read(byteChunk)) > 0) {
                bais.write(byteChunk, 0, n);
            }
            oldImageData = bais.toByteArray();
        } catch (IOException e) {}

        return oldImageData;

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
}