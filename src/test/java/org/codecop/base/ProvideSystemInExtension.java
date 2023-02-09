package org.codecop.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.function.Consumer;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class ProvideSystemInExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final int BUFFER_SIZE = 8 * 1024;

    private InputStream in;
    private ByteArrayOutputStream stdIn;

    @Override
    public void beforeEach(ExtensionContext context) {
        in = System.in;
        System.setIn(new ByteArrayInputStream(new byte[0]));
        stdIn = new ByteArrayOutputStream(BUFFER_SIZE);
    }

    private void updateSystemIn() {
        byte[] writtenBytes = stdIn.toByteArray();
        System.setIn(new ByteArrayInputStream(writtenBytes));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        System.setIn(in);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.isAnnotated(SystemIn.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        if (parameterType != Consumer.class) {
            throw new ExtensionConfigurationException("Can only resolve @SystemIn parameter of type "
                    + Consumer.class.getName() + " but was: " + parameterType.getName());
        }
        return new Consumer<String>() {
            @Override
            public void accept(String line) {
                new PrintStream(stdIn).println(line);
                updateSystemIn();
            }
        };
    }

}
