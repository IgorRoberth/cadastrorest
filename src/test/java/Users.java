
import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.*;

@Entity
@JsonFilter("usuarios")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;
    private String nome;
    private String username;
    private String email;
    private String senha;
    private String telefone;

    public Users() {
    }

    public Users(Integer id, String nome, String username, String email, String senha, String telefone) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getTelefone() {
        return telefone;
    }

    @Override
    public String toString() {
        return "Users [id=" + id + ", nome=" + nome + ", username=" + username + ", email=" + email + ", senha=" + senha + ", telefone=" + telefone + "]";
    }
}