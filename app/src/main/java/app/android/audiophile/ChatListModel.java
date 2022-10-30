package app.android.audiophile;

public class ChatListModel {

    String chatListId, date, lastMessage, member;
    public ChatListModel(){

    }

    public ChatListModel(String chatListId, String date, String lastMessage, String member) {
        this.chatListId = chatListId;
        this.date = date;
        this.lastMessage = lastMessage;
        this.member = member;
    }

    public String getChatListId() {
        return chatListId;
    }

    public void setChatListId(String chatListId) {
        this.chatListId = chatListId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}
