package padroesestruturais.proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Proxy: tem a mesma interface do prontuário real e controla o acesso a ele.
 * - Proteção: só perfis autorizados veem o histórico clínico.
 * - Virtual: o prontuário real só é carregado do banco quando realmente necessário (uma única vez).
 * - Auditoria: registra toda tentativa de acesso ao histórico.
 */
public class ProntuarioProxy implements Prontuario {

    private final String numero;
    private final BancoDeDados banco;
    private ProntuarioReal prontuarioReal;
    private final List<String> auditoria = new ArrayList<>();

    public ProntuarioProxy(String numero, BancoDeDados banco) {
        this.numero = numero;
        this.banco = banco;
    }

    private ProntuarioReal real() {
        if (prontuarioReal == null) {
            prontuarioReal = banco.carregar(numero);
        }
        return prontuarioReal;
    }

    @Override
    public String obterDadosCadastrais() {
        return real().obterDadosCadastrais();
    }

    @Override
    public String obterHistoricoClinico(Usuario usuario) {
        if (usuario == null || !usuario.getPerfil().podeVerHistorico()) {
            String quem = usuario == null ? "anônimo" : usuario.getNome() + " (" + usuario.getPerfil() + ")";
            auditoria.add("NEGADO: " + quem);
            throw new SecurityException("Acesso ao histórico clínico negado");
        }
        auditoria.add("LIBERADO: " + usuario.getNome() + " (" + usuario.getPerfil() + ")");
        return real().obterHistoricoClinico(usuario);
    }

    public boolean isCarregado() {
        return prontuarioReal != null;
    }

    public List<String> getAuditoria() {
        return Collections.unmodifiableList(auditoria);
    }
}
