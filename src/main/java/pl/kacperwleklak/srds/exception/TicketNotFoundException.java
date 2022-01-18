package pl.kacperwleklak.srds.exception;

import pl.kacperwleklak.srds.model.dao.TicketKey;

public class TicketNotFoundException extends Throwable {

    public TicketNotFoundException(TicketKey ticketKey) {
        super("Ticket not found: " + ticketKey.toString());
    }

    public TicketNotFoundException(String message) {
        super(message);
    }

}
