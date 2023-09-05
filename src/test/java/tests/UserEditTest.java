package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String testEmail;
    String password;
    int idUser;
    Response responseGetAuth;

    @BeforeEach
    public void CreateUser(){

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUserAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/", userData);
        this.idUser = this.getIntFromJson(responseCreateUserAuth, "id");

        this.testEmail = userData.get("email");
        this.password = userData.get("password");
        Map<String, String> authData = new HashMap<>();
        authData.put("email", this.testEmail);
        authData.put("password", this.password);

        this.responseGetAuth = apiCoreRequests
                .makeUserLogin("https://playground.learnqa.ru/api/user/login", authData);

    }
    @Test
    public void testEditJustCreatedTest(){
        //EDIT
        String newName = DataGenerator.getRandomFirstName();
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makeUserEdit("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);

        //GET
        Response  responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "firstName", newName);



    }
}
