package com.fancy_software.crawling.utils;

import com.fancy_software.accounts_matching.io_local_base.LocalAccountWriter;
import com.fancy_software.accounts_matching.model.AccountVector;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by Yaro
 * Date: 01.11.13
 * Time: 17:49
 */
public class UserWriter implements Runnable {

    private Queue<AccountVector> userToWrite;
    private String               folder;
    private AtomicBoolean        stop;

    public UserWriter(Queue<AccountVector> usersToWrite, String folder, AtomicBoolean stop) {
        this.userToWrite = usersToWrite;
        this.folder = folder;
        this.stop = stop;
    }

    @Override
    public void run() {
        int pause = 3000;
        while (true) {
            if (!stop.get()) {
                if (!userToWrite.isEmpty()) {
                    AccountVector vector = userToWrite.remove();
                    LocalAccountWriter.writeAccountToLocalBase(vector, folder);
                } else {
                    try {
                        Thread.sleep(pause);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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