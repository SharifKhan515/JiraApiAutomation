package org.qa.jira.testcases;

import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.qa.jira.utility.HelperMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class JiraApiTest {

    @Test
    public void JiraComment(){
        RestAssured.baseURI = "http://127.0.0.1:8000";
        SessionFilter sessionFilter = new SessionFilter();

      String authResponse =   given().log().all().header("content-type","application/json").
                body("{ \"username\": \"sharifkhan515\", \"password\": \"razordota2\" }").filter(sessionFilter).
                when().post("/rest/auth/1/session").then().extract().response().asString();


          //add comment
       String comment =  given().relaxedHTTPSValidation().log().all().pathParam("id","TES-1").header("content-type","application/json").body("{\n" +
                "    \"body\": \" Comment using Script 456\",\n" +
                "    \"visibility\": {\n" +
                "        \"type\": \"role\",\n" +
                "        \"value\": \"Administrators\"\n" +
                "    }\n" +
                "}").filter(sessionFilter).when().post("/rest/api/2/issue/{id}/comment").
                then().assertThat().statusCode(201).extract().response().asString();

        System.out.println(comment);

           //add attachment
        given().log().all().pathParam("id","TES-1").header("X-Atlassian-Token","no-check").
                header("content-type","multipart/form-data").filter(sessionFilter).
                multiPart("file",new File("Jira.txt")).
                when().post("/rest/api/2/issue/{id}/attachments").then().assertThat().statusCode(200);


        // get issue details

       String jiraDetails =  given().log().all().pathParam("id","TES-1").filter(sessionFilter).
               queryParam("fields","comment").
                when().get("/rest/api/2/issue/{id}")
                .then().log().all().assertThat().statusCode(200).extract().response().asString();

        System.out.println("Comment: "+jiraDetails);

        //verify the created comment is returned

        JsonPath commentJson = HelperMethod.stringToJson(jiraDetails);
        JsonPath commentIdJson = HelperMethod.stringToJson(comment);

        String commentId = commentIdJson.getString("id");
        int commentsCount = commentJson.getInt("fields.comment.comments.size()");

        for(int i=0;i<commentsCount;i++){

            String id = commentJson.get("fields.comment.comments["+i+"].id").toString();
            if(id.equalsIgnoreCase(commentId)){
                System.out.println(commentJson.get("fields.comment.comments["+i+"].body"));
                break;
            }
        }


    }

    @Test
    public void JiraGetIssue(){


    }

}
