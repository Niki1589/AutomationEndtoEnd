package com.rms.automation.TCRunner;

import com.rms.automation.PATEApi.PATETests;
import com.rms.automation.batchApi.BatchTests;
import com.rms.automation.climateChange.ClimateChangeTests;
import com.rms.automation.currencyConverterApi.CurrencyConverter;
import com.rms.automation.dataProviders.LoadData;
import com.rms.automation.exportApi.export;
import com.rms.automation.mriImportApi.MRIImportTests;
import com.rms.automation.renameAnalysisApi.RenameAnalysis;
import com.rms.automation.Upload.UploadEDM;
import com.rms.automation.utils.Utils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.Map;

public class TCRunner {

    @DataProvider(name = "loadFromExcel",parallel = true)
    public Object[] provider() throws Exception {
        return LoadData.readCaseTCFromLocalExcel();
    }

    @Test(dataProvider = "loadFromExcel")
    public void execute(Map<String, String> tc) throws Exception {

      Thread thread = new Thread(() -> {
            runSelectively(tc);
      });

     thread.start();
     thread.join();

    }

    public void runSelectively(Map<String, String> tc) {
        if (tc != null) {
            try {
                if (Utils.isTrue(tc.get("IF_TEST_CASE_RUN"))) {
                    System.out.println("Test Case No: " + tc.get("TEST_CASE_NO"));
                    if (Utils.isTrue(tc.get("EXP_IS_RUN_UPLOAD_IMPORT"))) {
                        switch (tc.get("EXP_IF_UPLOAD_OR_IMPORT").toUpperCase()) {
                            case "IMPORT":
                                MRIImportTests mriImportTests = new MRIImportTests();
                                Boolean isCreateEDM = tc.get("EXP_IF_CREATE_EDM").equalsIgnoreCase("YES");
                                mriImportTests.MRIImport(tc, isCreateEDM);
                                break;
                            case "UPLOAD":
                                UploadEDM.executeUploadEdm(tc);
                                break;

                            case "DOWNSTREAM":
                                if (Utils.isTrue(tc.get("if_rdm_export"))) {
                                    export.exportType(tc, tc.get("analysisId"));
                                }
                                if (Utils.isTrue(tc.get("isConvertCurrency"))) {
                                    CurrencyConverter.convert(tc, tc.get("analysisId"));
                                }
                                if (Utils.isTrue(tc.get("isRenameAnalysis"))) {
                                    RenameAnalysis.rename(tc, tc.get("analysisId"));
                                }
                                if (Utils.isTrue(tc.get("isPate"))) {
                                    PATETests.executePATETests(tc.get("caseNo"), tc.get("analysisId"));
                                }
                                if (Utils.isTrue(tc.get("is_ClimateChange"))) {
                                    ClimateChangeTests.climateChange(tc, tc.get("analysisId"));
                                }
                                break;
                            default:
                                break;
                        }
                    } else {
                        String portfolioId = tc.get("EXP_EXISTING_PORTFOLIO_ID");
                        String dataSourceName = tc.get("EXP_EDM_DATASOURCE_NAME");
                        BatchTests.batchAPI(tc, portfolioId, dataSourceName);
                    }
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

}

