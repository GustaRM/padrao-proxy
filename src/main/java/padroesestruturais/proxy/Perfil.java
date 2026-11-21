package padroesestruturais.proxy;

public enum Perfil {
    MEDICO(true),
    ENFERMEIRO(true),
    RECEPCIONISTA(false);

    private final boolean podeVerHistorico;

    Perfil(boolean podeVerHistorico) {
        this.podeVerHistorico = podeVerHistorico;
    }

    public boolean podeVerHistorico() {
        return podeVerHistorico;
    }
}
