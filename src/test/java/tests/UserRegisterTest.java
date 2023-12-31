package tests;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.Assertions;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;

@Severity(value = SeverityLevel.CRITICAL)
@Epic("Авторизация и работа с профилем пользователя")
@Feature("Регистрация нового пользователя")
@Owner("Иванов Иван Иванович")
public class UserRegisterTest extends  BaseTestCase{
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Story(value = "Неуспешная регистрация пользователя")
    @Description("Регистрация с некорректным email")
    @Test
    public void testCreateUserWithExistingEmail(){
        String url = "https://playground.learnqa.ru/api/user/";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", "test.com");
        userData.put("password", "123");
        userData.put("username", "testName");
        userData.put("firstName", "testFirstName");
        userData.put("lastName", "testLastName");

        Response responseCreateAuth = apiCoreRequests
                .makeGetRequest(url, userData);

        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");

    }
    @Story(value = "Неуспешная регистрация пользователя")
    @Description("Регистрация с коротким username")
    @Test
    public void testCreateUserWithShortName(){
        String url = "https://playground.learnqa.ru/api/user/";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", DataGenerator.getRandomEmail());
        userData.put("password", "123");
        userData.put("username", "t");
        userData.put("firstName", "testFirstName");
        userData.put("lastName", "testLastName");

        Response responseCreateAuth = apiCoreRequests
                .makeGetRequest(url, userData);

        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }
    @Story(value = "Неуспешная регистрация пользователя")
    @Description("Регистрация с длинным username >255 символов")
    @Test
    public void testCreateUserWithLongName(){
        String url = "https://playground.learnqa.ru/api/user/";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", DataGenerator.getRandomEmail());
        userData.put("password", "123");
        userData.put("username", "qazxlswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvkdazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcvqazxswedcv");
        userData.put("firstName", "testFirstName");
        userData.put("lastName", "testLastName");

        Response responseCreateAuth = apiCoreRequests
                .makeGetRequest(url, userData);

        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }
    @Story(value = "Неуспешная регистрация пользователя")
    @Description("Регистрация пользователя без одного из параметра")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password","username","firstName","lastName"})
    public void testCreateUserWithoutOneParameter(String condition) throws IllegalAccessException {
        Map<String, String> userData = new HashMap<>();
        String url = "https://playground.learnqa.ru/api/user/";

        if (condition.equals("email")) {
            userData.put("password", "123");
            userData.put("username", "testCat");
            userData.put("firstName", "testFirstName");
            userData.put("lastName", "testLastName");

            Response responseForChek = apiCoreRequests.makeGetRequest(
                    url,
                    userData
            );
            Assertions.assertResponseTextEquals(responseForChek, "The following required params are missed: "+condition);
        } else if (condition.equals("password")) {
            userData.put("email", DataGenerator.getRandomEmail());
            userData.put("username", "testCat");
            userData.put("firstName", "testFirstName");
            userData.put("lastName", "testLastName");

            Response responseForChek = apiCoreRequests.makeGetRequest(
                    url,
                    userData
            );
            Assertions.assertResponseTextEquals(responseForChek, "The following required params are missed: "+condition);
        } else if (condition.equals("username")) {
            userData.put("email", DataGenerator.getRandomEmail());
            userData.put("password", "123");
            userData.put("firstName", "testFirstName");
            userData.put("lastName", "testLastName");

            Response responseForChek = apiCoreRequests.makeGetRequest(
                    url,
                    userData
            );
            Assertions.assertResponseTextEquals(responseForChek, "The following required params are missed: "+condition);
        } else if (condition.equals("firstName")) {
            userData.put("email", DataGenerator.getRandomEmail());
            userData.put("password", "123");
            userData.put("username", "testCat");
            userData.put("lastName", "testLastName");

            Response responseForChek = apiCoreRequests.makeGetRequest(
                    url,
                    userData
            );
            Assertions.assertResponseTextEquals(responseForChek, "The following required params are missed: "+condition);
        } else if (condition.equals("lastName")) {
            userData.put("email", DataGenerator.getRandomEmail());
            userData.put("password", "123");
            userData.put("username", "testCat");
            userData.put("firstName", "testFirstName");

            Response responseForChek = apiCoreRequests.makeGetRequest(
                    url,
                    userData
            );
            Assertions.assertResponseTextEquals(responseForChek, "The following required params are missed: "+condition);;
        } else {
            throw new IllegalArgumentException("Codition value is know"+ condition);
        }
    }

    @Story(value = "Успешная регистрация пользователя")
    @Description("Успешная регистрация пользователя")
    @Test
    public void testCreateUser(){
        String url = "https://playground.learnqa.ru/api/user/";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", DataGenerator.getRandomEmail());
        userData.put("password", "123");
        userData.put("username", "testName");
        userData.put("firstName", "testFirstName");
        userData.put("lastName", "testLastName");

        Response responseCreateAuth = apiCoreRequests
                .makeGetRequest(url, userData);

        Assertions.assertResponseStatusCode(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");

    }

}
