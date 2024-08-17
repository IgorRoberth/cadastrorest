
import com.google.gson.Gson;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.registerParser;

public class RestAssured {

    public static String baseURI;

    @BeforeClass
    public static void setup() {

        RestAssured.baseURI = "http://localhost:8002";
        registerParser("text/plain", Parser.TEXT);
    }

    @Test
    public void listarTodosUsuarios200() {

        given()
                .when()
                .get(baseURI + "/usuarios")
                .then()
                .statusCode(200)
                .log().all()
        ;
    }

    @Test
    public void verificarUsuarioUnico200() {

        Users usuario = given()
                .queryParam("nome", "Igor Roberth")
                .when()
                .get(baseURI + "/usuarios")
                .then()
                .statusCode(200)
                .extract().jsonPath().getObject("find{it.nome == 'Igor Roberth'}", Users.class);

        Assert.assertTrue(usuario.getId() == 193);
        Assert.assertEquals("Igor Roberth", usuario.getNome());
        Assert.assertEquals("Igor Roberth", usuario.getUsername());
        Assert.assertEquals("igorroberth1992@gmail.com", usuario.getEmail());
        Assert.assertEquals(null, usuario.getSenha());
        Assert.assertEquals("11998102030", usuario.getTelefone());

        System.out.println(usuario);
    }

    @Test
    public void deveCriarUsuario201() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Ricardo Gonzalez\"," +
                        "\"username\": \"Rick\"," +
                        "\"email\": \"rick@gmail.com.br\"," +
                        "\"senha\": \"senhaRicardo\", " +
                        "\"telefone\": \"11978451235\"}")
                .when()
                .post(baseURI + "/usuarios")
                .thenReturn();

        Assert.assertNull(response.jsonPath().getString("senha"));
        System.out.println("Senha retornada como: " + response.jsonPath().getString("senha"));
        System.out.println("Código de status: " + response.getStatusCode());
    }

    @Test
    public void editaUsuarioExistente200() {

        Response response = given()
                .contentType("application/json")
                .body("{\"nome\": \"Kevin Aleatório Peixoto\"," +
                        "\"username\": \"Kevin\"," +
                        "\"email\": \"kevin@email.com\"," +
                        "\"senha\": \"senhaKevin\", " +
                        "\"telefone\": \"11988948024\"}")
                .when()
                .put(baseURI + "/usuarios/61")
                .thenReturn();

        int statusCode = response.getStatusCode();
        System.out.println("Código de status: " + statusCode);
        Assert.assertEquals(200, statusCode);

        String responseBody = response.getBody().asString().toLowerCase();
        System.out.println("Resposta do servidor: " + responseBody);
    }

    @Test
    public void realizarLoginComSucesso200() {

        // Cria um mapa com as credenciais de usuário
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "Igor Roberth");
        credentials.put("senha", "senhaIgor");

        String jsonCredentials = new Gson().toJson(credentials);

        ValidatableResponse body = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(jsonCredentials)
                .when()
                .post(baseURI + "/usuarios/login")
                .then()
                .statusCode(200)
                .body("mensagem", Matchers.equalTo("Login realizado com sucesso."));

    }

    @Test
    public void deveDeletarUsuarioDoBanco204() {

        given()
                .log().all()
                .when()
                .delete(baseURI + "/usuarios/contadelete/Rick")
                .then()
                .log().all()
                .statusCode(204)
        ;
    }
}