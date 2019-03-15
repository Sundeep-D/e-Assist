package com.example.sundeep.egen_2;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class GmailSender extends javax.mail.Authenticator {

    private String user;
    private String password;
    private Session session;
   // private Multipart _multipart;


    private Multipart _multipart = new MimeMultipart();

    static {

        Security.addProvider(new com.example.sundeep.egen_2.JSSEProvider());

    }

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GmailSender(String user, String password) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) throws Exception {
        try {
            MimeMessage message = new MimeMessage(session);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);

            //try
            String htmlText = "<b><font size =\"5\" face=\"Century Gothic\" style=\"color:#534280\">Thanks for registering with EGEN</font></b>";
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setContent(htmlText,"text/html");
            _multipart.addBodyPart(messageBodyPart1);

            //try

//            //try
//            String Identification_Details = "<b><font size =\"4\" face=\"Exo2\" style=\"color:#615151\"><br /><br />Identification Details</font></b>";
//            BodyPart messageBodyPart2 = new MimeBodyPart();
//            messageBodyPart2.setContent(Identification_Details,"text/html");
//            _multipart.addBodyPart(messageBodyPart2);
//
//            //try






            //new

            BodyPart messageBodyPart = new MimeBodyPart();



            messageBodyPart.setText(body);   //old

            _multipart.addBodyPart(messageBodyPart);  //old

            message.setContent(_multipart);   //old

            //new


                        //try
            String Identification_Details = "<a size =\"4\" face=\"Century Gothic\" style=\"color:#000\" href=\"www.sundeepdayalan.com\"><br /><br />Developer info</a>";
            BodyPart messageBodyPart2 = new MimeBodyPart();
            messageBodyPart2.setContent(Identification_Details,"text/html");
            _multipart.addBodyPart(messageBodyPart2);

            //try




            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }


    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName("Your Life saver QR");

        _multipart.addBodyPart(messageBodyPart);
    }


}