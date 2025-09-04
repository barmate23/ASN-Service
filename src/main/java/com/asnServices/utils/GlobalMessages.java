package com.asnServices.utils;



import com.asnServices.model.ResponseMessage;
import com.asnServices.repository.ResponseMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Configuration
@Transactional
public class GlobalMessages {

    @Autowired
    private ResponseMessageRepository responseMessageRepository;

    public static List<ResponseMessage> RESPONSEMESSAGES;

    @PostConstruct
    public void GetGlobalMessage() {
        RESPONSEMESSAGES = responseMessageRepository.findAll();
    }

    public static ResponseMessage getResponseMessages(String responseKey) {
        Optional optional = RESPONSEMESSAGES.stream().filter(am -> am.getKey().equals(responseKey)).findFirst();
        if (!optional.isEmpty()) {
            return (ResponseMessage) optional.get();
        }
        return null;
    }

}
