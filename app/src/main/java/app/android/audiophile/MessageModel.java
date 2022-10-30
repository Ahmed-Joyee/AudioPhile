package app.android.audiophile;

public class MessageModel {

    String sender, receiver, message, data, type, receiverUsername;

    public MessageModel(){

    }

    public MessageModel(String sender, String receiver, String message, String data, String type, String receiverUsername) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.data = data;
        this.type = type;
        this.receiverUsername = receiverUsername;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }
}
