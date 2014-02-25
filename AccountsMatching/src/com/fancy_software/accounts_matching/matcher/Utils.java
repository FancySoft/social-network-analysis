package com.fancy_software.accounts_matching.matcher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Utils {

    // Таблица транслита по госту для загранпаспортов с википедии
    private static final Map<Character, String> translitTable = new TreeMap<>();

    static {
        translitTable.put('а', "a");
        translitTable.put('б', "b");
        translitTable.put('в', "v");
        translitTable.put('г', "g");
        translitTable.put('д', "d");
        translitTable.put('е', "e");
        translitTable.put('ё', "e");
        translitTable.put('ж', "zh");
        translitTable.put('з', "z");
        translitTable.put('и', "i");
        translitTable.put('й', "y");
        translitTable.put('к', "k");
        translitTable.put('л', "l");
        translitTable.put('м', "m");
        translitTable.put('н', "n");
        translitTable.put('о', "o");
        translitTable.put('п', "p");
        translitTable.put('р', "r");
        translitTable.put('с', "s");
        translitTable.put('т', "t");
        translitTable.put('у', "u");
        translitTable.put('ф', "f");
        translitTable.put('х', "kh");
        translitTable.put('ц', "ts");
        translitTable.put('ч', "ch");
        translitTable.put('ш', "sh");
        translitTable.put('щ', "shch");
        translitTable.put('ъ', "'");
        translitTable.put('ы', "y");
        translitTable.put('ь', "'");
        translitTable.put('э', "e");
        translitTable.put('ю', "yu");
        translitTable.put('я', "ya");
    }

    @SuppressWarnings("unused")
    public static void print(List<?> text) {
        for (Object object : text) {
            System.out.println(object);
        }
    }

    @SuppressWarnings("unused")
    public static void print(String[] text) {
        for (String s : text) {
            System.out.println(s);
        }
    }

    @SuppressWarnings("unused")
    /**
     * Reads the text file in 1251 encoding
     */
    public static List<String> getText(String fileName) throws IOException {
        Reader reader = new InputStreamReader(new FileInputStream(new File(fileName)), "windows-1251");
        List<String> text = new ArrayList<>();
        LineNumberReader r = new LineNumberReader(reader);
        for (; ; ) {
            String s = r.readLine();
            if (s == null)
                return text;
            text.add(s);
        }
    }

    /**
     * Транслитерация и приведение к нижнему регистру
     *
     * @param s строка
     * @return кириллица транслитом, остальное без изменений, все нижний регистр
     */
    public static String transliterate(String s) {
        StringBuilder result = new StringBuilder();
        String lower = s.toLowerCase();
        for (int i = 0; i < lower.length(); i++) {
            Character ch = lower.charAt(i);
            String charFromMap = translitTable.get(ch);
            if (charFromMap == null) {
                result.append(ch);
            } else {
                result.append(charFromMap);
            }
        }
        return result.toString();
    }

    /**
     * Считает количество замен в строке 1 для получения строки 2
     *
     * @param s1 строка 1
     * @param s2 строка 2
     * @return количество замен
     */
    public static int replaces(String s1, String s2) {
        int count = s2.length();
        if (s1.length() < count)
            count = s1.length();
        for (int i = 0; i < count; i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                String left = s1.substring(0, i);
                String right = s1.substring(i + 1, s1.length());
                int res1 = replaces(left + right, s2);
                int res2 = replaces(left + s2.charAt(i) + right, s2);
                if (res1 > res2)
                    return res2 + 1;
                return res1 + 1;
            }
        }
        if (count == s1.length())
            return s2.length() - count;
        return s1.length() - count;
    }

    public static void print(Map<?, ?> map) {
        for (Object object : map.keySet()) {
            System.out.println("Key: " + object.toString() + " Value: " + map.get(object));
        }
    }

    public static String getCountryId(String country) {
        return "RU";
    }
}
