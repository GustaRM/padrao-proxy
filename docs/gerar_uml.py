"""Gera o diagrama UML (estilo ferramenta de desenho, fundo quadriculado)."""
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle, FancyArrowPatch

LH, PAD, NAME_H = 0.52, 0.25, 0.75

def novo(W_UN, H_UN, w_in):
    global ax, fig
    fig, ax = plt.subplots(figsize=(w_in, w_in * H_UN / W_UN))
    ax.set_xlim(0, W_UN); ax.set_ylim(0, H_UN); ax.set_aspect("equal"); ax.axis("off")
    for i in range(W_UN + 1):
        ax.plot([i, i], [0, H_UN], color="#E3E3E3", lw=0.6, zorder=0)
    for j in range(H_UN + 1):
        ax.plot([0, W_UN], [j, j], color="#E3E3E3", lw=0.6, zorder=0)

def box(cx, top, w, name, stereo=None, attrs=(), methods=()):
    h_name = NAME_H * (2 if stereo else 1) + (0.15 if stereo else 0)
    h_attr = max(len(attrs), 1) * LH + PAD
    h_meth = max(len(methods), 1) * LH + PAD
    h = h_name + h_attr + h_meth
    x0, y0 = cx - w / 2, top - h
    ax.add_patch(Rectangle((x0, y0), w, h, fc="white", ec="black", lw=1.4, zorder=2))
    ax.plot([x0, x0 + w], [top - h_name] * 2, color="black", lw=1.2, zorder=3)
    ax.plot([x0, x0 + w], [top - h_name - h_attr] * 2, color="black", lw=1.2, zorder=3)
    ty = top - 0.45
    if stereo:
        ax.text(cx, ty, f"«{stereo}»", ha="center", va="center", fontsize=9, zorder=4)
        ty -= 0.65
    ax.text(cx, ty, name, ha="center", va="center", fontsize=11, fontweight="bold", zorder=4)
    for k, a in enumerate(attrs):
        ax.text(x0 + 0.25, top - h_name - PAD / 2 - LH * (k + .5), a, ha="left", va="center", fontsize=8.5, zorder=4)
    for k, m in enumerate(methods):
        ax.text(x0 + 0.25, top - h_name - h_attr - PAD / 2 - LH * (k + .5), m, ha="left", va="center", fontsize=8.5, zorder=4)
    return dict(cx=cx, cy=(top + y0) / 2, top=top, bottom=y0, left=x0, right=x0 + w)

def line(pts, dashed=False):
    xs, ys = zip(*pts)
    ax.plot(xs, ys, color="black", lw=1.3, zorder=1, linestyle=(0, (5, 4)) if dashed else "-")

def arrow(p0, p1, style, dashed=False):
    a = FancyArrowPatch(p0, p1, arrowstyle=style, mutation_scale=22 if "|" in style else 18, lw=1.3,
                        color="black", zorder=1, shrinkA=0, shrinkB=0,
                        linestyle=(0, (5, 4)) if dashed else "-")
    if "|" in style:
        a.set_facecolor("white")
    ax.add_patch(a)

def legenda(x, y, titulo, itens):
    ax.text(x, y, titulo, fontsize=9.5, fontweight="bold")
    for k, t in enumerate(itens):
        ax.text(x, y - 0.9 - k * 0.8, f"• {t}", fontsize=8.5)

def salvar(nome="diagrama-uml.png"):
    plt.savefig(nome, dpi=140, bbox_inches="tight", facecolor="white")
novo(76, 36, 26)
cli = box(8, 34, 12, "Main", None, [], ["+ main(args)"])
sub = box(40, 34, 30, "Prontuario", "interface", [], ["+ obterDadosCadastrais(): String", "+ obterHistoricoClinico(u: Usuario): String"])
real = box(16, 24, 28, "ProntuarioReal", "RealSubject", ["- numero: String", "- paciente: String", "- dataNascimento: String", "- historicoClinico: String"],
           ["+ obterDadosCadastrais(): String", "+ obterHistoricoClinico(u: Usuario): String"])
prx = box(57, 24, 34, "ProntuarioProxy", "Proxy", ["- numero: String", "- banco: BancoDeDados", "- prontuarioReal: ProntuarioReal", "- auditoria: List<String>"],
          ["- real(): ProntuarioReal", "+ obterDadosCadastrais(): String", "+ obterHistoricoClinico(u: Usuario): String", "+ isCarregado(): boolean", "+ getAuditoria(): List<String>"])
bd = box(58, 11, 28, "BancoDeDados", None, ["- registros: Map<String, String[]>", "- consultas: int"],
         ["+ inserir(numero, paciente, nasc, historico)", "+ carregar(numero): ProntuarioReal", "+ getConsultas(): int", "+ exemplo(): BancoDeDados {static}"])
usu = box(12, 11, 18, "Usuario", None, ["- nome: String", "- perfil: Perfil"], ["+ getNome(): String", "+ getPerfil(): Perfil"])
per = box(31, 11, 15, "Perfil", "enumeration", ["MEDICO", "ENFERMEIRO", "RECEPCIONISTA"], ["+ podeVerHistorico(): boolean"])
arrow((cli["right"], 32.6), (sub["left"], 32.6), "->", True)
bus = 27.3
line([(real["cx"], real["top"]), (real["cx"], bus), (prx["cx"], bus)], True)
line([(prx["cx"], prx["top"]), (prx["cx"], bus)], True)
arrow((40, bus), (40, sub["bottom"]), "-|>", True)
arrow((prx["left"], 21.5), (real["right"], 21.5), "->")
ax.text(real["right"] + 0.4, 21.9, "prontuarioReal 0..1", fontsize=9)
arrow((60, prx["bottom"]), (60, bd["top"]), "->")
ax.text(60.4, (prx["bottom"] + bd["top"]) / 2, "banco 1", fontsize=9)
line([(48, bd["top"]), (48, 15.6), (24, 15.6)], True)
arrow((24, 15.6), (24, real["bottom"]), "->", True)
ax.text(36, 16.0, "«cria»", fontsize=9)
arrow((usu["right"], 9.5), (per["left"], 9.5), "->")
legenda(1, 5.2, "Legenda", ["ProntuarioProxy = Proxy (acesso, carga sob demanda e auditoria)", "ProntuarioReal = RealSubject; Prontuario = Subject",
                            "Realização: tracejada + triângulo vazado"])
salvar("diagrama-uml.png")
