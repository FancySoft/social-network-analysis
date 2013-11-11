package com.fancy_software.accounts_matching.matcher;

import com.fancy_software.accounts_matching.matcher.lda.TestTextComparator;
import com.fancy_software.accounts_matching.model.BirthDate;

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
