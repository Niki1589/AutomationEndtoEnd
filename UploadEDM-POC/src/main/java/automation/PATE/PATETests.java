package automation.PATE;

import automation.batch.BatchTests;
import automation.edm.ApiUtil;
import automation.edm.LoadData;
import automation.merge.SingleInputClass;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static automation.edm.ApiUtil.getTreatyIdByAnalysisId;

public class PATETests {
    //    @DataProvider(name = "loadFromPateCSV")
//  public static Object[] provider() throws IOException {
//      return LoadData.readPATEFromLocalCSV();
//
//    }
    // @Test(dataProvider = "loadFromPateCSV")
    public static void executePATETests(String caseNo, String analysisId) throws Exception {
            List<Map<String, String>> pateList = LoadData.loadPateCSVByCase(caseNo);

        for ( Map<String, String> tc : pateList) {
            PATE(tc,analysisId );
        }
    }

    public static void PATE(Map<String, String> tc, String analysisIdBatch) throws Exception {
        if (tc != null) {
            Map<String, List<Map<String, Object>>> payload = new HashMap<>();
            if (tc.get("ifRun").equals("YES")) {

                String analysisId_pate ="";
                int treatyId =0;

               // String analysisId_pate = analysisIdBatch;
                System.out.println("***** Running PATE API ********");

                String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

                String pateOperationType = tc.get("operationType");

                Map<String, Object> treatyMap = new HashMap<>();
                treatyMap.put("analysisId", tc.get("analysisId"));
                treatyMap.put("operationType", tc.get("operationType"));
                treatyMap.put("pcntRetent", tc.get("pcntRetent"));
                treatyMap.put("priority", tc.get("priority"));
                Map<String, Object> cedantMap = new HashMap<>();
                cedantMap.put("id", "C1");
                cedantMap.put("name", "C1");
                treatyMap.put("cedant", cedantMap);
                treatyMap.put("treatyNumber", tc.get("treatyNumber"));
                treatyMap.put("maolAmount", "");
                treatyMap.put("attachPt", tc.get("attachPt"));
                treatyMap.put("treatyName", tc.get("treatyName"));
                treatyMap.put("userId1", "");
                treatyMap.put("riskLimit", 0);
                treatyMap.put("userId2", "");
                treatyMap.put("expireDate", tc.get("expireDate"));
                treatyMap.put("reinstCharge", tc.get("reinstCharge"));
                treatyMap.put("pcntRiShare", tc.get("pcntRiShare"));
                treatyMap.put("occurLimit", tc.get("occurLimit"));
                Map<String, Object> currencyMap = new HashMap<>();
                currencyMap.put("code", "USD");
                currencyMap.put("name", "US Dollar");
                treatyMap.put("currency", currencyMap);
                treatyMap.put("isValid", true);
                treatyMap.put("producer", null);
                treatyMap.put("lobs", new ArrayList<>());
                treatyMap.put("aggregateDeductible", tc.get("aggregateDeductible"));
                treatyMap.put("pcntPlaced", tc.get("pcntPlaced"));
                treatyMap.put("numOfReinst", tc.get("numOfReinst"));
                treatyMap.put("premium", tc.get("Premium"));
                Map<String, Object> attachBasisMap = new HashMap<>();
                attachBasisMap.put("code", "L");
                attachBasisMap.put("name", "Losses Occurring");
                treatyMap.put("attachBasis", attachBasisMap);

                Map<String, Object> treatyTypeMap = new HashMap<>();
                treatyTypeMap.put("code", "CATA");
                treatyTypeMap.put("name", "Catastrophe");
                treatyMap.put("treatyType", treatyTypeMap);

                treatyMap.put("treatyId", tc.get("treatyId"));
                treatyMap.put("aggregateLimit", tc.get("aggregateLimit"));
                treatyMap.put("pcntCovered", tc.get("pcntCovered"));
                treatyMap.put("retentAmt", "");

                Map<String, Object> attachLevelMap = new HashMap<>();
                attachLevelMap.put("code", "PORT");
                attachLevelMap.put("name", "Portfolio");
                treatyMap.put("attachLevel", attachLevelMap);

                treatyMap.put("effectDate", tc.get("effectDate"));

                List<Map<String, Object>> treatyMapList = new ArrayList<>();
                treatyMapList.add(treatyMap);

                if (pateOperationType != null && pateOperationType.equals("insert")) {
                    payload.put("insert", treatyMapList);
                    payload.put("update", new ArrayList<>());
                    payload.put("delete", new ArrayList<>());

                    Response response = ApiUtil.pateApi(pateOperationType, payload, Integer.parseInt(analysisIdBatch), token);
                    System.out.println("PATE  Status: " + response.getStatusCode());
                    if (response.getStatusCode() == 202) {
                        String locationHdr = response.getHeader("Location");
                        String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                        System.out.println("exportFile_wf_id: " + jobId);
                        if (jobId == null) {
                            throw new Exception("JobId is null");
                        }
                        String msg = ApiUtil.waitForJobToComplete(jobId, token);
                        System.out.println("wait for job msg: " + msg);
                        analysisId_pate = String.valueOf(ApiUtil.getAnalysisIDByJobId_Pate(jobId, token));
                        Response pateTreatiesResponse = getTreatyIdByAnalysisId(token,analysisId_pate);

                        if (pateTreatiesResponse.getStatusCode() == 200)
                        {
                            treatyId = pateTreatiesResponse.jsonPath().getInt("searchMatchingPateList[0].treatyId");


                            System.out.println("Treaty Id is "+ treatyId);

                        }

                        if(analysisId_pate!="")
                        {
                            LoadData.UpdateTCInLocalCSV_Pate(Integer.parseInt(tc.get("index")), "operationType", "update");
                            LoadData.UpdateTCInLocalCSV_Pate(Integer.parseInt(tc.get("index")), "analysisId_pate", String.valueOf(analysisId_pate));
                            LoadData.UpdateTCInLocalCSV_Pate(Integer.parseInt(tc.get("index")), "treatyId", String.valueOf(treatyId));


                            //    LoadData.UpdateTCInLocalCSV_Pate(Integer.parseInt(tc.get("index")), "treatyId", String.valueOf(analysisId_pate));
                        }
                    } else {
                        String msg = response.getBody().jsonPath().get("message");
                        System.out.println("Pate Api Message: " + msg);
                    }

                }
                else if (pateOperationType != null && pateOperationType.equals("update")) {
                    payload.put("insert", new ArrayList<>());
                    payload.put("update", treatyMapList);
                    payload.put("delete", new ArrayList<>());

                    analysisId_pate = tc.get("analysisId");
                }
                else if (pateOperationType != null && pateOperationType.equals("delete")) {
                    payload.put("insert", new ArrayList<>());
                    payload.put("update", new ArrayList<>());
                    payload.put("delete", treatyMapList);

                    analysisId_pate = tc.get("analysisId");
                }

                Response response = ApiUtil.pateApi(pateOperationType, payload, Integer.parseInt(analysisId_pate), token);
                System.out.println("PATE  Status: " + response.getStatusCode());
                if (response.getStatusCode() == 202) {
                    String locationHdr = response.getHeader("Location");
                    String jobId = locationHdr.substring(locationHdr.lastIndexOf('/') + 1);
                    System.out.println("exportFile_wf_id: " + jobId);
                    if (jobId == null) {
                        throw new Exception("JobId is null");
                    }
                    String msg = ApiUtil.waitForJobToComplete(jobId, token);
                    System.out.println("wait for job msg: " + msg);
                    analysisId_pate = String.valueOf(ApiUtil.getAnalysisIDByJobId_Pate(jobId, token));
                    if(analysisId_pate!="")
                    {
                        LoadData.UpdateTCInLocalCSV_Pate(Integer.parseInt(tc.get("index")), "analysisId", String.valueOf(analysisId_pate));
                    }
                } else {
                    String msg = response.getBody().jsonPath().get("message");
                    System.out.println("Pate Api Message: " + msg);
                }

            }
        }
    }
}



