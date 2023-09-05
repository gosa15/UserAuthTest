package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import lib.DataGenerator;

public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String testEmail;
    String password;
    Response responseCreateUserAuth;
    Response responseGetAuth;

    @BeforeEach
        public void CreateUser(){

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Map<String, String> authData = new HashMap<>();
        authData.put("email", this.testEmail);
        authData.put("password", this.password);

        this.responseCreateUserAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/", userData);

        this.testEmail = userData.get("email");
        this.password = userData.get("password");
        this.responseGetAuth = apiCoreRequests
                .makeUserLogin("https://playground.learnqa.ru/api/user/login", authData);

    }


    @Test
    public void testGetUserDataNotAuth(){
        Response  responseUserData = apiCoreRequests
                .GetNotAuthUserData("https://playground.learnqa.ru/api/user/"+this.getIntFromJson(this.responseCreateUserAuth, "id"));

        String[] unexpectedFields = {"id", "email", "firstName", "lastName"};

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser(){

        Response  responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+this.getIntFromJson(this.responseCreateUserAuth, "id"),
                                     this.getHeader(responseGetAuth, "x-csrf-token"),
                                     this.getCookie(responseGetAuth, "auth_sid"));

        String[] expectedFields = {"id", "username", "email", "firstName", "lastName"};

        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    public void testGetUserDetailsAuthAsAnotherUser(){

        Map<String, String> secondUserData = DataGenerator.getRegistrationData();

        Response responseCreateSecondUserAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/", secondUserData);

        Response  responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+getIntFromJson(responseCreateSecondUserAuth, "id"),
                                     this.getHeader(responseGetAuth, "x-csrf-token"),
                                     this.getCookie(responseGetAuth, "auth_sid"));

        String[] unexpectedFields = {"id", "email", "firstName", "lastName"};

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
    }
}
