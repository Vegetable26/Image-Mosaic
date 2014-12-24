package com.google.appengine.demos.ImageCollage;

import be.hogent.tarsos.lsh.Index;
import be.hogent.tarsos.lsh.Vector;
import be.hogent.tarsos.lsh.families.EuclidianHashFamily;
import com.flickr4java.flickr.*;
import com.flickr4java.flickr.photos.*;
import com.flickr4java.flickr.photos.licenses.*;
import com.google.appengine.api.datastore.*;

import java.util.Date;
import java.util.List;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import java.util.Random;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/*
This class enables users to crawl flickr for images.
It can also crawl the datastore for near
 */
public class Crawler {
    //authentication for flickr API
    private String apiKey = "c3916472c30d567c38898c61ee7d0638";
    private String sharedSecret = "06cd65d9183f0d70";
    //flickr object
    private Flickr f;
    //object to search flickr
    private PhotosInterface finder;
    //index built on LSH
    private Index index;
    //the GAE datastore
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    /*
    constructor for the crawler
     */
    public Crawler() {
        //try to make the Flickr object
        try {
            f = new Flickr(apiKey, sharedSecret, new REST());
            finder = f.getPhotosInterface();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //arguments to EuclidianHashFamily are : int w (which seems arbitrary), int lengthOfVectors
        EuclidianHashFamily hashFam = new EuclidianHashFamily(10, 24);
        //arguments to Index are : a HashFamily, int numHashTables, int numHashes
        index = new Index(hashFam, 5, 5);
    }

    /*
    inputs: String[] topics, the search parameters
            int numImg, the number of images we want to get from flickr
    Downloads numImg photos related to topics from flickr
     */
    public void getPhotos(String[] topics, int numImg){
        PhotoList<Photo> photos = new PhotoList<Photo>();
        SearchParameters param = new SearchParameters();
        param.setTags(topics);
        //we can only download images licensed with CC
        param.setLicense("2");
        try {
            photos = finder.search(param,numImg,1);
            String searchParam = "";
            //concatentate topics into a single string, searchParam
            for (int i = 0; i < topics.length; i++){
                searchParam += topics[i];
                if (i != topics.length-1){
                    searchParam += " ";
                }
            }
            //get the time of the search so that we can store it
            String when = new Date().toString();
            DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
            //parse the String when into a Date object
            //we need to do this to remove some of the precision of the long time
            Date date = df.parse(when);
            long time = date.getTime();
            //make an Entity for the search
            Entity search = new Entity("search", date.toString());
            search.setProperty("time", time);
            System.out.println("The search occured at "+time);
            search.setProperty("searchParam", searchParam);
            search.setProperty("numImg", numImg);
            //put the search into the datastore
            datastore.put(search);
            //put the photos we found into the datastore
            addToDatastore(photos, time);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
    inputs: PhotoList<Photo> photos, the photos we found using getPhotos
            long time, the time at which the search was executed
    adds all of the photos in photos to the datastore
     */
    public void addToDatastore(PhotoList<Photo> photos, long time){
        //iterate over all of the photos in the list
        for (Photo pic : photos){
            try {
                //make a ProcessedImage for the photo
                ProcessedImage processed = new ProcessedImage(pic, f);
                //this makes it easy to get an rgb histogram for the photo
                double[] rgbHist = processed.getRGBHistogram();
                //the key is the photo's url, creator, and id
                String key = processed.getUrl()+" "+processed.getUsername() +" " + processed.getId();
                //set key as the key for the flickrPic
                Entity flickrPic = new Entity("flickrPic", key);
                for (int i = 0; i < rgbHist.length; i++){
                    double binVal = rgbHist[i];
                    if (i < 8){
                        flickrPic.setProperty("r"+Integer.toString(i), binVal);
                    }
                    else if (i < 16){
                        flickrPic.setProperty("g"+Integer.toString(i-8), binVal);
                    }
                    else{
                        flickrPic.setProperty("b"+Integer.toString(i-16), binVal);
                    }
                }
                flickrPic.setProperty("blob", new Blob(processed.getImage().getImageData()));
                flickrPic.setProperty("time", time);
                datastore.put(flickrPic);
            }
            catch (Exception e){

            }
        }
    }

    public void buildIndex(){
        Query getAll = new Query("flickrPic");
        PreparedQuery allPics = datastore.prepare(getAll);
        int i = 0;
        for (Entity result : allPics.asIterable()){
            double[] rgbHist = getArrayFromEntity(result);
            String key = result.getKey().getName();
            Vector vec = new Vector(key, rgbHist);
            index.index(vec);
            i++;
        }
        System.out.println("Added " + i + " photos to the LSH.");
    }

    public void updateIndex(){
    }

    public double[] getArrayFromEntity(Entity result) {
        double[] array = new double[24];
        for (int i = 0; i < 24; i++){
            String propertyName;
            if (i < 8){
                propertyName = "r"+Integer.toString(i);
            }
            else if (i < 16){
                propertyName = "g"+Integer.toString(i-8);
            }
            else{
                propertyName = "b"+Integer.toString(i-16);
            }
            array[i] = (double)result.getProperty(propertyName);
        }
        return array;
    }

    //returns the key (url+ " " + username) for the closest vector to rgbHistogram in Index
    public Image query(double[] rgbHistogram){
        Vector vector = new Vector("", rgbHistogram);
        List<Vector> closest = index.query(vector, 1);

        try {
            Entity closestEnt = datastore.get(KeyFactory.createKey("flickrPic", closest.get(0).getKey()));
            return ImagesServiceFactory.makeImage(((Blob)closestEnt.getProperty("blob")).getBytes());

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public ProcessedImage ditherQuery(double[] rgbHistogram){

        Vector vector = new Vector("", rgbHistogram);
        List<Vector> closest = index.query(vector, 3);
        Random random = new Random();
        double dither = random.nextDouble();
        int whichOne = -1;
        if (dither < .5){
            whichOne = 0;
        }
        else if (dither < .85){
            whichOne = 1;
        }
        else{
            whichOne = 2;
        }
        try {
            String key = closest.get(whichOne).getKey();
            Entity closestEnt = datastore.get(KeyFactory.createKey("flickrPic", key));
            Image returnVal = ImagesServiceFactory.makeImage(((Blob)closestEnt.getProperty("blob")).getBytes());
            return new ProcessedImage(returnVal,key.split("\\s")[0],key.split("\\s")[1], key.split("\\s")[2]);

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }




}