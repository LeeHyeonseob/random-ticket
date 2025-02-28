package com.seob.systemdomain.entry.service;

import com.seob.systemdomain.entry.domain.EntryDomain;
import com.seob.systemdomain.event.domain.EventDomain;
import com.seob.systemdomain.ticket.domain.TicketDomain;
import com.seob.systemdomain.user.domain.UserDomain;

public interface EntryService {

    EntryDomain apply(UserDomain userDomain, TicketDomain ticketDomain, EventDomain eventDomain);
}
