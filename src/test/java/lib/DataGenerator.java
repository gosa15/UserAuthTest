package lib;
import io.restassured.response.Response;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


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

    public static String getRandomFirstName(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "testFirstName"+timestamp;
    }

    public static Map<String,String> getRegistrationData() {
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail());
        data.put("password", "123");
        data.put("username", DataGenerator.getRandomUserName());
        data.put("firstName", "testFirstName");
        data.put("lastName", "testLastName");

        return data;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues){
        Map<String, String> defaultValues = DataGenerator.getRegistrationData();

        Map<String, String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys){
            if (nonDefaultValues.containsKey(key)){
                userData.put(key, nonDefaultValues.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }



        return userData;
    }







}
