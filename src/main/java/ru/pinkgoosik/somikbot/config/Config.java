package ru.pinkgoosik.somikbot.config;

public class Config {
    public static General general;
    public static Secrets secrets;

    public static void initConfig(){
        general = General.init();
        secrets = Secrets.init();
    }
}
