package com.google.appengine.demos.ImageCollage;

import java.lang.Runnable;
import com.google.appengine.api.images.*;
import java.util.HashMap;
import java.util.List;

/*

Update: 12/24
Added in the non-Smart sized collage
Added in the scaled image cache

**********
NOTE:

To use smart-sizing, assign True to the last parameter of Collage()
To use equal-sizing, assign False

**********


Runnable wrapper for the Collage class. Produces a collage for an image and its attributionTable

 */

class RunnableCollage implements Runnable{

    private Image myImg; // The image to be collaged
    private int depth, scalingFactor, threshold; // The max-recursion depth, scaling factor, and RGB variance stop condition
    private Image myMosaic; // The finished collage
    private Crawler crawler; // Acceses the image database
    private int firstX; // X coordinate of the upper left corner of myImg within the base image
    private int firstY; // Y coordinate of the upper left corner of myImg within the base image
    private List<Collage.AttributionCell> attributionTable; // Attribution table of the Flickr images in the collage
    private CollageTimer collageTimer; // Timer for the Collage object
    private HashMap<String, ProcessedImage> imageCache; // Cache for the scaled images
    private boolean smartSize; // Determines whether the image is smart-sized or normal sized

    /*

    Constructs a RunnableCollage object with the given parameters

     */

    public RunnableCollage(Image myImg, int depth, int threshold, int scalingFactor, Crawler crawler, int firstX, int firstY, CollageTimer collageTimer, HashMap<String, ProcessedImage> imageCache, boolean smartSize){
        this.myImg = myImg;
        this.depth = depth;
        this.threshold = threshold;
        this.scalingFactor = scalingFactor;
        this.crawler = crawler;
        this.firstX = firstX;
        this.firstY = firstY;
        this.collageTimer = collageTimer;
        this.imageCache = imageCache;
        this.smartSize = smartSize;
    }

    /*

    Creates a collage and its attribution table with the given parameters

     */

    public void run(){

        Collage collage = new Collage(myImg, depth, threshold, scalingFactor, crawler, firstX, firstY,collageTimer, imageCache);
        myMosaic = collage.getCollage(smartSize);
        attributionTable = collage.getAttributionTable();

    }

    /*

    Returns the finished collage

     */

    public Image getCollage(){return myMosaic;}

    /*

    Returns the attribution table for the collage

    */

    public List<Collage.AttributionCell> getAttributionTable(){return attributionTable;}

}