package com.google.appengine.demos.ImageCollage;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.images.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Created by compsci on 12/22/14.
 */
public class CollageMaster {

    private List<Collage.AttributionCell> attributionTable = new ArrayList<Collage.AttributionCell>();
    private int width;
    private int height;

    public Image getCollage(ImagesService imgService, BlobKey blobKey, int depth, int threshold, int inputFactor){
        try {
            Crawler crawler = new Crawler();
            crawler.buildIndex();
            byte[] imageData = getData(blobKey);
            Image image = ImagesServiceFactory.makeImage(imageData);

            System.out.println("starting to get the collage");
            ThreadFactory tf = ThreadManager.currentRequestThreadFactory();
            width = image.getWidth();
            height = image.getHeight();
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
                    //System.out.println("on block "+j+", "+i);
                    double x = (double)i / initSplit;
                    Transform crop = ImagesServiceFactory.makeCrop(x, y, x + prop, y + prop);
                    RunnableCollage subCollage = new RunnableCollage(imgService.applyTransform(crop, image),
                            depth - 3, threshold, inputFactor, crawler, (int)(x*width), (int)(y*height));
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
                            (int) (m*inputFactor / (double)initSplit * width), (int) (n*inputFactor / (double)initSplit * height), 1f, Composite.Anchor.TOP_LEFT));
                    attributionTable.addAll(subCollages.get(n * initSplit + m).getAttributionTable());
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
