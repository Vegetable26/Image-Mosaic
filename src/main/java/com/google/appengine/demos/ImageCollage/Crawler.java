package com.google.appengine.demos.ImageCollage;

import be.hogent.tarsos.lsh.Index;
import be.hogent.tarsos.lsh.Vector;
import be.hogent.tarsos.lsh.families.EuclidianHashFamily;
import com.flickr4java.flickr.*;
import com.flickr4java.flickr.photos.*;
import com.flickr4java.flickr.photos.licenses.*;
import com.google.appengine.api.datastore.*;
import java.util.List;


public class Crawler {
    private String apiKey = "c3916472c30d567c38898c61ee7d0638";
    private String sharedSecret = "06cd65d9183f0d70";
    private Flickr f;
    private PhotosInterface finder;
    private Index index;

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

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

    public PhotoList<Photo> getPhotos(String[] topics, int numImg){
        PhotoList<Photo> photos = new PhotoList<Photo>();
        SearchParameters param = new SearchParameters();
        param.setTags(topics);
        try {
            photos = finder.search(param,numImg,1);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return photos;
    }

    //inserts all of the photos in the list to the index
    public void addToDatastore(PhotoList<Photo> photos){

        for (Photo pic : photos){
            ProcessedImage processed = new ProcessedImage(pic, f);
            double[] rgbHist = processed.getRGBHistogram(false, 0, 0, 0, 0);

            String key = processed.getUrl()+" "+processed.getUsername();
            System.out.println("The key is "+key);
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
            datastore.put(flickrPic);
            /*Vector vec = new Vector(key, rgbHist);
            if (query(rgbHist).compareTo(key) != 0){
                index.index(vec);
            }*/
        }
    }

    public void buildIndex(){
        Query getAll = new Query("flickrPic");
        PreparedQuery allPics = datastore.prepare(getAll);
        for (Entity result : allPics.asIterable()){
            double[] rgbHist = getArrayFromEntity(result);
            String key = result.getKey().getName();
            Vector vec = new Vector(key, rgbHist);
            index.index(vec);
        }
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
    public String query(double[] rgbHistogram){
        Vector vector = new Vector("", rgbHistogram);
        List<Vector> closest = index.query(vector, 1);
        return closest.get(0).getKey();
    }




}