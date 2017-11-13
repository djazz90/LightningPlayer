package hu.davidp.player.test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import javafx.application.Application;
import javafx.stage.Stage;

public class JavaFxJUnit4Application extends Application {

	private static final ReentrantLock LOCK = new ReentrantLock();

	private static AtomicBoolean started = new AtomicBoolean();

	public static void startJavaFx() {
		try {

			LOCK.lock();

			if (!started.get()) {

				final ExecutorService executor = Executors.newSingleThreadExecutor();
				executor.execute(new Runnable() {
					@Override
					public void run() {
						JavaFxJUnit4Application.launch();
					}
				});

				while (!started.get()) {
					Thread.yield();
				}
			}
		} finally {
			LOCK.unlock();
		}
	}

	protected static void launch() {
		Application.launch();
	}

	@Override
	public void start(final Stage stage) throws IOException {
		started.set(Boolean.TRUE);
	}
}
