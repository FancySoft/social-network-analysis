package com.fancy_software.accounts_matching.core.entities;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.LinkedList;
import java.util.List;

public class AccountVector implements IsSerializable {

    // TODO: Вектор данных о пользователе. Нужно определиться с полями.
    private long id;
    private String firstName;
    private String lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
                ", first_name='" + firstName + '\'' +
                ", last_name='" + lastName + '\'' +
                /*", groups=" + groups +*/
                ", sex=" + sex +
                /*", friends=" + friends +*/
                ", birthDate=" + birthDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountVector that = (AccountVector) o;

        if (id != that.id) return false;
        if (birthDate != null ? !birthDate.equals(that.birthDate) : that.birthDate != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (friends != null ? !friends.equals(that.friends) : that.friends != null) return false;
        if (groups != null ? !groups.equals(that.groups) : that.groups != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (sex != that.sex) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (friends != null ? friends.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        return result;
    }

    public static enum Sex {MALE, FEMALE, NA}
}
