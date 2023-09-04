package lib;
import io.qameta.allure.Step;
import io.restassured.http.Header;
import io.restassured.response.Response;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Выполнение post-запроса создания нового пользователя с переданными полями")
    public Response makeGetRequest(String url, Map<String, String> userData){

        return given()
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Выполнение post-запроса авторизации под пользователем")
    public Response makeUserLogin(String url, Map<String, String> authData){

        return given()
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Выполнение get-запроса получения данных по id не авторизированного пользователя")
    public Response GetNotAuthUserData(String url){

        return given()
                .get(url)
                .andReturn();
    }

    @Step("Выполнение get-запроса получения данных по id авторизированного пользователя")
    public Response GetAuthUserData(String url, String token, String cookie){

        return given()
                .header(new Header("x-csrf-token",token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }




}
