/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;

/**
 *
 * @author insane
 */
public class Main {

    static boolean start = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(410, 400);
            f.setLayout(new FlowLayout(FlowLayout.LEFT));
            JButton statrt = new JButton("start");
            JTextField pass = new JTextField(15);
            JTextField login = new JTextField(15);
            login.setText("root");
            pass.setText("111111");
            JTextArea log = new JTextArea();
            Server server = new Server(log);
            JScrollPane pane = new JScrollPane(log);
            pane.setPreferredSize(new Dimension(400, 300));

            statrt.addActionListener((action) -> {
                DataBaseConnection.getInstance(login.getText(),pass.getText());
                if (!start) {
                    server.start();
                    start = true;
                    statrt.setText("stop");
                }else{
                    start = false;
                    statrt.setText("start");
                    server.stop();
                }
                
            });

            f.add(new JLabel("User in db:                                  "));
            f.add(login);
            f.add(new JLabel("Password in db:                        "));
            f.add(pass);
            f.add(statrt);
            f.add(pane);
            f.setVisible(true);
        });

    }

}
