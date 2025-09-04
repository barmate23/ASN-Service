package com.asnServices.EmailModule;

import com.asnServices.model.ASNHead;
import org.springframework.stereotype.Service;

@Service
public interface EmailSender {
    Boolean sendMail(String subject, String message, String to, Integer orgId);

    Boolean sendMailWithLink(ASNHead asnHead, String to, String accessLink);
}
