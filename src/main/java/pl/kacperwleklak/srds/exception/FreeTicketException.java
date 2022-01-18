package pl.kacperwleklak.srds.exception;

import pl.kacperwleklak.srds.model.dao.TicketKey;

public class FreeTicketException extends Exception {

    public FreeTicketException(TicketKey ticketKey) {
        super("Ticket already taken: " + ticketKey.toString());
    }

    public FreeTicketException(String message) {
        super(message);
    }
}
