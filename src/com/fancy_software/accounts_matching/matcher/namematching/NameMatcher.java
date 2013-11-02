/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fancy_software.accounts_matching.matcher.namematching;

import java.io.*;
import java.util.ArrayList;


public class NameMatcher {
    
    private ArrayList<ArrayList<String>> femaleNames;
    private ArrayList<ArrayList<String>> maleNames;
    
    // femalePath - path to femaleNamesSet.txt
    // malePath - path to MaleNamesSet.txt
    public NameMatcher(String femalePath, String malePath) {
        femaleNames = new ArrayList();
        maleNames = new ArrayList();
        femaleNames = Transform.transformToArrayList(Transform.transform(femalePath));
        maleNames = Transform.transformToArrayList(Transform.transform(malePath));
    }
    
    public boolean match(String name1, String name2) {
        return (matchIgnoreSex(name1, name2, femaleNames) ||
                    matchIgnoreSex(name1, name2, maleNames));
    }
    
    // sex = 1 -> female
    // sex = 2 -> male
    public boolean match(String name1, String name2, int sex) {
        switch (sex) {
            case (1): {
                return matchIgnoreSex(name1, name2, femaleNames);
            }
            case (2): {
                return matchIgnoreSex(name1, name2, maleNames);
            }
            default: return (matchIgnoreSex(name1, name2, femaleNames) |
                    matchIgnoreSex(name1, name2, maleNames));
        }
    }
    
    private boolean matchIgnoreSex(String name1, String name2, ArrayList<ArrayList<String>> names) {
        for (int i=0; i<names.size(); ++i)
            if (isThere(name1, names.get(i)) & isThere(name2, names.get(i)))
                return true;
        return false;
    }
    
    private boolean isThere(String name, ArrayList<String> names) {
        for (int i=0; i<names.size(); ++i)
            if (name.equalsIgnoreCase(names.get(i))) return true;
        return false;
    }
    
    // if the name is in the list returns corresponding list of forms (ArrayList<String>)
    // else returns null
    public ArrayList<String> allForms(String name, int sex) {
        if (sex == 1) {
            for (int i=0; i<femaleNames.size(); ++i)
                if (isThere(name, femaleNames.get(i)))
                    return femaleNames.get(i);
        } else if (sex == 2) {
            for (int j=0; j<maleNames.size(); ++j)
                if (isThere(name, maleNames.get(j)))
                    return maleNames.get(j);
        }
        return null;
    }
}



class Transform {
    
    static ArrayList<String> transform(String filename) {
        try {
            ArrayList<String> namesSet = new ArrayList();
            BufferedReader br = new BufferedReader (
            new InputStreamReader(
            new FileInputStream(filename), "Cp1251"));
               String line;
            while ((line = br.readLine()) != null) {
                namesSet.add(line + br.readLine() + " " + br.readLine() + br.readLine());
            }
            return namesSet;
        } catch (IOException e) {}
     return null;
    }
    
    static String deleteExcessSymbols(String line) {
        StringBuilder str = new StringBuilder(line);
        for (int i=0; i<str.length();)
            if (str.charAt(i) == ',')
                str.deleteCharAt(i);
            else ++i;
        return str.toString();
    }
    
    static ArrayList<String> toArrayList(String line) {
        StringBuilder str = new StringBuilder(line + ' ');
        ArrayList<String> names = new ArrayList();
        int i=0;
        while (str.length() > 0) {
            while (str.charAt(i) != ' ')
                ++i;
            names.add(str.substring(0, i));
            str.delete(0, i).toString();
            i=0;
            if (str.length() > 0) str.deleteCharAt(0);
        }
        return names;
    }
    
    static ArrayList<ArrayList<String>> transformToArrayList(ArrayList<String> nameSets) {
        ArrayList<ArrayList<String>> setOfNames = new ArrayList();
        String line;
        for (int i=0; i<nameSets.size(); ++i) {
            line = Transform.deleteExcessSymbols(nameSets.get(i));
            setOfNames.add(Transform.toArrayList(line));
        }
        return setOfNames;
    }
    
}


