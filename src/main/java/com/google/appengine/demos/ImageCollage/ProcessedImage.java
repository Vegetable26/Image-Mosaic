package com.google.appengine.demos.ImageCollage;

// I changed the Constructors of ProcessedImage to allow for the attribution tables 12/21


import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.photos.*;
import com.google.appengine.api.images.*;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import java.net.URL;
import java.io.IOException;



public class ProcessedImage{
    private double[] average = new double[3];
    private double[] rgbHistogram;
    private byte[] imageBytes;
    private ImagesService imagesService = ImagesServiceFactory.getImagesService();
    private String id = null;
    protected Image img;
    protected int width; // Width of the base image
    protected int height; // Height of the base image
    protected String username;
    protected String url;

    public ProcessedImage(String url, String inputUser, String id){
        this.url = url;
        readImage(url);
        getDim();
        username = inputUser;
        this.id = id;
    }
    // To process a Photo retrieved from flickr, used in Crawler.find()
    public ProcessedImage(Photo photo, Flickr f){
        url = photo.getThumbnailUrl();
        readImage(url);
        getDim();
        readUsername(photo,f);
        this.id = photo.getOwner().getId();
    }

    // To process a Image from the query
    public ProcessedImage(Image photo, String url, String username, String id, String smallUrl){
        img = photo;
        getDim();
        this.url = url;
        this.username = username;
        this.id = id;
    }
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

        return variance;
    }

    public ProcessedImage getBlock(int firstX, int firstY, int partitionHeight, int partitionWidth){

        Transform cropBlock = ImagesServiceFactory.makeCrop((float)firstX/width, (float)firstY/height, (float)(firstX+partitionWidth)/width, (float)(firstY+partitionHeight)/height);
        Image cropped = ImagesServiceFactory.makeImage(img.getImageData());
        return new ProcessedImage(imagesService.applyTransform(cropBlock, cropped),null,null, null, null);
    }

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
    public String getSmallUrl(){
        String returnVal = url.replace("t.jpg","m.jpg");
        return returnVal;
    }
    public String getId(){
        return id;
    }
}