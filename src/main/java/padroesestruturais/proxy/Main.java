package padroesestruturais.proxy;

public class Main {

    public static void main(String[] args) {
        BancoDeDados banco = BancoDeDados.exemplo();
        ProntuarioProxy prontuario = new ProntuarioProxy("001", banco);

        Usuario recepcao = new Usuario("Carla", Perfil.RECEPCIONISTA);
        Usuario medica = new Usuario("Dra. Helena", Perfil.MEDICO);

        System.out.println(prontuario.obterDadosCadastrais());
        try {
            prontuario.obterHistoricoClinico(recepcao);
        } catch (SecurityException e) {
            System.out.println("Recepção: " + e.getMessage());
        }
        System.out.println("Médica: " + prontuario.obterHistoricoClinico(medica));
        System.out.println("Consultas ao banco: " + banco.getConsultas());
        prontuario.getAuditoria().forEach(System.out::println);
    }
}
