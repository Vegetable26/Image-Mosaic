package com.google.appengine.demos.ImageCollage;

import com.google.appengine.api.images.Composite;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;

import java.util.ArrayList;

public class Collage {
    private ArrayList<Composite> listComposites=new ArrayList<Composite>();
    private Image img;
    private  ProcessedImage processedImage;
    private ImagesService imgService;
    private  int width; // Width of the base image
    private  int height; // Height of the base image
    private  int minSize; // Stores the size of the minimum base image
    private  int maxDepth;
    private  double varianceThreshhold;
    private  Crawler crawler = new Crawler();

    public Collage(Image inputImg, int depth, double inputThresh){
        img = inputImg;
        processedImage = new ProcessedImage(img);
        setMaxDepth(depth);
        height = inputImg.getHeight();
        width = inputImg.getWidth();
        varianceThreshhold =  inputThresh;
        imgService = ImagesServiceFactory.getImagesService();
    }
    /*
    public Pixelate(String url,int depth,double inputThresh){
        img =
        processedImage = new ProcessedImage(img);
        setMaxDepth(depth);
        varianceThreshhold =  inputThresh;
    }
    */


    public  void setMaxDepth(int input){
        maxDepth = input;
    }

    public Image getCollage(int partitionHeight, int partitionWidth){
        // Pixelates the image
        if(partitionHeight%2  !=0 || partitionWidth %2!=0){
            System.out.println("Please use a multiple of 8 for your width/height");
            // This is necessary for the recursive variance formula
        }
        // Creates a blank image of the same size as the base image
        for(int i = 0; i<Math.ceil((double) width / partitionWidth);i++){ // Splits the image into partition-size columns
            for(int j = 0; j<Math.ceil((double) height / partitionHeight);j++){	 // Splits the image into partition-size columns
                System.out.println("looking at the highest level partitions");
                colorBlock(i*partitionWidth,j*partitionHeight,partitionHeight,partitionWidth,0);
                // Colors each partition of the graph
            }
        }
        Image newImage = imgService.composite(listComposites,width,height,0);
        return newImage;
    }

    public  void colorBlock(int firstX, int firstY, int partitionHeight, int partitionWidth, int depth){
        // Depth tracks how many recursions down the current image is

        if(firstX<width && firstY<height) {
            boolean isBest = true;
            if (depth < maxDepth) {
                int halfWidth = partitionWidth / 2;
                int halfHeight = partitionHeight / 2;
                double original = processedImage.getVariance(true, firstX, firstY, Math.min(partitionHeight, height - firstY - 1), Math.min(partitionWidth, width - firstX - 1));
                if (original > varianceThreshhold){
                    isBest = false;
                    depth++;
                    colorBlock(firstX, firstY, partitionHeight - halfHeight, halfWidth, depth); // Upper Left
                    colorBlock(firstX + halfWidth, firstY, partitionHeight - halfHeight, partitionWidth - halfWidth, depth); // Upper Right
                    colorBlock(firstX, firstY + halfHeight, halfHeight, halfWidth, depth); // Lower Left
                    colorBlock(firstX + halfWidth, firstY + halfHeight, halfHeight, partitionWidth - halfWidth, depth); // Lower Right
                }
            }
            if (isBest) {
                colorIn(firstX,firstY,Math.min(partitionHeight,height-firstY-1),Math.min(partitionWidth,width-firstX-1));
                // drawBorder(newImage, firstX, firstY, Math.min(partitionHeight,height-firstY-1), Math.min(partitionWidth,width-firstX-1));
            }
        }
    }
    private void colorIn( int firstX, int firstY, int partitionHeight, int partitionWidth){
        // Have to perform a query
        System.out.println("trying to color shit in");
        String[] urlAndUsername = crawler.query(processedImage.getRGBHistogram(true,firstX,firstY,partitionHeight,partitionWidth)).split(" ");
        Image fillIn = new ProcessedImage(urlAndUsername[0],urlAndUsername[1]).getScaled(partitionWidth,partitionHeight);
        Composite aPaste = ImagesServiceFactory.makeComposite(fillIn, firstX, firstY, 1f, Composite.Anchor.TOP_LEFT);
        listComposites.add(aPaste);
    }


    /*
    public  void render(Image showImage){
        // Renders a BufferedImage
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        BufferedImage jo = showImage;
        //frame.add(new ImagePane(jo));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    
    public int[] RGBArray(int code){
        // Converts a pixel integer into a tuple[r,b,g]
        int[] triple = new int[3];
        Color c = new Color(code);
        triple[0] = c.getRed();
        triple[1] = c.getGreen();
        triple[2] = c.getBlue();
        return triple;
    }
    
    public int toPixel(double[] color){
        // Converts a tuple[r,b,g] into a pixel integer
        int rgb = 255;
        rgb = (rgb << 8) + (int)color[0];
        rgb = (rgb << 8) + (int)color[1];
        rgb = (rgb << 8) + (int)color[2];
        return rgb;
    }
    
    public void drawBorder(BufferedImage newImage, int firstX, int firstY, int partitionHeight, int partitionWidth){
        int black =-16777216;
        for(int k = 0; k<partitionWidth; k++){ //Iterates over the partition by columns
            newImage.setRGB(firstX+k,firstY,black); //Colors the left border
            newImage.setRGB(firstX + k,firstY+partitionHeight,black);
        }
        for(int l = 0; l<partitionHeight; l++){
            newImage.setRGB(firstX,firstY+l,black); //Colors the left border
            newImage.setRGB(firstX+partitionWidth,firstY+l,black); // Colors the right border
        }
    }
    */
}