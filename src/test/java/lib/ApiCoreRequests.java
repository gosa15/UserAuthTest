package lib;
import io.qameta.allure.Step;
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
}
