package ua.javabegin.javafx.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.Window;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import ua.javabegin.javafx.interfaces.impls.DBAddressBook;
import ua.javabegin.javafx.objects.DialogManager;
import ua.javabegin.javafx.objects.Lang;
import ua.javabegin.javafx.objects.LocaleManager;
import ua.javabegin.javafx.objects.Person;
import ua.javabegin.javafx.start.Main;

import java.util.ResourceBundle;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;

public class MainController extends Observable implements Initializable {
    @FXML
    public TableColumn<Person, String> columnName;
    @FXML
    public TableColumn<Person, String> columnAddress;
    @FXML
    public TableColumn<Person, String> columnPhone;
    @FXML
    private Button btnAdd;
    @FXML
    private ComboBox<Lang> comboLocales;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnSearch;
    @FXML
    private CustomTextField fieldSearch;
    @FXML
    private MenuItem btnEditNote;
    @FXML
    private MenuItem btnDeleteNote;
    @FXML
    private TableView tableAddressBook;
    @FXML
    public Label labelCount;

    private DBAddressBook dbAddressBook = new DBAddressBook();;
    private Window windowOwner;
    private Stage mainStage;
    private Person person;

    private Parent parent;
    private EditController editController;
    private FXMLLoader fxmlLoader;
    private ResourceBundle resourceBundle;

