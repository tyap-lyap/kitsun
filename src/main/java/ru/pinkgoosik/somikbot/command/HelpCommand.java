package ru.pinkgoosik.somikbot.command;

public class HelpCommand extends Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Sends list of available commands.";
    }

    @Override
    public String respond(String[] args, String nickname) {
        StringBuilder string = new StringBuilder();
        for (Command command : Commands.COMMANDS){
            string.append(command.appendDescription()).append("\n");
        }
        string.append("Little Tip: Typing `self` instead of full nickname will use your discord username");
        return string.toString();
    }
}
