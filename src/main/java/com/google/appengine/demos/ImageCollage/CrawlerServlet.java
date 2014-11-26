package com.google.appengine.demos.ImageCollage;

import com.flickr4java.flickr.*;
import com.flickr4java.flickr.photos.*;
import com.flickr4java.flickr.photos.licenses.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CrawlerServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] searchParam = new String[1];
        searchParam[0]= req.getParameter("searchParam");
        int howMany = Integer.parseInt(req.getParameter("howMany"));
        Crawler crawl = new Crawler();
        PhotoList<Photo> photos = crawl.getPhotos(searchParam, howMany);
        crawl.addToDatastore(photos);
        resp.sendRedirect("/");
    }
}