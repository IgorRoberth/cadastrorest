
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("usuarios") // Supondo que você tenha configurado um filtro com esse nome
public class Users {

    private Long id;
    private String nome;
    private String username;
    private String email;
    private String senha;
    private String telefone;

    public Users() {
    }

    public Users(String nome, String username, String email, String senha, String telefone) {
        this.nome = nome;
        this.username = username;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return "Users [id=" + id + ", nome=" + nome + ", username=" + username + ", email=" + email + ", senha=" + senha + ", telefone=" + telefone + "]";
    }
}