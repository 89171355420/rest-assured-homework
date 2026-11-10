package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class BaseApiTest {

    protected static final String BASE_URI = "http://localhost:8080";
    protected static final String LOGIN = "admin";
    protected static final String PASSWORD = "secret123";

    protected static final RequestSpecification AUTH_SPEC = new RequestSpecBuilder()
            .setBaseUri(BASE_URI)
            .setAuth(basic(LOGIN, PASSWORD))
            .setContentType(ContentType.JSON)
            .build();

    // чистим список товаров перед каждым тестом
    @BeforeEach
    void clearGoods() {
        List<Integer> ids = given()
                .baseUri(BASE_URI)
                .when()
                .get("/goods/list?page=0&size=1000")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("goods.id", Integer.class);

        if (ids != null) {
            for (Integer id : ids) {
                given()
                        .spec(AUTH_SPEC)
                        .when()
                        .delete("/goods/" + id)
                        .then()
                        .statusCode(200);
            }
        }
    }

    protected int createProduct(String name, double price) {
        int id = given()
                .spec(AUTH_SPEC)
                .body(Map.of("name", name, "price", price))
                .when()
                .post("/goods/add")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getInt("data.id");

        assertThat(id).isPositive();
        return id;
    }

    protected String randomName(String prefix) {
        return prefix + " " + System.nanoTime();
    }
}