    private static final String UA_CODE="ua";
    private static final String RU_CODE="ru";
    private static final String EN_CODE="en";
    private static final String FXML_EDIT = "../fxml/edit.fxml";

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
       this.resourceBundle = resources;
       columnName.setCellValueFactory(new PropertyValueFactory<Person, String>("name"));
       columnAddress.setCellValueFactory(new PropertyValueFactory<Person, String>("address"));
       columnPhone.setCellValueFactory(new PropertyValueFactory<Person, String>("phone"));
       tableAddressBook.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
       tableAddressBook.setItems(dbAddressBook.searchAll());
       dbAddressBook.initListener(labelCount);
       setupClearButtonField(fieldSearch);
       fieldSearchListener();
       fillLangComboBox();
    }

    private void initLoader(){
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(FXML_EDIT));
        fxmlLoader.setResources(ResourceBundle.getBundle(Main.BUNDLES_FOLDER, LocaleManager.getCurrentLang().getLocale()));
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.editController = fxmlLoader.getController();
    }

    private void showDialog() {
        initLoader();

        if (person != null) {
            editController.setPerson(person);
        }

        editController.showDialog(windowOwner, parent, fxmlLoader);

        if (editController.isSaveClicked()) {
           if (person == null) {
               person = editController.getPerson();
               if(!dbAddressBook.addNote(person)){
                   DialogManager.showInfoDialog("", person.getName() + " " + resourceBundle.getString("existence"));
                   person = null;
               }
           } else {
               dbAddressBook.updateNote(person);
               person = null;
           }
        }
    }

    public void setWindowOwner(ActionEvent actionEvent){
        Object source = actionEvent.getSource();
        if (source instanceof Node) {
            windowOwner = ((Node)source).getScene().getWindow();
            person = null;
        } else if (source instanceof MenuItem) {
            if (getSelectedRows().size() > 0) {
                windowOwner = ((MenuItem) source).getParentPopup().getOwnerWindow().getScene().getWindow();
                person = getSelectedRows().get(0);
            }
        }
        showDialog();
    }

    private void setupClearButtonField(CustomTextField customTextField){
        try{
            Method m = TextFields.class.getDeclaredMethod("setupClearButtonField", TextField.class, ObjectProperty.class);
            m.setAccessible(true);
            m.invoke(null, customTextField, customTextField.rightProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteSelectedRows() {
        if(getSelectedRows().size() > 0) {
            if (DialogManager.showConfirmDialog(resourceBundle.getString("confirm"), resourceBundle.getString("confirm_delete")).get() == ButtonType.OK) {
                tableAddressBook.requestFocus();
                ArrayList<Person> pers = new ArrayList<>();
                pers.addAll(getSelectedRows());
                StringBuilder ids = new StringBuilder();
                for (Person person : pers) {
                    ids.append(person.getId() + ",");
                }
                String str = ids.toString().substring(0, ids.toString().length()-1);
                dbAddressBook.deleteNote(str, pers);
                tableAddressBook.getSelectionModel().clearSelection();
            }
        }
    }

    public void deleteRows(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source instanceof Button) {
            Button clickedButton = (Button) source;
            if (clickedButton.getId().equals("btnDelete")){
                deleteSelectedRows();
            }
        } else if (source instanceof MenuItem) {
            MenuItem clickedButton = (MenuItem) source;
            if (clickedButton.getId().equals("btnDeleteNote")){
                deleteSelectedRows();
            }
        }

    }

    private ObservableList<Person> getSelectedRows(){
        return tableAddressBook.getSelectionModel().getSelectedItems();
    }

    public void tableMouseClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY){
            if(event.getClickCount() == 2){
                windowOwner = mainStage;
                person = getSelectedRows().get(0);
                showDialog();
            }
        }
    }

    public void pressKey(KeyEvent event) {
        if ((tableAddressBook.isFocused() || btnDelete.isFocused()) && event.getCode() == KeyCode.DELETE) {
            deleteSelectedRows();
            return;
        }

        if (btnDelete.isFocused() && event.getCode() == KeyCode.ENTER) {
            deleteSelectedRows();
            return;
        }

        if (((btnSearch.isFocused() || fieldSearch.isFocused()) && !fieldSearch.getText().trim().isEmpty()) && event.getCode() == KeyCode.ENTER){
            search();
            return;
        }

        if ((btnAdd.isFocused() && event.getCode() == KeyCode.ENTER) || event.getCode() == KeyCode.INSERT){
            windowOwner = mainStage;
            person = null;
            showDialog();
            return;
        }

        if (tableAddressBook.isFocused() && event.getCode() == KeyCode.ENTER) {
            windowOwner = mainStage;
            if (getSelectedRows().size() > 0) {
                person = getSelectedRows().get(0);
            } else {
                person = null;
            }
            showDialog();
            return;
        }
    }

    public void searching(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof Button) {
            Button clickedButton = (Button) source;
            if (clickedButton.getId().equals("btnSearch")){
                search();
            }
        }
    }

    private void search(){
        String search = fieldSearch.getText().trim();
        dbAddressBook.getPersonList().clear();
        if (search.equals("*") || search.equals("%")){
            dbAddressBook.searchAll();
        } else if (!search.isEmpty()){
            dbAddressBook.searchNote(search);
            if(dbAddressBook.getPersonList().size() == 0){
                dbAddressBook.searchAll();
                DialogManager.showInfoDialog("", search + " " + resourceBundle.getString("notExistence"));
            }
        }
    }

    private void fieldSearchListener() {
        if (fieldSearch.getText().trim().isEmpty() || dbAddressBook.getPersonList().size() == 0) {
            btnSearch.setDisable(true);
        }
        fieldSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (fieldSearch.getText().trim().isEmpty() || dbAddressBook.getPersonList().size() == 0) {
                    btnSearch.setDisable(true);
                } else {
                    btnSearch.setDisable(false);
                }
            }
        });
    }

    private void fillLangComboBox() {
        Lang langEN = new Lang(0, EN_CODE, resourceBundle.getString("list.en"), LocaleManager.LOCALE_EN);
        Lang langUA = new Lang(1, UA_CODE, resourceBundle.getString("list.ua"), LocaleManager.LOCALE_UA);
        Lang langRU = new Lang(2, RU_CODE, resourceBundle.getString("list.ru"), LocaleManager.LOCALE_RU);

        comboLocales.getItems().add(langEN);
        comboLocales.getItems().add(langUA);
        comboLocales.getItems().add(langRU);

        if (LocaleManager.getCurrentLang() == null){
            comboLocales.getSelectionModel().select(0);
        } else {
            comboLocales.getSelectionModel().select(LocaleManager.getCurrentLang().getIndex());
        }
        LocaleManager.setCurrentLang(getSelectedItem());
        comboListen();
    }

    private void comboListen(){
        // слушает изменение языка
        comboLocales.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Lang selectedLang = getSelectedItem();
                LocaleManager.setCurrentLang(selectedLang);

                // уведомить всех слушателей, что произошла смена языка
                setChanged();
                notifyObservers(selectedLang);
            }
        });
    }

    private Lang getSelectedItem(){
        return (Lang)comboLocales.getSelectionModel().getSelectedItem();
    }
}
