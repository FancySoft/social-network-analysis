package com.fancy_software.accounts_matching.matcher.namematching;

import com.fancy_software.logger.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Transform {

    private static final String TAG = Transform.class.getSimpleName();

    static List<String> transform(String filename) {
        try {
            List<String> namesSet = new ArrayList<String>();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                namesSet.add(line + br.readLine() + ", " + br.readLine() + br.readLine());
            }
            return namesSet;
        } catch (IOException e) {
            Log.e(TAG, e);
        }
        return null;
    }

    static List<List<String>> transformToArrayList(List<String> nameSets) {
        List<List<String>> setOfNames = new ArrayList<List<String>>();
        for (String nameSet : nameSets) {
            setOfNames.add(Arrays.asList(nameSet.split(", ")));
        }
        return setOfNames;
    }

}
