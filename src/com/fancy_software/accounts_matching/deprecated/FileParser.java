package com.fancy_software.accounts_matching.deprecated;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;

import java.io.*;

public class FileParser {

    /**
     * Парсить текстовый файл в формате:
     * BLOCK [название]
     * [данные]
     * Названия блоков, по порядку: NAME, SEX, BDATE, GROUPS
     * В файле обязательно должны быть все блоки, обязательно заполненные (кроме групп)
     *
     * @param path путь к файлу
     * @return вектор пользователя
     */

    public static AccountVector parseFile(String path) {
        AccountVector result = new AccountVector();
        File f = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
            boolean reading = false;
            int readingNow = -1;
            //0=id, 1 = name, 2 = sex, 3 = bdate, 4 = groups, 5=friends
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (reading) {
                    switch (readingNow) {
                        case 0: {
                            result.setId(Integer.parseInt(line));
                            reading = false;
                            break;
                        }
                        case 1: {
                            result.setFirst_name(line);
                            result.setLast_name(reader.readLine());
                            reading = false;
                            break;
                        }
                        case 2: {
                            if (line.equals("NA")) {
                                result.setSex(AccountVector.Sex.NA);

                            } else if (line.equals("FEMALE")) {
                                result.setSex(AccountVector.Sex.FEMALE);

                            } else if (line.equals("MALE")) {
                                result.setSex(AccountVector.Sex.MALE);

                            }
                        }
                        case 3: {
                            result.setBdate(BirthDate.generateBirthDate(line));
                            reading = false;
                            break;
                        }
                        case 4: {
                            int groupsAmount = Integer.parseInt(line);
                            for (int i = 0; i < groupsAmount; i++)
                                result.addGroup(reader.readLine());
                            reading = false;
                            break;
                        }
                        case 5: {
                            int friendsAmount = Integer.parseInt(line);
                            for (int i = 0; i < friendsAmount; i++)
                                result.addFriend(Long.parseLong(reader.readLine()));
                        }
                    }
                } else {
                    String[] cmd = line.split(" ");
                    if (cmd[1].equals("ID"))
                        readingNow = 0;
                    else if (cmd[1].equals("NAME"))
                        readingNow = 1;
                    else if (cmd[1].equals("SEX"))
                        readingNow = 2;
                    else if (cmd[1].equals("BDATE"))
                        readingNow = 3;
                    else if (cmd[1].equals("GROUPS"))
                        readingNow = 4;
                    else
                        readingNow = 5;
                    reading = true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
