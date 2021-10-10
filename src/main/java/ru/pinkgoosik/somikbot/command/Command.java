package ru.pinkgoosik.somikbot.command;

public abstract class Command {

    public abstract String getName();

    public abstract String getDescription();

    public String appendDescription(){
        return "`!" + this.getName() + "` - " + this.getDescription();
    }

    public String respond(String[] args, String nickname){
        return null;
    }
}
