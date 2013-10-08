package com.fancy_software.accounts_matching.model;

import java.util.LinkedList;
import java.util.List;

public class AccountVector {

    // TODO: Вектор данных о пользователе. Нужно определиться с полями.
    private long id;
    private String first_name;
    private String last_name;
    private List<String> groups;
    private Sex sex;
    private List<Long> friends;
    private BirthDate birthDate;

    public AccountVector() {
        groups = new LinkedList<String>();
        friends = new LinkedList<Long>();
        sex = Sex.NA;
    }

    public List<String> getGroups() {
        return groups;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public List<Long> getFriends() {

        return friends;
    }

    public String getLast_name() {

        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {

        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public BirthDate getBdate() {

        return birthDate;
    }

    public void setBdate(BirthDate birthDate) {
        this.birthDate = birthDate;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addFriend(Long friendId) {
        friends.add(friendId);
    }

    public void addGroup(String group) {
        groups.add(group);
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }

    public void removeGroup(String group) {
        groups.remove(group);
    }

    public boolean hasFriend(Long friendId) {
        for (long id : friends)
            if (id == friendId)
                return true;
        return false;
    }

    public boolean hasGroup(String group) {
        for (String group1 : groups)
            if (group1.equals(group))
                return true;
        return false;
    }

    @Override
    public String toString() {
        return "AccountVector{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", groups=" + groups +
                ", sex=" + sex +
                ", friends=" + friends +
                ", birthDate=" + birthDate +
                '}';
    }

    public static enum Sex {MALE, FEMALE, NA}
}
