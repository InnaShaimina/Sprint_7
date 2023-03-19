import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class OrderParametrizedTest {
    private OrderClient orderClient;
    private int track;
    @Parameterized.Parameter()
    public String[] color;

    @Parameterized.Parameters()
    public static Object[] params() {
        return new Object[][]{
                {new String[]{"BLACK", "GRAY"}},
                {new String[]{"BLACK"}},
                {new String[]{"GRAY"}},
                {new String[]{}},
        };
    }

    @Before
    public void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @After
    public void cleanUp() {
        orderClient.cancelOrder(track);
    }

    @Test
    @DisplayName("It is possible to create new order")
    public void createOrderParametrizedCheck() {
        Order order = new Order("Джейн", "Доу", "Нью - Вегас",
                "Убежище 42", "+79098888888", 5, "2023-03-23", "Тестовый комментарий", color);
        ValidatableResponse createResponse = orderClient.create(order);
        int statusCode = createResponse.extract().statusCode();
        track = createResponse.extract().path("track");
        assertEquals(HTTP_CREATED, statusCode);
        assertThat(track, greaterThan(0));
    }
}
