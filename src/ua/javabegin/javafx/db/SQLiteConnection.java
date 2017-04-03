package ua.javabegin.javafx.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteConnection {

    public static Connection getConnection(){
        try{
            Class.forName("org.sqlite.JDBC").newInstance();
            return DriverManager.getConnection("jdbc:sqlite:db/addressbook.db");
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            Logger.getLogger(SQLiteConnection.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

}
