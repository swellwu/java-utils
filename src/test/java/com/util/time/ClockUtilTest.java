package com.util.time;

import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by wuxinjian on 2017/7/7.
 */
public class ClockUtilTest {
    @Test
    public void testDummyClock() {
        ClockUtil.DummyClock clock = new ClockUtil.DummyClock();
        clock.updateNow(111);
        assertThat(clock.currentTimeMillis()).isEqualTo(111);
        assertThat(clock.currentDate().getTime()).isEqualTo(111);

        clock.updateNow(new Date(112));
        assertThat(clock.currentTimeMillis()).isEqualTo(112);
        clock.increaseTime(200);
        assertThat(clock.currentTimeMillis()).isEqualTo(312);
        clock.decreaseTime(100);
        assertThat(clock.currentTimeMillis()).isEqualTo(212);

        clock.setNanoTime(150);
        assertThat(clock.nanoTime()).isEqualTo(150);

    }

    @Test
    public void elapsedTime() {
        try {
            ClockUtil.DummyClock clock = ClockUtil.useDummyClock(2000);
            clock.increaseTime(1000);
            assertThat(ClockUtil.elapsedTime(2000)).isEqualTo(1000);
        } finally {
            ClockUtil.useDefaultClock();
        }
    }
}