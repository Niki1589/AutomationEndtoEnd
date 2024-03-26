package automation.merge;

import automation.edm.ApiUtil;
import automation.edm.LoadData;
import automation.edm.TestCase;
import automation.merge.jsonMapper.Perils;
import automation.mriImport.MRIImportTests;
import automation.tests.UploadEDM;
import com.google.gson.Gson;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

//import static automation.mriImport.MRIImportTests.portfolioId_created;

public class SingleInputClass {
    @DataProvider(name = "loadFromCSV")
    public Object[] provider() throws IOException {
        return LoadData.readCaseTCFromLocalCSV();
    }
    @Test (dataProvider = "loadFromCSV")
    public void executeSingleInputCsv(Map<String, String> tc) throws InterruptedException {


//        Thread thread = new Thread(() -> {
        uploadOrImportEdm(tc);
//        });
//
//        thread.start();
//        thread.join();

    }
    public void uploadOrImportEdm(Map<String, String> tc) {
        if(tc != null) {
            try {
                System.out.println("Test Case No: "+tc.get("caseNo"));
                if (tc.get("ifRun").equalsIgnoreCase("YES")) {
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
                } else {
                    System.out.println("Test case: " + tc.get("caseNo") + "ifRun is set to NO");
                }
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }

    }


}
