package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.matcher.lda.TestTextComparator;
import com.fancy_software.accounts_matching.model.BirthDate;
import com.fancy_software.accounts_matching.model.education.SchoolData;
import com.fancy_software.accounts_matching.model.education.UniversityData;

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

    public static double measureSchoolsLists(List<SchoolData> schools1, List<SchoolData> schools2) {
        double schoolWeight = 1 / (double) Math.max(schools1.size(), schools2.size());
        double totalMeasurement = 0;
        double maxWeight;
        for (SchoolData aSchools1 : schools1) {
            maxWeight = 0;
            for (SchoolData aSchools2 : schools2) {
                maxWeight = Math.max(maxWeight, measureSchools(aSchools1, aSchools2));
            }
            totalMeasurement += maxWeight * schoolWeight;
        }
        return totalMeasurement;
    }

    private static double measureSchools(SchoolData school1, SchoolData school2) {
        if (school1 == null || school2 == null)
            return 0;

        double totalWeight = stringMeasure(school1.getName(), school1.getName()) +
                             stringMeasure(school1.getClassroom(), school1.getClassroom()) +
                             (school1.getYearFrom() == school2.getYearFrom() ? 1 : 0) +
                             (school1.getYearTo() == school2.getYearTo() ? 1 : 0) +
                             (school1.getGraduate() == school2.getGraduate() ? 1 : 0);

        double weight = 0;
        weight += stringMeasure(school1.getName(), school2.getName());
        if (school1.getName().equals(school2.getName())) {
            weight += school1.getYearFrom() == school2.getYearFrom() ? 1 : 0;
            weight += school1.getYearTo() == school2.getYearTo() ? 1 : 0;
            weight += school1.getGraduate() == school2.getGraduate() ? 1 : 0;
            weight += stringMeasure(school1.getClassroom(), school2.getClassroom());
        }

        return weight / totalWeight;
    }

    public static double measureUniversitiesLists(List<UniversityData> universities1, List<UniversityData> universities2) {
        double universityWeight = 1 / (double) Math.max(universities1.size(), universities2.size());
        double totalMeasurement = 0;
        double maxWeight;
        for (UniversityData anUniversities1 : universities1) {
            maxWeight = 0;
            for (UniversityData anUniversities2 : universities2) {
                maxWeight = Math.max(maxWeight, measureUniversites(anUniversities1, anUniversities2));
            }
            totalMeasurement += maxWeight * universityWeight;
        }
        return totalMeasurement;
    }

    private static double measureUniversites(UniversityData univ1, UniversityData univ2) {
        if (univ1 == null || univ2 == null)
            return 0;

        double totalWeight = stringMeasure(univ1.getName(), univ2.getName()) +
                             stringMeasure(univ1.getFacultyName(), univ2.getFacultyName()) +
                             stringMeasure(univ1.getChairName(), univ2.getChairName()) +
                             (univ1.getGraduation() == univ2.getGraduation() ? 1 : 0) +
                             (univ1.getEducationForm() == univ2.getEducationForm() ? 1 : 0) +
                             (univ1.getEducationStatus() == univ2.getEducationStatus() ? 1 : 0);

        double weight = 0;
        weight += stringMeasure(univ1.getName(), univ2.getName());
        if (univ1.getName().equals(univ2.getName())) {
            weight += stringMeasure(univ1.getFacultyName(), univ2.getFacultyName());
            if (univ1.getFacultyName().equals(univ2.getFacultyName())) {
                weight += stringMeasure(univ1.getChairName(), univ2.getChairName());
                if (univ1.getChairName().equals(univ2.getChairName())) {
                    weight += univ1.getGraduation() == univ2.getGraduation() ? 1 : 0;
                    if (univ1.getGraduation() == univ2.getGraduation()) {
                        weight += univ1.getEducationForm() == univ2.getEducationForm() ? 1 : 0;
                        if (univ1.getEducationForm() == univ2.getEducationForm()) {
                            weight += univ1.getEducationStatus() == univ2.getEducationStatus() ? 1 : 0;
                        }
                    }
                }
            }
        }

        return weight / totalWeight;
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
