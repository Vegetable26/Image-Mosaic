package com.google.appengine.demos.ImageCollage;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Random;

public class UploadServlet extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //UserService userService = UserServiceFactory.getUserService();
        //User user = userService.getCurrentUser();

        //String guestbookName = req.getParameter("guestbookName");

        BufferedImage userPic = req.getParameter("userPic");
        String theme = req.getParameter("theme");
        Date date = new Date();
        Key uploadKey = KeyFactory.createKey("uploader", date.toString());

        Entity uploadedImage = new Entity("upload", uploadKey);
        uploadedImage.setProperty("userPic", userPic);
        uploadedImage.setProperty("date", date);
        uploadedImage.setProperty("theme", theme);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(uploadedImage);

        //resp.sendRedirect("/guestbook.jsp?guestbookName=" + guestbookName);
    }
}