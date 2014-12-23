package com.google.appengine.demos.ImageCollage;

import java.lang.Runnable;
import com.google.appengine.api.images.*;
import java.util.List;


class RunnableCollage implements Runnable{
    private Image myImg;
    private int depth, scalingFactor, threshold;
    private Image myMosaic;
    private Crawler crawler;
    private int firstX;
    private int firstY;
    private List<Collage.AttributionCell> attributionTable;
    public RunnableCollage(Image myImg, int depth, int threshold, int scalingFactor, Crawler crawler, int firstX, int firstY){
        this.myImg = myImg;
        this.depth = depth;
        this.threshold = threshold;
        this.scalingFactor = scalingFactor;
        this.crawler = crawler;
        this.firstX = firstX;
        this.firstY = firstY;
    }
    public void run(){
        Collage collage = new Collage(myImg, depth, threshold, scalingFactor, crawler, firstX, firstY);
        myMosaic = collage.getCollage();
        attributionTable = collage.getAttributionTable();
    }

    public Image getCollage(){
        return myMosaic;
    }
    public List<Collage.AttributionCell> getAttributionTable(){return attributionTable;}
}