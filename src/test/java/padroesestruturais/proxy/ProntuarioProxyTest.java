package padroesestruturais.proxy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProntuarioProxyTest {

    private BancoDeDados banco;
    private ProntuarioProxy proxy;
    private final Usuario medica = new Usuario("Dra. Helena", Perfil.MEDICO);
    private final Usuario enfermeiro = new Usuario("Paulo", Perfil.ENFERMEIRO);
    private final Usuario recepcionista = new Usuario("Carla", Perfil.RECEPCIONISTA);

    @BeforeEach
    void iniciar() {
        banco = BancoDeDados.exemplo();
        proxy = new ProntuarioProxy("001", banco);
    }

    @Test
    void proxyDeveSerUmProntuario() {
        Prontuario p = proxy;
        assertNotNull(p);
    }

    @Test
    void naoDeveCarregarDoBancoAoCriarProxy() {
        assertFalse(proxy.isCarregado());
        assertEquals(0, banco.getConsultas());
    }

    @Test
    void deveRetornarDadosCadastraisParaQualquerUm() {
        assertEquals("Prontuário 001 - Maria Silva (nasc. 12/03/1980)", proxy.obterDadosCadastrais());
        assertTrue(proxy.isCarregado());
    }

    @Test
    void medicoDeveVerHistoricoClinico() {
        assertEquals("Hipertensão arterial; alergia a penicilina", proxy.obterHistoricoClinico(medica));
    }

    @Test
    void enfermeiroDeveVerHistoricoClinico() {
        assertEquals("Hipertensão arterial; alergia a penicilina", proxy.obterHistoricoClinico(enfermeiro));
    }

    @Test
    void recepcionistaNaoDeveVerHistoricoClinico() {
        SecurityException e = assertThrows(SecurityException.class, () -> proxy.obterHistoricoClinico(recepcionista));
        assertEquals("Acesso ao histórico clínico negado", e.getMessage());
    }

    @Test
    void usuarioNuloDeveSerNegado() {
        assertThrows(SecurityException.class, () -> proxy.obterHistoricoClinico(null));
        assertEquals("NEGADO: anônimo", proxy.getAuditoria().get(0));
    }

    @Test
    void acessoNegadoNaoDeveConsultarOBanco() {
        assertThrows(SecurityException.class, () -> proxy.obterHistoricoClinico(recepcionista));
        assertFalse(proxy.isCarregado());
        assertEquals(0, banco.getConsultas());
    }

    @Test
    void deveCarregarDoBancoApenasUmaVez() {
        proxy.obterDadosCadastrais();
        proxy.obterHistoricoClinico(medica);
        proxy.obterHistoricoClinico(enfermeiro);
        assertEquals(1, banco.getConsultas());
    }

    @Test
    void deveRegistrarAuditoriaDeAcessos() {
        proxy.obterHistoricoClinico(medica);
        assertThrows(SecurityException.class, () -> proxy.obterHistoricoClinico(recepcionista));
        assertEquals(2, proxy.getAuditoria().size());
        assertEquals("LIBERADO: Dra. Helena (MEDICO)", proxy.getAuditoria().get(0));
        assertEquals("NEGADO: Carla (RECEPCIONISTA)", proxy.getAuditoria().get(1));
    }

    @Test
    void auditoriaDeveSerSomenteLeitura() {
        assertThrows(UnsupportedOperationException.class, () -> proxy.getAuditoria().add("x"));
    }

    @Test
    void prontuarioInexistenteSoFalhaQuandoForUsado() {
        ProntuarioProxy inexistente = new ProntuarioProxy("999", banco);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, inexistente::obterDadosCadastrais);
        assertEquals("Prontuário não encontrado: 999", e.getMessage());
    }
}
