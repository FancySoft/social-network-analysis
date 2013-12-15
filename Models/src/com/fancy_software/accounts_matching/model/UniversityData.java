package com.fancy_software.accounts_matching.model;

/**
 * Created by Greg on 16.12.13.
 */

import java.util.Map;

public class UniversityData {
    public enum EducationForm{
        NONE,           //
        DAYTIME,        // Дневное
        EVENING,        // Вечернее
        CORRESPONDENCE  // Заочное
    }

    public enum EducationStatus{
        NONE,           //
        ENROLLE,        // Абитуриент
        STUDENT,        // Студент
        GRADUATE,       // Выпускник
        POSTGRADUATE,   // Аспирант
        PHD,            // Кандидат наук
        DOCTOR          // Доктор наук
    }

    private String name;
    private String faculty_name;
    private String chair_name;
    private int graduation;
    private EducationForm education_form;
    private EducationStatus education_status;

    public UniversityData(String _name, String _faculty_name,
                          String _chair_name, int _graduation,
                          EducationForm _education_form, EducationStatus _education_status){
        this.education_status = _education_status;
        this.education_form = _education_form;
        this.graduation = _graduation;
        this.chair_name = _chair_name;
        this.faculty_name = _faculty_name;
        this.name = _name;
    }

    public static UniversityData GetUniversityDataByMap(Map<String,Object> _data){
        String name = "";
        String faculty_name = "";
        String chair_name = "";
        int graduation = 2025;
        EducationForm education_form = EducationForm.NONE;
        EducationStatus education_status = EducationStatus.NONE;

        if(_data.containsKey("name"))
            name = (String) _data.get("name");

        if(_data.containsKey("faculty_name"))
            faculty_name = (String) _data.get("faculty_name");

        if(_data.containsKey("chair_name"))
            chair_name = (String) _data.get("chair_name");

        if(_data.containsKey("graduation"))
            graduation = (int) _data.get("graduation");

        if(_data.containsKey("education_form"))
            education_form = GetEducationFormByRusString((String) _data.get("education_form"));

        if(_data.containsKey("education_status"))
            education_status = GetEducationStatusByRusString((String) _data.get("education_status"));

        return new UniversityData(name, faculty_name, chair_name, graduation, education_form, education_status);
    }

    public static EducationStatus GetEducationStatusByRusString(String status){
        if(status.toLowerCase().contains("абитуриент"))
            return EducationStatus.ENROLLE;
        if(status.toLowerCase().contains("студент"))
            return EducationStatus.STUDENT;
        if(status.toLowerCase().contains("выпускник"))
            return EducationStatus.GRADUATE;
        if(status.toLowerCase().contains("аспирант"))
            return EducationStatus.POSTGRADUATE;
        if(status.toLowerCase().contains("кандидат"))
            return EducationStatus.PHD;
        if(status.toLowerCase().contains("доктор"))
            return EducationStatus.DOCTOR;
        return EducationStatus.NONE;
    }

    public static EducationForm GetEducationFormByRusString(String form){
        if(form.toLowerCase().contains("дневн"))
            return EducationForm.DAYTIME;
        if(form.toLowerCase().contains("вечерн"))
            return EducationForm.EVENING;
        if(form.toLowerCase().contains("заочн"))
            return EducationForm.CORRESPONDENCE;
        return EducationForm.NONE;
    }

    @Override
    public String toString(){
        return "UniversityData{" +
                "name=" + name + ", " +
                "faculty_name=" + faculty_name + ", " +
                "chair_name=" + chair_name + ", " +
                "graduation=" + graduation + ", " +
                "education_form=" + education_form + ", " +
                "education_status=" + education_status +
                "}";
    }
}
