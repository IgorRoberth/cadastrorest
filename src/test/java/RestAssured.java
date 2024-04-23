
import org.junit.Assert;
import org.junit.Test;
import static io.restassured.RestAssured.given;

public class RestAssured {

    @Test
    public void listarTodosUsuarios() {

        given()
                .when()
                .get("http://localhost:8002/usuarios")
                .then()
                .statusCode(200)
                .log().all()
        ;
    }

    @Test
    public void verificarUsuarioUnico() {

        Users usuario = given()
                .queryParam("nome", "Jorgino Alfredo Gonçalves")
                .when()
                .get("http://localhost:8002/usuarios")
                .then()
                .statusCode(200)
                .extract().jsonPath().getObject("find{it.nome == 'Jorgino Alfredo Gonçalves'}", Users.class);

        Assert.assertEquals("Jorgino Alfredo Gonçalves", usuario.getNome());
        Assert.assertEquals("Jorgino", usuario.getUsername());
        Assert.assertEquals("jorgino@email.com", usuario.getEmail());
        Assert.assertEquals("$2a$10$v5wFlt4WCgYzXWotN7XRuOd9Il2rhRmwWF1IAkJ2jXjOTUClJXVda", usuario.getSenha());
        Assert.assertEquals("11960503456", usuario.getTelefone());

        System.out.println(usuario);
    }
}