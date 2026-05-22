package school.sptech.dto;

import java.util.ArrayList;
import java.util.List;

public class EmailConfigDto {
    private String email;
    private List<String> tiposAtivos;

    public EmailConfigDto(String email) {
        this.email = email;
        this.tiposAtivos = new ArrayList<>();
    }

    public String getEmail() { return email; }
    public List<String> getTiposAtivos() { return tiposAtivos; }

    public void adicionarTipo(String tipo) {
        this.tiposAtivos.add(tipo);
    }
}
