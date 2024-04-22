package com.rms.automation;

import com.rms.automation.edm.LoadData;
import org.apache.poi.ss.usermodel.*;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("Tests Running with config = "+System.getenv("configPath"));

        TestNG testng = new TestNG();

        List<String> suites = Lists.newArrayList();

        suites.add("testng.xml");
        testng.setTestSuites(suites);

        testng.run();

        System.out.println("Finished Running all the tests.");


    }

}
