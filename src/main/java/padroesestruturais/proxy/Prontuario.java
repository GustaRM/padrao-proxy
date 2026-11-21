package padroesestruturais.proxy;

/** Subject: interface comum ao objeto real e ao proxy. */
public interface Prontuario {

    String obterDadosCadastrais();

    String obterHistoricoClinico(Usuario usuario);
}
