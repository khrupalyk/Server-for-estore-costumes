/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author insane
 */
public class Server implements Runnable {

    JTextArea log;
    private ServerSocket server;
    public static final int PORT = 1234;
    public static final int AUTHENTICATION_SUCCESS = 1;
    public static final int AUTHENTICATION_FAILED = 2;

    public Server(JTextArea log) {
        this.log = log;
    }
    
    

    public void start() {
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(PORT);
            DataInputStream in = null;
            DataOutputStream out = null;
            Statement statement = DataBaseConnection.getInstance().getStatement();
            while (true) {
                Socket accept = server.accept();
                in = new DataInputStream(accept.getInputStream());
                out = new DataOutputStream(accept.getOutputStream());

                ResultSet set = statement.executeQuery("SELECT id_users,role, id_store FROM users WHERE login = '" + in.readUTF()
                        + "' AND password = '" + in.readUTF() + "' AND id_store = " + in.readInt());
                if (set.next()) {
//                    if(set.getInt("role") )
                    out.writeInt(AUTHENTICATION_SUCCESS);
                    out.writeInt(set.getInt("role"));
                    System.out.println(set.getInt("id_store"));
                    new ClientListener(accept, in, out, set.getInt("id_store"), set.getInt("id_users"),log);

                } else {
                    out.writeInt(AUTHENTICATION_FAILED);
                }

            }
        } catch (IOException | SQLException ex) {
            log.setText(log.getText() + "\n" + ex.getMessage());
        }

    }

    public void stop() {
        try {
            server.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
