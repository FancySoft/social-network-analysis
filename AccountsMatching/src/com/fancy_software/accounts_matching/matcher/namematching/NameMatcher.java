package com.fancy_software.accounts_matching.matcher.namematching;

import com.fancy_software.accounts_matching.matcher.Utils;
import com.fancy_software.utils.Settings;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;


public class NameMatcher {

    private static final String FEMALE_ID = "female_names_path";
    private static final String MALE_ID = "male_names_path";
    private static SoftReference<NameMatcher> instance;
    private List<List<String>> femaleNames;
    private List<List<String>> maleNames;

    /**
     * Initializes names dictionaries
     *
     * @param femalePath path to femaleNamesSet.txt
     * @param malePath   path to MaleNamesSet.txt
     */
    private NameMatcher(String femalePath, String malePath) {
        femaleNames = new ArrayList<>();
        maleNames = new ArrayList<>();
        femaleNames = Transform.transformToArrayList(Transform.transform(femalePath));
        maleNames = Transform.transformToArrayList(Transform.transform(malePath));
    }

    public static NameMatcher getInstance() {
        if (instance == null || instance.get() == null) {
            Settings settings = Settings.getInstance();
            String f_path = settings.get(FEMALE_ID);
            String m_path = settings.get(MALE_ID);
            instance = new SoftReference<>(new NameMatcher(f_path, m_path));
        }
        return instance.get();
    }

    public boolean match(String name1, String name2) {
        return (matchIgnoreSex(name1, name2, femaleNames) ||
                matchIgnoreSex(name1, name2, maleNames));
    }

    /**
     * Check if given names are synonymous
     *
     * @param name1 to match
     * @param name2 to match
     * @param sex   1: female, 2: male
     * @return are synonymous
     */
    public boolean match(String name1, String name2, int sex) {
        switch (sex) {
            case (1): {
                return matchIgnoreSex(name1, name2, femaleNames);
            }
            case (2): {
                return matchIgnoreSex(name1, name2, maleNames);
            }
            default:
                return (matchIgnoreSex(name1, name2, femaleNames) ||
                        matchIgnoreSex(name1, name2, maleNames));
        }
    }

    private boolean matchIgnoreSex(String name1, String name2, List<List<String>> names) {
        String norm1 = Utils.transliterate(name1.toUpperCase());
        String norm2 = Utils.transliterate(name2.toUpperCase());
        for (List<String> name : names)
            if (name.contains(norm1) && name.contains(norm2))
                return true;
        return false;
    }

    /**
     * Returns corresponding list of forms (ArrayList<String>)
     * for the given name
     *
     * @param name name to give form for
     * @param sex  1: female, 2: male
     * @return forms
     */
    public List<String> allForms(String name, int sex) {
        String uName = Utils.transliterate(name.toUpperCase());
        if (sex == 1) {
            for (List<String> femaleName : femaleNames)
                if (femaleName.contains(uName))
                    return femaleName;
        } else if (sex == 2) {
            for (List<String> maleName : maleNames)
                if (maleName.contains(uName))
                    return maleName;
        }
        return null;
    }
}


