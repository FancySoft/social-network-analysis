package com.fancy_software.accounts_matching.matcher;


import com.fancy_software.accounts_matching.deprecated.Parsers;
import com.fancy_software.accounts_matching.model.AccountVector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main implements Runnable {

    public JTextField tf1;
    public JTextField tf2;
    public JTextArea output;

    /**
     * @param args они очень нужны
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке");
        }
        SwingUtilities.invokeLater(new Main());
    }

    @Override
    public void run() {
        JFrame f = new JFrame("Accounts matching");
        f.setSize(400, 350);
        f.getContentPane().setLayout(null);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel foff = new JLabel("F.. off");
        foff.setBounds(10, 10, 150, 10);
        f.add(foff, null);
        tf1 = new JTextField("emocoder");
        tf1.setBounds(10, 25, 150, 20);
        tf2 = new JTextField("emocoder");
        tf2.setBounds(10, 50, 150, 20);
        f.add(tf1, null);
        f.add(tf2, null);
        JButton buttonCompare = new JButton();
        buttonCompare.setText("Compare");
        buttonCompare.setBounds(10, 75, 80, 20);
        ActionListener btnListener = new CompareListener();
        buttonCompare.addActionListener(btnListener);
        f.add(buttonCompare);
        output = new JTextArea();
        output.setBounds(10, 100, 350, 200);
        output.setEditable(false);
        output.setFont(new Font("Dialog", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(output);
        scrollPane.setBounds(10, 100, 360, 210);
        f.add(scrollPane);

        f.setLocation(300, 300);
        f.setVisible(true);
    }

    /**
     * Обработчик нажатий на кнопку "Compare"
     */
    public class CompareListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                AccountVector account1 = Parsers.parseVK(tf1.getText());
                AccountVector account2 = Parsers.parseVK(tf2.getText());
                output.append("Name: " + account1.getFirst_name() + " " + account1.getLast_name() + "\n");
                output.append("Birthday: " + account1.getBdate() + "\n");
                output.append("Name: " + account2.getFirst_name() + " " + account2.getLast_name() + "\n");
                output.append("Birthday: " + account2.getBdate() + "\n");
                double measure = (new AccountMeasurer(account1, account2)).measure(false);
                output.append("measure: " + measure + "\n");
                //output.append("LDA: " + AccountMeasurer.debug_ldaResult + "\n");
                String result;
                if (measure < 3)
                    result = "One user";
                else
                    result = "Different users";
                output.append("Result: " + result + "\n");
            } catch (IOException e1) {
                e1.printStackTrace();
                output.append("Something wrong with IO\n");
            } catch (Exception e2) {
                e2.printStackTrace();
                output.append("Something wrong with LDA\n");
            }
        }
    }
}
