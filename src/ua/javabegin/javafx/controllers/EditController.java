package ua.javabegin.javafx.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ua.javabegin.javafx.objects.LocaleManager;
import ua.javabegin.javafx.objects.Person;
import ua.javabegin.javafx.start.Main;
import java.io.IOException;
import java.util.ResourceBundle;

public class EditController {

    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private TextField fieldName;
    @FXML
    private TextField fieldAddress;
    @FXML
    private TextField fieldPhone;
    private Stage stage;

    private Person person;
    private Parent fxmlEdit;
    private Window windowOwner;
    private static final String FXML_EDIT = "../fxml/edit.fxml";
    private boolean saveClicked = false; //для определения нажатой кнопки


    public void showDialog(Window window, Parent fxmlEdit, FXMLLoader fxmlLoader){
        this.windowOwner = window;
        stage = new Stage();
        stage.setTitle("Edit Note");
        stage.setMinHeight(200);
        stage.setMaxHeight(200);
        stage.setMinWidth(500);
        stage.setScene(new Scene(fxmlEdit));
        stage.initOwner(windowOwner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(fxmlLoader.getResources().getString("title.editNoteWindow"));
        fieldNameListener();
        stage.showAndWait();
    }

    public EditController initLoader(){
        Parent fxmlEdit = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(FXML_EDIT));
        fxmlLoader.setResources(ResourceBundle.getBundle(Main.BUNDLES_FOLDER, LocaleManager.getCurrentLang().getLocale()));
        try {
            fxmlEdit = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.fxmlEdit = fxmlEdit;
        return fxmlLoader.getController();
    }


    public void setPerson(Person person){
        this.person = person;
        fieldName.setText(person.getName());
        fieldAddress.setText(person.getAddress());
        fieldPhone.setText(person.getPhone());
    }

    private void updatePerson(String name, String address, String phone){
        person.setName(name);
        person.setAddress(address);
        person.setPhone(phone);
    }

    public Person getPerson() {
        return person;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    public TextField getFieldName() {
        return fieldName;
    }

    public TextField getFieldAddress() {
        return fieldAddress;
    }

    public TextField getFieldPhone() {
        return fieldPhone;
    }

    public void saveNote() {
        String name = getFieldName().getText().trim();
        String address = getFieldAddress().getText().trim();
        String phone = getFieldPhone().getText().trim();
        if (person == null && !name.isEmpty()) {
            person = new Person();
        }
        updatePerson(name, address, phone);
        saveClicked = true;
        closeWindow();
    }

    public void closeWindow() {
        stage.close();
    }

    public void pressSave(KeyEvent event) {
        if(btnSave.isPressed() || event.getCode() == KeyCode.ENTER){
            saveNote();
        }
    }

    public void pressCancel(KeyEvent event) {
        if(btnCancel.isPressed() || event.getCode() == KeyCode.ENTER){
            closeWindow();
        }
    }

    private void fieldNameListener() {
        if (getFieldName().getText().trim().isEmpty()) btnSave.setDisable(true);
        fieldName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (getFieldName().getText().trim().isEmpty()) {
                    btnSave.setDisable(true);
                } else {
                    btnSave.setDisable(false);
                }
            }
        });
    }

    /*
    public void saveNote() {
        String name = getFieldName().getText();
        String address = getFieldAddress().getText();
        String phone = getFieldPhone().getText();
        Person per = new Person(name, address, phone);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning");
        if (!(person == null)) {
            updatePerson(name, address, phone);
        } else if (!name.equals("")) {
            boolean found = false;
            for (Person p : dbAddressBook.searchAll()) {
                if ((p.getName().toLowerCase().equals(name.toLowerCase()) && p.getAddress().toLowerCase().equals(address.toLowerCase()) && phone.isEmpty()) ||
                        (p.getName().toLowerCase().equals(name.toLowerCase()) && p.getPhone().toLowerCase().equals(phone.toLowerCase()) && address.isEmpty()) ||
                        (p.getName().toLowerCase().equals(name.toLowerCase()) && phone.isEmpty() && address.isEmpty()) ||
                        p.equals(per)){
                    found = true;
                    break;
                }
            }
            if (!found) {
                //dbAddressBook.addNote(per);
                saveClicked = true;
                this.person = per;
                closeWindow();
            } else {
                alert.setHeaderText("Such person '" + per.getName() + "' is already present in the address book");
                alert.showAndWait();
            }
        } else if (name.equals("")){
            alert.setHeaderText("Please type a name");
            alert.showAndWait();
        }
    }
    */
}
