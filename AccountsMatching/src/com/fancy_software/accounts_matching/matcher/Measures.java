package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.matcher.lda.TestTextComparator;
import com.fancy_software.accounts_matching.model.BirthDate;
import com.fancy_software.accounts_matching.model.SchoolData;
import com.fancy_software.accounts_matching.model.UniversityData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Measures {

    /**
     * Мера для строк. Транслитерирует и считает количество замен.
     *
     * @param s1 строка 1
     * @param s2 строка 2
     * @return наименьшее количество замен
     */
    public static int stringMeasure(String s1, String s2) {
        String ss1 = Utils.transliterate(s1);
        String ss2 = Utils.transliterate(s2);
        int res1 = Utils.replaces(ss1, ss2);
        int res2 = Utils.replaces(ss2, ss1);
        return res1 > res2 ? res2 : res1;
    }

    public static double measureBirthdate(BirthDate bithdate1, BirthDate bithdate2) throws IndexOutOfBoundsException, NullPointerException {
        if (bithdate1 == null || bithdate2 == null)
            return 0;
        if ((bithdate1.getDay() == bithdate2.getDay()) && (bithdate1.getMonth() == bithdate2.getMonth()) && (bithdate1.getYear() == bithdate2.getYear()))
            return 1;

        if ((bithdate1.getDay() == bithdate2.getDay()) && (bithdate1.getMonth() == bithdate2.getMonth()))
            if (bithdate1.isNormal() && bithdate2.isNormal())
                return 0.5;
            else
                return 0.7;
        return 0;
    }

    public static double measureSchoolsLists(List<SchoolData> schools1, List<SchoolData> schools2){
        double schoolWeight = 1/(double)Math.max(schools1.size(), schools2.size());
        double totalMeasurement = 0;
        double maxWeight = 0;
        for(int i = 0; i < schools1.size(); i++){
            maxWeight = 0;
            for(int j = 0; j < schools2.size(); j++){
                maxWeight = Math.max(maxWeight, measureSchools(schools1.get(i), schools2.get(j)));
            }
            totalMeasurement += maxWeight*schoolWeight;
        }
        return totalMeasurement;
    }

    private static double measureSchools(SchoolData school1, SchoolData school2){
        if(school1 == null || school2 == null)
            return 0;

        double totalWeight = stringMeasure(school1.getName(), school1.getName()) +
                             stringMeasure(school1.getClassroom(), school1.getClassroom()) +
                             (school1.getYear_from() == school1.getYear_from() ? 1 : 0) +
                             (school1.getYear_to() == school1.getYear_to() ? 1 : 0) +
                             (school1.getGraduate() == school1.getGraduate() ? 1 : 0);

        double weight = 0;
        weight += stringMeasure(school1.getName(), school2.getName());
        if(school1.getName() == school2.getName()){
            weight += school1.getYear_from() == school2.getYear_from() ? 1 : 0;
            weight += school1.getYear_to() == school2.getYear_to() ? 1 : 0;
            weight += school1.getGraduate() == school2.getGraduate() ? 1 : 0;
            weight += stringMeasure(school1.getClassroom(), school1.getClassroom());
        }

        return weight/totalWeight;
    }

    public static double measureUniversitiesLists(List<UniversityData> universities1, List<UniversityData> universities2){
        double universityWeight = 1/(double)Math.max(universities1.size(), universities2.size());
        double totalMeasurement = 0;
        double maxWeight = 0;
        for(int i = 0; i < universities1.size(); i++){
            maxWeight = 0;
            for(int j = 0; j < universities2.size(); j++){
                maxWeight = Math.max(maxWeight, measureUniversites(universities1.get(i), universities2.get(j)));
            }
            totalMeasurement += maxWeight*universityWeight;
        }
        return totalMeasurement;
    }

    private static double measureUniversites(UniversityData univ1, UniversityData univ2){
        if(univ1 == null || univ2 == null)
            return 0;

        double totalWeight = stringMeasure(univ1.getName(), univ1.getName()) +
                             stringMeasure(univ1.getFaculty_name(), univ1.getFaculty_name()) +
                             stringMeasure(univ1.getChair_name(), univ1.getChair_name()) +
                             (univ1.getGraduation() == univ2.getGraduation() ? 1 : 0) +
                             (univ1.getEducation_form() == univ1.getEducation_form() ? 1 : 0) +
                             (univ1.getEducation_status() == univ1.getEducation_status() ? 1 : 0);

        double weight = 0;
        weight += stringMeasure(univ1.getName(), univ2.getName());
        if(univ1.getName() == univ2.getName()){
            weight += stringMeasure(univ1.getFaculty_name(), univ1.getFaculty_name());
            if(univ1.getFaculty_name() == univ1.getFaculty_name()){
                weight += stringMeasure(univ1.getChair_name(), univ2.getChair_name());
                if(univ1.getChair_name() == univ2.getChair_name()){
                    weight += univ1.getGraduation() == univ2.getGraduation() ? 1 : 0;
                    if(univ1.getGraduation() == univ2.getGraduation()){
                        weight += univ1.getEducation_form() == univ2.getEducation_form() ? 1 : 0;
                        if(univ1.getEducation_form() == univ2.getEducation_form()){
                            weight += univ1.getEducation_status() == univ2.getEducation_status() ? 1 : 0;
                        }
                    }
                }
            }
        }

        return weight/totalWeight;
    }

    //lda
    public static double measureWithLDA(List<String> l1, List<String> l2) throws IOException {
        //todo return something other than zero in such cases
        if (l1 == null || l2 == null)
            return 0;
        if (l1.size() == 0 || l2.size() == 0)
            return 0;
        Map<Double, String[]> map1 = TestTextComparator.getLocalRes(l1);
        Map<Double, String[]> map2 = TestTextComparator.getLocalRes(l2);
        /*for (String s : l1) {
            String[] temp = s.split(" ");
            Double help = Double.parseDouble(temp[0]);
            String[] temp1 = new String[temp.length - 1];
            for (int i = 0; i < temp1.length; i++)
                temp1[i] = temp[i + 1];
            map1.put(help, temp1);

        }
        for (String s : l2) {
            String[] temp = s.split(" ");
            Double help = Double.parseDouble(temp[0]);
            String[] temp1 = new String[temp.length - 1];
            for (int i = 0; i < temp1.length; i++)
                temp1[i] = temp[i + 1];
            map2.put(help, temp1);

        } */
        return TestTextComparator.compare(map1, map2);
    }
}
