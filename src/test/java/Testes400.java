import com.google.gson.Gson;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.given;

public class Testes400 extends RestAssured {

    @Test
    public void validarUsernameEmUso409() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Tiago Luiz Porto\"," +
                        "\"username\": \"Vitor\"," +
                        "\"email\": \"tiagoporto@gmail.com.br\"," +
                        "\"senha\": \"senhaTiago\", " +
                        "\"telefone\": \"11990234528\"}")
                .when()
                .post(baseURI+"/usuarios")
                .thenReturn();

        Assert.assertEquals(409, response.getStatusCode());
        System.out.println("Mensagem de erro: " + response.getBody().asString());
        System.out.println("Código de status: " + response.getStatusCode());
    }

    @Test
    public void validarEmailEmUso409() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Tiago Luiz Porto\"," +
                        "\"username\": \"Tiago\"," +
                        "\"email\": \"vitortiagoporto@gmail.com.br\"," +
                        "\"senha\": \"senhaTiago\", " +
                        "\"telefone\": \"11990234528\"}")
                .when()
                .post(baseURI+"/usuarios")
                .thenReturn();
        Assert.assertEquals(409, response.getStatusCode());
        System.out.println("Mensagem de erro: " + response.getBody().asString());
        System.out.println("Código de status: " + response.getStatusCode());

    }

    @Test
    public void validarTelefoneEmUso409() {

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Flasino\"," +
                        "\"username\": \"Flausino\"," +
                        "\"email\": \"flausinopto@gmail.com\"," +
                        "\"senha\": \"senhaFlau\"," +
                        "\"telefone\": \"11990234528\"}")
                .when()
                .put(baseURI + "/usuarios/169")
                .thenReturn();

        Assert.assertEquals(409, response.getStatusCode());
        System.out.println("Mensagem de erro: " + response.getBody().asString());
        System.out.println("Código de status: " + response.getStatusCode());
    }

    @Test
    public void tentativaDeEdicaoComTelefoneIncorreto400() {

        String mensagemEsperada = "O campo telefone não pode conter letras ou caracteres especiais.";

        Response response = given()
                .log().all()
                .contentType("application/json")
                .body("{\"nome\": \"Kevin Peixoto Alves\"," +
                        "\"username\": \"Kevin\"," +
                        "\"email\": \"kevin@email.com\"," +
                        "\"senha\": \"null\", " +
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
    public void tentativaDeExclusaoUsuarioInexistente404() {

        given()
                .log().all()
                .when()
                .delete(baseURI +"/usuarios/contadelete/Fraga")
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
                .post(baseURI +"/usuarios")
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
        Assert.assertEquals(404, response.getStatusCode());
        System.out.println("Mensagem de erro: " + responseBody);
        System.out.println("Código de status: " + response.getStatusCode());
    }

    @Test
    public void tentativaDeListarUsuariosComMetodoPost415() {

        given()
                .when()
                .post(baseURI +"/usuarios")
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
                .post(baseURI +"/usuarios")
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
                .post(baseURI +"/usuarios")
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
        Assert.assertEquals(404, statusCode);

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
        Assert.assertEquals(404, statusCode);

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
        Assert.assertEquals(404, statusCode);

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
        Assert.assertEquals(404, statusCode);

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
                .put(baseURI + "/usuarios/173")
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
                .get(RestAssured.baseURI +"/usuarios/contadelete/Flavio")
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
