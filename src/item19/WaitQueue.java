package com.khc.practice.effectivejava.ch04.item19;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

public class WaitQueue {

	private final Queue<Integer> queue = new ArrayDeque<>();

	public void enqueue(Integer element) {
		enqueueHelper(element);
	}

	public void enqueueAll(Collection<Integer> produce) {
		enqueueAllHelper(produce);
	}

	private void enqueueHelper(Integer element) {
		queue.add(element);
	}

	private void enqueueAllHelper(Collection<Integer> produce) {
		produce.forEach(this::enqueueHelper);
	}
}

class WaitCountQueue extends WaitQueue {

	private int count;

	public void enqueue(Integer element) {
		count++;
		super.enqueue(element);
	}

	public void enqueueAll(Collection<Integer> produce) {
		count += produce.size();
		super.enqueueAll(produce);
	}

	public int getCount() {
		return count;
	}

	public static void main(String[] args) {
		WaitCountQueue wcq = new WaitCountQueue();
		wcq.enqueueAll(List.of(1, 2, 3));
		System.out.println(wcq.getCount()); // 6이 튀어나옴
	}
}
