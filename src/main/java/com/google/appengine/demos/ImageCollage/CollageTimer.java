package com.google.appengine.demos.ImageCollage;

/*

Update 12/24
Created class

CollageTimer: Halts the execution of the Collage threads so that the entire MasterCollage method runs in less
than a minute, because we are using Google Servers

 */

public class CollageTimer {
    private double limit; // Length of time MasterCollage is allowed to take in seconds
    public CollageTimer(double limit){
        this.limit = limit*Math.pow(10,9) + System.nanoTime(); // Convert the seconds into nano-seconds
    }

    /*

    Returns true if the time limit has been exceeded

    Inputs:weight: Extra or less time a given Collage.colorBlock() function is alotted

     */

    public boolean timeElapsed(double weight){

        if(System.nanoTime() >limit-(weight*Math.pow(10,9))){
            return true; // Time limit has been exceeded
        }
        return false;
    }

}