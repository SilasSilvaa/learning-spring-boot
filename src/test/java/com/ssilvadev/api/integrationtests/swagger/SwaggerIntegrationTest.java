package com.ssilvadev.api.integrationtests.swagger;

import static org.junit.Assert.assertTrue;
import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.ssilvadev.api.config.TestConfigs;
import com.ssilvadev.api.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void shouldDisplaySwaggerUIPage() {
        var content = given()
            .basePath("/swagger-ui/index.html")
                .port(TestConfigs.SERVER_PORT)
            .when()
                .get()
            .then()
                .statusCode(200)
            .extract()
                .body()
                    .asString();
        
        assertTrue(content.contains("Swagger UI"));
    }
}
