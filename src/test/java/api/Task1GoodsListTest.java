package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;

// Задача 1 (лекция): 4 автотеста на GET /goods/list и POST /goods/add
@Tag("api")
class Task1GoodsListTest extends BaseApiTest {

    // 1. given() / when() / then(): проверяем код ответа и пустое тело
    @Test
    void listIsEmptyGivenWhenThen() {
        given()
                .baseUri(BASE_URI)
                .when()
                .get("/goods/list")
                .then()
                .statusCode(200)
                .body("goods", empty());
    }

    // 2. RequestSpecification: проверяем код ответа и пустое тело
    @Test
    void listIsEmptyWithRequestSpecification() {
        RequestSpecification spec = new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                .build();

        given()
                .spec(spec)
                .when()
                .get("/goods/list")
                .then()
                .statusCode(200)
                .body("goods", empty());
    }

    // 3. создаём товар через POST /goods/add и ищем его в списке
    //    встроенными проверками REST Assured
    @Test
    void addedProductIsInListRestAssured() {
        String name = randomName("Товар REST Assured");
        createProduct(name, 100.0);

        given()
                .baseUri(BASE_URI)
                .when()
                .get("/goods/list")
                .then()
                .statusCode(200)
                .body("goods.name", hasItem(name));
    }

    // 4. создаём товар через POST /goods/add и ищем его в списке через AssertJ
    @Test
    void addedProductIsInListAssertJ() {
        String name = randomName("Товар AssertJ");
        createProduct(name, 250.0);

        List<String> names = given()
                .baseUri(BASE_URI)
                .when()
                .get("/goods/list")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("goods.name", String.class);

        assertThat(names).contains(name);
    }
}
