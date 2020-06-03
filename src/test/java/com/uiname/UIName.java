package com.uiname;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


import com.pojo.User;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class UIName {

    @BeforeAll
    public static void setup(){
        baseURI = "https://cybertek-ui-names.herokuapp.com/api/";
    }

    //No params test 1.Send a get request without providing any parameters
    // 2.Verify status code 200, content type application/json; charset=utf-8
    // 3.Verify that name, surname, gender, region fields have value

    @Test
    @DisplayName("no parameter")
    public void noParam(){

        Response response = when().get().prettyPeek();

        response.then().
                assertThat().
                statusCode(200).
                and().
                contentType("application/json; charset=utf-8").
                and().
                body("name", notNullValue()).
                body("surname",notNullValue()).
                body("gender",notNullValue()).
                body("region",notNullValue());

    }

    //Gender test1.Create a request by providing query parameter: gender, male or female
    // 2.Verify status code 200, content type application/json; charset=utf-8
    // 3.Verify that value of gender field is same from step 1
    @Test
    @DisplayName("Gender test")
    public void genderTest(){

        Response response = given().
                                   queryParam("gender", "male").
                                   get().
                                   prettyPeek();

                   response.then().
                                  assertThat().statusCode(200).
                                  contentType(ContentType.JSON).
                                  body("gender", notNullValue());
    }

    //2 params test
    // 1.Create a request by providing query parameters: a valid region and gender
    // NOTE: Available region values are given in the documentation
    // 2.Verify status code 200, content type application/json; charset=utf-8
    // 3.Verify that value of gender field is same from step 1
    // 4.Verify that value of region field is same from step 1
    @Test
    @DisplayName("2 params test valid region and gender")
    public void paramsTest(){

        Response response = given().
                                   queryParam("region", "Romania").
                                   queryParam("gender", "male").
                             get().prettyPeek();

                    response.then().
                                    assertThat().
                                    statusCode(200).
                            and().
                                    contentType("application/json; charset=utf-8").
                            and().
                                    body("gender", notNullValue()).
                                    body("region", notNullValue());
    }

    //Invalid gender test
    // 1.Create a request by providing query parameter: invalid gender
    // 2.Verify status code 400 and status line contains Bad Request
    // 3.Verify that value of error field is Invalid gender
    @Test
    @DisplayName("Invalid gender test")
    public void invalidGenderTest(){

        Response response = given().
                                   queryParam("gender", "hello").
                                   get().prettyPeek();
                 response.then().assertThat().statusCode(400).
                 statusLine(containsString("Bad Request")).
                 body("error", is("Invalid gender"));
    }

    //Invalid region test
    // 1.Create a request by providing query parameter: invalid region
    // 2.Verify status code 400 and status line contains Bad Request
    // 3.Verify that value of error field is Region or language not found
    @Test
    @DisplayName("Invalid region test")
    public void invalidRegionTest(){
        Response response = given().
                                   queryParam("region", "hello").
                                   get().prettyPeek();
                    response.then().
                                   assertThat().
                                   statusCode(400).
                            and().
                                   contentType(ContentType.JSON).
                            and().
                                   body("error", is("Region or language not found"));
    }

    //Amount and regions test
    // 1.Create request by providing query parameters: a valid region and amount(must be bigger than 1)
    // 2.Verify status code 200, content type application/json; charset=utf-8
    // 3.Verify that all objects have different name+surname combination
    @Test
    @DisplayName("Amount and regions test")
    public void validRegionAndAmount(){
        Response response = given().
                queryParam("region","United States").
                queryParam("amount",5).
                when().
                get();

        List<User> usersList =  response.jsonPath().getList("",User.class);
        //System.out.println("usersList = " + usersList);
        //List<Map<String,String >> usersMap = response.jsonPath().getList("");
        //System.out.println("usersMap = " + usersMap);

        Set<String> uniqueNames = new LinkedHashSet<>();
        List<String> originalNames = new ArrayList<>();

        for (int i = 0; i < usersList.size() ; i++) {
            String fullName = usersList.get(i).getName() +" " + usersList.get(i).getSurname();
            uniqueNames.add(fullName);
            originalNames.add(fullName);
        }

        response. then().
                statusCode(200).
                header("Content-Type","application/json; charset=utf-8");
        assertTrue(uniqueNames.size()==originalNames.size());

    }



    //3 params test
    // 1.Create a request by providing query parameters: a valid region, gender and amount (must be biggerthan 1)
    // 2.Verify status code 200, content type application/json; charset=utf-8
    // 3.Verify that all objects the response have the same region and gender passed in step 1

    @Test
    @DisplayName("Verifying of region with invalid parameter")
    public void threeParamsTest() {
        Response response = given().
                queryParam("region", "United States").
                queryParam("gender","male").
                queryParam("amount", 5).
                when().
                get();
        response.then().
                statusCode(200).
                contentType("application/json; charset=utf-8");
        List<String> usersRegion = response.jsonPath().getList("region");
        //System.out.println("usersRegions = " + usersRegion);
        List<String> usersGender = response.jsonPath().getList("gender");
        //System.out.println("usersGender = " + usersGender);

        Set<String> usersRegionSet = new HashSet<>();
        usersRegionSet.addAll(usersRegion);
        //System.out.println("usersRegionSet = " + usersRegionSet);
        Set<String> usersGenderSet = new HashSet<>();
        usersGenderSet.addAll(usersGender);
        //System.out.println("usersGenderSet = " + usersGenderSet);

        assertTrue(usersRegionSet.size()==1);
        assertTrue(usersGenderSet.size()==1);


    }




    //Amount count test
    //1. Create a request by providing query parameter: amount (must be bigger than 1)
    //2. Verify status code 200, content type application/json; charset=utf-8
    //3. Verify that number of objects returned in the response is same as the amount passed in step 1

    @Test
    @DisplayName("Verifying of region with invalid parameter")
    public void amountCountTest() {
        Response response = given().
                queryParam("amount", 5).
                when().
                get();
        response.   then().
                statusCode(200).
                contentType("application/json; charset=utf-8");
        List<User> usersList = response.jsonPath().getList("",User.class);

        assertTrue(usersList.size()==5);


    }



}
