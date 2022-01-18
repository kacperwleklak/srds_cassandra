package pl.kacperwleklak.srds.exception;

import pl.kacperwleklak.srds.model.dao.TicketKey;

public class TakenTicketException extends Exception {

    public TakenTicketException(TicketKey ticketKey) {
        super("Ticket already taken: " + ticketKey.toString());
    }

    public TakenTicketException(String message) {
        super(message);
    }
}
