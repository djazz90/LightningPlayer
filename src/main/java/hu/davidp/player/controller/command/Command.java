package hu.davidp.player.controller.command;

public interface Command {
    void execute();
    void addCommand(Runnable runnable);
}
