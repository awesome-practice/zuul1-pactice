package com.practice.zuul1.gateway.metrics;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * use reflection, but recommend to use create the same package class as AbstractCommand
 *
 * @author Luo Bao Ding
 * @since 2018/12/8
 */
public class SemaphoreMetric {
    private static final Logger LOGGER = LoggerFactory.getLogger(SemaphoreMetric.class);
    private static Class<?> classTryableSemaphoreActual;
    private static Method methodGetNumberOfPermitsUsed;
    private static Field fieldNumberOfPermits;
    private static ConcurrentHashMap<String, ?> executionSemaphorePerCircuit;

    static {
        try {
            Class<? super HystrixCommand> classAbstractCommand = HystrixCommand.class.getSuperclass();
            Field fieldExecutionSemaphorePerCircuit = classAbstractCommand.getDeclaredField("executionSemaphorePerCircuit");
            fieldExecutionSemaphorePerCircuit.setAccessible(true);
            executionSemaphorePerCircuit = (ConcurrentHashMap<String, ?>) fieldExecutionSemaphorePerCircuit.get(classAbstractCommand);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("init SemaphoreMetric exception", e);
        }

    }

    public void logSemaphore() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Enumeration<String> keys = executionSemaphorePerCircuit.keys();
        while (keys.hasMoreElements()) {
            String commandName = keys.nextElement();
            Object tryableSemaphoreActual = executionSemaphorePerCircuit.get(commandName);//TryableSemaphoreActual
            if (classTryableSemaphoreActual == null) {
                initTryableSemaphoreActualReflection(tryableSemaphoreActual);
            }
            //get maxSemaphore
            HystrixProperty hystrixPropertyNumberOfPermits = (HystrixProperty) fieldNumberOfPermits.get(tryableSemaphoreActual);
            Object maxSemaphore = hystrixPropertyNumberOfPermits.get();

            //get busySemaphore
            Object busySemaphore = methodGetNumberOfPermitsUsed.invoke(tryableSemaphoreActual);

            //log
            String jsonLine = new StringJoiner(",", "{", "}")
                    .add("\"commandName\":" + "\"" + commandName + "\"")
                    .add("\"busySemaphore\":" + busySemaphore)
                    .add("\"maxSemaphore\":" + maxSemaphore)
                    .toString();
            LOGGER.info("line = " + jsonLine);

        }

    }

    private void initTryableSemaphoreActualReflection(Object tryableSemaphoreActual) throws NoSuchMethodException, NoSuchFieldException {
        //init class: TryableSemaphoreActual
        classTryableSemaphoreActual = tryableSemaphoreActual.getClass();

        //init method: TryableSemaphoreActual.getNumberOfPermitsUsed
        methodGetNumberOfPermitsUsed = classTryableSemaphoreActual.getMethod("getNumberOfPermitsUsed");
        methodGetNumberOfPermitsUsed.setAccessible(true);

        //init field: TryableSemaphoreActual.numberOfPermits
        fieldNumberOfPermits = classTryableSemaphoreActual.getDeclaredField("numberOfPermits");
        fieldNumberOfPermits.setAccessible(true);
    }

}
