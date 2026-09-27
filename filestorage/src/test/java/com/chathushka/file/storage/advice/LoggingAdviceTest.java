package com.chathushka.file.storage.advice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests that logging advice returns results and propagates invocation failures.
 */
class LoggingAdviceTest {

    private final LoggingAdvice loggingAdvice = new LoggingAdvice();

    /**
     * Confirms the advice returns the result of the target invocation.
     *
     * @throws Throwable if the advised invocation fails
     */
    @Test
    @DisplayName("Should return the advised method result")
    void shouldReturnAdvisedResult() throws Throwable {
        // prepare //
        final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        final Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.proceed()).thenReturn("result");

        // act //
        final Object result = this.loggingAdvice.logAround(joinPoint);

        // assert //
        assertThat(result).isEqualTo("result");

        // verify //
        verify(joinPoint).proceed();
    }

    /**
     * Confirms runtime failures from the target invocation are propagated.
     *
     * @throws Throwable if the advised invocation fails
     */
    @Test
    @DisplayName("Should propagate a runtime failure from the advised method")
    void shouldPropagateRuntimeFailure() throws Throwable {
        // prepare //
        final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        final Signature signature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        final IllegalStateException expectedException = new IllegalStateException("failure");
        when(joinPoint.proceed()).thenThrow(expectedException);

        // assert //
        assertThatThrownBy(() -> this.loggingAdvice.logAround(joinPoint))
                .isSameAs(expectedException);

        // verify //
        verify(joinPoint).proceed();
    }
}
