package com.google.appengine.demos.ImageCollage;

// I added the attribution table methods and added Timers for performance testing 12/21

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

import java.util.List;

import java.util.ArrayList;

public class Collage {
    private Image img;
    private  ProcessedImage processedImage;
    private ImagesService imgService;
    private  int width; // Width of the base image
    private  int height; // Height of the base image
    private  int maxDepth;
    private  int varianceThreshhold;
    private  Crawler crawler;
    private int factor = 1;
    private List<AttributionCell> attributionTable = new ArrayList<AttributionCell>();
    private int initialX;
    private int initialY;

    public Collage(Image inputImg, int depth, int inputThresh, int inputFactor, Crawler crawler, int initialX, int initialY){

        factor = inputFactor;
        this.crawler = crawler;
        img = inputImg;
        processedImage = new ProcessedImage(img);
        setMaxDepth(depth);
        height = inputImg.getHeight();
        width = inputImg.getWidth();
        int limit = 1500000;
        this.initialX = initialX;
        this.initialY = initialY;
        varianceThreshhold =  inputThresh;
        imgService = ImagesServiceFactory.getImagesService();
        System.out.println("Starting to Collage-ify");

        // Used for testing


    }

    public  void setMaxDepth(int input){
        maxDepth = input;
    }

    public Image getCollage(){

        return alphaOverlay(.4,colorBlock(0 , 0, height, width, 0, processedImage));

    }

    public Image colorBlock(int firstX, int firstY, int partitionHeight, int partitionWidth, int depth, ProcessedImage colorMe){
        // Depth tracks how many recursions down the current image is
        if(firstX<width && firstY<height) {
            boolean isBest = true;
            if (depth < maxDepth) {
                int halfWidth = partitionWidth / 2;
                int halfHeight = partitionHeight / 2;



                double original = colorMe.getVariance(false, firstX, firstY, Math.min(partitionHeight, height - firstY - 1), Math.min(partitionWidth, width - firstX - 1));
                if (original > varianceThreshhold){
                    ArrayList<Composite> composites = new ArrayList<Composite>();
                    isBest = false;
                    depth++;

                    // Start cropTime

                    Image upperLeft = colorBlock(firstX, firstY, halfHeight, halfWidth, depth, colorMe.getBlock(0, 0, halfHeight, halfWidth)); // Upper Left
                    Image upperRight = colorBlock(firstX + halfWidth, firstY, halfHeight, partitionWidth - halfWidth, depth, colorMe.getBlock(halfWidth, 0, halfHeight, partitionWidth - halfWidth)); // Upper Right
                    Image lowerLeft = colorBlock(firstX, firstY + halfHeight, partitionHeight-halfHeight, halfWidth, depth, colorMe.getBlock(0, halfHeight, partitionHeight-halfHeight, halfWidth)); // Lower Left
                    Image lowerRight = colorBlock(firstX + halfWidth, firstY + halfHeight, partitionHeight-halfHeight, partitionWidth - halfWidth, depth, colorMe.getBlock(halfWidth, halfHeight, partitionHeight-halfHeight, partitionWidth - halfWidth)); // Lower Right

                    // End cropTime



                    composites.add(ImagesServiceFactory.makeComposite(upperLeft,0,0,1f, Composite.Anchor.TOP_LEFT));

                    composites.add(ImagesServiceFactory.makeComposite(upperRight, halfWidth*factor, 0, 1f, Composite.Anchor.TOP_LEFT));

                    composites.add(ImagesServiceFactory.makeComposite(lowerLeft,0, halfHeight*factor,1f,Composite.Anchor.TOP_LEFT));

                    composites.add(ImagesServiceFactory.makeComposite(lowerRight,(halfWidth)*factor, (halfHeight)*factor,1f,Composite.Anchor.TOP_LEFT));

                    Image returnVal = imgService.composite(composites,partitionWidth*factor,partitionHeight*factor,0);

                    return returnVal;


                }
                else{

                    return colorIn(firstX,firstY, partitionHeight, partitionWidth,depth);
                }
            }
            else {
                return colorIn(firstX,firstY,partitionHeight, partitionWidth,depth);

            }
        }
        return colorIn(firstX,firstY, partitionHeight, partitionWidth,depth);
    }
    private Image colorIn( int firstX, int firstY, int partitionHeight, int partitionWidth, int depth){
        // Have to perform a query
        ProcessedImage processed =  crawler.ditherQuery(processedImage.getRGBHistogram(true, firstX, firstY, partitionHeight, partitionWidth));
        if(depth<1){
            processed = new ProcessedImage(processed.getSmallUrl(),processed.getUsername(),processed.getId());
        }
        // Attribution Table for the collage

        attributionTable.add(new AttributionCell(firstX+initialX,firstY+initialY,firstX+initialX+partitionWidth,firstY+initialY+partitionHeight,processed.getUsername(),processed.getUrl(),processed.getId()));

        //System.out.println(processed.getUsername());

        return processed.getScaled(partitionWidth*factor,partitionHeight*factor);

    }
    public List<AttributionCell> getAttributionTable(){
        return attributionTable;
    }

    public Image alphaOverlay(double opacity, Image collage){
        ArrayList<Composite>composites = new ArrayList<Composite>();
        composites.add(ImagesServiceFactory.makeComposite(processedImage.getImage(),0,0,(float)opacity, Composite.Anchor.TOP_LEFT));
        composites.add(ImagesServiceFactory.makeComposite(collage,0,0,.85f,Composite.Anchor.TOP_LEFT));
        return imgService.composite(composites,width*factor,height*factor,0);
    }
    public class AttributionCell{
        double x1;
        double x2;
        double y1;
        double y2;
        String author;
        String url;
        String trueUrl;
        String id;
        public AttributionCell(double x1, double y1, double x2, double y2, String author, String url, String id){
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.author = author;
            this.url = url;
            this.id = id;
            findUrl();

        }
        private void findUrl(){
            try {
                String returnVal;
                String codeNumber = url.split("[^a-zA-Z0-9 ]")[8];
                trueUrl = codeNumber.split("_")[0];
                trueUrl = "https://www.flickr.com/photos/" +  id + "/" + trueUrl;
            }catch(Exception e){
                System.out.println("Error at "+ url);

            }
        }
    }

}
