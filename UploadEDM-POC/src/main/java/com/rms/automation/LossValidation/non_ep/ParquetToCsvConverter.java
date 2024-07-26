package com.rms.automation.LossValidation.non_ep;

import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.example.data.Group;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.schema.Type;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParquetToCsvConverter {

    public static void main(String[] args) throws IOException {
        convertToCSV();
    }

    public static List<Map<String, String>> convertToCSV() throws IOException {
        String parquetFilePath = "/Users/Nikita.Arora/Documents/UploadEdmPoc/A002_SMOKE_EUWS/Baselines/non-ep/RM20_FM_EUWS_FP_03/meout/EventStats/Portfolio/GR/EventStats0.parquet";

        Configuration conf = new Configuration();
        Path path = new Path(parquetFilePath);

        List<Map<String, String>> data = new ArrayList<>();
        List<String> listOfFields = new ArrayList<>();

        try (ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), path)
                .withConf(conf)
                .build()) {

            // Read and write rows
            Group group;
            while ((group = reader.read()) != null) {

                if (group != null && listOfFields.size() == 0) {
                    List<Type> types = group.asGroup().getType().getFields();
                    for ( Type type : types) {
                        listOfFields.add(type.getName());
                    }
                }

                int colIndex = 0;
                Map<String, String> row = new HashMap<>();
                for (String field: listOfFields) {
                    String value = group.getValueToString(colIndex++, 0);
                    row.put(field, value);
                }
                data.add(row);

            }

        }

        return data;
    }
}