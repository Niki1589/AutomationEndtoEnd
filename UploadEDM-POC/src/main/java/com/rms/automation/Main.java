package com.rms.automation;

import com.rms.automation.edm.LoadData;
import org.apache.poi.ss.usermodel.*;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {


//        logger.trace("trace We've just greeted the user!");
//        logger.debug("debug We've just greeted the user!");
//        logger.info("info We've just greeted the user!");
//        logger.warn("warn We've just greeted the user!");
//        logger.error("error We've just greeted the user!");
//        logger.fatal("fatal We've just greeted the user!");

        System.out.println("Tests Running with config = "+System.getenv("configPath"));

        TestNG testng = new TestNG();

        List<String> suites = Lists.newArrayList();

        suites.add("testng.xml");
        testng.setTestSuites(suites);

        testng.run();

        System.out.println("Finished Running all the tests.");


    }

}
