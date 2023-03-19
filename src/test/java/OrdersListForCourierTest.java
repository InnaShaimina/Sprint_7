import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class OrdersListForCourierTest {

    private OrderClient orderClient;
    private List orders;

    @Before
    public void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    //Проверь, что в тело ответа возвращается список заказов.
    @Test
    @DisplayName("It is possible to get list of courier's orders")
    public void getOrdersListCheck() {
        ValidatableResponse createResponse = orderClient.getOrderList();
        int statusCode = createResponse.extract().statusCode();
        orders = createResponse.extract().path("orders");
        assertEquals(HTTP_OK, statusCode);
        assertThat(orders, notNullValue());
    }
}
