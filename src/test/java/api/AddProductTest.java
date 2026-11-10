package api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

// POST /goods/add — все возможные коды ответа
@Tag("api")
class AddProductTest extends BaseApiTest {

    @Test
    void addValidProductReturnsOk() {
        given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", randomName("New product"), "price", 99.9))
                .when()
                .post("/goods/add")
                .then()
                .statusCode(200)
                .body("data.id", notNullValue())
                .body("message", equalTo("success"));
    }

    @Test
    void addDuplicateNameReturnsBadRequest() {
        String name = randomName("Duplicate");
        createProduct(name, 10);

        given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", name, "price", 20))
                .when()
                .post("/goods/add")
                .then()
                .statusCode(400)
                .body("message", containsString("already exists"));
    }

    @Test
    void addEmptyNameReturnsBadRequest() {
        given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", "", "price", 10))
                .when()
                .post("/goods/add")
                .then()
                .statusCode(400)
                .body("name", equalTo("Name cannot be empty"));
    }

    @Test
    void addNegativePriceReturnsBadRequest() {
        given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", randomName("Negative price item"), "price", -1))
                .when()
                .post("/goods/add")
                .then()
                .statusCode(400)
                .body("price", equalTo("Price can not be less, than 0"));
    }

    @Test
    void addWithoutAuthorizationReturnsUnauthorized() {
        given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body(Map.of("name", randomName("No auth item"), "price", 10))
                .when()
                .post("/goods/add")
                .then()
                .statusCode(401);
    }

    @Test
    void addWithBrokenJsonReturnsBadRequest() {
        given()
                .spec(AUTH_SPEC)
                .body("{ broken json")
                .when()
                .post("/goods/add")
                .then()
                .statusCode(400);
    }

    @Test
    void addWithoutBodyReturnsBadRequest() {
        given()
                .spec(AUTH_SPEC)
                .when()
                .post("/goods/add")
                .then()
                .statusCode(400)
                .body(containsString("Required request body is missing"));
    }

    @Test
    void addWithWrongContentTypeReturnsUnsupportedMediaType() {
        given()
                .spec(AUTH_SPEC)
                .contentType(ContentType.TEXT)
                .body("{\"name\":\"Text item\",\"price\":1}")
                .when()
                .post("/goods/add")
                .then()
                .statusCode(415);
    }
}
