package com.fancy_software.crawling;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountWriter;
import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.Queue;


/**
 * Created by Yaro
 * Date: 01.11.13
 * Time: 17:49
 */
public class UserWriter implements Runnable {

    private Queue<AccountVector> userToWrite;
    private Thread               parentThread;
    private String               folder;

    public UserWriter(Queue<AccountVector> usersToWrite, Thread parentThread, String folder) {
        this.userToWrite = usersToWrite;
        this.parentThread = parentThread;
        this.folder = folder;
    }

    @Override
    public void run() {
        while (true) {
            if (parentThread.isAlive()) {
                if (!userToWrite.isEmpty()) {
                    AccountVector vector = userToWrite.remove();
                    LocalAccountWriter.writeAccountToLocalBase(vector, folder);
                }
            } else {
                System.out.println("Writing to local base still running, wait");
                if (!userToWrite.isEmpty()) {
                    for (AccountVector vector : userToWrite) {
                        LocalAccountWriter.writeAccountToLocalBase(vector, folder);
                    }
                }
                System.out.println("Writing to local base finished");
                break;
            }
        }
    }

}