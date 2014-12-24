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
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CrawlerServlet extends HttpServlet {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String writeMe = "";
        String get = req.getParameter("get");
        if (get == null){
            String[] deleteThem = req.getParameterValues("deleteMe");
            if (deleteThem == null) {
                String[] searchParam = new String[1];
                searchParam[0] = req.getParameter("searchParam");
                int howMany = Integer.parseInt(req.getParameter("howMany"));
                crawl(searchParam, howMany);
            }
            else{
                deleteImages(deleteThem);
            }
        }
        else {
            if (get.compareTo("crawlerSearches") == 0){
                writeMe = getCrawlerSearches(datastore);
            }
            else {
                writeMe = getImages(get, datastore);
            }
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(writeMe);
        }
    }

    private void crawl(String[] searchParam, int howMany){
        Crawler crawl = new Crawler();
        crawl.getPhotos(searchParam, howMany);
    }

    private String getCrawlerSearches(DatastoreService datastore){
        Query q = new Query("search");
        PreparedQuery pq = datastore.prepare(q);
        List<CrawlerSearch> searches = new ArrayList<CrawlerSearch>();
        for (Entity result : pq.asIterable()){
            searches.add(new CrawlerSearch(new Date((long)result.getProperty("time")).toString(), (String)result.getProperty("searchParam"), (int)(long)result.getProperty("numImg")));
        }
        return new Gson().toJson(searches);

    }

    private String getImages(String time, DatastoreService datastore){
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            Date date = df.parse(time);
            System.out.println("we want to get images that were searched for at time "+ date.getTime());
            Filter sinceDate = new FilterPredicate("time", FilterOperator.EQUAL,
                    date.getTime());
            // Use class Query to assemble a query
            Query q = new Query("flickrPic").setFilter(sinceDate);

            // Use PreparedQuery interface to retrieve results
            PreparedQuery pq = datastore.prepare(q);
            List<String> urls = new ArrayList<String>();
            for (Entity result : pq.asIterable()){
                urls.add(result.getKey().getName());
            }
            return new Gson().toJson(urls);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    private void deleteImages(String[] deleteUs){
        for (String deleteMe : deleteUs){
            Key key = KeyFactory.createKey("flickrPic", deleteMe);
            datastore.delete(key);
        }
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