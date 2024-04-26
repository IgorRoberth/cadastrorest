
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
import static org.hamcrest.Matchers.*;

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
                .get(baseURI+"/usuarios")
                .then()
                .statusCode(200)
                .log().all()
        ;
    }

    @Test
    public void verificarUsuarioUnico200() {

        Users usuario = given()
                .queryParam("nome", "Jorgino Alfredo Gonçalves")
                .when()
                .get(baseURI+"/usuarios")
                .then()
                .statusCode(200)
                .extract().jsonPath().getObject("find{it.nome == 'Jorgino Alfredo Gonçalves'}", Users.class);

        Assert.assertTrue(usuario.getId() == 12);
        Assert.assertEquals("Jorgino Alfredo Gonçalves", usuario.getNome());
        Assert.assertEquals("Jorgino", usuario.getUsername());
        Assert.assertEquals("jorgino@email.com", usuario.getEmail());
        Assert.assertEquals("$2a$10$v5wFlt4WCgYzXWotN7XRuOd9Il2rhRmwWF1IAkJ2jXjOTUClJXVda", usuario.getSenha());
        Assert.assertEquals("11960503456", usuario.getTelefone());

        System.out.println(usuario);
    }

    @Test
    public void deveCriarUsuario201() {

        given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Vitor Tiago Luiz Porto\"," +
                        "\"username\": \"Vitor\"," +
                        "\"email\": \"vitortiagoporto@gmail.com.br\"," +
                        "\"senha\": \"senhaVitor\", " +
                        "\"telefone\": \"11997299814\"}")
                .when()
                .post(baseURI+"/usuarios")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", Matchers.is(notNullValue()))
                .body("nome", is("Vitor Tiago Luiz Porto"))
                .body("username", is("Vitor"))
                .body("email", Matchers.is("vitortiagoporto@gmail.com.br"))
                .body("senha", is("senhaVitor"))
                .body("telefone", is("11997299814"))
        ;
    }

    @Test
    public void validarUsernameEmUso409() {

        given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Tiago Luiz Porto\"," +
                        "\"username\": \"Vitor\"," +
                        "\"email\": \"tiagoporto@gmail.com.br\"," +
                        "\"senha\": \"senhaTiago\", " +
                        "\"telefone\": \"11990234528\"}")
                .when()
                .post(baseURI+"/usuarios")
                .then()
                .log().all()
                .statusCode(409)
                .body("error", Matchers.is("O username já está em uso por outro usuário."))
        ;
    }

    @Test
    public void validarEmailEmUso409() {

        given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Tiago Luiz Porto\"," +
                        "\"username\": \"Tiago\"," +
                        "\"email\": \"vitortiagoporto@gmail.com.br\"," +
                        "\"senha\": \"senhaTiago\", " +
                        "\"telefone\": \"11990234528\"}")
                .when()
                .post(baseURI+"/usuarios")
                .then()
                .log().all()
                .statusCode(409)
                .body("error", Matchers.is("O email já está em uso por outro usuário."))
        ;
    }

    @Test
    public void validarTelefoneEmUso409() {

        given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flasino\"," +
                        "\"username\": \"Flausino\"," +
                        "\"email\": \"flausinoporto@gmail.com\"," +
                        "\"senha\": \"senhaFlau\", " +
                        "\"telefone\": \"11990234528\"}")
                .when()
                .post(baseURI+"/usuarios")
                .then()
                .log().all()
                .statusCode(409)
                .body("error", Matchers.is("O telefone já está em uso por outro usuário."))
        ;
    }

    @Test
    public void editaUsuarioExistente200() {

        Response response = given()
                .contentType("application/json")
                .body("{\"nome\": \"Carla Barros\"," +
                        "\"username\": \"Carla\"," +
                        "\"email\": \"carla@gmail.com\"," +
                        "\"senha\": \"senhaCarla\", " +
                        "\"telefone\": \"11988948024\"}")
                .when()
                .put(baseURI + "/usuarios/170")
                .thenReturn();

        int statusCode = response.getStatusCode();
        System.out.println("Código de status: " + statusCode);
        Assert.assertEquals(200, statusCode);

        String responseBody = response.getBody().asString().toLowerCase();
        System.out.println("Resposta do servidor: " + responseBody);
    }

    @Test
    public void tentativaDeEdicaoComTelefoneIncorreto400() {

        String mensagemEsperada = "O campo telefone não pode conter letras ou qualquer tipo de caracteres especiais.";

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Kevin Peixoto Alves\"," +
                        "\"username\": \"Kevin\"," +
                        "\"email\": \"kevinalves@gmail.com\"," +
                        "\"senha\": \"senhaKevin\", " +
                        "\"telefone\": \"1199838fea9508\"}")
                .when()
                .put(baseURI + "/usuarios/61")
                .thenReturn();
        String responseBody = response.getBody().asString();
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertTrue(responseBody.contains(mensagemEsperada));
        System.out.println("Mensagem de erro: " + responseBody);
        System.out.println("Código de status: " + response.getStatusCode());
    }

    @Test
    public void tentativaDeEdicaoComUsuarioInexistente404() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Kevin Peixoto Alves\"," +
                        "\"username\": \"Kevin\"," +
                        "\"email\": \"kevinalves@gmail.com\"," +
                        "\"senha\": \"senhaKevin\", " +
                        "\"telefone\": \"11998389508\"}")
                .when()
                .put(baseURI + "/usuarios/600")
                .thenReturn();

        String responseBody = response.getBody().asString();
        Assert.assertEquals(404, response.getStatusCode());
        System.out.println("Mensagem de erro: " + responseBody);
        System.out.println("Código de status: " + response.getStatusCode());
    }

    @Test
    public void realizarLoginComSucesso200() {

        // Cria um mapa com as credenciais de usuário
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "Kevin");
        credentials.put("senha", "senhaKevin");

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
    public void tentativaDeLoginComUsername_E_SenhaEmBranco400() {

        // Cria um mapa com as credenciais de usuário
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "");
        credentials.put("senha", "");

        String jsonCredentials = new Gson().toJson(credentials);

        ValidatableResponse body = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(jsonCredentials)
                .when()
                .post(baseURI + "/usuarios/login")
                .then()
                .statusCode(400)
                .body("erro", Matchers.equalTo("Usuário não encontrado. Por favor, cadastre-se primeiro ou verifique suas credenciais e tente novamente."));
    }

    @Test
    public void tentativaDeLoginComSenhaIncorreta400() {

        // Cria um mapa com as credenciais de usuário
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "Igor Roberth");
        credentials.put("senha", "senha");

        String jsonCredentials = new Gson().toJson(credentials);

        ValidatableResponse body = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(jsonCredentials)
                .when()
                .post(baseURI + "/usuarios/login")
                .then()
                .statusCode(400)
                .body("erro", Matchers.equalTo("Senha incorreta ou não foi fornecida. Por favor, verifique."));
    }

    @Test
    public void tentativaDeLoginComUsuarioInexistente400() {

        // Cria um mapa com as credenciais de usuário
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "API Jose");
        credentials.put("senha", "senha");

        String jsonCredentials = new Gson().toJson(credentials);

        ValidatableResponse body = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(jsonCredentials)
                .when()
                .post(baseURI + "/usuarios/login")
                .then()
                .statusCode(400)
                .body("erro", Matchers.equalTo("Usuário não encontrado. Por favor, cadastre-se primeiro ou verifique suas credenciais e tente novamente."));
    }

    @Test
    public void deveDeletarUsuarioDoBanco204() {

        given()
                .log().all()
                .when()
                .delete(baseURI+"/usuarios/contadelete/Flavio")
                .then()
                .log().all()
                .statusCode(204)
        ;
    }

    @Test
    public void tentartivaDeExclusaoUsuarioInexistente404() {

        given()
                .log().all()
                .when()
                .delete(baseURI+"/usuarios/contadelete/Fraga")
                .then()
                .log().all()
                .statusCode(404)
        ;
    }

    @Test
    public void validarUmNovoUsuárioComCampoNomeSemPreencher400() {
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"\"," +
                        "\"username\": \"Luiz\"," +
                        "\"email\": \"luizporto@gmail.com\"," +
                        "\"senha\": \"senhaLuiz\", " +
                        "\"telefone\": \"11990234528\"}")
                .when()
                .post(baseURI+"/usuarios")
                .then()
                .log().all()
                .statusCode(400)
                .contentType("text/plain")
                .body(Matchers.equalTo("Para concluir o cadastro é necessário preencher todos os campos."))
        ;
    }

    @Test
    public void tentativaDeEdicaoDeUsuarioComDadosInvalidos400() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Carla Barros\"," +
                        "\"username\": \"Carla\"," +
                        "\"email\": \"carla@gm\"," +
                        "\"senha\": \"senhaCarla\", " +
                        "\"telefone\": \"11988948024\"}")
                .when()
                .put(baseURI + "/usuarios/170")
                .thenReturn();

        String responseBody = response.getBody().asString();
        Assert.assertEquals(400, response.getStatusCode());
        System.out.println("Mensagem de erro: " + responseBody);
        System.out.println("Código de status: " + response.getStatusCode());
    }

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
                .put(baseURI + "/usuarios/170")
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
                .post(baseURI+"/usuarios")
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
                .delete(baseURI+"/usuarios/contadelete/Carla")
                .then()
                .log().all()
                .statusCode(500)
        ;
    }

    @Test
    public void listarUsuáriosQuando_O_ServidorEstaComErroInterno500() {

        given()
                .when()
                .get(baseURI+"/usuarios")
                .then()
                .statusCode(500)
                .log().all()
        ;
    }

    @Test
    public void tentativaDeListarUsuariosComMetodoPost405() {

        given()
                .when()
                .post(baseURI+"/usuarios")
                .then()
                .statusCode(415)
                .log().all()
        ;
    }

    @Test
    public void tentativaDeInclusãoDeUsuárioSemNome400() {

       Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"\"," +
                        "\"username\": \"Flavio\"," +
                        "\"email\": \"flavio@gmail.com.br\"," +
                        "\"senha\": \"senhaFlavio\", " +
                        "\"telefone\": \"11997294629\"}")
                .when()
                .post(baseURI+"/usuarios")
                .thenReturn();

        String responseBody = response.getBody().asString();
        Assert.assertEquals(400, response.getStatusCode());
        System.out.println("Mensagem de erro: " + responseBody);
        System.out.println("Código de status: " + response.getStatusCode());
    }

    @Test
    public void tentativaDeInclusãoDeUsuárioSemUsername400() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flavio Flowers\"," +
                        "\"username\": \"\"," +
                        "\"email\": \"flavio@gmail.com.br\"," +
                        "\"senha\": \"senhaFlavio\", " +
                        "\"telefone\": \"11997294629\"}")
                .when()
                .post(baseURI+"/usuarios")
                .thenReturn();

        String responseBody = response.getBody().asString();
        Assert.assertEquals(400, response.getStatusCode());
        System.out.println("Mensagem de erro: " + responseBody);
        System.out.println("Código de status: " + response.getStatusCode());
    }

    @Test
    public void tentativaDeInclusãoDeUsuárioComEmailEscritoDeFormaIncorreta400() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flavio Flowers\"," +
                            "\"username\":\"Flavio\"," +
                            "\"email\": \"flavio@gm\"," +
                            "\"senha\": \"senhaFlavio\", " +
                            "\"telefone\": \"11997294629\"}")
                .when()
                .post(baseURI + "/usuarios")
                .then()
                .statusCode(400)
                .extract().response();
            System.out.println(response.getBody().asString());
    }

    @Test
    public void tentativaDeCriarUsuárioComCampoSenhaVazio400() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flavio Flowers\"," +
                        "\"username\":\"Flavio\"," +
                        "\"email\": \"flavio@gmail.com\"," +
                        "\"senha\": \"\", " +
                        "\"telefone\": \"11997294629\"}")
                .when()
                .post(baseURI + "/usuarios")
                .then()
                .statusCode(400)
                .extract().response();
        System.out.println(response.getBody().asString());
    }

    @Test
    public void tentativaDeCriarUsuárioComCampoTelefoneInvalido400() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flavio Flowers\"," +
                        "\"username\":\"Flavio\"," +
                        "\"email\": \"flavio@gmail.com\"," +
                        "\"senha\": \"senhaFlavio\", " +
                        "\"telefone\": \"119972A94629\"}")
                .when()
                .post(baseURI + "/usuarios")
                .then()
                .statusCode(400)
                .extract().response();
        System.out.println(response.getBody().asString());
    }

    @Test
    public void tentativaDeCriarUsuárioComUsernameEmUso400() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flavio Flowers\"," +
                        "\"username\":\"Flavio\"," +
                        "\"email\": \"renan@email.com\"," +
                        "\"senha\": \"senhaFlavio\", " +
                        "\"telefone\": \"11997294629\"}")
                .when()
                .post(baseURI + "/usuarios")
                .then()
                .statusCode(409)
                .extract().response();
        System.out.println(response.getBody().asString());
    }

    @Test
    public void tentativaDeCriarUsuárioComTelefoneEmUso400() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flavio Flowers\"," +
                        "\"username\":\"Flavio\"," +
                        "\"email\": \"flavio@email.com\"," +
                        "\"senha\": \"senhaFlavio\", " +
                        "\"telefone\": \"11978502350\"}")
                .when()
                .post(baseURI + "/usuarios")
                .then()
                .statusCode(409)
                .extract().response();
        System.out.println(response.getBody().asString());
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

    @Test
    public void editaUsuarioComMetodoGET405() {

        Response response = given()
                .contentType("application/json")
                .body("{\"nome\": \"Carla Barros\"," +
                        "\"username\": \"Carla\"," +
                        "\"email\": \"carla@gmail.com\"," +
                        "\"senha\": \"senhaCarla\", " +
                        "\"telefone\": \"11988948024\"}")
                .when()
                .get(baseURI + "/usuarios/170")
                .thenReturn();

        int statusCode = response.getStatusCode();
        System.out.println("Código de status: " + statusCode);
        Assert.assertEquals(405, statusCode);

        String responseBody = response.getBody().asString().toLowerCase();
        System.out.println("Resposta do servidor: " + responseBody);
    }

    @Test
    public void tetnativaDeEditaUsuarioComCampoNomeVazio400() {

        Response response = given()
                .contentType("application/json")
                .body("{\"nome\": \"\"," +
                        "\"username\": \"Carla Blazer\"," +
                        "\"email\": \"carlab@gmail.com\"," +
                        "\"senha\": \"senhaCarla\", " +
                        "\"telefone\": \"11988948024\"}")
                .when()
                .put(baseURI + "/usuarios/170")
                .thenReturn();

        int statusCode = response.getStatusCode();
        System.out.println("Código de status: " + statusCode);
        Assert.assertEquals(400, statusCode);

        String responseBody = response.getBody().asString().toLowerCase();
        System.out.println("Resposta do servidor: " + responseBody);
    }

    @Test
    public void tetnativaDeEditaUsuarioComCampoUsernameVazio400() {

        Response response = given()
                .contentType("application/json")
                .body("{\"nome\": \"Carla Barros de Lima\"," +
                        "\"username\": \"\"," +
                        "\"email\": \"carlab@gmail.com\"," +
                        "\"senha\": \"senhaCarla\", " +
                        "\"telefone\": \"11988948024\"}")
                .when()
                .put(baseURI + "/usuarios/170")
                .thenReturn();

        int statusCode = response.getStatusCode();
        System.out.println("Código de status: " + statusCode);
        Assert.assertEquals(400, statusCode);

        String responseBody = response.getBody().asString();
        System.out.println("Resposta do servidor: " + responseBody);
    }

    @Test
    public void tetnativaDeEditaUsuarioComEmailIndevido400() {

        Response response = given()
                .contentType("application/json")
                .body("{\"nome\": \"Carla Barros de Lima\"," +
                        "\"username\": \"Carla Barros\"," +
                        "\"email\": \"carlab@gmai\"," +
                        "\"senha\": \"senhaCarla\", " +
                        "\"telefone\": \"11988948024\"}")
                .when()
                .put(baseURI + "/usuarios/170")
                .thenReturn();

        int statusCode = response.getStatusCode();
        System.out.println("Código de status: " + statusCode);
        Assert.assertEquals(400, statusCode);

        String responseBody = response.getBody().asString();
        System.out.println("Resposta do servidor: " + responseBody);
    }

    @Test
    public void tetnativaDeEditaUsuarioComCampoSenhaVazio400() {

        Response response = given()
                .contentType("application/json")
                .body("{\"nome\": \"Carla Barros de Lima\"," +
                        "\"username\": \"Carla Barros\"," +
                        "\"email\": \"carlab@gmail.com\"," +
                        "\"senha\": \"\", " +
                        "\"telefone\": \"11988948024\"}")
                .when()
                .put(baseURI + "/usuarios/170")
                .thenReturn();

        int statusCode = response.getStatusCode();
        System.out.println("Código de status: " + statusCode);
        Assert.assertEquals(400, statusCode);

        String responseBody = response.getBody().asString();
        System.out.println("Resposta do servidor: " + responseBody);
    }

    @Test
    public void tetnativaDeEditaUsuarioComTelefoneInvalido400() {

        Response response = given()
                .contentType("application/json")
                .body("{\"nome\": \"Carla Barros de Lima\"," +
                        "\"username\": \"Carla Barros\"," +
                        "\"email\": \"carlab@gmail.com\"," +
                        "\"senha\": \"senhaCarla\", " +
                        "\"telefone\": \"119889S48024\"}")
                .when()
                .put(baseURI + "/usuarios/170")
                .thenReturn();

        int statusCode = response.getStatusCode();
        System.out.println("Código de status: " + statusCode);
        Assert.assertEquals(400, statusCode);

        String responseBody = response.getBody().asString();
        System.out.println("Resposta do servidor: " + responseBody);
    }

    @Test
    public void tetnativaDeLoginComMetodoGET405() {

            // Cria um mapa com as credenciais de usuário
            Map<String, String> credentials = new HashMap<>();
            credentials.put("username", "Roberto");
            credentials.put("senha", "senhaRoberto");

            String jsonCredentials = new Gson().toJson(credentials);

            ValidatableResponse body = given()
                    .log().all()
                    .header("Content-Type", "application/json")
                    .body(jsonCredentials)
                    .when()
                    .get(baseURI + "/usuarios/login")
                    .then()
                    .statusCode(405)
                    ;
            }

    @Test
    public void tetnativaDeLoginComCampoSenhaVazio400() {

        // Cria um mapa com as credenciais de usuário
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "Kevin");
        credentials.put("senha", "");

        String jsonCredentials = new Gson().toJson(credentials);

        ValidatableResponse body = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(jsonCredentials)
                .when()
                .post(baseURI + "/usuarios/login")
                .then()
                .statusCode(400)
                .body("erro",Matchers.equalTo("Senha incorreta ou não foi fornecida. Por favor, verifique."))
                ;
    }

    @Test
    public void tetnativaDeLoginComUsernameIncorreto400() {

        // Cria um mapa com as credenciais de usuário
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "Kevin Jorge");
        credentials.put("senha", "SenhaKevin");

        String jsonCredentials = new Gson().toJson(credentials);

        ValidatableResponse body = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(jsonCredentials)
                .when()
                .post(baseURI + "/usuarios/login")
                .then()
                .statusCode(400)
                .body("erro",Matchers.equalTo("Usuário não encontrado. Por favor, cadastre-se primeiro ou verifique suas credenciais e tente novamente."))
                ;
    }

    @Test
    public void tetnativaDeLoginComSenhaIncorreta400() {

        // Cria um mapa com as credenciais de usuário
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", "Kevin");
        credentials.put("senha", "Senha");

        String jsonCredentials = new Gson().toJson(credentials);

        ValidatableResponse body = given()
                .log().all()
                .header("Content-Type", "application/json")
                .body(jsonCredentials)
                .when()
                .post(baseURI + "/usuarios/login")
                .then()
                .statusCode(400)
                .body("erro",Matchers.equalTo("Senha incorreta ou não foi fornecida. Por favor, verifique."))
                ;
    }

    @Test
    public void tetnativaDeExclusaoComMetodoGET405() {

            given()
                    .log().all()
                    .when()
                    .get(baseURI+"/usuarios/contadelete/Flavio")
                    .then()
                    .log().all()
                    .statusCode(405)
            ;
    }

    @Test
    public void tetnativaDeExclusaoSemInserir_O_Username() {

        given()
                .log().all()
                .when()
                .delete(baseURI+"/usuarios/contadelete/")
                .then()
                .log().all()
                .statusCode(404)
        ;
    }
}