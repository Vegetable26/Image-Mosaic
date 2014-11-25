package com.google.appengine.demos.ImageCollage;

import java.applet.Applet;
import com.google.appengine.api.images.*;
import java.io.*;
import java.net.URL;
import com.flickr4java.flickr.photos.*;

// Big changes: newest version

import
import java.io.*;
import java.net.URL;
import javax.imageio.*;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.Flickr;

public class ProcessedImage implements IProcessedImage{

    // To process a Photo retrieved from flickr, used in Crawler.find()
    public ProcessedImage(Photo photo, Flickr f){
        url = photo.getThumbnailUrl();
        readImage(url);
        getDim();
        readUsername(photo,f);
    }

    // To process a BufferedImage
    public ProcessedImage(Image photo){
        img = photo;
        getDim();
        url = null;
        username = null;
    }


    // To process an image from the Crawler.query()
    public ProcessedImage(String url, String inputUser){
        readImage(url);
        getDim();
        username = inputUser;
    }

    private void readImage(String myURL) {
        URL url = new URL(myURL); // read the url
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        InputStream is = null;
        byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
        try {
            is = url.openStream ();
            int n;
            while ( (n = is.read(byteChunk)) > 0 ) {
                bais.write(byteChunk, 0, n);
            }
        }
        catch (IOException e) {
            System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
            e.printStackTrace ();
            // Perform any other exception handling that's appropriate.
        }
        finally {
            if (is != null) { is.close(); }
        }
        img = ImageServiceFactor.makeImage(btyeChunk);
    }

    private void readUsername(Photo photo, Flickr f){
        try {
            PeopleInterface member = f.getPeopleInterface();
            username = member.getInfo(photo.getOwner().getId()).getUsername();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void getDim(){
        width = img.getWidth();
        height = img.getHeight();
    }

    public String getUrl(){
        return url;
    }

    public String getUsername(){
        return username;
    }

    //if forBlock is true, we only want to make the RGB histogram for the specified area

    public double[] getRGBHistogram(boolean forBlock, int firstX, int firstY, int partitionHeight, int partitionWidth){
        double[] rgbHist = new double[24];
        for (int j = 0; j < (forBlock ? partitionHeight : height); j++){
            for (int i = 0; i < (forBlock ? partitionWidth : width); i++){
                int[] rgb = RGBArray(img.getRGB(forBlock ? firstX+i : i, forBlock ? firstY+j : j));
                for (int k = 0; k < 3; k++){
                    int color = rgb[k];
                    int bin = 255/color;
                    rgbHist[k*8+bin]++;
                }
            }
        }
        return normalizeArray(rgbHist);
    }


    private double[] findAverage(int firstX, int firstY, int partitionHeight, int partitionWidth){
        // Returns an array containing the average (pixel integer) and sum of the RGB variances.
        for(int k = 0; k<partitionWidth; k++){ //Iterates over the partition by columns
            for(int l = 0; l<partitionHeight; l++){
                int[] rgb = RGBArray(img.getRGB((firstX+k),(firstY+l)));
                for(int m=0;m<3;m++){
                    average[m] = average[m] + rgb[m];
                }
            }
        }
        double varSum = 0;
        for(int h = 0;h<3;h++){
            average[h] = average[h]/(partitionHeight*partitionWidth); // Divides by the number of pixels in the partition
        }
        return average;
    }


    private int[] RGBArray(int code){
        // Converts a pixel integer into a tuple[r,b,g]
        int[] triple = new int[3];
        Color c = new Color(code);
        triple[0] = c.getRed();
        triple[1] = c.getGreen();
        triple[2] = c.getBlue();
        return triple;
    }

    private int toPixel(double[] color){
        // Converts a tuple[r,b,g] into a pixel integer
        int rgb = 255;
        rgb = (rgb << 8) + (int)color[0];
        rgb = (rgb << 8) + (int)color[1];
        rgb = (rgb << 8) + (int)color[2];
        return rgb;
    }

    public double[] getAverage(){
        return average;
    }

    // Scales an image to the given dimensions (x,y)
    public Image getScaled(int x, int y){
        Transform scaleTransform = ImageFactoryService.makeResize(x, y);
        Image scaled = ImageServiceFactory.getImageService().applyTransform(scaleTransform, img);
        return img;
    }

    private double[] normalizeArray(double[] rgbHist){
        double[] normalized = new double[rgbHist.length];
        double sum = 0;
        for (double element : rgbHist){
            sum += element;
        }
        for (int i = 0; i < rgbHist.length; i++){
            normalized[i] = rgbHist[i]/sum;
        }
        return normalized;
    }

}