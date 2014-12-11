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

    private Image img;
    private int width; // Width of the base image
    private int height; // Height of the base image
    private String username;
    private String url;
    private double[] average = new double[3];
    private double[] rgbHistogram;
    private byte[] imageBytes;
    ImagesService imagesService = ImagesServiceFactory.getImagesService();

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
    public ProcessedImage(String url, String inputUser){
        this.url = url;
        readImage(url);
        getDim();
        username = inputUser;
    }



    private void readImage(String myURL) {
        /*
        URL urlObj = null;
        try {
            urlObj = new URL(myURL); // read the url
        }
        catch (MalformedURLException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        InputStream is = null;
        //byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
        try {
            is = urlObj.openStream ();
            int n;
            while ( (n = is.read()) > -1 ) {
                bais.write(n);
            }
        }
        catch (IOException e) {
            e.printStackTrace ();
            // Perform any other exception handling that's appropriate.
        }
        try{
            if (is != null){
                is.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        ByteBuffer buff = ByteBuffer.put(bais.toByteArray());
        GcsFilename gcsName = new GcsFilename("plucky-respect-765.appspot.com", url);
        gcs.createOrReplace(gcsName, GcsFileOptions.Builder().build(), buff);
        img =
        */
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
        /*
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
        */
        Image getVarForMe;
        if (forBlock){
            getVarForMe = getBlock(firstX, firstY, partitionHeight, partitionWidth).getImage();
        }
        else{
            getVarForMe = img;
        }
        return Math.sqrt(getVarianceFromImage(getVarForMe));
    }


    private double getVarianceFromImage(Image image){
        int[][] histogram = imagesService.histogram(image);
        double[] average = new double[3];
        double[] vars = new double[3];
        //get the sum for each row
        for (int color = 0; color < 3; color++){
            for (int bin = 0; bin < 256; bin++){
                average[color] += histogram[color][bin];
            }
        }
        //get the variance for each color channel
        for (int color = 0; color < 3; color++){
            average[color] /= 256;
            for (int bin = 0; bin < 256; bin++){
                vars[color] += (Math.pow(histogram[color][bin], 2) - Math.pow(average[color], 2));
            }
        }
        //sum the variances
        double variance = 0;
        for (double var : vars){
            variance += Math.pow(var, 2);
        }
        //square root the variance
        return Math.sqrt(variance);


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