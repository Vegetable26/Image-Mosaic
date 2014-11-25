package com.google.appengine.demos.ImageCollage;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.flickr4java.flickr.*;
import com.flickr4java.flickr.photos.*;
import com.flickr4java.flickr.test.TestInterface;
import java.util.*;
import com.flickr4java.flickr.photos.licenses.*;
import java.io.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class CrawlerServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] searchParam = new String[1];
        searchParam[0]= req.getParameter("searchParam");
        int howMany = Integer.parseInt(req.getParameter("howMany"));
        Crawler crawl = new Crawler();
        PhotoList<Photo> photos = crawl.getPhotos(searchParam, howMany);
        crawl.addToDatastore(photos);
        resp.sendRedirect("/ImageCollage.jsp");
    }
}