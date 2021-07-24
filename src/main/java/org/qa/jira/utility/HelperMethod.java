package org.qa.jira.utility;

import io.restassured.path.json.JsonPath;

public class HelperMethod {

    public static JsonPath stringToJson(String response) {

        JsonPath responseJson = new JsonPath(response);

        return responseJson;
    }
}
