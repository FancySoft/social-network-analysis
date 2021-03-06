package com.fancy_software.accounts_matching.data_randomizer;

import com.fancy_software.accounts_matching.model.AccountVector;
import com.fancy_software.accounts_matching.model.BirthDate;

import java.util.Random;

public class Randomizer {

    private static final String[] names = new String[]{
            "Paul",
            "Sam",
            "Василий",
            "Вася",
            "Серега",
            "Bill",
            "George",
            "Ringo",
            "John"
    };
    private static final String[] surnames = new String[]{
            "McCartney",
            "Wilson",
            "Сиплый",
            "Иванов",
            "Петров",
            "Gates",
            "Harrison",
            "Star",
            "Lennon"
    };

    private static Random random = new Random(System.currentTimeMillis());

    public static void randomize(AccountVector vector) {
        if (random.nextBoolean()) {
            // Change name
            vector.setFirst_name(names[random.nextInt(1000) % names.length]);
            if (random.nextBoolean()) {
                // Surname also
                vector.setLast_name(surnames[random.nextInt(1000) % surnames.length]);
            }
        }
        if (random.nextBoolean()) {
            // Change bday/sex
            if (random.nextBoolean()) {
                vector.setSex(AccountVector.Sex.NA);
            } else {
                vector.setBdate(new BirthDate(random.nextInt(31),random.nextInt(12),random.nextInt(2013)));
            }
        }
        if (random.nextBoolean()) {
            // Change groups
            if (vector.getGroups().size() != 0) {
                int count = random.nextInt(1000) % vector.getGroups().size();
                int toDelete;
                count /= 2;
                while (count > 0) {
                    toDelete = random.nextInt(1000) % vector.getGroups().size();
                    vector.getGroups().remove(toDelete);
                    count--;
                }
            }
        }
    }
}
