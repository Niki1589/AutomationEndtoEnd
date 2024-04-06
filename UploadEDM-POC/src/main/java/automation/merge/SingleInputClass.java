package automation.merge;

import automation.PATE.PATETests;
import automation.batch.BatchTests;
import automation.currencyConverter.CurrencyConverter;
import automation.edm.LoadData;
import automation.export.FileExportTests;
import automation.export.RdmExportTests;
import automation.export.export;
import automation.mriImport.MRIImportTests;
import automation.export.RdmExportTests;
import automation.renameAnalysis.RenameAnalysis;
import automation.tests.UploadEDM;
import automation.utils.Utils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

//import static automation.mriImport.MRIImportTests.portfolioId_created;

public class SingleInputClass {

//public static String caseNumber="";
    @DataProvider(name = "loadFromCSV")
    public Object[] provider() throws Exception {
        return LoadData.readCaseTCFromLocalCSV();


    }
    @Test (dataProvider = "loadFromCSV")
    public void executeSingleInputCsv(Map<String, String> tc) throws InterruptedException {

      Thread thread = new Thread(() -> {
        uploadOrImportEdm(tc);
       //   caseNumber = tc.get("caseNo");
      });

      thread.start();
       thread.join();

    }
    public void uploadOrImportEdm(Map<String, String> tc) {
        if(tc != null) {
            try {
                System.out.println("Test Case No: "+tc.get("caseNo"));
                if (tc.get("ifRun").equalsIgnoreCase("YES")) {


                    if (Utils.isTrue(tc.get("isRunUploadImportExpo"))) {
                        if (tc.get("ifUploadImportExpo").equalsIgnoreCase("Import")) {
                            MRIImportTests mriImportTests = new MRIImportTests();
                            if (tc.get("ifCreateEdm").equalsIgnoreCase("YES")) {
                                mriImportTests.MRIImport(tc, true);
                            } else {
                                mriImportTests.MRIImport(tc, false);
                            }
                        } else if (tc.get("ifUploadImportExpo").equalsIgnoreCase("Upload")) {
                            UploadEDM uploadEDM = new UploadEDM();
                            uploadEDM.executeUploadEdm(tc);
                        }
                        else if(tc.get("ifUploadImportExpo").equalsIgnoreCase("Downstream"))
                        {
                            //export.exportType(tc, tc.get("analysisId"));
                            if (Utils.isTrue(tc.get("isConvertCurrency"))) {
                                CurrencyConverter.convert(tc, tc.get("analysisId"));
                            }
                            if (Utils.isTrue(tc.get("isRenameAnalysis"))) {
                                RenameAnalysis.rename(tc, tc.get("analysisId"));
                            }
                            if (Utils.isTrue(tc.get("isPate"))) {
                                PATETests.executePATETests(tc.get("caseNo"),tc.get("analysisId"));
                            }
                        }
                    } else {

                        String portfolioId = tc.get("existingPortfolioId");
                        String dataSourceName = tc.get("edmDatasourceName");

                        //Batch API call
                        BatchTests batchTests = new BatchTests();
                        batchTests.batchAPI(tc, portfolioId, dataSourceName);

                    }
                }

                else {
                    System.out.println("Test case: " + tc.get("caseNo") + "ifRun is set to NO");
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}
