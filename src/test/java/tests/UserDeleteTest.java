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

public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    //Попытка удалить пользователя по ID 2
    @Test
    public void testDeleteTestUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuthUser = apiCoreRequests
                .makeUserLogin("https://playground.learnqa.ru/api/user/login", authData);

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteAuthUser("https://playground.learnqa.ru/api/user/2",
                        this.getHeader(responseGetAuthUser, "x-csrf-token"),
                        this.getCookie(responseGetAuthUser, "auth_sid"));

        //Проверка соответствия текста ошибки и статус кода ответа ожидаемому результату
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
        Assertions.assertResponseStatusCode(responseDeleteUser, 400);

        //Проверка, что под пользователем возможно авторизоваться
        Response  responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/2",
                        this.getHeader(responseGetAuthUser, "x-csrf-token"),
                        this.getCookie(responseGetAuthUser, "auth_sid"));


        String[] expectedFields = {"id", "username", "email", "firstName", "lastName"};

        Assertions.assertJsonHasFields(responseUserData, expectedFields);
        Assertions.assertResponseStatusCode(responseGetAuthUser, 200);

    }

    //Удаление авторизированного пользователя
    @Test
    public void testDeleteAuthUser(){
        //Создание нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUserAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/", userData);
        int idUser = this.getIntFromJson(responseCreateUserAuth, "id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

       Response responseGetAuth = apiCoreRequests
                .makeUserLogin("https://playground.learnqa.ru/api/user/login", authData);

        //Удаление пользователя
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteAuthUser("https://playground.learnqa.ru/api/user/"+idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseStatusCode(responseDeleteUser, 200);

        //Проверка, что под пользователем не возможно авторизоваться
        Response  responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseTextEquals(responseUserData, "User not found");
        Assertions.assertResponseStatusCode(responseUserData, 404);

        String[] expectedFields = {"id", "username", "email", "firstName", "lastName"};
        Assertions.assertJsonHasNotFields(responseUserData, expectedFields);

    }

    //Удаление пользователя, будучи авторизированным под другим пользователем
    @Test
    public void testDeleteAuthAnotherUser(){
        //Создание нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateUserAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/", userData);
        int idUser = this.getIntFromJson(responseCreateUserAuth, "id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makeUserLogin("https://playground.learnqa.ru/api/user/login", authData);

        //Создание второго пользователя
        Map<String, String> nonDefaultValuesEmail = new HashMap<>();
        authData.put("email", "secondUser"+DataGenerator.getRandomEmail());
        Map<String, String> secondUserData = DataGenerator.getRegistrationData(nonDefaultValuesEmail);

        Response responseCreateSecondUser = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/", secondUserData);
        int idSecondUser = this.getIntFromJson(responseCreateSecondUser, "id");

        Map<String, String> authDataSecondUser = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuthSecondUser = apiCoreRequests
                .makeUserLogin("https://playground.learnqa.ru/api/user/login", authDataSecondUser);

        //Удаление пользователя
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteAuthUser("https://playground.learnqa.ru/api/user/"+idSecondUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseStatusCode(responseDeleteUser, 401);

        //Проверка, что под пользователем возможно авторизоваться
        Response  responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertResponseStatusCode(responseUserData, 200);

        //Проверка, что под dnjhsv пользователем возможно авторизоваться
        Response  responseSecondUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+idUser,
                        this.getHeader(responseGetAuthSecondUser, "x-csrf-token"),
                        this.getCookie(responseGetAuthSecondUser, "auth_sid"));

        Assertions.assertResponseStatusCode(responseUserData, 200);

    }

}
