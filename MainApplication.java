package chatapp.tcp.multipleclient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.*;

public class MainApplication {
    public static void main(String[] args) throws IOException {

        JFrame frame = new JFrame("Chat Application");
        JLabel usernameLabel = new JLabel("Enter Username:");
        JTextField usernameField = new JTextField();
        JButton addUserBtn = new JButton("Add User");
        

        frame.setLayout(null);
        frame.setSize(300, 300);

        usernameLabel.setBounds(0, 0, 150, 25);
        usernameField.setBounds(0, 25, 150, 25);
        addUserBtn.setBounds(25, 50, 100, 25);

        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(addUserBtn);
        frame.setVisible(true);


        addUserBtn.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e){
            Boolean checker=true;
            String SQL_SELECT="select * from users_table";
            try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
                "postgres");
            PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {
                ResultSet resultSet= preparedStatement.executeQuery();
                while(resultSet.next()){
                    String uname= resultSet.getString("username");
                    Boolean connected= resultSet.getBoolean("connected");
                    if(uname.equals(usernameField.getText()))
                    {
                        checker=false;
                        if(connected==true){
                            ErrorFrame();
                            return;
                        }
                        else{
                            break;
                        }
                    }
                }
            } catch (SQLException ex) {
            System.out.print(ex.getMessage());
            } catch (Exception ex) {
            ex.printStackTrace();
            }
            if (checker==false){
                String SQL_CHANGE="update users_table set connected=true where username='"+usernameField.getText()+"'";
                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
                "postgres");
                PreparedStatement preparedStatement = conn.prepareStatement(SQL_CHANGE)) {
                    preparedStatement.executeUpdate();
                    System.out.println("Success");
                } catch (SQLException ex) {
                System.out.print(ex.getMessage());
                } catch (Exception ex) {
                ex.printStackTrace();
                }
                NetworkClient networkClient = new NetworkClient(usernameField.getText());
                usernameField.setText("");
                networkClient.start();
            }
            else{
                String SQL_INSERT="insert into users_table (username,connected) values(?,?)";
                try (Connection conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
                "postgres");
                PreparedStatement preparedStatement = conn.prepareStatement(SQL_INSERT)) {
                    preparedStatement.setString(1, usernameField.getText());
                    preparedStatement.setBoolean(2, true);
                    preparedStatement.executeUpdate();
                    System.out.println("Success");
                } catch (SQLException ex) {
                System.out.print(ex.getMessage());
                } catch (Exception ex) {
                ex.printStackTrace();
                }
                NetworkClient networkClient = new NetworkClient(usernameField.getText());
                usernameField.setText("");
                networkClient.start();
            }
            }
            public void ErrorFrame(){
                JFrame newFrame= new JFrame("Already connected");
                JLabel newLabel = new JLabel("This username is already connected in the chat.");
                newFrame.setLayout(null);
                newFrame.setSize(500,500);
                newLabel.setBounds(0,0,400,400);
                newFrame.add(newLabel);
                newFrame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
    }
    
}

