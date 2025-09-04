package com.asnServices.response;

import com.asnServices.model.ASNHead;
import com.asnServices.model.ASNLine;
import com.asnServices.model.Organization;
import com.asnServices.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ASNDetResponse {
    private ASNHead asnHead;
    private List<ASNLine> asnLineList;
    private Organization organization;
    private User buyer;
}
