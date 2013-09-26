package org.solovyev.android.messenger.accounts.connection;

import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.solovyev.android.messenger.accounts.Account;
import org.solovyev.android.messenger.accounts.AccountConnectionException;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class LoopedAccountConnectionTest {

	public static final int LOOP_TIME = 50;

	@Test
	public void testShouldRunInfinitely() throws Exception {
		final TestLoopedConnection connection = new TestLoopedConnection(10);
		try {
			connection.start();
			fail();
		} catch (AccountConnectionException e) {
			assertEquals("test", e.getAccountId());
		}
	}

	@Test
	public void testShouldStopOnStop() throws Exception {
		int maxCount = 20;
		final TestLoopedConnection connection = new TestLoopedConnection(maxCount);
		newScheduledThreadPool(1).schedule(new Runnable() {
			@Override
			public void run() {
				connection.stop();
			}
		}, maxCount * LOOP_TIME / 2, MILLISECONDS);

		connection.start();
		assertTrue(connection.isStopped());
		assertTrue(connection.count < maxCount);
		assertTrue(connection.disconnectCalled);
	}

	@Test
	public void testShouldContinueWorkAfterInterruption() throws Exception {
		int maxCount = 20;
		final TestLoopedConnection connection = new TestLoopedConnection(maxCount);
		final Thread connectionThread = Thread.currentThread();
		newScheduledThreadPool(1).schedule(new Runnable() {
			@Override
			public void run() {
				connectionThread.interrupt();
			}
		}, maxCount * LOOP_TIME / 2, MILLISECONDS);

		try {
			connection.start();
			fail();
		} catch (AccountConnectionException e) {
			assertEquals("test", e.getAccountId());
		}

		assertFalse(connection.isStopped());
		assertTrue(connection.count == maxCount);
		assertFalse(connection.disconnectCalled);
	}

	@Test
	public void testDisconnectShouldBeCalledOnStop() throws Exception {

	}

	private static final class TestLoopedConnection extends LoopedAccountConnection {

		private final int maxCount;
		private int count;
		private volatile boolean disconnectCalled = false;

		protected TestLoopedConnection(int maxCount) {
			super(mock(Account.class), mock(Context.class), false, LOOP_TIME);
			this.maxCount = maxCount;
		}

		@Override
		protected void disconnect() {
			disconnectCalled = true;
		}

		@Override
		protected void reconnectIfDisconnected() throws AccountConnectionException {
			count++;
			if(count >= maxCount) {
				throw new AccountConnectionException("test");
			}
		}
	}
}
