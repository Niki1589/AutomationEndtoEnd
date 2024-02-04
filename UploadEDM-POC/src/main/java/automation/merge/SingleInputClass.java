package automation.merge;

import automation.edm.LoadData;
import automation.edm.TestCase;
import automation.mriImport.MRIImportTests;
import automation.tests.UploadEDM;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

public class SingleInputClass extends TestCase {
    @DataProvider(name = "loadFromCSV")
    public Object[] provider() throws IOException {
        return LoadData.readCaseTCFromLocalCSV();
    }
    @Test (dataProvider = "loadFromCSV")
    public void executeSingleInputCsv(Map<String, String> tc) {

        uploadOrImportEdm(tc);
    }
    public void uploadOrImportEdm(Map<String, String> tc) {
        if(tc != null) {
            try {
                if (tc.get("ifRun").equalsIgnoreCase("YES")) {
                    if (tc.get("caseNo").equalsIgnoreCase("A002_SMOKE_NAWF") || tc.get("ifUploadImportExpo").equalsIgnoreCase("Import")) {
                        MRIImportTests mriImportTests = new MRIImportTests();
                        if (tc.get("ifCreateEdm").equalsIgnoreCase("YES")) {
                            mriImportTests.MRIImport(tc, true);
                        } else {
                            mriImportTests.MRIImport(tc, false);
                        }

                    } else if (tc.get("caseNo").equalsIgnoreCase("A001_E2E_USFL") || tc.get("ifUploadImportExpo").equalsIgnoreCase("Upload")) {
                        UploadEDM uploadEDM = new UploadEDM();
                        uploadEDM.executeUploadEdm(tc);
                    }
                }
            } catch (Exception exception) {

                System.out.println(exception.getMessage());
            }
        }

    }
}
