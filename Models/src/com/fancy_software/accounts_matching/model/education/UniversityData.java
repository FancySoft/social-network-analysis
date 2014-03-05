package com.fancy_software.accounts_matching.model.education;

/**
 * Created by Greg on 16.12.13.
 */

import java.util.Map;

public class UniversityData {
    public enum EducationForm {
        NONE,           //
        DAYTIME,        // Дневное
        EVENING,        // Вечернее
        CORRESPONDENCE  // Заочное
    }

    public enum EducationStatus {
        NONE,           //
        ENROLLE,        // Абитуриент
        STUDENT,        // Студент
        GRADUATE,       // Выпускник
        POSTGRADUATE,   // Аспирант
        PHD,            // Кандидат наук
        DOCTOR          // Доктор наук
    }

    private String name;

    private String country;

    private String city;
    private String facultyName;

    private String chairName;
    private int    graduation;

    private EducationForm educationForm;

    private EducationStatus educationStatus;

    public UniversityData() {

    }

    @SuppressWarnings("deprecated")
    public UniversityData(String _name, String _faculty_name,
                          String _chair_name, int _graduation,
                          EducationForm _education_form, EducationStatus _education_status) {
        this.educationStatus = _education_status;
        this.educationForm = _education_form;
        this.graduation = _graduation;
        this.chairName = _chair_name;
        this.facultyName = _faculty_name;
        this.name = _name;
    }

    @SuppressWarnings("deprecated")
    public static UniversityData GetUniversityDataByMap(Map<String, Object> _data) {
        String name = "";
        String faculty_name = "";
        String chair_name = "";
        int graduation = 2025;
        EducationForm education_form = EducationForm.NONE;
        EducationStatus education_status = EducationStatus.NONE;

        if (_data.containsKey("name"))
            name = (String) _data.get("name");

        if (_data.containsKey("facultyName"))
            faculty_name = (String) _data.get("facultyName");

        if (_data.containsKey("chairName"))
            chair_name = (String) _data.get("chairName");

        if (_data.containsKey("graduation"))
            graduation = (int) _data.get("graduation");

        if (_data.containsKey("educationForm"))
            education_form = GetEducationFormByRusString((String) _data.get("educationForm"));

        if (_data.containsKey("educationStatus"))
            education_status = GetEducationStatusByRusString((String) _data.get("educationStatus"));

        return new UniversityData(name, faculty_name, chair_name, graduation, education_form, education_status);
    }

    public static EducationStatus GetEducationStatusByRusString(String status) {
        if (status.toLowerCase().contains("абитуриент"))
            return EducationStatus.ENROLLE;
        if (status.toLowerCase().contains("студент"))
            return EducationStatus.STUDENT;
        if (status.toLowerCase().contains("выпускник"))
            return EducationStatus.GRADUATE;
        if (status.toLowerCase().contains("аспирант"))
            return EducationStatus.POSTGRADUATE;
        if (status.toLowerCase().contains("кандидат"))
            return EducationStatus.PHD;
        if (status.toLowerCase().contains("доктор"))
            return EducationStatus.DOCTOR;
        return EducationStatus.NONE;
    }

    public static EducationForm GetEducationFormByRusString(String form) {
        if (form.toLowerCase().contains("дневн"))
            return EducationForm.DAYTIME;
        if (form.toLowerCase().contains("вечерн"))
            return EducationForm.EVENING;
        if (form.toLowerCase().contains("заочн"))
            return EducationForm.CORRESPONDENCE;
        return EducationForm.NONE;
    }

    public String getName() {
        return name;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public String getChairName() {
        return chairName;
    }

    public int getGraduation() {
        return graduation;
    }

    public EducationStatus getEducationStatus() {
        return educationStatus;
    }

    public EducationForm getEducationForm() {
        return educationForm;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public void setChairName(String chairName) {
        this.chairName = chairName;
    }

    public void setGraduation(int graduation) {
        this.graduation = graduation;
    }

    public void setEducationForm(EducationForm educationForm) {
        this.educationForm = educationForm;
    }

    public void setEducationStatus(EducationStatus educationStatus) {
        this.educationStatus = educationStatus;
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

    @Override
    public String toString() {
        return "UniversityData{" +
               "name=" + name + ", " +
               "facultyName=" + facultyName + ", " +
               "chairName=" + chairName + ", " +
               "graduation=" + graduation + ", " +
               "educationForm=" + educationForm + ", " +
               "educationStatus=" + educationStatus +
               "}";
    }
}
