package hu.davidp.beadando.player.controller.command;

public interface Command {
    void execute();
    void addCommand(Runnable runnable);
}
