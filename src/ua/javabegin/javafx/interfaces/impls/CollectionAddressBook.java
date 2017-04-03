package ua.javabegin.javafx.interfaces.impls;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import ua.javabegin.javafx.interfaces.AddressBook;
import ua.javabegin.javafx.objects.Person;

public class CollectionAddressBook implements AddressBook {

    //ArrayList<Person> addressBookList = new ArrayList<>();
    private ObservableList<Person> addressBookList = FXCollections.observableArrayList();
    private int size = 0;

    public ObservableList<Person> getAddressBookList() {
        return addressBookList;
    }

    @Override
    public boolean updateNote(Person person) {
        return true;
    }

    @Override
    public boolean addNote(Person person) {
        addressBookList.add(person);
        return true;
    }

    @Override
    public boolean deleteNote(Person person) {
        addressBookList.remove(person);
        return true;
    }

    @Override
    public ObservableList<Person> searchAll() {
        return null;
    }

    public int getIndex(Person person){
        return addressBookList.indexOf(person);
    }

//    @Override
//    public void deleteField(Person person) {
//
//    }
//
//    @Override
//    public void deleteAddress(Person address) {
//
//    }
//
//    @Override
//    public void deletePhone(Person phone) {
//
//    }

    @Override
    public ObservableList<Person> searchNote(String note) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public void print(){
        for (Person person : addressBookList) {
            System.out.println("name > " + person.getName() + " | address > " + person.getAddress() + " | phone > " + person.getPhone());
        }
    }

    public void fillTestData(){
        addNote(new Person("Joan", "Street1", "111"));
        addNote(new Person("Bill", "Street2", "222"));
        addNote(new Person("Sara", "Street3", "333"));
    }

    public void initListener(Label label){
        label.setText(String.valueOf(addressBookList.size()));
        addressBookList.addListener(new ListChangeListener<Person>() {
            @Override
            public void onChanged(Change<? extends Person> c) {
                label.setText(String.valueOf(addressBookList.size()));
            }
        });
    }
}
