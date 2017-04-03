package ua.javabegin.javafx.objects;

import java.util.Locale;

public class LocaleManager {

    public static final Locale LOCALE_EN = new Locale("en");
    public static final Locale LOCALE_RU = new Locale("ru");
    public static final Locale LOCALE_UA = new Locale("ua");

    private static Lang currentLang;
    private static Locale currentLocale;

    public static Locale getCurrentLocale() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale currentLocale) {
        LocaleManager.currentLocale = currentLocale;
    }

    public static Lang getCurrentLang() {
        return currentLang;
    }

    public static void setCurrentLang(Lang currentLang) {
        LocaleManager.currentLang = currentLang;
    }
}
