package com.corvindevelop.redpill.core;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** Reproduces the original BluePill sequence and cadence in Java. */
public final class MouseActivityService implements AutoCloseable {
    private static final int BLUEPILL_STEPS = 50;
    private static final int BLUEPILL_Y_STEP = 4;
    // PyAutoGUI's default PAUSE is 0.1 s after each public action.
    private static final int PY_AUTO_GUI_PAUSE_MS = 100;
    private static final DateTimeFormatter LOG_TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSS");

    private final Robot robot;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean active = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final AtomicLong actionCount = new AtomicLong(0);

    private volatile ScheduledFuture<?> scheduledTask;
    private volatile Duration interval = Duration.ofSeconds(20);
    private volatile ActivityListener listener = new NoOpListener();
    private volatile HumanActivityMonitor humanActivityMonitor;

    public MouseActivityService() throws AWTException {
        this(new Robot());
    }

    MouseActivityService(Robot robot) {
        this.robot = Objects.requireNonNull(robot);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new RedPillThreadFactory());
    }

    public void setHumanActivityMonitor(HumanActivityMonitor monitor) {
        this.humanActivityMonitor = monitor;
    }

    public synchronized void start(Duration interval) {
        Objects.requireNonNull(interval, "interval");
        if (interval.isNegative() || interval.isZero()) {
            throw new IllegalArgumentException("Interval must be greater than zero.");
        }

        stopScheduledTask();
        this.interval = interval;
        resetCounter();
        paused.set(false);
        active.set(true);

        HumanActivityMonitor monitor = humanActivityMonitor;
        if (monitor != null) {
            monitor.setArmed(true);
            // Ignore the mouse/key event used to press START, not the automatic sequence itself.
            monitor.ignoreFor(Duration.ofMillis(650));
        }

        listener.onStateChanged(true);
        scheduleNext(0L);
    }

    public synchronized void stop() {
        boolean wasActive = active.getAndSet(false);
        HumanActivityMonitor monitor = humanActivityMonitor;
        if (monitor != null) {
            monitor.setArmed(false);
        }
        paused.set(false);
        stopScheduledTask();
        resetCounter();
        if (wasActive) {
            listener.onStateChanged(false);
        }
    }

    public synchronized boolean pauseForHumanActivity() {
        if (!active.get() || paused.getAndSet(true)) {
            return false;
        }
        stopScheduledTask();
        return true;
    }

    public synchronized void resumeAfterHumanActivity() {
        if (!active.get() || !paused.getAndSet(false)) {
            return;
        }
        HumanActivityMonitor monitor = humanActivityMonitor;
        if (monitor != null) {
            monitor.ignoreFor(Duration.ofMillis(650));
        }
        scheduleNext(Math.max(interval.toMillis(), 1_000L));
    }

    public boolean isActive() {
        return active.get();
    }

    public boolean isPaused() {
        return paused.get();
    }

    public long getActionCount() {
        return actionCount.get();
    }

    public void setListener(ActivityListener listener) {
        this.listener = listener == null ? new NoOpListener() : listener;
    }

    private synchronized void scheduleNext(long initialDelayMillis) {
        if (!active.get() || paused.get()) {
            return;
        }
        stopScheduledTask();
        scheduledTask = scheduler.scheduleWithFixedDelay(
                this::actionSafely,
                initialDelayMillis,
                Math.max(interval.toMillis(), 1_000L),
                TimeUnit.MILLISECONDS
        );
    }

    private void actionSafely() {
        if (!active.get() || paused.get()) {
            return;
        }

        try {
            listener.onLog("Movement start at " + LocalTime.now().format(LOG_TIME));
            performBluePillAction();
            if (!active.get() || paused.get()) {
                return;
            }
            listener.onLog("Movement made at " + LocalTime.now().format(LOG_TIME));
            listener.onLog("---------------------------------");
            long count = actionCount.incrementAndGet();
            listener.onAction(count);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        } catch (Throwable error) {
            stop();
            listener.onError(error);
        }
    }

    private void performBluePillAction() throws InterruptedException {
        for (int i = 0; i < BLUEPILL_STEPS; i++) {
            if (!active.get() || paused.get()) {
                return;
            }
            int y = i * BLUEPILL_Y_STEP;
            expectOwnMouseMove(0, y);
            robot.mouseMove(0, y);
            Thread.sleep(PY_AUTO_GUI_PAUSE_MS);
        }

        if (!active.get() || paused.get()) {
            return;
        }
        expectOwnMouseMove(1, 1);
        robot.mouseMove(1, 1);
        Thread.sleep(PY_AUTO_GUI_PAUSE_MS);

        if (!active.get() || paused.get()) {
            return;
        }
        HumanActivityMonitor monitor = humanActivityMonitor;
        if (monitor != null) {
            monitor.expectShiftPress();
        }
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        Thread.sleep(PY_AUTO_GUI_PAUSE_MS);
    }

    private void expectOwnMouseMove(int x, int y) {
        HumanActivityMonitor monitor = humanActivityMonitor;
        if (monitor != null) {
            monitor.expectMouseMove(x, y);
        }
    }

    private void resetCounter() {
        actionCount.set(0L);
        listener.onAction(0L);
    }

    private void stopScheduledTask() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
            scheduledTask = null;
        }
    }

    @Override
    public void close() {
        stop();
        scheduler.shutdownNow();
    }

    private static final class RedPillThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "redpill-activity");
            thread.setDaemon(true);
            return thread;
        }
    }

    private static final class NoOpListener implements ActivityListener {
        @Override public void onStateChanged(boolean active) { }
        @Override public void onAction(long actionCount) { }
        @Override public void onLog(String message) { }
        @Override public void onError(Throwable error) { }
    }
}
