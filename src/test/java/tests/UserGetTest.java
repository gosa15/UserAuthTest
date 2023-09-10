package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;

@Severity(value = SeverityLevel.BLOCKER)
@Epic("Авторизация и работа с профилем пользователя")
@Feature("Получение профиля пользователя")
@Owner("Петров Петр Иванович")
//@Severity("BLOCKER")
public class UserGetTest extends BaseTestCase {
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

    @Story(value = "Неуспешное получение данных пользователя")
    @Description("Запрос данных не авторизированного пользователя")
    @Test
    public void testGetUserDataNotAuth(){
        Response  responseUserData = apiCoreRequests
                .GetNotAuthUserData("https://playground.learnqa.ru/api/user/"+this.idUser);

        String[] unexpectedFields = {"id", "email", "firstName", "lastName"};

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, unexpectedFields);
    }

    @Story(value = "Успешное получение данных пользователя")
    @Description("Получение данных пользователя")
    @Test
    public void testGetUserDetailsAuthAsSameUser(){

        Response  responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+this.idUser,
                                     this.getHeader(responseGetAuth, "x-csrf-token"),
                                     this.getCookie(responseGetAuth, "auth_sid"));

        String[] expectedFields = {"id", "username", "email", "firstName", "lastName"};

        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Story(value = "Неуспешное получение данных пользователя")
    @Description("В запросе получения данных пользователя передаются авторизационные данные другого пользователя")
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
