package com.google.appengine.demos.ImageCollage;

import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.User;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GettingStartedServlet extends HttpServlet{
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] status = CheckLogInStatus.getStatus().split(" ");
        try {
            req.setAttribute("isApproved", status[0]);
            req.setAttribute("log", status[1]);
            req.getRequestDispatcher("GettingStarted.jsp").forward(req, resp);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}