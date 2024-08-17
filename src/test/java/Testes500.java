import com.google.gson.Gson;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Testes500 extends RestAssured {

    @Test
    public void tentativaDeEdicaoDeUsuarioComServidoComErroInterno500() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Carla Barros Noronha\"," +
                        "\"username\": \"Carla\"," +
                        "\"email\": \"carla@gmail.com\"," +
                        "\"senha\": \"senhaCarla\", " +
                        "\"telefone\": \"11988948024\"}")
                .when()
                .put(baseURI + "/usuarios/169")
                .thenReturn();

        String responseBody = response.getBody().asString();
        Assert.assertEquals(500, response.getStatusCode());
        System.out.println("Mensagem de erro: " + responseBody);
        System.out.println("Código de status: " + response.getStatusCode());
    }

    @Test
    public void tentarCriarUmNovoUsuárioQuando_O_ServidorEstaComErroInterno500() {

        given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flavio Bauler\"," +
                        "\"username\": \"Flavio\"," +
                        "\"email\": \"flavio@gmail.com.br\"," +
                        "\"senha\": \"senhaFlavio\", " +
                        "\"telefone\": \"11997294629\"}")
                .when()
                .post(baseURI +"/usuarios")
                .then()
                .log().all()
                .statusCode(500)
        ;
    }

    @Test
    public void tentativaDeLogin_O_ServidorComErroInterno500() {

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "Roberto");
        credentials.put("senha", "senhaRoberto");

        String jsonCredentials = new Gson().toJson(credentials);

        ValidatableResponse body = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(jsonCredentials)
                .when()
                .post(baseURI + "/usuarios/login")
                .then()
                .statusCode(500)
                ;
    }

    @Test
    public void tentarExecutarExclusãoDeUsuarioQuando_O_ServidorEstaComErroInterno500() {

        given()
                .log().all()
                .when()
                .delete(baseURI +"/usuarios/contadelete/Carla")
                .then()
                .log().all()
                .statusCode(500)
        ;
    }

    @Test
    public void listarUsuáriosQuando_O_ServidorEstaComErroInterno500() {

        given()
                .when()
                .get(baseURI +"/usuarios")
                .then()
                .statusCode(500)
                .log().all()
        ;
    }

    @Test
    public void tentativaDeCriarUsuárioComServidorInoperante500() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flavio Flowers\"," +
                        "\"username\":\"Flavio\"," +
                        "\"email\": \"flavio@email.com\"," +
                        "\"senha\": \"senhaFlavio\", " +
                        "\"telefone\": \"11978508976\"}")
                .when()
                .post(baseURI + "/usuarios")
                .then()
                .statusCode(500)
                .extract().response();
        System.out.println(response.getBody().asString());
    }
}
