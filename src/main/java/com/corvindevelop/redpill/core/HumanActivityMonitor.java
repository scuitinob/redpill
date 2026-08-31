package com.corvindevelop.redpill.core;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Detects real global keyboard/mouse activity while filtering RedPill's own Robot events. */
public final class HumanActivityMonitor implements NativeKeyListener, NativeMouseMotionListener, AutoCloseable {
    private final Runnable humanActivityCallback;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean armed = new AtomicBoolean(false);
    private final AtomicBoolean notificationInProgress = new AtomicBoolean(false);
    private final AtomicLong ignoreUntilNanos = new AtomicLong(0L);

    private final AtomicInteger expectedMouseX = new AtomicInteger(Integer.MIN_VALUE);
    private final AtomicInteger expectedMouseY = new AtomicInteger(Integer.MIN_VALUE);
    private final AtomicLong expectedMouseUntilNanos = new AtomicLong(0L);
    private final AtomicLong expectedShiftUntilNanos = new AtomicLong(0L);

    public HumanActivityMonitor(Runnable humanActivityCallback) {
        this.humanActivityCallback = Objects.requireNonNull(humanActivityCallback);
    }

    public synchronized void start() throws NativeHookException {
        if (running.get()) {
            return;
        }

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);

        if (!GlobalScreen.isNativeHookRegistered()) {
            GlobalScreen.registerNativeHook();
        }
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseMotionListener(this);
        running.set(true);
    }

    public void setArmed(boolean armed) {
        this.armed.set(armed);
        if (!armed) {
            notificationInProgress.set(false);
            clearExpectedEvents();
        }
    }

    /** Used around UI transitions such as pressing START or closing the human-activity dialog. */
    public void ignoreFor(Duration duration) {
        long until = System.nanoTime() + Math.max(0L, duration.toNanos());
        ignoreUntilNanos.accumulateAndGet(until, Math::max);
    }

    /** Marks the exact mouse position RedPill is about to generate, so it is not considered human. */
    public void expectMouseMove(int x, int y) {
        expectedMouseX.set(x);
        expectedMouseY.set(y);
        expectedMouseUntilNanos.set(System.nanoTime() + Duration.ofMillis(450).toNanos());
    }

    /** Marks RedPill's own Shift press so it is not considered human keyboard activity. */
    public void expectShiftPress() {
        expectedShiftUntilNanos.set(System.nanoTime() + Duration.ofMillis(500).toNanos());
    }

    public void notificationHandled() {
        notificationInProgress.set(false);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        if (isIgnoredGlobally()) {
            return;
        }
        if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_SHIFT
                && System.nanoTime() <= expectedShiftUntilNanos.get()) {
            expectedShiftUntilNanos.set(0L);
            return;
        }
        notifyHumanActivity();
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        handleMouse(nativeEvent);
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        handleMouse(nativeEvent);
    }

    private void handleMouse(NativeMouseEvent nativeEvent) {
        if (isIgnoredGlobally()) {
            return;
        }

        long now = System.nanoTime();
        if (now <= expectedMouseUntilNanos.get()
                && nativeEvent.getX() == expectedMouseX.get()
                && nativeEvent.getY() == expectedMouseY.get()) {
            expectedMouseUntilNanos.set(0L);
            return;
        }

        notifyHumanActivity();
    }

    private boolean isIgnoredGlobally() {
        return !running.get() || !armed.get() || System.nanoTime() < ignoreUntilNanos.get();
    }

    private void notifyHumanActivity() {
        if (!running.get() || !armed.get()) {
            return;
        }
        if (!notificationInProgress.compareAndSet(false, true)) {
            return;
        }
        humanActivityCallback.run();
    }

    private void clearExpectedEvents() {
        expectedMouseX.set(Integer.MIN_VALUE);
        expectedMouseY.set(Integer.MIN_VALUE);
        expectedMouseUntilNanos.set(0L);
        expectedShiftUntilNanos.set(0L);
    }

    @Override
    public synchronized void close() {
        if (!running.getAndSet(false)) {
            return;
        }

        GlobalScreen.removeNativeKeyListener(this);
        GlobalScreen.removeNativeMouseMotionListener(this);
        armed.set(false);
        notificationInProgress.set(false);
        clearExpectedEvents();

        if (GlobalScreen.isNativeHookRegistered()) {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException ignored) {
                // Clean shutdown even if the OS hook is already gone.
            }
        }
    }
}
