import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import base.ScooterRestClient;
import model.Courier;
import model.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient extends ScooterRestClient {

    private static final String COURIER_URI = BASE_URI + "courier/";

    @Step("Создать курьера {courier}")
    public ValidatableResponse create(Courier courier) {
        return given()
                .spec(getBaseReqSpec())
                .body(courier)
                .when()
                .post(COURIER_URI)
                .then();
    }

    @Step("Login как {courierCredentials}")
    public ValidatableResponse login(CourierCredentials courierCredentials) {
        return given()
                .spec(getBaseReqSpec())
                .body(courierCredentials)
                .when()
                .post(COURIER_URI+ "login/")
                .then();
    }

    @Step("Удалить курьера с id: {id}")
    public ValidatableResponse delete(int id) {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .delete(COURIER_URI + id)
                .then();
    }

}
