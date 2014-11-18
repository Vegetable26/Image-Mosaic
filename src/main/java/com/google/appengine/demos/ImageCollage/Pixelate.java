package com.google.appengine.demos.ImageCollage;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import javax.imageio.*;
import javax.swing.JFrame;

public class Pixelate {
    private static BufferedImage img;
    private static int width; // Width of the base image
    private static int height; // Height of the base image
    private static int minSize; // Stores the size of the minimum base image
    private static int maxDepth;
    public Pixelate(BufferedImage img){
        this.img = img;
        setMaxDepth(4);
        width = img.getWidth();
        height = img.getHeight();
    }


    public static void setMaxDepth(int input){
        maxDepth = input;
    }
    public static BufferedImage pixelateImage(int partitionHeight, int partitionWidth){
        // Pixelates the image
        if(partitionHeight%2  !=0 || partitionWidth %2!=0){
            System.out.println("Please use a multiple of 8 for your width/height");
            // This is necessary for the recursive variance formula
        }
        BufferedImage newImage = new BufferedImage(img.getWidth(),img.getHeight(),BufferedImage.TYPE_INT_ARGB);
        // Creates a blank image of the same size as the base image
        for(int i = 0; i<(int)(width/partitionWidth);i++){ // Splits the image into partition-size columns
            for(int j = 0; j<(int)(height/partitionHeight);j++){	 // Splits the image into partition-size columns
                colorBlock(newImage, i*partitionWidth,j*partitionHeight,partitionHeight,partitionWidth,0);
                // Colors each partition of the graph
            }
        }
        return newImage;
    }
    public static double[] findStats(int firstX, int firstY, int partitionHeight, int partitionWidth){
        // Returns an array containing the average (pixel integer) and sum of the RGB variances.
        double[] average = new double[3]; // Stores the average RGB value of a partition
        double[] variance = new double[3]; // Stores the squared average of the RGB values of a partition
        for(int k = 0; k<partitionWidth; k++){ //Iterates over the partition by columns
            for(int l = 0; l<partitionHeight; l++){
                int[] rgb = RGBArray(img.getRGB((firstX+k),(firstY+l)));
                for(int m=0;m<3;m++){
                    average[m] = average[m] + rgb[m];
                    variance[m] = variance[m] + Math.pow(rgb[m],2);
                }
            }
        }
        double varSum = 0;
        for(int h = 0;h<3;h++){
            average[h] = average[h]/(partitionHeight*partitionWidth); // Divides by the number of pixels in the partition
            variance[h] = (variance[h])/(partitionHeight*partitionWidth);
            varSum = varSum + variance[h] - Math.pow(average[h],2);
        }
        double[] avgAndVar = new double[2];
        avgAndVar[0] = toPixel(average);
        avgAndVar[1] = varSum;

		/* if(baseImage is too small)
			Return an infinite variance
		*/
        return avgAndVar;
    }

    public static void colorBlock(BufferedImage newImage, int firstX, int firstY, int partitionHeight, int partitionWidth, int depth){
        // Depth tracks how many recursions down the current image is
        boolean currentIsBest = true;
        if(depth<maxDepth){
            int halfWidth = partitionWidth/2;
            int halfHeight = partitionHeight/2;
            double[] original = findStats(firstX,firstY,partitionHeight,partitionWidth);
            double[] LU = findStats(firstX,firstY,halfHeight,halfWidth); // Upper left
            double[] RU = findStats(firstX + halfWidth,firstY,halfHeight,halfWidth); //Upper right
            double[] BL = findStats(firstX,firstY + halfHeight,halfHeight,halfWidth); //Lower left
            double[] RL = findStats(firstX + halfWidth,firstY+halfHeight,halfHeight,halfWidth); //Lower right
            double meanVariance = (LU[1] + RU[1] + BL[1] + RL[1])/4;
            System.out.println("Scores" + depth + ": "+original[1]+","+meanVariance);
            if(.98*(original[1])>meanVariance){
                currentIsBest = false;
                depth++;
                colorBlock(newImage,firstX,firstY,halfHeight,halfWidth,depth);
                colorBlock(newImage,firstX + halfWidth,firstY,halfHeight,halfWidth,depth);
                colorBlock(newImage,firstX,firstY + halfHeight,halfHeight,halfWidth,depth);
                colorBlock(newImage,firstX + halfWidth,firstY+halfHeight,halfHeight,halfWidth,depth);
            }
        }
        if(currentIsBest){
            double[] avgAndSum = findStats(firstX,firstY, partitionHeight, partitionWidth);






            int newColor = (int)avgAndSum[0];
            for(int k = 0; k<partitionWidth; k++){ //Iterates over the partition by columns
                for(int l = 0; l<partitionHeight; l++){
                    newImage.setRGB((firstX+k),(firstY+l),newColor); //Colors the partition with a new color
                }
            }
        }
    }

    public static int[] RGBArray(int code){
        // Converts a pixel integer into a tuple[r,b,g]
        int[] triple = new int[3];
        Color c = new Color(code);
        triple[0] = c.getRed();
        triple[1] = c.getGreen();
        triple[2] = c.getBlue();
        return triple;
    }
    public static int toPixel(double[] color){
        // Converts a tuple[r,b,g] into a pixel integer
        int rgb = 255;
        rgb = (rgb << 8) + (int)color[0];
        rgb = (rgb << 8) + (int)color[1];
        rgb = (rgb << 8) + (int)color[2];
        return rgb;
    }
}