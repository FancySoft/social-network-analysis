package com.fancy_software.accounts_matching.model.education;

/**
 * Created by Greg on 16.12.13.
 */

import java.util.Map;

public class SchoolData {
    private String name;
    private String country;
    private String city;
    private int    yearFrom;
    private int    yearTo;
    private int    graduate;
    private String classroom;

    public SchoolData() {

    }

    @SuppressWarnings("deprecated")
    public SchoolData(String _name,
                      int _year_from, int _year_to,
                      int _graduate, String _classroom) {
        this.name = _name;
        this.yearFrom = _year_from;
        this.yearTo = _year_to;
        this.graduate = _graduate;
        this.classroom = _classroom;
    }

    @SuppressWarnings("deprecated")
    public static SchoolData GetSchoolDataByMap(Map<String, Object> _data) {
        String name = "";
        int year_from = 2025;
        int year_to = 2025;
        int graduate = 2025;
        String classroom = "";

        if (_data.containsKey("name"))
            name = (String) _data.get("name");
        if (_data.containsKey("yearFrom"))
            year_from = (int) _data.get("yearFrom");
        if (_data.containsKey("yearTo"))
            year_to = (int) _data.get("yearTo");
        if (_data.containsKey("year_graduated"))
            graduate = (int) _data.get("year_graduated");
        if (_data.containsKey("class"))
            classroom = (String) _data.get("class");

        return new SchoolData(name, year_from, year_to, graduate, classroom);
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public int getYearTo() {
        return yearTo;
    }

    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public int getGraduate() {
        return graduate;
    }

    public void setGraduate(int graduate) {
        this.graduate = graduate;
    }

    @Override
    public String toString() {
        return "SchoolData{" +
               "name=" + name +
               ", yearFrom=" + yearFrom +
               ", yearTo=" + yearTo +
               ", graduate=" + graduate +
               ", classroom=" + classroom +
               '}';
    }
}
