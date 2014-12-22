package com.google.appengine.demos.ImageCollage;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

import com.google.appengine.api.images.*;

public class EmailCollage {

<<<<<<< HEAD
    public void sendMailTwo(String collageName, Image collage, String hisEmail){
=======
    public static void sendMailTwo(String collageName, Image collage, String hisEmail){
>>>>>>> f0cfa30363e6c484c6bd25d7f8e24d952f9c1929
        try {
            System.out.println(collage.getFormat());
            String msgBody = "FUCKKKK";
            byte[] attachmentData = collage.getImageData();
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress("Jhwang261@gmail.com"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(hisEmail));
            msg.setSubject("Testing testing");
            msg.setText(msgBody);

            Multipart mp = new MimeMultipart();
            MimeBodyPart attachment = new MimeBodyPart();
            attachment.setFileName(collageName + ".png");
            attachment.setContent(attachmentData, "image/png");
            mp.addBodyPart(attachment);
            //msg.setContent(mp);

            Transport.send(msg);


        }catch(Exception e){
            e.printStackTrace();
        }
    }
<<<<<<< HEAD
    public void sendMail(String collageName, Image collage,String hisEmail){
=======
    public static void sendMail(String collageName, Image collage,String hisEmail){
>>>>>>> f0cfa30363e6c484c6bd25d7f8e24d952f9c1929
        try {

            String msgBody = "Trial run";
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("Jhwang261@gmail.com"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(hisEmail));
            msg.setSubject("Testing testing");
            msg.setText(msgBody);

            String headerImageCid = "header";
            DataSource ds = new ByteArrayDataSource(collage.getImageData(), "image/png");
            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setDataHandler(new DataHandler(ds));
            imagePart.setFileName(headerImageCid + ".png");
            imagePart.setHeader("Content-Type", "image/png");
            imagePart.addHeader("Content-ID", "<" + headerImageCid + ">");

            final Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(imagePart);
            msg.setContent(multipart);
            msg.saveChanges();

            Transport.send(msg);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}