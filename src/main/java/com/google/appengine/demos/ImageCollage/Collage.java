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
    private  double varianceThreshhold;
    private  Crawler crawler = new Crawler();
    private int factor = 1;
    private List<AttributionCell> attributionTable = new ArrayList<AttributionCell>();



    private ArrayList<Time> scalingTime = new ArrayList<Time>();
    private Time searchTime = new Time();
    private Time compositeTime = new Time();
    private Time cropTime = new Time();
    private Time histogramTime = new Time();
    private Time totalTime = new Time();


    public Collage(Image inputImg, int depth, double inputThresh, int inputFactor){

        factor = inputFactor;
        crawler.buildIndex();
        img = inputImg;
        processedImage = new ProcessedImage(img);
        setMaxDepth(depth);
        height = inputImg.getHeight();
        width = inputImg.getWidth();
        int limit = 1500000;
        if (height*width > limit){  // Scales the image if the current image is too high resolution
            double scalingFactor = Math.pow((double)limit/(height*width),.5);
            img = processedImage.getScaled((int)(scalingFactor*width), (int)(scalingFactor*height), new Time());
            processedImage = new ProcessedImage(img);
            height = img.getHeight();
            width = img.getWidth();
        }
        varianceThreshhold =  inputThresh;
        imgService = ImagesServiceFactory.getImagesService();
        System.out.println("Starting to Collage-ify");

        // Used for testing

        for(int i=0;i<=maxDepth;i++){
            scalingTime.add(new Time());
        }
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

                histogramTime.startTimer();

                double original = colorMe.getVariance(false, firstX, firstY, Math.min(partitionHeight, height - firstY - 1), Math.min(partitionWidth, width - firstX - 1));

                histogramTime.endTimer();

                if (original > varianceThreshhold){
                    ArrayList<Composite> composites = new ArrayList<Composite>();
                    isBest = false;
                    depth++;

                    // Start cropTime

                    Image upperLeft = colorBlock(firstX, firstY, halfHeight, halfWidth, depth, colorMe.getBlock(0, 0, halfHeight, halfWidth, cropTime)); // Upper Left
                    Image upperRight = colorBlock(firstX + halfWidth, firstY, halfHeight, partitionWidth - halfWidth, depth, colorMe.getBlock(halfWidth, 0, halfHeight, partitionWidth - halfWidth,cropTime)); // Upper Right
                    Image lowerLeft = colorBlock(firstX, firstY + halfHeight, partitionHeight-halfHeight, halfWidth, depth, colorMe.getBlock(0, halfHeight, partitionHeight-halfHeight, halfWidth,cropTime)); // Lower Left
                    Image lowerRight = colorBlock(firstX + halfWidth, firstY + halfHeight, partitionHeight-halfHeight, partitionWidth - halfWidth, depth, colorMe.getBlock(halfWidth, halfHeight, partitionHeight-halfHeight, partitionWidth - halfWidth,cropTime)); // Lower Right

                    // End cropTime

                    compositeTime.startTimer();

                    composites.add(ImagesServiceFactory.makeComposite(upperLeft,0,0,1f, Composite.Anchor.TOP_LEFT));

                    composites.add(ImagesServiceFactory.makeComposite(upperRight, halfWidth*factor, 0, 1f, Composite.Anchor.TOP_LEFT));

                    composites.add(ImagesServiceFactory.makeComposite(lowerLeft,0, halfHeight*factor,1f,Composite.Anchor.TOP_LEFT));

                    composites.add(ImagesServiceFactory.makeComposite(lowerRight,(halfWidth)*factor, (halfHeight)*factor,1f,Composite.Anchor.TOP_LEFT));

                    //System.out.println("At depth"+depth+"this is compositing"+composites.size()+"images" + firstX +',' +firstY);

                    Image returnVal = imgService.composite(composites,partitionWidth*factor,partitionHeight*factor,0);

                    compositeTime.endTimer();

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
        ProcessedImage processed =  crawler.ditherQuery(processedImage.getRGBHistogram(true, firstX, firstY, partitionHeight, partitionWidth),searchTime);

        // Attribution Table for the collage

        attributionTable.add(new AttributionCell(firstX,firstY,firstX+partitionWidth,firstY+partitionHeight,processed.getUsername(),processed.getUrl()));
        System.out.println(processed.getUsername());

        return processed.getScaled(partitionWidth*factor,partitionHeight*factor,scalingTime.get(depth));

    }
    public List<AttributionCell> getAttributionTable(){
        return attributionTable;
    }

    public Image alphaOverlay(double opacity, Image collage){
        ArrayList<Composite>composites = new ArrayList<Composite>();
        composites.add(ImagesServiceFactory.makeComposite(processedImage.getImage(),0,0,(float)opacity, Composite.Anchor.TOP_LEFT));
        composites.add(ImagesServiceFactory.makeComposite(collage,0,0,.85f,Composite.Anchor.TOP_LEFT));

        System.out.println("Cropping time took"+cropTime.getTotalTime());
        for(int i=0;i<maxDepth;i++){
            System.out.println("Scaling time took"+scalingTime.get(i).getTotalTime());
        }
        System.out.println("Search time took"+searchTime.getTotalTime());
        System.out.println("Standard Deviation time took"+histogramTime.getTotalTime());
        System.out.println("Composite time took"+compositeTime.getTotalTime());



        return imgService.composite(composites,width*factor,height*factor,0);
    }
    public class AttributionCell{
        double x1;
        double x2;
        double y1;
        double y2;
        String author;
        String url;
        public AttributionCell(double x1, double y1, double x2, double y2, String author, String url){
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.author = author;
            this.url = url;
        }
    }

}
