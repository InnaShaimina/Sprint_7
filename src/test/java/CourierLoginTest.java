import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.CourierCredentials;
import model.CourierGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CourierLoginTest {
    private CourierClient courierClient;
    private int courierId;

    @Before
    public void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void cleanUp() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Courier can login with valid data")
    public void loginWithValidDataIsPossibleCheck() {
        Courier courier = CourierGenerator.getRandom();
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");
        assertEquals(HTTP_CREATED, statusCode);
        assertTrue(isCourierCreated);
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");
        assertTrue(courierId != 0);
    }

    @Test
    @DisplayName("It is impossible to login via incorrect login")
    public void loginWithIncorrectLoginIsImpossibleCheck() {
        Courier courier = CourierGenerator.getRandom();
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");
        assertEquals(HTTP_CREATED, statusCode);
        assertTrue(isCourierCreated);
        CourierCredentials courierCredentials = new CourierCredentials(courier.getLogin(), "Querty");
        ValidatableResponse loginResponse = courierClient.login(courierCredentials);
        int secondStatusCode = loginResponse.extract().statusCode();
        String isCourierAuthorized = loginResponse.extract().path("message");
        String expectedMessage = "Учетная запись не найдена";
        assertEquals(HTTP_NOT_FOUND, secondStatusCode);
        assertEquals(expectedMessage, isCourierAuthorized);
    }

    @Test
    @DisplayName("It is impossible to login via incorrect password")
    public void loginWithIncorrectPasswordIsImpossibleCheck() {
        Courier courier = CourierGenerator.getRandom();
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");
        assertEquals(HTTP_CREATED, statusCode);
        assertTrue(isCourierCreated);
        CourierCredentials courierCredential = new CourierCredentials("Querty", courier.getPassword());
        ValidatableResponse loginResponse = courierClient.login(courierCredential);
        int secondStatusCode = loginResponse.extract().statusCode();
        String isCourierAuthorized = loginResponse.extract().path("message");
        String expectedMessage = "Учетная запись не найдена";
        assertEquals(HTTP_NOT_FOUND, secondStatusCode);
        assertEquals(expectedMessage, isCourierAuthorized);
    }

    @Test
    @DisplayName("It is impossible to login without login and password")
    public void loginWithoutRequiredFieldIsImpossibleCheck() {
        CourierCredentials courierCredential = new CourierCredentials("", "Querty");
        ValidatableResponse loginResponse = courierClient.login(courierCredential);
        int secondStatusCode = loginResponse.extract().statusCode();
        String isCourierAuthorized = loginResponse.extract().path("message");
        String expectedMessage = "Недостаточно данных для входа";
        assertEquals(HTTP_BAD_REQUEST, secondStatusCode);
        assertEquals(expectedMessage, isCourierAuthorized);
    }

    @Test
    @DisplayName("It is impossible to login as non existent user")
    public void loginWithIncorrectDataIsImpossibleCheck() {
        Courier courier = CourierGenerator.getRandom();
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int secondStatusCode = loginResponse.extract().statusCode();
        String isCourierAuthorized = loginResponse.extract().path("message");
        String expectedMessage = "Учетная запись не найдена";
        assertEquals(HTTP_NOT_FOUND, secondStatusCode);
        assertEquals(expectedMessage, isCourierAuthorized);
    }
}
