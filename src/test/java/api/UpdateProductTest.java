package api;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

// PATCH /goods/{id} — все возможные коды ответа
@Tag("api")
class UpdateProductTest extends BaseApiTest {

    @Test
    void updateProductReturnsOk() {
        int id = createProduct(randomName("Old name"), 10);
        String newName = randomName("New name");

        given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", newName, "price", 15))
                .when()
                .patch("/goods/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(newName));
    }

    @Test
    void updateNotFoundReturnsNotFound() {
        given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", randomName("Missing item"), "price", 10))
                .when()
                .patch("/goods/999999")
                .then()
                .statusCode(404)
                .body(containsString("is not found"));
    }

    @Test
    void updateEmptyNameReturnsBadRequest() {
        int id = createProduct(randomName("PATCH item"), 10);

        given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", "", "price", 10))
                .when()
                .patch("/goods/" + id)
                .then()
                .statusCode(400)
                .body("name", equalTo("Name cannot be empty"));
    }

    @Test
    void updateNegativePriceReturnsBadRequest() {
        int id = createProduct(randomName("PATCH item"), 10);

        given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", randomName("Price item"), "price", -5))
                .when()
                .patch("/goods/" + id)
                .then()
                .statusCode(400)
                .body("price", equalTo("Price can not be less, than 0"));
    }

    @Test
    void updateDuplicateNameReturnsBadRequest() {
        String firstName = randomName("First item");
        String secondName = randomName("Second item");
        createProduct(firstName, 10);
        int secondId = createProduct(secondName, 20);

        given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", firstName, "price", 20))
                .when()
                .patch("/goods/" + secondId)
                .then()
                .statusCode(400)
                .body(containsString("already exists"));
    }

    @Test
    void updateWithoutAuthorizationReturnsUnauthorized() {
        int id = createProduct(randomName("PATCH item"), 10);

        given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .body(Map.of("name", randomName("No auth patch"), "price", 10))
                .when()
                .patch("/goods/" + id)
                .then()
                .statusCode(401);
    }
}
