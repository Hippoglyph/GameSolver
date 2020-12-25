package solver.persist;

import java.util.concurrent.CountDownLatch;

public class CountUpDownLatch {
	CountDownLatch latch;
	int count;

	public CountUpDownLatch() {
		latch = new CountDownLatch(1);
		this.count = 0;
	}

	public void await() throws InterruptedException {

		if (count == 0) {
			return;
		}

		latch.await();
	}

	public synchronized void countDown() {

		count--;

		if (count == 0) {
			latch.countDown();
		}
	}

	public synchronized long getCount() {
		return count;
	}

	public synchronized void countUp() {

		if (latch.getCount() == 0) {
			latch = new CountDownLatch(1);
		}

		count++;
	}

	public synchronized void setCount(int count) {

		if (count == 0) {
			if (latch.getCount() != 0) {
				latch.countDown();
			}
		} else if (latch.getCount() == 0) {
			latch = new CountDownLatch(1);
		}

		this.count = count;
	}
}
