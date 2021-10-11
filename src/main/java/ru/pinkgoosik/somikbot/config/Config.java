package ru.pinkgoosik.somikbot.config;

public class Config {

    public static Secrets secrets;

    public static void initConfig(){
        secrets = Secrets.init();
    }
}
