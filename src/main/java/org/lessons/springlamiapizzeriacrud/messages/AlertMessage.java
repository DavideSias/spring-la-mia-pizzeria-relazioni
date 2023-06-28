package org.lessons.springlamiapizzeriacrud.messages;

public class AlertMessage {
    private AltertMessageType type;
    private String message;

    public AlertMessage(AltertMessageType type, String message) {
        this.type = type;
        this.message = message;
    }

    public AltertMessageType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
