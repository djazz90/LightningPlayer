package hu.davidp.beadando.player.controller.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActionCommand implements Command {
    private final List<Runnable> runnables;

    public ActionCommand(final Runnable runnable, final Runnable... runnableArray) {
        runnables = new ArrayList<>();
        runnables.add(runnable);
        runnables.addAll(Arrays.asList(runnableArray));
    }

    @Override
    public void execute() {
        for (Runnable runnable : runnables) {
            runnable.run();
        }
    }

    @Override
    public void addCommand(final Runnable runnable) {
        runnables.add(runnable);
    }
}
