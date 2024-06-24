package org.example.arex.plugin;

import com.ly.tcbase.config.ConfigCenterClient;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.time.Clock;
import java.util.Calendar;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigCenterClientInstrumentationTest {

    @Test
    void typeMatcher() {
        ConfigCenterClientInstrumentation inst = new ConfigCenterClientInstrumentation();
        assertTrue(inst.typeMatcher().matches(TypeDescription.ForLoadedType.of(ConfigCenterClient.class)));

    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("methodAdvicesArguments")
    void methodAdvices(ConfigCenterClientInstrumentation inst, Method method) throws NoSuchMethodException {
        assertTrue(
                inst.methodAdvices().get(0).getMethodMatcher().matches(new MethodDescription.ForLoadedMethod(method)));
    }

    static Stream<Arguments> methodAdvicesArguments() throws NoSuchMethodException {
        ConfigCenterClientInstrumentation calendarInst = new ConfigCenterClientInstrumentation();
        Method calendarMethod = ConfigCenterClient.class.getDeclaredMethod("get", String.class);


        return Stream.of(
                Arguments.arguments(calendarInst, calendarMethod)
        );
    }
}
