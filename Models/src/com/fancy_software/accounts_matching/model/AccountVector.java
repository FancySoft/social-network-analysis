package com.fancy_software.accounts_matching.model;

import com.fancy_software.accounts_matching.model.education.SchoolData;
import com.fancy_software.accounts_matching.model.education.UniversityData;

import java.util.LinkedList;
import java.util.List;

public class AccountVector {

    private String               id;
    private String               first_name;
    private String               last_name;
    private String               city;
    private String               country;
    private List<String>         groups;
    private Sex                  sex;
    private List<String>         friends;
    private BirthDate            birthDate;
    private List<UniversityData> universities;
    private List<SchoolData>     schools;
    private List<WallMessage>    wallMessages;

    public AccountVector() {
        groups = new LinkedList<>();
        friends = new LinkedList<>();
        universities = new LinkedList<>();
        schools = new LinkedList<>();
        wallMessages = new LinkedList<>();
        sex = Sex.NA;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<UniversityData> getUniversities() {
        return universities;
    }

    public List<SchoolData> getSchools() {
        return schools;
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

    public List<String> getFriends() {

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

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addFriend(String friendId) {
        friends.add(friendId);
    }

    public void addGroup(String group) {
        groups.add(group);
    }

    public void addUniversity(UniversityData universityData) {
        universities.add(universityData);
    }

    public void addSchool(SchoolData schoolData) {
        schools.add(schoolData);
    }

    public void addWallMessage(WallMessage message) {
        wallMessages.add(message);
    }

    @SuppressWarnings("unused")
    public void removeWallMessage(WallMessage message) {
        wallMessages.remove(message);
    }

    @SuppressWarnings("unused")
    public void removeFriend(Long friendId) {
        friends.remove(friendId);
    }

    @SuppressWarnings("unused")
    public void removeGroup(String group) {
        groups.remove(group);
    }

    public boolean hasFriend(String friendId) {
        for (String id : friends)
            if (id.equals(friendId))
                return true;
        return false;
    }

    @SuppressWarnings("unused")
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
               ", universities=" + universities +
               ", schools=" + schools +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountVector that = (AccountVector) o;

        if (id != that.id) return false;
        if (birthDate != null ? !birthDate.equals(that.birthDate) : that.birthDate != null) return false;
        if (first_name != null ? !first_name.equals(that.first_name) : that.first_name != null) return false;
        if (friends != null ? !friends.equals(that.friends) : that.friends != null) return false;
        if (groups != null ? !groups.equals(that.groups) : that.groups != null) return false;
        if (last_name != null ? !last_name.equals(that.last_name) : that.last_name != null) return false;
        if (sex != that.sex) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (first_name != null ? first_name.hashCode() : 0);
        result = 31 * result + (last_name != null ? last_name.hashCode() : 0);
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        result = 31 * result + (friends != null ? friends.hashCode() : 0);
        result = 31 * result + (birthDate != null ? birthDate.hashCode() : 0);
        return result;
    }

    public static enum Sex {MALE, FEMALE, NA}
}
