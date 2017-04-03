package ua.javabegin.javafx.interfaces.impls;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import ua.javabegin.javafx.db.SQLiteConnection;
import ua.javabegin.javafx.interfaces.AddressBook;
import ua.javabegin.javafx.objects.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBAddressBook implements AddressBook{

    private ObservableList<Person> personList = FXCollections.observableArrayList();

    @Override
    public ObservableList<Person> searchAll() {
        gettingList("select * from PERSON");
        return personList;
    }

    @Override
    public ObservableList<Person> searchNote(String note) {
        String searching = "%" + note + "%";
        gettingList("select * from PERSON where name like '" + searching + "' or address like '" + searching + "' or phone like '" + searching + "'");
        return personList;
    }

    public ObservableList<Person> getPersonList() {
        return personList;
    }

    @Override
    public boolean updateNote(Person person) {
        String sql = "update person set name=?, address=?, phone=? where id=?";
        try (Connection con = SQLiteConnection.getConnection(); PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, person.getName());
            statement.setString(2, person.getAddress());
            statement.setString(3, person.getPhone());
            statement.setInt(4, person.getId());
            int result = statement.executeUpdate();
            if (result > 0) return true;
        }catch (SQLException ex){
            Logger.getLogger(DBAddressBook.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean addNote(Person person) {
        String sql = "INSERT INTO PERSON (name, address, phone) SELECT ?, ?, ? WHERE NOT EXISTS (SELECT * FROM PERSON WHERE name = ?)";
        try (Connection con = SQLiteConnection.getConnection(); PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, person.getName());
            statement.setString(2, person.getAddress());
            statement.setString(3, person.getPhone());
            statement.setString(4, person.getName());
            int result = statement.executeUpdate();
            if (result > 0) {
                int id = statement.getGeneratedKeys().getInt(1);// получить сгенерированный id вставленной записи
                person.setId(id);
                personList.add(person);
                return true;
            }
        }catch (SQLException ex){
            Logger.getLogger(DBAddressBook.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean deleteNote(Person person) {
        try (Connection con = SQLiteConnection.getConnection();  Statement statement = con.createStatement();) {
            int result = statement.executeUpdate("delete from person where id in (" + person.getId() + ")");
            if (result > 0) {
                personList.remove(person);
                return true;
            }
        }catch (SQLException ex){
            Logger.getLogger(DBAddressBook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public boolean deleteNote(String ids, ArrayList<Person> persons) {
        try (Connection con = SQLiteConnection.getConnection();  Statement statement = con.createStatement();) {
            int result = statement.executeUpdate("delete from person where id in (" + ids + ")");
            if (result > 0) {
                personList.removeAll(persons);
                return true;
            }
        }catch (SQLException ex){
            Logger.getLogger(DBAddressBook.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private void gettingList(String sql){
        try (Connection con = SQLiteConnection.getConnection(); Statement stm = con.createStatement(); ResultSet rs = stm.executeQuery(sql);){
            while (rs.next()){
                Person person = new Person();
                person.setId(rs.getInt("id"));
                person.setName(rs.getString("name"));
                person.setAddress(rs.getString("address"));
                person.setPhone(rs.getString("phone"));
                personList.add(person);
            }
        } catch (SQLException e){
            Logger.getLogger(DBAddressBook.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void initListener(Label label){
        label.setText(String.valueOf(personList.size()));
        personList.addListener(new ListChangeListener<Person>() {
            @Override
            public void onChanged(Change<? extends Person> c) {
                label.setText(String.valueOf(personList.size()));
            }
        });
    }
}
