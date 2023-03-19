import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import base.ScooterRestClient;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends ScooterRestClient {
    private static final String ORDER_URI = BASE_URI + "orders/";

    @Step("Создать заказ {order}")
    public ValidatableResponse create(Order order) {
        return
                given()
                        .spec(getBaseReqSpec())
                        .body(order)
                        .when()
                        .post(ORDER_URI)
                        .then();

    }

    @Step("Удалить заказ {track}")
    public ValidatableResponse cancelOrder(int track) {
        return
                given()
                        .spec(getBaseReqSpec())
                        .body(track)
                        .when()
                        .put(ORDER_URI + track)
                        .then();
    }

    @Step("Получить список заказов")
    public ValidatableResponse getOrderList() {
        return
                given()
                        .spec(getBaseReqSpec())
                        .when()
                        .get(ORDER_URI)
                        .then();
    }
}