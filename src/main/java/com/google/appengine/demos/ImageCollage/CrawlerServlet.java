package com.google.appengine.demos.ImageCollage;

import com.flickr4java.flickr.*;
import com.flickr4java.flickr.photos.*;
import com.flickr4java.flickr.photos.licenses.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.*;
import com.google.gson.Gson;

public class CrawlerServlet extends HttpServlet {


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        String writeMe = "";
        String get = req.getParameter("get");
        if (get == null){
            String[] searchParam = new String[1];
            searchParam[0]= req.getParameter("searchParam");
            int howMany = Integer.parseInt(req.getParameter("howMany"));
            Crawler crawl = new Crawler();
            PhotoList<Photo> photos = crawl.getPhotos(searchParam, howMany);
            crawl.addToDatastore(photos);
            resp.sendRedirect("/");
        }
        else {
            System.out.println("get is "+ get);
            if (get.compareTo("crawlerSearches") == 0){
                writeMe = getCrawlerSearches(datastore);
            }
            else {
                writeMe = getImagesSince(get, datastore);
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(writeMe);
        }
    }

    private String getCrawlerSearches(DatastoreService datastore){
        Query q = new Query("search");
        PreparedQuery pq = datastore.prepare(q);
        List<CrawlerSearch> searches = new ArrayList<CrawlerSearch>();
        for (Entity result : pq.asIterable()){
            searches.add(new CrawlerSearch((String)result.getProperty("time"), (String)result.getProperty("searchParam"), (Integer)result.getProperty("numImg")));
        }
        return new Gson().toJson(searches);

    }

    private String getImagesSince(String time, DatastoreService datastore){
        Filter sinceDate = new FilterPredicate("time", FilterOperator.GREATER_THAN_OR_EQUAL,
                Long.parseLong(time));
        // Use class Query to assemble a query
        Query q = new Query("flickrPic").setFilter(sinceDate);

        // Use PreparedQuery interface to retrieve results
        PreparedQuery pq = datastore.prepare(q);
        List<String> urls = new ArrayList<String>();
        for (Entity result : pq.asIterable()){
            urls.add(result.getKey().getName().split(" ")[0]);
        }
        return new Gson().toJson(urls);
    }

    class CrawlerSearch{
        String time;
        String searchParam;
        int numImg;
        public CrawlerSearch(String time, String searchParam, int numImg){
            this.time = time;
            this.searchParam = searchParam;
            this.numImg = numImg;
        }
    }
}