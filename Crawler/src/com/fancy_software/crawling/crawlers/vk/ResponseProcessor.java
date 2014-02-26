package com.fancy_software.crawling.crawlers.vk;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;
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
public class ResponseProcessor {

    //todo deleted accounts shouldn't be extracted
    public List<AccountVector> processResponse(String response) throws NullPointerException {
        List<AccountVector> extraction = new LinkedList<AccountVector>();
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
            e.printStackTrace();
            return extraction;
        }
        return extraction;
    }

    //for groups and friends
    public List<Long> processInfo(String response) throws NullPointerException {
        List<Long> extraction = new LinkedList<Long>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseNode = mapper.readTree(response).get("response");
            for (int i = 0; i < responseNode.size(); i++) {
                JsonNode node = responseNode.get(i);
                try {
                    extraction.add(Long.valueOf(node.toString()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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
            if (node.has(FieldNames.SEX))
                result.setSex(convertSexFromApi(Integer.parseInt(node.get(FieldNames.SEX).asText())));
            if (node.has(FieldNames.BIRTH_DATE))
                result.setBdate(BirthDate.generateBirthDate(node.get(FieldNames.BIRTH_DATE).asText()));
        } catch (NumberFormatException e) {
            System.out.println("Bad data");
            return null;
        } catch (NullPointerException e) {
            System.out.println("No important fields in account");
            return result;
        }
        return result;
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

}

