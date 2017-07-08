package com.util.base;

import com.util.base.ExceptionUtil.*;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.*;
/**
 * <p>Description:</p>
 *
 * @author xinjian.wu
 * @date 2017-07-08
 */
public class ExceptionUtilTest {

    private static RuntimeException TIMEOUT_EXCEPTION = ExceptionUtil.setStackTrace(new RuntimeException("Timeout"),
            ExceptionUtilTest.class, "hello");

    private static CloneableException TIMEOUT_EXCEPTION2 = new CloneableException("Timeout")
            .setStackTrace(ExceptionUtilTest.class, "hello");

    private static CloneableRuntimeException TIMEOUT_EXCEPTION3 = new CloneableRuntimeException("Timeout")
            .setStackTrace(ExceptionUtilTest.class, "hello");

    @Test
    public void unchecked() {
        // convert Exception to RuntimeException with cause
        Exception exception = new Exception("my exception");
        try {
            ExceptionUtil.unchecked(exception);
            fail("should fail before");
        } catch (Throwable t) {
            assertThat(t.getCause()).isSameAs(exception);
        }

        // do nothing of Error
        Error error = new LinkageError();
        try {
            ExceptionUtil.unchecked(error);
            fail("should fail before");
        } catch (Throwable t) {
            assertThat(t).isSameAs(error);
        }

        // do nothing of RuntimeException
        RuntimeException runtimeException = new RuntimeException("haha");
        try {
            ExceptionUtil.unchecked(runtimeException);
            fail("should fail before");
        } catch (Throwable t) {
            assertThat(t).isSameAs(runtimeException);
        }
    }

    @Test
    public void unwrap() {
        RuntimeException re = new RuntimeException("my runtime");
        assertThat(ExceptionUtil.unwrap(re)).isSameAs(re);

        ExecutionException ee = new ExecutionException(re);
        assertThat(ExceptionUtil.unwrap(ee)).isSameAs(re);

        InvocationTargetException ie = new InvocationTargetException(re);
        assertThat(ExceptionUtil.unwrap(ie)).isSameAs(re);

        Exception e = new Exception("my exception");
        ExecutionException ee2 = new ExecutionException(e);
        try{
            ExceptionUtil.uncheckedAndWrap(ee2);
        }catch (Throwable t) {
            assertThat(t).isInstanceOf(UncheckedException.class).hasCauseExactlyInstanceOf(Exception.class);
        }
    }

    @Test
    public void getStackTraceAsString() {
        Exception exception = new Exception("my exception");
        RuntimeException runtimeException = new RuntimeException(exception);

        String stack = ExceptionUtil.stackTraceText(runtimeException);
        System.out.println(stack);
    }

    @Test
    public void isCausedBy() {
        IOException ioexception = new IOException("my exception");
        IllegalStateException illegalStateException = new IllegalStateException(ioexception);
        RuntimeException runtimeException = new RuntimeException(illegalStateException);

        assertThat(ExceptionUtil.isCausedBy(runtimeException, IOException.class)).isTrue();
        assertThat(ExceptionUtil.isCausedBy(runtimeException, IllegalStateException.class, IOException.class)).isTrue();
        assertThat(ExceptionUtil.isCausedBy(runtimeException, Exception.class)).isTrue();
        assertThat(ExceptionUtil.isCausedBy(runtimeException, IllegalAccessException.class)).isFalse();
    }

    @Test
    public void getRootCause() {
        IOException ioexception = new IOException("my exception");
        IllegalStateException illegalStateException = new IllegalStateException(ioexception);
        RuntimeException runtimeException = new RuntimeException(illegalStateException);

        assertThat(ExceptionUtil.getRootCause(runtimeException)).isSameAs(ioexception);
        // 无cause
        assertThat(ExceptionUtil.getRootCause(ioexception)).isSameAs(ioexception);
    }

    @Test
    public void buildMessage() {
        IOException ioexception = new IOException("my exception");
        assertThat(ExceptionUtil.toStringWithShortName(ioexception)).isEqualTo("IOException: my exception");
        assertThat(ExceptionUtil.toStringWithShortName(null)).isEqualTo("");

        RuntimeException runtimeExcetpion = new RuntimeException("my runtimeException", ioexception);
        assertThat(ExceptionUtil.toStringWithRootCause(runtimeExcetpion))
                .isEqualTo("RuntimeException: my runtimeException; <---IOException: my exception");

        assertThat(ExceptionUtil.toStringWithRootCause(null)).isEqualTo("");
        // 无cause
        assertThat(ExceptionUtil.toStringWithRootCause(ioexception)).isEqualTo("IOException: my exception");
    }

    @Test
    public void clearStackTrace() {
        IOException ioexception = new IOException("my exception");
        RuntimeException runtimeException = new RuntimeException(ioexception);

        System.out.println(ExceptionUtil.stackTraceText(ExceptionUtil.clearStackTrace(runtimeException)));

    }

    @Test
    public void staticException() {
        String packageName = "com.util.base.ExceptionUtil";
        String message = "at com.util.base.ExceptionUtilTest.hello(Unknown Source)";
        assertThat(ExceptionUtil.stackTraceText(TIMEOUT_EXCEPTION)).hasLineCount(2)
                .contains("java.lang.RuntimeException: Timeout")
                .contains(message);

        assertThat(ExceptionUtil.stackTraceText(TIMEOUT_EXCEPTION2)).hasLineCount(2)
                .contains(packageName + "$CloneableException: Timeout")
                .contains(message);

        CloneableException timeoutException = TIMEOUT_EXCEPTION2.clone("Timeout for 30ms");
        assertThat(ExceptionUtil.stackTraceText(timeoutException)).hasLineCount(2)
                .contains(packageName + "$CloneableException: Timeout for 30ms")
                .contains(message);

        assertThat(ExceptionUtil.stackTraceText(TIMEOUT_EXCEPTION3)).hasLineCount(2)
                .contains(packageName + "$CloneableRuntimeException: Timeout")
                .contains(message);

        CloneableRuntimeException timeoutRuntimeException = TIMEOUT_EXCEPTION3.clone("Timeout for 40ms");
        assertThat(ExceptionUtil.stackTraceText(timeoutRuntimeException)).hasLineCount(2)
                .contains(packageName + "$CloneableRuntimeException: Timeout for 40ms")
                .contains(message);
    }
}