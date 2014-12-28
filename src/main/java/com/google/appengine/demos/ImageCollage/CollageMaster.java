package com.google.appengine.demos.ImageCollage;

/*

 Update: 12/24
 Added in a cache for scaled images
 Created the class
 Jpeg and PNG offer almost the exact same performance

 CollageMaster: Controls the many Collage threads that are called on an image. Produces the composited collage and
 also produces the composited AttributionTable for the collage.

*/

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.images.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadFactory;


public class CollageMaster {

    private List<Collage.AttributionCell> attributionTable = new ArrayList<Collage.AttributionCell>();

    // A composited list of the AttributionCell's of the sub-collages

    private int width; // Width of the image to be collaged
    private int height; // Height of the image to be collaged
    private CollageTimer collageTimer; // Used to halt execution of threads at a specified time limit
    private HashMap<String, ProcessedImage> imageCache = new HashMap<String,ProcessedImage>();

    // Caches scaled images to reduce the amount of time using Image.scale()

    /*

    Controls the execution of multiple threads that will create the overall collage

    Inputs: imgService: Google API ImagesService
    blobKey: Blob key corresponding to the image to be collaged
    depth: Maximum recursion depth of the collage
    threshold: Variance threshhold of the collage
    inputFactor: The scaling factor of the collage

     */

    public Image getCollage(ImagesService imgService, BlobKey blobKey, int depth, int threshold, int inputFactor, boolean smartSize){

        try {
            collageTimer = new CollageTimer(55); // Used to trigger the Quick-Finish in the Collage objects

            // Must be determined empirically, along with the weights in the Collage.colorBlock() method

            Crawler crawler = new Crawler(); // Provides access to the image database
            crawler.buildIndex(); // Builds the LSH M-Tree for the images
            byte[] imageData = getData(blobKey); // Finds the base image submitted by the user
            Image image = ImagesServiceFactory.makeImage(imageData);
            ThreadFactory tf = ThreadManager.currentRequestThreadFactory();
            width = image.getWidth(); // Dimensions of the base-image
            height = image.getHeight();
            int limit = 1000000; // Automatically rescales base-images that are larger than this pixel threshold
            if (height*width > limit){  // Scales the image if the current image is too high resolution
                double scalingFactor = Math.pow((double)limit/(height*width),.5); // Rescales the image to "limit" number of pixels
                Transform scaleTransform = ImagesServiceFactory.makeResize((int)(width*scalingFactor), (int)(height*scalingFactor));
                image = imgService.applyTransform(scaleTransform, image);
                imageData = image.getImageData();
                height = image.getHeight();
                width = image.getWidth();
            }
            ArrayList<RunnableCollage> subCollages = new ArrayList<>(); // ArrayList of the subCollage threads
            ArrayList<Thread> threads = new ArrayList<>();
            int initSplit = 4; // Splits the base-image into initSplit x initSplit smaller images
            double prop = 1.0 / initSplit;
            for (int j = 0; j < initSplit; j++) { // Create 4 rows
                double y = (double)j / initSplit;
                for (int i = 0; i < initSplit; i++) { // Create 4 columns
                    image = ImagesServiceFactory.makeImage(imageData);
                    //System.out.println("on block "+j+", "+i);
                    double x = (double)i / initSplit;
                    Transform crop = ImagesServiceFactory.makeCrop(x, y, x + prop, y + prop); // Crop the sub-image from the base-image
                    RunnableCollage subCollage = new RunnableCollage(imgService.applyTransform(crop, image),
                            depth - 3, threshold, inputFactor, crawler, (int)(x*width), (int)(y*height), collageTimer, imageCache ,smartSize);
                    subCollages.add(subCollage); //create the object of FutureTask
                    Thread t = tf.newThread(subCollage); //Create a thread object using the task object created
                    t.start(); // Begin the thread as soon as possible
                    threads.add(t);
                }
            }
            int z = 0;
            for (Thread thread : threads){ // Compile the completed threads
                thread.join();
                System.out.println("thread "+ z + " is done");
                z++;
            }
            ArrayList<Composite> composites = new ArrayList<>();
            for (int n = 0; n < initSplit; n++) { // Loop over the 4 rows
                for (int m = 0; m < initSplit; m++) { // Loop over the 4 columns
                    composites.add(ImagesServiceFactory.makeComposite(subCollages.get(n * initSplit + m).getCollage(),
                            (int) (m*inputFactor / (double)initSplit * width), (int) (n*inputFactor / (double)initSplit * height), 1f, Composite.Anchor.TOP_LEFT));

                    // Composite the 16 sub-collages

                    attributionTable.addAll(subCollages.get(n * initSplit + m).getAttributionTable());

                    // Compile the attribution cells from each of the collages

                }
            }
            return imgService.composite(composites, width * inputFactor, height * inputFactor, 0);
        }
        catch (Exception e) {
            return null;
        }

    }

    /*

    Reads the image data from a blobkey

    */

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
        } catch (IOException e) {

        }
        return oldImageData;

    }

    public List<Collage.AttributionCell> getAttributionTable(){
        return attributionTable;
    }
    public int getX(){
        return width;
    }
    public int getY(){
        return height;
    }





}
