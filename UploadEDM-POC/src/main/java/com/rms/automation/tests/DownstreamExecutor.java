package com.rms.automation.tests;

import com.rms.automation.PATEApi.PATETests;
import com.rms.automation.UploadEdmApi.UploadEDM;
import com.rms.automation.batchApi.BatchTests;
import com.rms.automation.climateChange.ClimateChangeTests;
import com.rms.automation.currencyConverterApi.CurrencyConverter;
import com.rms.automation.edm.LoadData;
import com.rms.automation.exportApi.export;
import com.rms.automation.mriImportApi.MRIImportTests;
import com.rms.automation.renameAnalysisApi.RenameAnalysis;
import com.rms.automation.utils.Utils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class DownstreamExecutor {

    public static void execute(Map<String, String> tc) throws Exception {

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

    }

}
