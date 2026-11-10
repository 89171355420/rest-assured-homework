package api;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

// GET /goods/list — все возможные коды ответа
@Tag("api")
class ListEndpointTest extends BaseApiTest {

    @Test
    void listReturnsOk() {
        given()
                .baseUri(BASE_URI)
                .when()
                .get("/goods/list")
                .then()
                .statusCode(200)
                .body("goods", notNullValue());
    }

    @Test
    void listWithNegativePageReturnsBadRequest() {
        given()
                .baseUri(BASE_URI)
                .queryParam("page", -1)
                .when()
                .get("/goods/list")
                .then()
                .statusCode(400)
                .body(equalTo("Page index must not be less than zero"));
    }

    @Test
    void listWithZeroSizeReturnsBadRequest() {
        given()
                .baseUri(BASE_URI)
                .queryParam("size", 0)
                .when()
                .get("/goods/list")
                .then()
                .statusCode(400)
                .body(equalTo("Page size must not be less than one"));
    }
}
