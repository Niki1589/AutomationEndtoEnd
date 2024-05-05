package com.rms.automation.tests;

import com.rms.automation.PATEApi.PATETests;
import com.rms.automation.UploadEdmApi.UploadEDM;
import com.rms.automation.batchApi.BatchTests;
import com.rms.automation.climateChange.ClimateChangeTests;
import com.rms.automation.currencyConverterApi.CurrencyConverter;
import com.rms.automation.edm.LoadData;
import com.rms.automation.exportApi.export;
import com.rms.automation.mappers.TestExecutorConfigMapper;
import com.rms.automation.mriImportApi.MRIImportTests;
import com.rms.automation.objects.TestExecutorConfig;
import com.rms.automation.renameAnalysisApi.RenameAnalysis;
import com.rms.automation.utils.Utils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class TestExecutor {

    @DataProvider(name = "data")
    public Object[] provider() throws Exception {
        return LoadData.readCaseTCFromLocalExcel();
    }

    @Test(dataProvider = "data")
    public void executeTest(Map<String, String> tc) {
        TestExecutorConfig testExecutorConfig = TestExecutorConfigMapper.map(tc);
        if (testExecutorConfig.getRun()) {

            try {

                System.out.println("Test Case No: " + testExecutorConfig.getTestNo());

                if (testExecutorConfig.getRunUploadImportExpo()) {
                    switch (testExecutorConfig.getIfUploadImportExpo()) {
                        case IMPORT:
                            MRIImportTests.MRIImport(tc, tc.get("ifCreateEdm").equalsIgnoreCase("YES"));
                            break;
                        case UPLOAD:
                            UploadEDM.executeUploadEdm(tc);
                            break;
                        case DOWNSTREAM:
                            DownstreamExecutor.execute(tc);
                            break;
                        default:
                            break;
                    }
                } else {
                    String portfolioId = tc.get("existingPortfolioId");
                    String dataSourceName = tc.get("edmDatasourceName");
                    BatchTests.batchAPI(tc, portfolioId, dataSourceName);
                }

            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

}
