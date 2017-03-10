package hu.davidp.beadando.player.test;

import javafx.application.Platform;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.concurrent.CountDownLatch;

public class JavaFxJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	public JavaFxJUnit4ClassRunner(final Class<?> clazz) throws InitializationError {
		super(clazz);

		JavaFxJUnit4Application.startJavaFx();
	}

	@Override
	protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {

		final CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(() -> {

            JavaFxJUnit4ClassRunner.super.runChild(method, notifier);

            latch.countDown();
        });
		try {
			latch.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}
}