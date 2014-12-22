package com.google.appengine.demos.ImageCollage;

import java.lang.Runnable;
import com.google.appengine.api.images.*;


class RunnableCollage implements Runnable{
    private Image myImg;
    private int depth, scalingFactor, threshold;
    private Image myMosaic;
    private Crawler crawler;
    public RunnableCollage(Image myImg, int depth, int threshold, int scalingFactor, Crawler crawler){
        this.myImg = myImg;
        this.depth = depth;
        this.threshold = threshold;
        this.scalingFactor = scalingFactor;
        this.crawler = crawler;
    }
    public void run(){
        Collage collage = new Collage(myImg, depth, threshold, scalingFactor, crawler);
        myMosaic = collage.getCollage();
    }

    public Image getCollage(){
        return myMosaic;
    }
}