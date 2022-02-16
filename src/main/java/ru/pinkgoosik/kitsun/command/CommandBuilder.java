package ru.pinkgoosik.kitsun.command;

public class CommandBuilder {
    String name = "";
    String description = "";
    String requiredPermission = "";
    String args = "";

    Responder responder;

    public static CommandBuilder create(String name) {
        CommandBuilder builder = new CommandBuilder();
        builder.name = name;
        return builder;
    }

    public CommandBuilder description(String description) {
        this.description = description;
        return this;
    }

    public CommandBuilder requires(String permission) {
        this.requiredPermission = permission;
        return this;
    }

    public CommandBuilder args(String args) {
        this.args = args;
        return this;
    }

    public CommandBuilder respond(Responder responder) {
        this.responder = responder;
        return this;
    }

    public Command build() {
        CommandBuilder builder = this;
        return new Command() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public String appendArgs() {
                if(!builder.args.isBlank()) {
                    return " " + args;
                }
                return super.appendArgs();
            }

            @Override
            public void respond(CommandUseContext ctx) {
                if (!requiredPermission.isBlank() && disallowed(ctx, requiredPermission)) return;
                builder.responder.respond(ctx);
            }
        };
    }

    @FunctionalInterface
    public interface Responder {
        void respond(CommandUseContext ctx);
    }
}
