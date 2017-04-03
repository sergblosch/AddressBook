package ua.javabegin.javafx.interfaces;

import javafx.collections.ObservableList;
import ua.javabegin.javafx.objects.Person;

/**
 * Created by sergey.bloschenko on 05.11.2016.
 */
public interface AddressBook {
    boolean updateNote(Person person);
    boolean addNote(Person person);
    boolean deleteNote(Person person);
    ObservableList<Person> searchAll();
    ObservableList<Person> searchNote(String note);

//    void deleteField(Person person);
//    void deleteAddress(Person address);
//    void deletePhone(Person phone);

}
