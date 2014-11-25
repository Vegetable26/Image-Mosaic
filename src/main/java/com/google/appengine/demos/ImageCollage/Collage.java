package com.google.appengine.demos.ImageCollage;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.*;
import com.flickr4java.flickr.photos.*;

public class Collage {

    private  BufferedImage img;
    private  ProcessedImage processedImage;
    private  int width; // Width of the base image
    private  int height; // Height of the base image
    private  int minSize; // Stores the size of the minimum base image
    private  int maxDepth;
    private  double varianceThreshhold;

    private  Crawler crawler = new Crawler();

    public Collage(BufferedImage inputImg,int depth,double inputThresh){
        crawler.buildIndex();
        img = inputImg;
        processedImage = new ProcessedImage(img);
        setMaxDepth(depth);
        height = inputImg.getHeight();
        width = inputImg.getWidth();
        varianceThreshhold =  inputThresh;
    }
    public Collage(String fileName,int depth,double inputThresh){
        readImage(fileName);
        processedImage = new ProcessedImage(img);
        setMaxDepth(depth);
        varianceThreshhold =  inputThresh;
    }

    public  void readImage(String filename){
        // Reads the image from a file and saves its dimensions
        img = null;
        try {
            img = ImageIO.read(new File(filename));
            width = img.getWidth();
            height = img.getHeight();

        } catch (IOException e) {}
    }

    public  void setMaxDepth(int input){

        maxDepth = input;
    }

    public  BufferedImage getCollage(int partitionHeight, int partitionWidth){
        // Pixelates the image
        if(partitionHeight%2  !=0 || partitionWidth %2!=0){
            System.out.println("Please use a multiple of 8 for your width/height");
            // This is necessary for the recursive variance formula
        }
        BufferedImage newImage = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_ARGB);
        // Creates a blank image of the same size as the base image
        for(int i = 0; i<Math.ceil((double) width / partitionWidth);i++){ // Splits the image into partition-size columns
            for(int j = 0; j<Math.ceil((double) height / partitionHeight);j++){	 // Splits the image into partition-size columns
                colorBlock(newImage, i*partitionWidth,j*partitionHeight,partitionHeight,partitionWidth,0);
                // Colors each partition of the graph
            }
        }
        return newImage;
    }
    public  double[] findStats(int firstX, int firstY, int partitionHeight, int partitionWidth){
        // Returns an array containing the average (pixel integer) and sum of the RGB variances.
        double[] avgAndVar = new double[2];
        double[] average = new double[3]; // Stores the average RGB value of a partition
        double[] variance = new double[3]; // Stores the squared average of the RGB values of a partition
        for (int k = 0; k < Math.min(partitionWidth,width-firstX-1); k++) { //Iterates over the partition by columns
            for (int l = 0; l < Math.min(partitionHeight,height-firstY-1); l++) {
                int[] rgb = RGBArray(img.getRGB((firstX + k), (firstY + l)));
                for (int m = 0; m < 3; m++) {
                    average[m] = average[m] + rgb[m];
                    variance[m] = variance[m] + Math.pow(rgb[m], 2);
                }
            }
        }
        double varSum = 0;
        for(int h = 0;h<3;h++){
            average[h] = average[h]/(partitionHeight*partitionWidth); // Divides by the number of pixels in the partition
            variance[h] = (variance[h])/(partitionHeight*partitionWidth);
            varSum = varSum + variance[h] - Math.pow(average[h],2);
        }

        avgAndVar[0] = toPixel(average);
        avgAndVar[1] = varSum;

		/* if(baseImage is too small)
			Return an infinite variance
		*/
        return avgAndVar;
    }

    public  void colorBlock(BufferedImage newImage, int firstX, int firstY, int partitionHeight, int partitionWidth, int depth){
        // Depth tracks how many recursions down the current image is

        if(firstX<width && firstY<height) {
            boolean isBest = true;
            if (depth < maxDepth) {
                int halfWidth = partitionWidth / 2;
                int halfHeight = partitionHeight / 2;
                double[] original = findStats(firstX, firstY, partitionHeight, partitionWidth);
                if ((original[1]) > varianceThreshhold) {
                    isBest = false;
                    depth++;
                    colorBlock(newImage, firstX, firstY, partitionHeight - halfHeight, halfWidth, depth); // Upper Left
                    colorBlock(newImage, firstX + halfWidth, firstY, partitionHeight - halfHeight, partitionWidth - halfWidth, depth); // Upper Right
                    colorBlock(newImage, firstX, firstY + halfHeight, halfHeight, halfWidth, depth); // Lower Left
                    colorBlock(newImage, firstX + halfWidth, firstY + halfHeight, halfHeight, partitionWidth - halfWidth, depth); // Lower Right
                }
            }
            if (isBest) {
                colorIn(newImage,firstX,firstY,Math.min(partitionHeight,height-firstY-1),Math.min(partitionWidth,width-firstX-1));
                drawBorder(newImage, firstX, firstY, Math.min(partitionHeight,height-firstY-1), Math.min(partitionWidth,width-firstX-1));
            }
        }
    }
    private void colorIn(BufferedImage newImage, int firstX, int firstY, int partitionHeight, int partitionWidth){
        double[] histogram = processedImage.getRGBHistogram(true,firstX,firstY,partitionHeight,partitionWidth);
        // double[] avgAndSum = findStats(firstX, firstY, partitionHeight, partitionWidth);
        //int newColor = (int) avgAndSum[0];
        String[] urlAndUser = crawler.query(histogram).split("\\s+");
        ProcessedImage queried = new ProcessedImage(urlAndUser[0],urlAndUser[1]);
        BufferedImage buildImage = queried.getScaled(partitionWidth,partitionHeight);
        for (int k = 0; k < partitionWidth ; k++) { //Iterates over the partition by columns
            for (int l = 0; l < partitionHeight; l++) {
                newImage.setRGB((firstX + k), (firstY + l), buildImage.getRGB(k,l)); //Colors the partition with a new color
            }
        }
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


}