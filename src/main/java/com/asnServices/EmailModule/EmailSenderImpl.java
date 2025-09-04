package com.asnServices.EmailModule;


import com.asnServices.configuration.LoginUser;
import com.asnServices.model.ASNHead;
import com.asnServices.model.SysConfiguration;
import com.asnServices.utils.EmailConstant;
import com.asnServices.repository.ConfigurationRepository;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class EmailSenderImpl implements EmailSender {

    @Autowired
    ConfigurationRepository configurationRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    LoginUser loginUser;

    @Override
    public Boolean sendMail(String subject, String message, String to, Integer orgId) {
        Boolean status;
        List<SysConfiguration> config = configurationRepository.findByIsDeleted(false);

        SysConfiguration host = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_HOST)).findFirst().get();
        SysConfiguration port = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_PORT)).findFirst().get();
        SysConfiguration sslEnable = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_SSL_ENABLE)).findFirst().get();
        SysConfiguration username = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_USERNAME)).findFirst().get();
        SysConfiguration password = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_PASSWORD)).findFirst().get();
        SysConfiguration auth = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_AUTH)).findFirst().get();

        Properties properties = System.getProperties();
        properties.setProperty(host.getConfigName(), host.getConfigValue());
        properties.setProperty(port.getConfigName(), port.getConfigValue());
        properties.setProperty(sslEnable.getConfigName(), sslEnable.getConfigValue());
        properties.setProperty(auth.getConfigName(), auth.getConfigValue());             // SMTP port

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username.getConfigValue(), password.getConfigValue());
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom();
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); // Recipient's email
            msg.setSubject(subject);
            msg.setText(message);
            Transport.send(msg);
            status = true;
            System.out.println("EMAIL SENT SUCCESSFULLY");
        } catch (MessagingException e) {
            e.printStackTrace();
            status = false;
            System.err.println("Error sending email: " + e.getMessage());
        }
        return status;
    }

    @Override
    public Boolean sendMailWithLink(ASNHead asnHead, String to, String accessLink) {
        Boolean status;
        List<SysConfiguration> config = configurationRepository.findByIsDeleted(false);

        SysConfiguration host = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_HOST)).findFirst().get();
        SysConfiguration port = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_PORT)).findFirst().get();
        SysConfiguration sslEnable = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_SSL_ENABLE)).findFirst().get();
        SysConfiguration username = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_USERNAME)).findFirst().get();
        SysConfiguration password = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_PASSWORD)).findFirst().get();
        SysConfiguration auth = config.stream().filter(key -> key.getConfigName().equals(EmailConstant.MAIL_SMTP_AUTH)).findFirst().get();

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host.getConfigValue());
        mailSender.setPort(Integer.parseInt(port.getConfigValue()));
        mailSender.setUsername(username.getConfigValue());
        mailSender.setPassword(password.getConfigValue());
        mailSender.setProtocol("smtp");
        mailSender.setDefaultEncoding("UTF-8");

        // Set additional properties if needed
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.smtp.starttls.enable", sslEnable.getConfigValue());
        javaMailProperties.setProperty("mail.transport.protocol", "smtp");
        mailSender.setJavaMailProperties(javaMailProperties);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo("makay24@pickupizrg.com");
            helper.setSubject("ASN Generated - Access Details");

            // Load Thymeleaf template from file
            String htmlBody = loadThymeleafTemplate("AsnNotice");

            // Create the Thymeleaf context
            Context thymeleafContext = new Context();
            thymeleafContext.setVariable("recipientName", asnHead.getSupplier().getSupplierName());
            thymeleafContext.setVariable("asnNumber", asnHead.getAsnNumber());
            thymeleafContext.setVariable("shipmentDate", asnHead.getDeliveryDate());
            thymeleafContext.setVariable("asnAccessLink", accessLink);

            // Process Thymeleaf template to HTML
            htmlBody = templateEngine.process(htmlBody, thymeleafContext);

            helper.setText(htmlBody, true);
            Transport.send(message);
            status = true;
            System.out.println("EMAIL SENT SUCCESSFULLY");
        } catch (MessagingException e) {
            e.printStackTrace();
            status = false;
            System.err.println("Error sending email: " + e.getMessage());
        }
        return status;
    }

    private String loadThymeleafTemplate(String templateName) {
        try {
            Resource resource = new ClassPathResource("templates/" + templateName + ".html");
            byte[] contentBytes = resource.getInputStream().readAllBytes();
            return new String(contentBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error loading Thymeleaf template", e);
        }
    }
}

