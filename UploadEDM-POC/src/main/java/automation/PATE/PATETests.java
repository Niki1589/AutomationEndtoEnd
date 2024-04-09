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
            PATE(tc,analysisId);
        }
    }

    private static void PATE(Map<String, String> tc, String analysisIdBatch) throws Exception {
        if (tc != null) {
            Map<String, List<Map<String, Object>>> payload = new HashMap<>();
            if (tc.get("ifRun").equals("YES")) {

                String analysisId_pate = analysisIdBatch;
                System.out.println("***** Running PATE API ********");

                String token = ApiUtil.getSmlToken(LoadData.config.getUsername(), LoadData.config.getPassword(), LoadData.config.getTenant(), "accessToken");

                String pateOperationType = tc.get("operationType");

                Map<String, Object> treatyMap = new HashMap<>();

                treatyMap.put("operationType", tc.get("operationType"));
                treatyMap.put("pcntRetent", tc.get("pcntRetent"));
                treatyMap.put("priority", tc.get("priority"));
                treatyMap.put("treatyNumber", tc.get("treatyNumber"));
                treatyMap.put("maolAmount", "");
                treatyMap.put("attachPt", tc.get("attachPt"));
                treatyMap.put("treatyName", tc.get("treatyName"));
                treatyMap.put("userId1", "");
                treatyMap.put("riskLimit", 0);
                treatyMap.put("expireDate", tc.get("expireDate"));
                treatyMap.put("reinstCharge", tc.get("reinstCharge"));
                treatyMap.put("pcntRiShare", tc.get("pcntRiShare"));
                treatyMap.put("occurLimit", tc.get("occurLimit"));
                treatyMap.put("isValid", true);
                treatyMap.put("producer", null);
                treatyMap.put("lobs", new ArrayList<>());
                treatyMap.put("aggregateDeductible", tc.get("aggregateDeductible"));
                treatyMap.put("pcntPlaced", tc.get("pcntPlaced"));
                treatyMap.put("numOfReinst", tc.get("numOfReinst"));
                treatyMap.put("premium", tc.get("Premium"));
                treatyMap.put("effectDate", tc.get("effectDate"));
                treatyMap.put("aggregateLimit", tc.get("aggregateLimit"));
                treatyMap.put("pcntCovered", tc.get("pcntCovered"));
                treatyMap.put("retentAmt", "");

                Map<String, Object> cedantMap = new HashMap<>();
                cedantMap.put("id", "C1");
                cedantMap.put("name", "C1");

                Map<String, Object> currencyMap = new HashMap<>();
                currencyMap.put("code", "USD");
                currencyMap.put("name", "US Dollar");

                Map<String, Object> attachBasisMap = new HashMap<>();
                attachBasisMap.put("code", "L");
                attachBasisMap.put("name", "Losses Occurring");

                Map<String, Object> treatyTypeMap = new HashMap<>();
                treatyTypeMap.put("code", "CATA");
                treatyTypeMap.put("name", "Catastrophe");

                Map<String, Object> attachLevelMap = new HashMap<>();
                attachLevelMap.put("code", "PORT");
                attachLevelMap.put("name", "Portfolio");

                if (pateOperationType != null && pateOperationType.equals("update")) {

                    //EDIT
                    treatyMap.put("analysisId", tc.get("analysisId_pate"));
                    treatyMap.put("treatyId", tc.get("treatyId"));
                    treatyMap.put("groupId", 0);
                    treatyMap.put("pateId", 0);
                    treatyMap.put("lossOccurrences",new ArrayList<>());

                    currencyMap.put("id", 0);
                    attachBasisMap.put("id", 0);
                    treatyTypeMap.put("id", 0);
                    attachLevelMap.put("id", 0);

                }
                else if (pateOperationType != null && pateOperationType.equals("DELETE")) {

                    treatyMap.put("userId2", "");
                    treatyMap.put("analysisId", tc.get("analysisId_pate"));
                    treatyMap.put("treatyId", tc.get("treatyId"));
                    treatyMap.put("groupId", 0);
                    treatyMap.put("pateId", 0);
                    treatyMap.put("lossOccurrences", new ArrayList<>());

                    currencyMap.put("id", 0);
                    attachBasisMap.put("id", 0);
                    treatyTypeMap.put("id", 0);
                    attachLevelMap.put("id", 0);

                    Map<String, Object> producer = new HashMap<>();
                    producer.put("id", "");
                    producer.put("name", "");
                    treatyMap.put("producer", producer);

                }

                treatyMap.put("attachBasis", attachBasisMap);
                treatyMap.put("attachLevel", attachLevelMap);
                treatyMap.put("treatyType", treatyTypeMap);
                treatyMap.put("currency", currencyMap);
                treatyMap.put("cedant", cedantMap);
                List<Map<String, Object>> treatyMapList = new ArrayList<>();
                treatyMapList.add(treatyMap);

                if (pateOperationType != null && pateOperationType.equals("insert")) {
                    payload.put("insert", treatyMapList);
                    payload.put("update", new ArrayList<>());
                    payload.put("delete", new ArrayList<>());
                }
                else if (pateOperationType != null && pateOperationType.equals("update")) {
                    payload.put("insert", new ArrayList<>());
                    payload.put("update", treatyMapList);
                    payload.put("delete", new ArrayList<>());

                    analysisId_pate = tc.get("analysisId_pate");
                }
                else if (pateOperationType != null && pateOperationType.equals("DELETE")) {
                    payload.put("insert", new ArrayList<>());
                    payload.put("update", new ArrayList<>());
                    payload.put("delete", treatyMapList);

                    analysisId_pate = tc.get("analysisId_pate");
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
                    Response pateTreatiesResponse = getTreatyIdByAnalysisId(token,analysisId_pate);

                    if( analysisId_pate != "" )
                    {
                        LoadData.UpdateTCInLocalCSV_Pate(Integer.parseInt(tc.get("index")), "analysisId_pate", String.valueOf(analysisId_pate));
                    }

                    if (pateTreatiesResponse.getStatusCode() == 200 && pateOperationType != null && pateOperationType.equals("insert"))
                    {
                        int treatyId = pateTreatiesResponse.jsonPath().getInt("searchMatchingPateList[0].treatyId");
                        System.out.println("Treaty Id is "+ treatyId);
                        LoadData.UpdateTCInLocalCSV_Pate(Integer.parseInt(tc.get("index")), "treatyId", String.valueOf(treatyId));
                        LoadData.UpdateTCInLocalCSV_Pate(Integer.parseInt(tc.get("index")), "operationType", "update");
                    }
                } else {
                    String msg = response.getBody().jsonPath().get("message");
                    System.out.println("Pate Api Message: " + msg);
                }

            }
        }
    }
}



