package com.fancy_software.accounts_matching.model;

public class WallMessage {

    long id;
    long from_id;
    long to_id;
    String text;
    long copy_owner_id;
    long copy_post_id;
    String copy_text;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setFromId(long from_id) {
        this.from_id = from_id;
    }

    public long getFromId() {
        return from_id;
    }

    public void setToId(long to_id) {
        this.to_id = to_id;
    }

    public long getToId() {
        return to_id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setCopyOwnerId(long copy_owner_id) {
        this.copy_owner_id = copy_owner_id;
    }

    public long getCopyOwnerId() {
        return copy_owner_id;
    }

    public void setCopyPostId(long copy_post_id) {
        this.copy_post_id = copy_post_id;
    }

    public long getCopyPostId() {
        return copy_post_id;
    }

    public void setCopyText(String copy_text) {
        this.copy_text = copy_text;
    }

    public String getCopyText() {
        return copy_text;
    }

    public String toString() {
        return "id=" + id + ", " +
               "from_id=" + from_id + ", " +
               "to_id=" + from_id + ", " +
               "text=" + text + ", " +
               "copy_owner_id=" + copy_owner_id + ", " +
               "copy_post_id=" + copy_post_id + ", " +
               "copy_text=" + copy_text;
    }
}
