package api;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

// DELETE /goods/{id} — все возможные коды ответа
@Tag("api")
class DeleteProductTest extends BaseApiTest {

    @Test
    void deleteExistingProductReturnsOk() {
        int id = createProduct(randomName("Item to delete"), 10);

        given()
                .spec(AUTH_SPEC)
                .when()
                .delete("/goods/" + id)
                .then()
                .statusCode(200)
                .body(containsString("has been deleted successfully"));
    }

    @Test
    void deleteNotFoundReturnsNotFound() {
        given()
                .spec(AUTH_SPEC)
                .when()
                .delete("/goods/999999")
                .then()
                .statusCode(404)
                .body(containsString("is not found"));
    }

    @Test
    void deleteWithoutAuthorizationReturnsUnauthorized() {
        given()
                .baseUri(BASE_URI)
                .when()
                .delete("/goods/999999")
                .then()
                .statusCode(401);
    }
}
