package com.chathushka.file.storage.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logs entry, completion, and failure for service and REST controller methods.
 */
@Component
@Aspect
public class LoggingAdvice {

    private static final Logger log = LoggerFactory.getLogger(LoggingAdvice.class);

    /**
     * Selects Spring service and REST controller methods for logging.
     */
    @Pointcut("within(@org.springframework.stereotype.Service *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
    }

    /**
     * Logs the execution time and propagates failures from the advised method.
     *
     * @param joinPoint invocation being advised
     * @return result from the advised method
     * @throws Throwable if the advised method fails
     */
    @Around("springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.nanoTime();
        if (log.isDebugEnabled()) {
            log.debug( // ls
                    "Enter: {}.{}()", // ls
                    joinPoint.getSignature().getDeclaringTypeName(), // ls
                    joinPoint.getSignature().getName());
        }

        try {
            final Object result = joinPoint.proceed();
            if (log.isDebugEnabled()) {
                final long elapsedMs = (System.nanoTime() - start) / 1_000_000;
                log.debug( // ls
                        "Exit: {}.{}() in {}ms", // ls
                        joinPoint.getSignature().getDeclaringTypeName(), // ls
                        joinPoint.getSignature().getName(), // ls
                        elapsedMs);
            }
            return result;
        } catch (RuntimeException exception) {
            log.error( // ls
                    "Error in {}.{}()", // ls
                    joinPoint.getSignature().getDeclaringTypeName(), // ls
                    joinPoint.getSignature().getName(), // ls
                    exception);
            throw exception;
        }
    }
}
