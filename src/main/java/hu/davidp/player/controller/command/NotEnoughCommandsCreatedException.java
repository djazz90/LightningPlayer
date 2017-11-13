package hu.davidp.player.controller.command;

public class NotEnoughCommandsCreatedException extends RuntimeException {
    public NotEnoughCommandsCreatedException(final String message) {
        super(message);
    }

    public NotEnoughCommandsCreatedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
