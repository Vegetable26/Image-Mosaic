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
import java.util.Map;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.OutputStream;

public class UploadServlet extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        //UserService userService = UserServiceFactory.getUserService();
        //User user = userService.getCurrentUser();

        //String guestbookName = req.getParameter("guestbookName");

        /*BufferedImage userPic = req.getParameter("userPic");
        String theme = req.getParameter("theme");
        Date date = new Date();
        Key uploadKey = KeyFactory.createKey("uploader", date.toString());
        */

        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("userPic");
        BlobInfo blobInfo = blobstoreService.getBlobInfos(req).get("userPic").get(0);

        long fileSize = blobInfo.getSize();

        byte[] imageBytes = blobstoreService.fetchData(blobKey, (long)0, fileSize);

        InputStream imageStream = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(imageStream);

        Pixelate pix = new Pixelate(image);
        BufferedImage pixelated = pix.pixelateImage(30, 30);

        resp.setContentType("image/jpeg");
        OutputStream out = resp.getOutputStream();
        ImageIO.write(pixelated, "jpg", out);
        out.close();

        /*
        if (blobKey == null) {
            resp.sendRedirect("/");
        } else {
            resp.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
        }*/

        /*Entity uploadedImage = new Entity("upload", uploadKey);
        uploadedImage.setProperty("userPic", userPic);
        uploadedImage.setProperty("date", date);
        uploadedImage.setProperty("theme", theme);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(uploadedImage);

        //resp.sendRedirect("/guestbook.jsp?guestbookName=" + guestbookName);*/
    }
}