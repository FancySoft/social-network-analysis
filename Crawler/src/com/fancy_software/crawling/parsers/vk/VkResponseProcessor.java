package com.fancy_software.crawling.parsers.vk;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;
import com.fancy_software.accounts_matching.model.WallMessage;
import com.fancy_software.accounts_matching.model.education.SchoolData;
import com.fancy_software.accounts_matching.model.education.UniversityData;
import com.fancy_software.logger.Log;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yaro
 * Date: 01.11.13
 * Time: 23:10
 */
public class VkResponseProcessor {

    private static final String TAG = VkResponseProcessor.class.getSimpleName();

    //todo deleted accounts shouldn't be extracted
    public List<AccountVector> processAccountInfoResponse(String response) throws NullPointerException {
        List<AccountVector> extraction = new LinkedList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseNode = mapper.readTree(response).get("response");
            for (int i = 0; i < responseNode.size(); i++) {
                JsonNode node = responseNode.get(i);
                AccountVector vector = extractAccount(node);
                if (vector != null)
                    extraction.add(vector);
//                System.out.println(vector);
            }

        } catch (IOException e) {
            Log.e(TAG, e);
            return extraction;
        }
        return extraction;
    }

    public AccountVector processSingleAccount(String response) throws NullPointerException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseNode = mapper.readTree(response).get("response");
            JsonNode node = responseNode.get(0);
            return extractAccount(node);
        } catch (IOException e) {
            Log.e(TAG, e);
        }
        return null;
    }

    //for groups and friends
    public List<Long> processGroupsOrFriendsResponse(String response) throws NullPointerException {
        List<Long> extraction = new LinkedList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseNode = mapper.readTree(response).get("response");
            for (int i = 0; i < responseNode.size(); i++) {
                JsonNode node = responseNode.get(i);
                try {
                    extraction.add(Long.valueOf(node.toString()));
                } catch (NumberFormatException e) {
                    Log.e(TAG, e);
                }
            }

        } catch (IOException | NullPointerException e) {
            Log.e(TAG, e);
            return extraction;
        }
        return extraction;
    }

    private AccountVector extractAccount(JsonNode node) {
        AccountVector result = new AccountVector();
        try {
            result.setId(node.get(FieldNames.ID).asText());
            result.setFirst_name(node.get(FieldNames.FIRST_NAME).asText());
            result.setLast_name(node.get(FieldNames.LAST_NAME).asText());
            if (node.has(FieldNames.SEX)) {
                result.setSex(convertSexFromApi(Integer.parseInt(node.get(FieldNames.SEX).asText())));
            }
            if (node.has(FieldNames.BIRTH_DATE)) {
                result.setBdate(BirthDate.generateBirthDate(node.get(FieldNames.BIRTH_DATE).asText()));
            }
            extractUniversityData(node, result);
            if (node.has("schools")) {
                JsonNode schoolNode = node.get("schools");
                for (int i = 0; i < schoolNode.size(); i++) {
                    extractSchoolData(schoolNode.get(i), result);
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Bad data");
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "No important fields in account");
            return result;
        }
        return result;
    }

    private void extractUniversityData(JsonNode node, AccountVector vector) {
        vector.addUniversity(extractUniversityData(node));
    }

    private UniversityData extractUniversityData(JsonNode node) {
        UniversityData universityData = new UniversityData();
        if (node.has(FieldNames.UNIVERSITY_NAME))
            universityData.setName(node.get(FieldNames.UNIVERSITY_NAME).asText());
        if (node.has(FieldNames.UNIVERSITY_COUNTRY))
            universityData.setCountry(node.get(FieldNames.UNIVERSITY_COUNTRY).asText());
        if (node.has(FieldNames.UNIVERSITY_CITY))
            universityData.setCity(node.get(FieldNames.UNIVERSITY_CITY).asText());
//        if (node.has(FieldNames.FACULTY))
//            universityData.setFaculty(node.get(FieldNames.FACULTY).asText());
        if (node.has(FieldNames.FACULTY_NAME))
            universityData.setFacultyName(node.get(FieldNames.FACULTY_NAME).asText());
        if (node.has(FieldNames.GRADUATION))
            universityData.setName(node.get(FieldNames.GRADUATION).asText());
        if (node.has(FieldNames.CHAIR))
            universityData.setName(node.get(FieldNames.CHAIR).asText());
        if (node.has(FieldNames.CHAIR_NAME))
            universityData.setName(node.get(FieldNames.CHAIR_NAME).asText());
        if (node.has(FieldNames.EDUCATION_FORM))
            universityData.setEducationForm(
                    UniversityData.GetEducationFormByRusString(node.get(FieldNames.EDUCATION_FORM).asText()));
        if (node.has(FieldNames.EDUCATION_STATUS))
            universityData.setEducationStatus(
                    UniversityData.GetEducationStatusByRusString(node.get(FieldNames.EDUCATION_STATUS).asText()));
        return universityData;
    }

    private void extractSchoolData(JsonNode node, AccountVector vector) {
        vector.addSchool(extractSchoolData(node));
    }

    private SchoolData extractSchoolData(JsonNode node) {
        SchoolData schoolData = new SchoolData();
        if (node.has(FieldNames.SCHOOL_NAME))
            schoolData.setName(node.get(FieldNames.SCHOOL_NAME).asText());
        if (node.has(FieldNames.SCHOOL_COUNTRY))
            schoolData.setCountry(node.get(FieldNames.SCHOOL_COUNTRY).asText());
        if (node.has(FieldNames.SCHOOL_CITY))
            schoolData.setCity(node.get(FieldNames.SCHOOL_CITY).asText());
        if (node.has(FieldNames.YEAR_FROM))
            schoolData.setYearFrom(Integer.parseInt(node.get(FieldNames.YEAR_FROM).asText()));
        if (node.has(FieldNames.YEAR_TO))
            schoolData.setYearTo(Integer.parseInt(node.get(FieldNames.YEAR_TO).asText()));
        if (node.has(FieldNames.YEAR_GRADUATED))
            schoolData.setGraduate(Integer.parseInt(node.get(FieldNames.YEAR_GRADUATED).asText()));
        if (node.has(FieldNames.CLASS))
            schoolData.setClassroom(node.get(FieldNames.CLASS).asText());
        return schoolData;
    }


    private AccountVector.Sex convertSexFromApi(int sexId) {
        switch (sexId) {
            case 1:
                return AccountVector.Sex.FEMALE;
            case 2:
                return AccountVector.Sex.MALE;
            default:
                return AccountVector.Sex.NA;
        }
    }

    public List<WallMessage> processWall(String response) {
        List<WallMessage> extraction = new LinkedList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseNode = mapper.readTree(response).get("response");
            for (int i = 0; i < responseNode.size(); i++) {
                JsonNode node = responseNode.get(i);
                WallMessage message = extractWallMessage(node);
                if (message != null)
                    extraction.add(message);
//                System.out.println(message);
            }

        } catch (IOException e) {
            Log.e(TAG, e);
        } catch (NullPointerException e) {
            Log.e(TAG, e);
        }
        return extraction;
    }

    private WallMessage extractWallMessage(JsonNode node) {
        WallMessage result = new WallMessage();
        try {
            if (node.has(FieldNames.ID_1))
                result.setId(Long.parseLong(node.get(FieldNames.ID_1).asText()));
            if (node.has(FieldNames.FROM_ID))
                result.setFromId(Long.parseLong(node.get(FieldNames.FROM_ID).asText()));
            if (node.has(FieldNames.TO_ID))
                result.setFromId(Long.parseLong(node.get(FieldNames.TO_ID).asText()));
            if (node.has(FieldNames.TEXT))
                result.setText(node.get(FieldNames.TEXT).asText());
            if (node.has(FieldNames.COPY_OWNER_ID))
                result.setCopyOwnerId(Long.parseLong(node.get(FieldNames.COPY_OWNER_ID).asText()));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Bad data");
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "No important fields in account");
            return result;
        }
        return result;
    }
}

