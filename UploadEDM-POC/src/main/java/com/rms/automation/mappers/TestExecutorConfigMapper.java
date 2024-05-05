package com.rms.automation.mappers;

import com.rms.automation.enums.UIEEnums;
import com.rms.automation.objects.TestExecutorConfig;
import com.rms.automation.utils.Utils;

import java.util.Map;

public class TestExecutorConfigMapper {

    public static TestExecutorConfig map(Map<String, String> tc) {

        TestExecutorConfig testExecutorConfig = new TestExecutorConfig();

        testExecutorConfig.setRun(Utils.isTrue(tc.get("ifRun")));
        testExecutorConfig.setTestNo(tc.get("caseNo"));
        testExecutorConfig.setRunUploadImportExpo(Utils.isTrue(tc.get("isRunUploadImportExpo")));
        testExecutorConfig.setIfUploadImportExpo(UIEEnums.fromString(tc.get("ifUploadImportExpo")));

        return testExecutorConfig;
    }

}
