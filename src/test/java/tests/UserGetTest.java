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
    String userName = DataGenerator.getRandomUserName();
    String testEmail = DataGenerator.getRandomEmail();
    String password="123";
    Response responseCreateUserAuth;
    Response responseGetAuth;

    @BeforeEach
        public void CreateUser(){

        Map<String, String> userData = new HashMap<>();
        userData.put("email", this.testEmail);
        userData.put("password", this.password);
        userData.put("username", this.userName);
        userData.put("firstName", "testFirstName");
        userData.put("lastName", "testLastName");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", this.testEmail);
        authData.put("password", this.password);

        this.responseCreateUserAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/", userData);

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

        Map<String, String> secondUserData = new HashMap<>();
        secondUserData.put("email", DataGenerator.getRandomEmail());
        secondUserData.put("password", this.password);
        secondUserData.put("username", DataGenerator.getRandomUserName());
        secondUserData.put("firstName", "testFirstName");
        secondUserData.put("lastName", "testLastName");

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
