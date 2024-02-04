package automation;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.collections.Lists;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("Tests Running with config = "+System.getenv("configPath"));

        TestListenerAdapter tla = new TestListenerAdapter();
        TestNG testng = new TestNG();

        testng.addListener(tla);

        List<String> suites = Lists.newArrayList();

        suites.add("testng.xml");
        testng.setTestSuites(suites);

        testng.run();

        System.out.println("Finished Running all the tests.");


    }

}
