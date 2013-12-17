package com.fancy_software.accounts_matching.model;

/**
 * Created by Greg on 16.12.13.
 */
import java.util.Map;

public class SchoolData {
    private String name;
    private int year_from;
    private int year_to;
    private int graduate;
    private String classroom;

    public SchoolData(String _name,
                      int _year_from, int _year_to,
                      int _graduate, String _classroom){
        this.name = _name;
        this.year_from = _year_from;
        this.year_to = _year_to;
        this.graduate = _graduate;
        this.classroom = _classroom;
    }

    public static SchoolData GetSchoolDataByMap(Map<String,Object> _data){
        String name = "";
        int year_from = 2025;
        int year_to = 2025;
        int graduate = 2025;
        String classroom = "";

        if(_data.containsKey("name"))
            name = (String) _data.get("name");
        if(_data.containsKey("year_from"))
            year_from = (int) _data.get("year_from");
        if(_data.containsKey("year_to"))
            year_to = (int) _data.get("year_to");
        if(_data.containsKey("year_graduated"))
            graduate = (int) _data.get("year_graduated");
        if(_data.containsKey("class"))
            classroom = (String) _data.get("class");

        return new SchoolData(name, year_from, year_to, graduate, classroom);
    }

    public String getName(){return name;}
    public String getClassroom(){return classroom;}
    public int getYear_from(){return year_from;}
    public int getYear_to(){return year_to;}
    public int getGraduate(){return graduate;}

    @Override
    public String toString() {
        return "SchoolData{" +
                "name=" + name +
                ", year_from=" + year_from +
                ", year_to=" + year_to +
                ", graduate=" + graduate +
                ", classroom=" + classroom +
                '}';
    }
}
