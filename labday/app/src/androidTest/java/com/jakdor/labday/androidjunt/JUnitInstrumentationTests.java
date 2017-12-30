package com.jakdor.labday.androidjunt;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import io.appflate.restmock.RESTMockServer;

import static io.appflate.restmock.utils.RequestMatchers.hasExactQueryParameters;
import static io.appflate.restmock.utils.RequestMatchers.pathContains;

@RunWith(Suite.class)
@Suite.SuiteClasses({NetworkManagerIntegrationTest.class, ProjectRepositoryIntegrationTest.class})
public class JUnitInstrumentationTests {

    @BeforeClass
    public static void beforeAll(){
        RESTMockServer.whenGET(pathContains("api/app_data"))
            .thenReturnFile(200, "api/app_data.json");

        RESTMockServer.whenGET(pathContains("api/last_update"))
                .thenReturnFile(200, "api/last_update.json");

        RESTMockServer.whenGET(pathContains("api/login"))
                .thenReturnFile(200, "api/login.json");
    }

}
