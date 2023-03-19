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


public class CourierCreationTest {
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
    @DisplayName("It is possible to create courier with valid data")
    public void createCourierWithValidDataIsPossibleCheck() {
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
    @DisplayName("It is impossible to create courier duplicate")
    public void createCourierDoubleIsImpossibleCheck() {
        Courier courier = CourierGenerator.getRandom();
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");
        assertEquals(HTTP_CREATED, statusCode);
        assertTrue(isCourierCreated);

        ValidatableResponse createSecondResponse = courierClient.create(courier);
        int secondStatusCode = createSecondResponse.extract().statusCode();
        String isSecondCourierCreated = createSecondResponse.extract().path("message");
        String expectedMessage = "Этот логин уже используется. Попробуйте другой.";
        assertEquals(HTTP_CONFLICT, secondStatusCode);
        assertEquals(expectedMessage, isSecondCourierCreated);


    }

    @Test
    @DisplayName("Login and password fields are required to create courier")
    public void createCourierWithoutRequiredFieldsIsImpossibleCheck() {
        Courier courier = new Courier("", "");
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        String isCourierCreated = createResponse.extract().path("message");
        String expectedMessage = "Недостаточно данных для создания учетной записи";
        assertEquals(HTTP_BAD_REQUEST, statusCode);
        assertEquals(expectedMessage, isCourierCreated);
    }
}