package org.example.arex.plugin;

import io.arex.agent.bootstrap.model.MockResult;
import io.arex.agent.bootstrap.model.Mocker;
import io.arex.inst.runtime.context.ContextManager;
import io.arex.inst.runtime.serializer.Serializer;
import io.arex.inst.runtime.util.MockUtils;
import io.arex.inst.runtime.util.TypeUtil;

public class ConfigCenterClientAdvice {

    private static final String methodName = "tcconfigcenter.get";
    /**
     * 录制
     */
    public static void record(String param, Object result) {
        if (ContextManager.needRecord()) {
            Mocker mocker = buildMocker(param);
            mocker.getTargetResponse().setBody(Serializer.serialize(result));
            mocker.getTargetResponse().setType(TypeUtil.getName(result));
            MockUtils.recordMocker(mocker);
        }
    }

    /**
     * 回放
     */
    public static MockResult replay(String param) {
        if (ContextManager.needReplay()) {
            Mocker mocker = buildMocker(param);
            Object result = MockUtils.replayBody(mocker);
            return MockResult.success(result);
        }
        return null;
    }

    private static Mocker buildMocker( String param) {
        Mocker mocker = MockUtils.createDatabase("tcconfigcenter.get");
        mocker.getTargetRequest().setBody(param);
        return mocker;
    }
}
