package api;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

// GET /goods/{id} — все возможные коды ответа
@Tag("api")
class GetProductByIdTest extends BaseApiTest {

    @Test
    void getExistingProductReturnsOk() {
        String name = randomName("GET item");
        int id = createProduct(name, 12.5);

        given()
                .spec(AUTH_SPEC)
                .when()
                .get("/goods/" + id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("name", equalTo(name))
                .body("price", equalTo(12.5f));
    }

    @Test
    void getMissingProductReturnsNotFound() {
        // ищем id, которого нет в списке
        List<Integer> existingIds = given()
                .baseUri(BASE_URI)
                .when()
                .get("/goods/list?page=0&size=100")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("goods.id", Integer.class);

        int missingId = 1;
        while (existingIds != null && existingIds.contains(missingId)) {
            missingId++;
        }

        given()
                .spec(AUTH_SPEC)
                .when()
                .get("/goods/" + missingId)
                .then()
                .statusCode(404)
                .body("message", containsString("is not found"));
    }

    @Test
    void getProductWithInvalidIdReturnsBadRequest() {
        given()
                .spec(AUTH_SPEC)
                .when()
                .get("/goods/abc")
                .then()
                .statusCode(400)
                .body(containsString("Failed to convert"));
    }

    @Test
    void getWithoutAuthorizationReturnsUnauthorized() {
        given()
                .baseUri(BASE_URI)
                .when()
                .get("/goods/1")
                .then()
                .statusCode(401);
    }
}
