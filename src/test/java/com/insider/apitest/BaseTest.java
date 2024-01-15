package com.insider.apitest;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected static final String BASE_URI = "https://petstore.swagger.io/v2/pet";
    protected static final String API_KEY = "insider-test";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
    }
}
