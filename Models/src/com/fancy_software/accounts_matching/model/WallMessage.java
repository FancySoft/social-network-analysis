package com.fancy_software.accounts_matching.model;

public class WallMessage {

    long   id;
    long   fromId;
    long   toId;
    String text;
    long   copyOwnerId;
    long   copyPostId;
    String copyText;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setFromId(long from_id) {
        this.fromId = from_id;
    }

    public long getFromId() {
        return fromId;
    }

    public void setToId(long to_id) {
        this.toId = to_id;
    }

    public long getToId() {
        return toId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setCopyOwnerId(long copy_owner_id) {
        this.copyOwnerId = copy_owner_id;
    }

    public long getCopyOwnerId() {
        return copyOwnerId;
    }

    public void setCopyPostId(long copy_post_id) {
        this.copyPostId = copy_post_id;
    }

    public long getCopyPostId() {
        return copyPostId;
    }

    public void setCopyText(String copy_text) {
        this.copyText = copy_text;
    }

    public String getCopyText() {
        return copyText;
    }

    public String toString() {
        return "id=" + id + ", " +
               "fromId=" + fromId + ", " +
               "toId=" + fromId + ", " +
               "text=" + text + ", " +
               "copyOwnerId=" + copyOwnerId + ", " +
               "copyPostId=" + copyPostId + ", " +
               "copyText=" + copyText;
    }
}
