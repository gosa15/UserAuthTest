package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class UserEdutTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String testEmail;
    String password;
    int idUser;
    Response responseGetAuth;

    @BeforeEach
    public void CreateUser(){

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Map<String, String> authData = new HashMap<>();
        authData.put("email", this.testEmail);
        authData.put("password", this.password);

        Response responseCreateUserAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/", userData);
        this.idUser = this.getIntFromJson(responseCreateUserAuth, "id");


        this.testEmail = userData.get("email");
        this.password = userData.get("password");
        this.responseGetAuth = apiCoreRequests
                .makeUserLogin("https://playground.learnqa.ru/api/user/login", authData);

    }
    @Test
    public void testEditJustCreatedTest(){


    }
}
