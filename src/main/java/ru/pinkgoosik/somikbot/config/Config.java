package ru.pinkgoosik.somikbot.config;

public class Config {

    public Secrets secrets;

    public Config(){
        this.secrets = Secrets.init();
    }
}
