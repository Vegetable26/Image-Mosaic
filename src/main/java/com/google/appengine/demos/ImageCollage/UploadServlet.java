package com.google.appengine.demos.ImageCollage;


import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.images.*;
import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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
        //get the blob
        Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("userPic");
        Image image = ImagesServiceFactory.makeImage(getData(blobKey));

        //get the collage
        int depth = Integer.parseInt(req.getParameter("depth"));
        double threshold = Double.parseDouble(req.getParameter("threshold"));
        Collage pix = new Collage(image, depth, threshold);
        System.out.println("made the collage object");
        Image pixelated = pix.getCollage(10, 10);




        /*
        if (blobKey == null) {
            resp.sendRedirect("/");
        }
        else {
            resp.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
        }
        */
        /*Entity uploadedImage = new Entity("upload", uploadKey);
        uploadedImage.setProperty("userPic", userPic);
        uploadedImage.setProperty("date", date);
        uploadedImage.setProperty("theme", theme);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(uploadedImage);
        */
        //resp.sendRedirect(imageUrl);
    }

    private byte[] getData(BlobKey blobKey) {
        InputStream input;
        byte[] oldImageData = null;
        try {
            input = new BlobstoreInputStream(blobKey);
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = input.read(byteChunk)) > 0) {
                bais.write(byteChunk, 0, n);
            }
            oldImageData = bais.toByteArray();
        } catch (IOException e) {}

        return oldImageData;

    }
}