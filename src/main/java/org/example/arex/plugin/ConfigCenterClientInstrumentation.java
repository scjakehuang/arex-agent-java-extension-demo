package org.example.arex.plugin;

import io.arex.agent.bootstrap.model.MockResult;
import io.arex.inst.extension.MethodInstrumentation;
import io.arex.inst.extension.TypeInstrumentation;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;

import static java.util.Collections.singletonList;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;

public class ConfigCenterClientInstrumentation extends TypeInstrumentation {
    @Override
    public ElementMatcher<TypeDescription> typeMatcher() {
        // 这里声明的是你要修饰的类的全限定名，不是io.arex*开头的类
        return named("com.ly.tcbase.config.ConfigCenterClient");
    }

    @Override
    public List<MethodInstrumentation> methodAdvices() {
        ElementMatcher<MethodDescription> matcher = named("get")
                .and(takesArgument(0, named("java.lang.String")));

        return singletonList(new MethodInstrumentation(matcher, InvokeAdvice.class.getName()));
    }

    public static class InvokeAdvice {
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class, suppress = Throwable.class)
        public static boolean onEnter(@Advice.Argument(0) String param,
                                      @Advice.Local("mockResult") MockResult mockResult) {
            mockResult = ConfigCenterClientAdvice.replay(param); // 回放
            return mockResult != null && mockResult.notIgnoreMockResult();
        }

        @Advice.OnMethodExit(suppress = Throwable.class)
        public static void onExit(@Advice.Argument(0) String param,
                                  @Advice.Local("mockResult") MockResult mockResult,
                                  @Advice.Return(readOnly = false) String result) {
            if (mockResult != null && mockResult.notIgnoreMockResult()) {
                result = mockResult.getResult()+""; // 使用回放结果
                return;
            }
            ConfigCenterClientAdvice.record(param, result); // 录制
        }
    }
}
