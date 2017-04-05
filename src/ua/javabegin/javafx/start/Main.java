package ua.javabegin.javafx.start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ua.javabegin.javafx.controllers.MainController;
import ua.javabegin.javafx.objects.Lang;
import ua.javabegin.javafx.objects.LocaleManager;

import java.io.IOException;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class Main extends Application implements Observer{

    private static final String FXML_MAIN = "../fxml/main.fxml";
    public static final String BUNDLES_FOLDER = "ua.javabegin.javafx.bandles.locale";
    private Stage primaryStage;
    private MainController mainController;
    private FXMLLoader fxmlLoader;
    private VBox vBox = null;

    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        LocaleManager.setCurrentLocale(LocaleManager.LOCALE_EN);
        createGUI(LocaleManager.LOCALE_EN);
    }

    @Override
    public void update(Observable o, Object arg) {
        Lang lang = (Lang) arg;
        System.out.println(lang.toString());
        VBox newNode = loadFXML(lang.getLocale());
        vBox.getChildren().setAll(newNode.getChildren());
    }

    private void createGUI(Locale locale){
        vBox = loadFXML(locale);
        primaryStage.setScene(new Scene(vBox, 400, 500));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.show();
    }

    private VBox loadFXML(Locale locale){
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(FXML_MAIN));
        fxmlLoader.setResources(ResourceBundle.getBundle(BUNDLES_FOLDER, locale));
        VBox vBox = null;
        try {
            vBox = (VBox)fxmlLoader.load();
        } catch (IOException e){
            e.printStackTrace();
        }
        mainController = fxmlLoader.getController();
        mainController.addObserver(this);
        mainController.setMainStage(primaryStage);
        primaryStage.setTitle(fxmlLoader.getResources().getString("title.addressBook"));
        return vBox;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
