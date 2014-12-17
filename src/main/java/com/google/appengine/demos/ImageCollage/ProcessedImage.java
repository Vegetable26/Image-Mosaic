package com.google.appengine.demos.ImageCollage;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.photos.*;
import com.google.appengine.api.images.*;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;



import java.net.URL;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
// Big changes: newest version

public class ProcessedImage{
    private double[] average = new double[3];
    private double[] rgbHistogram;
    private byte[] imageBytes;
    private ImagesService imagesService = ImagesServiceFactory.getImagesService();

    protected Image img;
    protected int width; // Width of the base image
    protected int height; // Height of the base image
    protected String username;
    protected String url;
    public ProcessedImage(String url, String inputUser){
        this.url = url;
        readImage(url);
        getDim();
        username = inputUser;
    }
    // To process a Photo retrieved from flickr, used in Crawler.find()
    public ProcessedImage(Photo photo, Flickr f){
        url = photo.getThumbnailUrl();
        readImage(url);
        getDim();
        readUsername(photo,f);
    }

    // To process a Image
    public ProcessedImage(Image photo){
        img = photo;
        getDim();
        url = null;
        username = null;
    }

    // To process an image from the Crawler.query()

    private void readImage(String myURL) {
        URLFetchService fetchService = URLFetchServiceFactory.getURLFetchService();
        try {
            HTTPResponse fetchResponse = fetchService.fetch(new URL(myURL));
            img = ImagesServiceFactory.makeImage(fetchResponse.getContent());
        }
        catch (IOException e){
            e.printStackTrace();
        }


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
        Image getRGBHistForMe;
        if (forBlock){
            getRGBHistForMe = getBlock(firstX, firstY, partitionHeight, partitionWidth).getImage();
        }
        /*
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
        */
        else{
            getRGBHistForMe = img;
        }
        return normalizeArray(makeRGBHistogramFromImage(getRGBHistForMe));
    }

    private double[] makeRGBHistogramFromImage(Image image){
        int[][] histogram = imagesService.histogram(image);
        double[] rgbHistogram = new double[24];
        for (int color = 0; color < 3; color++){
            for (int magnitude = 0; magnitude < 8; magnitude++){
                double sum = 0;
                for (int j = magnitude*32; j < magnitude*32 + 32; j++){
                    sum+= histogram[color][j];
                }
                rgbHistogram[color*8+magnitude] = sum;
            }
        }
        return rgbHistogram;
    }

    public double getVariance(boolean forBlock, int firstX, int firstY, int partitionHeight, int partitionWidth){
        Image getVarForMe;
        if (forBlock){
            getVarForMe = getBlock(firstX, firstY, partitionHeight, partitionWidth).getImage();
        }
        else{
            getVarForMe = img;
        }
        return getVarianceFromImage(getVarForMe);
    }


    private double getVarianceFromImage(Image image){
        int[][] histogram = imagesService.histogram(image);
        double[] average = new double[3];
        double[] squareAverage = new double[3];
        int dim = image.getHeight()*image.getWidth();
        //get the sum for each row
        for (int color = 0; color < 3; color++){
            for (int bin = 0; bin < 256; bin++){
                average[color] += histogram[color][bin]*bin;
                squareAverage[color] += histogram[color][bin]*Math.pow(bin,2);
            }
        }
        //get the variance for each color channel
        double variance = 0;
        for (int color = 0; color < 3; color++){
            average[color] /= dim;
            squareAverage[color] /= dim;
            variance += squareAverage[color] - Math.pow(average[color],2);
        }
        //sum the variances
        //square root the variance
        return variance;
    }

    public ProcessedImage getBlock(int firstX, int firstY, int partitionHeight, int partitionWidth){
        Transform cropBlock = ImagesServiceFactory.makeCrop((float)firstX/width, (float)firstY/height, (float)(firstX+partitionWidth)/width, (float)(firstY+partitionHeight)/height);
        Image cropped = ImagesServiceFactory.makeImage(img.getImageData());
        return new ProcessedImage(imagesService.applyTransform(cropBlock, cropped));
    }

/*
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
*/

    // Scales an image to the given dimensions (x,y)
    public Image getScaled(int x, int y){
        Transform scaleTransform = ImagesServiceFactory.makeResize(x, y, true);
        Image scaled = imagesService.applyTransform(scaleTransform, img);
        return scaled;
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

    public Image getImage(){
        return img;
    }
}