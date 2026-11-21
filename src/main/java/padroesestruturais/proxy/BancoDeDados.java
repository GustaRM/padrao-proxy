package padroesestruturais.proxy;

import java.util.HashMap;
import java.util.Map;

/** Simula o banco de dados do hospital e conta quantas consultas foram feitas. */
public class BancoDeDados {

    private final Map<String, String[]> registros = new HashMap<>();
    private int consultas;

    public void inserir(String numero, String paciente, String dataNascimento, String historico) {
        registros.put(numero, new String[]{paciente, dataNascimento, historico});
    }

    public ProntuarioReal carregar(String numero) {
        consultas++;
        String[] r = registros.get(numero);
        if (r == null) {
            throw new IllegalArgumentException("Prontuário não encontrado: " + numero);
        }
        return new ProntuarioReal(numero, r[0], r[1], r[2]);
    }

    public int getConsultas() {
        return consultas;
    }

    public static BancoDeDados exemplo() {
        BancoDeDados banco = new BancoDeDados();
        banco.inserir("001", "Maria Silva", "12/03/1980", "Hipertensão arterial; alergia a penicilina");
        banco.inserir("002", "João Souza", "05/09/1972", "Diabetes tipo 2; cirurgia de apendicite em 2015");
        return banco;
    }
}
