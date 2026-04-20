package com.chathushka.file.storage.advice;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAdvice {
  private static final Logger log = LoggerFactory.getLogger(LoggingAdvice.class);

  /** Pointcut for services and Web REST endpoints. */
  @Pointcut(
      "within(@org.springframework.stereotype.Service *)"
          + " || within(@org.springframework.web.bind.annotation.RestController *)")
  public void springBeanPointcut() {}

  /**
   * Advice that logs when a method is entered and exited.
   *
   * @param joinPoint join point for advice
   * @return result
   * @throws Throwable throws IllegalArgumentException
   */
  @Around("springBeanPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.nanoTime();
    if (log.isDebugEnabled()) {
      log.debug(
          "Enter: {}.{}()",
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName());
    }
    try {
      Object result = joinPoint.proceed();
      if (log.isDebugEnabled()) {
        long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        log.debug(
            "Exit: {}.{}() in {}ms",
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            elapsedMs);
      }
      return result;
    } catch (RuntimeException e) {
      log.error(
          "Error in {}.{}()",
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName(),
          e);
      throw e;
    }
  }
}
