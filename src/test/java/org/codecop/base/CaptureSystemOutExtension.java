package org.codecop.base;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class CaptureSystemOutExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private PrintStream out;
    private ByteArrayOutputStream stdOut;

    @Override
    public void beforeEach(ExtensionContext context) {
        out = System.out;
        stdOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(stdOut));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        System.setOut(out);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.isAnnotated(SystemOut.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Class<?> parameterType = parameterContext.getParameter().getType();
        if (parameterType != Supplier.class) {
            throw new ExtensionConfigurationException("Can only resolve @SystemOut parameter of type "
                    + Supplier.class.getName() + " but was: " + parameterType.getName());
        }
        return new Supplier<String>() {
            @Override
            public String get() {
                return stdOut.toString().replaceAll("\r\n", "\n");
            }
        };
    }

}
