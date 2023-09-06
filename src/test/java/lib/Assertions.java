package lib;
import lib.BaseTestCase;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Assertions {
    public static void assertJsonByName(Response Response, String name, int expectedValue){
       Response.then().assertThat().body("$", hasKey(name));

       int value = Response.jsonPath().getInt(name);
       assertEquals(expectedValue, value, "JSON value is not equal to expected value");

    }

    public static void assertJsonByName(Response Response, String name, String expectedValue){
        Response.then().assertThat().body("$", hasKey(name));

        String value = Response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");

    }

    public static void assertJsonByNameNotEql(Response Response, String name, String expectedValue){
        Response.then().assertThat().body("$", hasKey(name));

        String value = Response.jsonPath().getString(name);
        assertNotEquals(expectedValue, value, "JSON value is not equal to expected value");

    }

    public static void assertResponseTextEquals(Response Response, String expectedAnswer){
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "Response text is not as expected"
        );
    }

    public static void assertResponseTextByKeyEquals(Response Response, String name, String expectedAnswer){
        assertEquals(
                expectedAnswer,
                Response.jsonPath().getString(name),
                "Response text is not as expected"
        );
    }

    public static void assertJsonHasField(Response Response, String expectedFieldName){
        Response.then().assertThat().body("$", hasKey(expectedFieldName));
    }

    public static void assertJsonHasNotField(Response Response, String unexpectedFieldName){
        Response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }

    public static void assertJsonHasFields (Response Response, String[] expectedFieldNames){
        for (String expectedFieldName : expectedFieldNames){
            Assertions.assertJsonHasField(Response, expectedFieldName);
        }
    }

    public static void assertJsonHasNotFields (Response Response, String[] unexpectedFieldNames){
        for (String unexpectedFieldName : unexpectedFieldNames){
            Assertions.assertJsonHasNotField(Response, unexpectedFieldName);
        }
    }

    public static void assertResponseStatusCode(Response Response, int expectedAnswer){
        assertEquals(
                expectedAnswer,
                Response.statusCode(),
                "Response status code is not as expected"
        );
    }





}
