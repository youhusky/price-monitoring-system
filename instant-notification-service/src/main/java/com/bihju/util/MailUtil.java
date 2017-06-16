package com.bihju.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@Log4j
public class MailUtil {
    @Autowired
    private AsyncTaskExecutor asyncTaskExecutor;
    private JavaMailSender javaMailSender;

    @Autowired
    public MailUtil(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(String[] receivers, String replyTo, String from, String subject, String body) {
        SendMail sendMail = new SendMail(receivers, replyTo, from, subject, body);
        asyncTaskExecutor.submit(sendMail);
    }

    @Data
    @AllArgsConstructor
    private class SendMail implements Runnable {
        private String[] receivers;
        private String replyTo;
        private String from;
        private String subject;
        private String body;

        @Override
        public void run() {
            MimeMessage message = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(receivers);
                helper.setReplyTo(replyTo);
                helper.setFrom(from);
                helper.setSubject(subject);
                helper.setText(body, true);
            } catch (MessagingException e) {
                e.printStackTrace();
                log.warn("Failed to send mail to " + StringUtils.join(receivers, ", "));
            } finally {}

            javaMailSender.send(message);
            log.info("Sent mail to " + StringUtils.join(receivers, ", "));
        }
    }
}
