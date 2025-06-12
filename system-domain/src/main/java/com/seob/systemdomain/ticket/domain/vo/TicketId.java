package com.seob.systemdomain.ticket.domain.vo;

import com.seob.systemdomain.user.domain.vo.UserId;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Value
@EqualsAndHashCode
public class TicketId implements Serializable {

    private String value;

    public static TicketId of(String value){
        return new TicketId(value);
    }

    public static TicketId create(UserId userId){
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = dateFormat.format(new Date());
        sb.append(date);
        sb.append("-");
        sb.append(userId.getValue());
        return new TicketId(sb.toString());
    }





}
