package com.jakdor.labday;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import io.appflate.restmock.RESTMockServer;

import static io.appflate.restmock.utils.RequestMatchers.pathContains;

@RunWith(Suite.class)
@Suite.SuiteClasses({NetworkManagerIntegrationTest.class, ProjectRepositoryIntegrationTest.class})
public class JUnitInstrumentationTests {

    @BeforeClass
    public static void beforeAll(){
        RESTMockServer.whenGET(pathContains("api/app_data"))
            .thenReturnFile(200, "api/app_data.json");
    }

}
