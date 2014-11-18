package com.google.appengine.demos.ImageCollage;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;



import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.OutputStream;

public class ServeServlet extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
        //blobstoreService.serve(blobKey, resp);
        //ByteRange range = blobstoreService.getByteRange(req);
        BlobInfo blobInfo = blobstoreService.getBlobInfos(req).get("userPic").get(0);
        long fileSize = blobInfo.getSize();

        byte[] imageBytes = blobstoreService.fetchData(blobKey, (long)0, fileSize);

        InputStream imageStream = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(imageStream);

        resp.setContentType("image/jpeg");
        OutputStream out = resp.getOutputStream();
        ImageIO.write(image, "jpg", out);
        out.close();

        //resp.sendRedirect("ShowBufferedImage.jsp");

    }
}
