package com.google.appengine.demos.ImageCollage;

import java.applet.Applet;
import com.google.appengine.api.images.*;
import java.io.*;
import java.net.URL;
import com.flickr4java.flickr.photos.*;

// Big changes: newest version


import java.io.*;
import java.net.URL;
import javax.imageio.*;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.Flickr;

public interface IProcessedImage {



    public void readImage(String myURL);

    public void readUsername(Photo photo, Flickr f);

    public void getDim();

    public String getUrl();

    public String getUsername();

    //if forBlock is true, we only want to make the RGB histogram for the specified area

    public double[] getRGBHistogram(boolean forBlock, int firstX, int firstY, int partitionHeight, int partitionWidth);


    public double[] findAverage(int firstX, int firstY, int partitionHeight, int partitionWidth);


    public int[] RGBArray(int code);

    public int toPixel(double[] color);

    public double[] getAverage();

    // Scales an image to the given dimensions (x,y)
    public Image getScaled(int x, int y);

    public double[] normalizeArray(double[] rgbHist);

}