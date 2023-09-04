package lib;
import io.restassured.response.Response;

import java.text.SimpleDateFormat;


public class DataGenerator {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    public static String getRandomEmail(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "testEmail"+timestamp+"@example.com";
    }

    public static String getRandomUserName(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "testUserName"+timestamp;
    }

}
