package com.bihju.util;

import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.util.Properties;

public class MailUtilTest {
    private String MAIL_USER = "my yahoo mail address";
    private String MAIL_PASS = "my yahoo mail password";
    private String MAIL_TEST_RECIPIENT = "my test recipient";

    @Test
    public void testSendMailSuccess() throws IOException {
        JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost("smtp.mail.yahoo.com");
        javaMailSenderImpl.setPort(587);
        javaMailSenderImpl.setUsername(MAIL_USER);
        javaMailSenderImpl.setPassword(MAIL_PASS);
        javaMailSenderImpl.setProtocol("smtp");

        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");

        javaMailSenderImpl.setJavaMailProperties(props);
        MailUtil mailUtil = new MailUtil(javaMailSenderImpl);
        mailUtil.send(MAIL_TEST_RECIPIENT
            , MAIL_USER
            , MAIL_USER
            , "Test Title"
            , "<html><body>Test Message Body</body></html>");
    }
}
