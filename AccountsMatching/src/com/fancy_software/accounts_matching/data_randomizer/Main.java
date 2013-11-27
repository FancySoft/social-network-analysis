package com.fancy_software.accounts_matching.data_randomizer;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountReader;
import com.fancy_software.accounts_matching.io_local_base.LocalAccountWriter;
import com.fancy_software.accounts_matching.model.AccountVector;

import java.io.File;

public class Main {

    private static final String PATH_FROM="accounts/vk";
    private static final String PATH_TO = "accounts/social_network2";

    public static void main(String[] args) {
        String pathFrom;
        String pathTo;
        if (args.length == 0){
            pathFrom=PATH_FROM;
            pathTo = PATH_TO;
        }
        else{
            pathFrom = args[0];
            pathTo=args[1];
        }
        System.out.println("Randomize started");
        String list[] = new File(pathFrom).list();
        for(int i = 0; i < list.length; i++) {
            AccountVector vector = LocalAccountReader.readAccountFromLocalBase(pathFrom+"/"+list[i]);
            Randomizer.randomize(vector);
            LocalAccountWriter.writeAccountToLocalBase(vector,pathTo+"/"+list[i]);
        }
        System.out.println("Randomize finished");

    }
}
