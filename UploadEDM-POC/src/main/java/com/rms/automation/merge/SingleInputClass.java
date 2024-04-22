package com.rms.automation.merge;

import com.rms.automation.PATEApi.PATETests;
import com.rms.automation.batchApi.BatchTests;
import com.rms.automation.climateChange.ClimateChangeTests;
import com.rms.automation.currencyConverterApi.CurrencyConverter;
import com.rms.automation.edm.LoadData;
import com.rms.automation.exportApi.FileExportTests;
import com.rms.automation.exportApi.export;
import com.rms.automation.mriImportApi.MRIImportTests;
import com.rms.automation.renameAnalysisApi.RenameAnalysis;
import com.rms.automation.UploadEdmApi.UploadEDM;
import com.rms.automation.utils.Utils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

//import static automation.mriImport.MRIImportTests.portfolioId_created;

public class SingleInputClass {

    //public static String caseNumber="";
    @DataProvider(name = "loadFromCSV")
    public Object[] provider() throws Exception {
        return LoadData.readCaseTCFromLocalExcel();


    }

    @Test(dataProvider = "loadFromCSV")
    public void executeSingleInputCsv(Map<String, String> tc) throws InterruptedException {

       // Thread thread = new Thread(() -> {
            uploadOrImportEdm(tc);
      // });

    //   thread.start();
   //   thread.join();

    }

    public void uploadOrImportEdm(Map<String, String> tc) {
        if (tc != null) {
            try {
                System.out.println("Test Case No: " + tc.get("caseNo"));
                if (Utils.isTrue(tc.get("ifRun"))) {
                    if (Utils.isTrue(tc.get("isRunUploadImportExpo"))) {
                        switch (tc.get("ifUploadImportExpo").toUpperCase()) {
                            case "IMPORT":
                                MRIImportTests mriImportTests = new MRIImportTests();
                                if (tc.get("ifCreateEdm").equalsIgnoreCase("YES")) {
                                    mriImportTests.MRIImport(tc, true);
                                } else {
                                    mriImportTests.MRIImport(tc, false);
                                }
                                break;

                            case "UPLOAD":

                                UploadEDM uploadEDM = new UploadEDM();
                                uploadEDM.executeUploadEdm(tc);
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
                        String portfolioId = tc.get("existingPortfolioId");
                        String dataSourceName = tc.get("edmDatasourceName");
                        BatchTests batchTests = new BatchTests();
                        batchTests.batchAPI(tc, portfolioId, dataSourceName);
                    }
                } else {
                    System.out.println("ifRun for this test case is set to NO");
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}

