package padroesestruturais.proxy;

/** RealSubject: prontuário completo, carregado do banco de dados (operação custosa). */
public class ProntuarioReal implements Prontuario {

    private final String numero;
    private final String paciente;
    private final String dataNascimento;
    private final String historicoClinico;

    public ProntuarioReal(String numero, String paciente, String dataNascimento, String historicoClinico) {
        this.numero = numero;
        this.paciente = paciente;
        this.dataNascimento = dataNascimento;
        this.historicoClinico = historicoClinico;
    }

    @Override
    public String obterDadosCadastrais() {
        return "Prontuário " + numero + " - " + paciente + " (nasc. " + dataNascimento + ")";
    }

    @Override
    public String obterHistoricoClinico(Usuario usuario) {
        return historicoClinico;
    }
}
