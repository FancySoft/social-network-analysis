package com.fancy_software.crawling.parsers.vk;

/**
 * Created by Yaro
 * Date: 01.11.13
 * Time: 16:28
 */
public final class FieldNames {
    public static final String ID         = "uid";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME  = "last_name";
    public static final String SEX        = "sex";
    public static final String BIRTH_DATE = "bdate";
    public static final String CITY       = "city";
    public static final String COUNTRY    = "country";
    public static final String PHOTO      = "photo_max";
    public static final String ACTIVITY   = "activity";

    //region University data
    public static final String UNIVERSITY         = "university";
    public static final String UNIVERSITY_NAME    = "university_name";
    public static final String UNIVERSITY_COUNTRY = COUNTRY;
    public static final String UNIVERSITY_CITY    = CITY;
    public static final String FACULTY            = "faculty";
    public static final String FACULTY_NAME       = "faculty_name";
    public static final String GRADUATION         = "graduation";
    public static final String CHAIR              = "chair";
    public static final String CHAIR_NAME         = "chair_name";
    public static final String EDUCATION_FORM     = "education_form";
    public static final String EDUCATION_STATUS   = "education_status";
    //end region

    //region School data
    public static final String SCHOOL_ID      = "id";
    public static final String SCHOOL_NAME    = "name";
    public static final String SCHOOL_COUNTRY = COUNTRY;
    public static final String SCHOOL_CITY    = CITY;
    public static final String YEAR_FROM      = "year_from";
    public static final String YEAR_TO        = "year_to";
    public static final String YEAR_GRADUATED = "year_graduated";
    public static final String CLASS          = "class";
    //end region
}
