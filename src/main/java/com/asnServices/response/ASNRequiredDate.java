package com.asnServices.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ASNRequiredDate {
    Date date;
    Time timing;
    Integer requiredQuantity;
    Integer ppeHeadId;

}
