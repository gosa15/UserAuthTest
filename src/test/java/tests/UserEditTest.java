package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;


@Epic("Авторизация и работа с профилем пользователя")
@Feature("Редактирование профиля пользователя")
@Severity(value = SeverityLevel.CRITICAL)
@Owner("Петров Петр Иванович")
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

    @Story(value = "Успешное изменение профиля пользователя")
    @Description("Изменение авторизированного пользователя")
    @Test
    public void testEditAuthUser(){
        //EDIT
        String newName = DataGenerator.getRandomFirstName();
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makeAuthUserEdit("https://playground.learnqa.ru/api/user/"+this.idUser,
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

    @Story(value = "Неуспешное изменение профиля пользователя")
    @Description("Изменение не авторизированного пользователя")
    //Изменение не авторизированного пользователя
    @Test
    public void testEditNotAuthUser(){
        //EDIT
        String newName = DataGenerator.getRandomFirstName();

        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makeNotAuthUserEdit("https://playground.learnqa.ru/api/user/"+this.idUser,
                        editData);
        //Проверка соответствия текста ошибки и статус кода ответа
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
        Assertions.assertResponseStatusCode(responseEditUser, 400);

        //GET
        Response responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(this.responseGetAuth, "x-csrf-token"),
                        this.getCookie(this.responseGetAuth, "auth_sid"));

        String oldName = getStrFromJson(responseUserData, "firstName");
        Assertions.assertJsonByName(responseUserData, "firstName", oldName);
        Assertions.assertJsonByNameNotEql(responseUserData, "firstName", newName);
    }

    @Story(value = "Неуспешное изменение профиля пользователя")
    @Description("Изменить данные пользователя, будучи авторизованными другим пользователем")
    @Flaky
    @Test
    public void testEditAuthAsAnotherUser(){
        //GET запрос данных первого пользователя для сохранения информации до попытки редактирования профиля
        Response  responseFirstUserDataBeforEditing = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(this.responseGetAuth, "x-csrf-token"),
                        this.getCookie(this.responseGetAuth, "auth_sid"));
        //Сохранение исходного значения поля "firstName" до попытки его редактирования
        String oldNameFirstUser = getStrFromJson(responseFirstUserDataBeforEditing, "firstName");


        //CREATE: Создание второго пользователя, id которого будем передавать при редактировании
        Map<String, String> secondUserData = DataGenerator.getRegistrationData();

        Response responseCreateSecondUser = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/", secondUserData);
        int idSecondUser = getIntFromJson(responseCreateSecondUser, "id");

        //EDIT: изменение данных пользователя, будучи авторизованными другим пользователем
        //формируем данные для редактирования поля newName
        String newName = "newtestFirstName";

        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        //редактируем данные по id вторго пользователя, авторизационные Cookie и токен передаем от первого пользоваеля (созданного в BeforeEach)
        Response responseEditUser = apiCoreRequests
                .makeAuthUserEdit("https://playground.learnqa.ru/api/user/"+idSecondUser ,
                        this.getHeader(this.responseGetAuth, "x-csrf-token"),
                        this.getCookie(this.responseGetAuth, "auth_sid"),
                        editData);

        //GET запрос данных первого пользователя после попытки редактирования
        Response  responseFirstUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(this.responseGetAuth, "x-csrf-token"),
                        this.getCookie(this.responseGetAuth, "auth_sid"));

        System.out.println("Значение firstName, которое передавалось при редактировании: "+editData.get("firstName"));
        System.out.println("Старое значение firstName первого пользователя перед выполнением редактирования: "+oldNameFirstUser);
        System.out.println("Тело ответа с данными первого пользователя, Cookie и токен которого передавались при редактировании: ");
        responseFirstUserData.prettyPrint();


        //Проверка того, что firstName первого пользователя не изменилось на новое
        //!!!!На данный момент тест падает, т.к. при редактировании пользователя с указанием id второго пользователя и передачей авторизационных данных другого пользователя происходит успешное редактирование профиля пользователя, авторизационные данные которого указаны
        Assertions.assertJsonByNameNotEql(responseFirstUserData, "firstName", newName);
        //Проверка того, что firstName первого пользователя осталось прежним
        Assertions.assertJsonByName(responseFirstUserData, "firstName", oldNameFirstUser);

       //Авторизация вторым пользователем
        Map<String, String> authDataSecondUser = new HashMap<>();
        authDataSecondUser.put("email", secondUserData.get("email"));
        authDataSecondUser.put("password", secondUserData.get("password"));
        Response responseGetAuthSecondUser = apiCoreRequests
                .makeUserLogin("https://playground.learnqa.ru/api/user/login", authDataSecondUser);

        //GET запрос данных второго пользователя
        Response  responseSecondUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+idSecondUser,
                        getHeader(responseGetAuthSecondUser, "x-csrf-token"),
                        getCookie(responseGetAuthSecondUser, "auth_sid"));

        //Проверка того, что firstName второго пользователя осталось прежним
        Assertions.assertJsonByName(responseSecondUserData, "firstName", secondUserData.get("firstName"));

    }

    @Story(value = "Неуспешное изменение профиля пользователя")
    @Description("Попытаемся изменить email пользователя на новый без символа @")
    @Test
    public void testEditIncorrectEmailAuthUser(){
        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("email", "test");

        Response responseEditUser = apiCoreRequests
                .makeAuthUserEdit("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);
        //Проверка соответствия текста ошибки и статус кода ответа ожидаемому результату
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
        Assertions.assertResponseStatusCode(responseEditUser, 400);

        //GET
        Response  responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        //Проверка того, что параметр email сохоанил старое значение
        Assertions.assertJsonByName(responseUserData, "email", this.testEmail);
    }

    @Story(value = "Неуспешное изменение профиля пользователя")
    @Description("Попытаемся изменить firstName пользователя на очень короткое значение в один символ")
    @Test
    public void testEditShortFirstNameAuthUser(){
        //GET
        Response  responseOldUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));
        String oldFirstName = getStrFromJson(responseOldUserData, "firstName");

        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", "t");

        Response responseEditUser = apiCoreRequests
                .makeAuthUserEdit("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData);
        //Проверка соответствия текста ошибки, статус кода ответа ожидаемому результату
        Assertions.assertResponseTextByKeyEquals(responseEditUser, "error", "Too short value for field firstName");
        Assertions.assertResponseStatusCode(responseEditUser, 400);

        //GET
        Response  responseUserData = apiCoreRequests
                .GetAuthUserData("https://playground.learnqa.ru/api/user/"+this.idUser,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        //Проверка того, что параметр email сохоанил старое значение
        Assertions.assertJsonByName(responseUserData, "firstName", oldFirstName);
    }
}
