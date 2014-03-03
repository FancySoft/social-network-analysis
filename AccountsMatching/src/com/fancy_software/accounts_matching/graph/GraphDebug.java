package com.fancy_software.accounts_matching.graph;

import com.fancy_software.utils.io.LocalAccountReader;
import com.fancy_software.accounts_matching.model.AccountVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class GraphDebug {
    public static void main(String args[]) throws FileNotFoundException {

        File folder = new File("accounts/vk");
        File[] listOfFiles = folder.listFiles();

        List<AccountVector> mygraph = new LinkedList<AccountVector>();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                mygraph.add(LocalAccountReader.readAccountFromLocalBase("accounts/vk/" + listOfFile.getName()));
                //mygraph.add(FileParser.parseFile("accounts/vk/" + listOfFiles[i].getName()));
            }
        }

        List<Clique> res = CliquesExtractor.getKCliques(mygraph, 4);
        List<Clique> res2 = CliquesExtractor.mergeKCliques(res, 4);
        List<Clique> res_filtered = CliquesExtractor.filter(res2);
        while (res_filtered.size() < res2.size()) {
            res2 = res_filtered;
            res_filtered = CliquesExtractor.filter(res2);
        }
        int i = 0;
        for (Clique c : res2) {
            i++;
            System.out.println("Clique #" + i);
            c.print();
        }
    }
}
