
package servico;

import modelo.*;
import excecao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Biblioteca {

    private final List<ItemBiblioteca> itens;
    private final Map<Integer, Usuario> usuarios;
    private final List<Emprestimo> emprestimos;

    private int proximoIdEmprestimo;

    public Biblioteca() {
        this.itens = new ArrayList<>();
        this.usuarios = new HashMap<>();
        this.emprestimos = new ArrayList<>();
        this.proximoIdEmprestimo = 1;
    }

    public void cadastrarItem(ItemBiblioteca item) {

        if (item == null) {
            throw new IllegalArgumentException(
                "O item não pode ser nulo."
            );
        }

        if (buscarItemPorId(item.getId()) != null) {
            throw new IllegalArgumentException(
                "Já existe um item com esse ID."
            );
        }

        itens.add(item);
    }

    public ItemBiblioteca buscarItemPorId(int id) {

        for (ItemBiblioteca item : itens) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    public void editarItem(int id, String novoTitulo, int novoAno) {

        ItemBiblioteca item = buscarItemPorId(id);

        if (item == null) {
            throw new IllegalArgumentException(
                "Item não encontrado."
            );
        }

        if (novoTitulo == null || novoTitulo.isBlank()) {
            throw new IllegalArgumentException(
                "O título não pode estar vazio."
            );
        }

        if (novoAno <= 0) {
            throw new IllegalArgumentException(
                "O ano deve ser positivo."
            );
        }

        item.setTitulo(novoTitulo);
        item.setAno(novoAno);
    }

    public void removerItem(int id) {

        ItemBiblioteca item = buscarItemPorId(id);

        if (item == null) {
            throw new IllegalArgumentException(
                "Item não encontrado."
            );
        }

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getItem() == item) {
                throw new IllegalStateException(
                    "Não é possível remover um item com histórico de empréstimos."
                );
            }
        }

        itens.remove(item);
    }

    public List<ItemBiblioteca> listarItens() {
        return new ArrayList<>(itens);
    }

    public void cadastrarUsuario(Usuario usuario) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                "O usuário não pode ser nulo."
            );
        }

        if (usuarios.containsKey(usuario.getId())) {
            throw new IllegalArgumentException(
                "Já existe um usuário com esse ID."
            );
        }

        usuarios.put(usuario.getId(), usuario);
    }

    public Usuario buscarUsuarioPorId(int id) {
        return usuarios.get(id);
    }

    public void editarUsuario(int id, String novoNome) {

        Usuario usuario = buscarUsuarioPorId(id);

        if (usuario == null) {
            throw new IllegalArgumentException(
                "Usuário não encontrado."
            );
        }

        if (novoNome == null || novoNome.isBlank()) {
            throw new IllegalArgumentException(
                "O nome não pode estar vazio."
            );
        }

        usuario.setNome(novoNome);
    }

    public void removerUsuario(int id) {

        Usuario usuario = buscarUsuarioPorId(id);

        if (usuario == null) {
            throw new IllegalArgumentException(
                "Usuário não encontrado."
            );
        }

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getUsuario() == usuario) {
                throw new IllegalStateException(
                    "Não é possível remover um usuário com histórico de empréstimos."
                );
            }
        }

        usuarios.remove(id);
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    public List<ItemBiblioteca> buscarPorTitulo(String titulo) {

        List<ItemBiblioteca> resultados = new ArrayList<>();

        if (titulo == null || titulo.isBlank()) {
            return resultados;
        }

        for (ItemBiblioteca item : itens) {
            if (item.getTitulo().toLowerCase()
                    .contains(titulo.toLowerCase())) {

                resultados.add(item);
            }
        }

        return resultados;
    }

    public List<Livro> buscarPorAutor(String autor) {

        List<Livro> resultados = new ArrayList<>();

        if (autor == null || autor.isBlank()) {
            return resultados;
        }

        for (ItemBiblioteca item : itens) {

            if (item instanceof Livro) {

                Livro livro = (Livro) item;

                if (livro.getAutor().toLowerCase()
                        .contains(autor.toLowerCase())) {

                    resultados.add(livro);
                }
            }
        }

        return resultados;
    }

    public List<ItemBiblioteca> buscarPorTipo(
            Class<? extends ItemBiblioteca> tipo) {

        if (tipo == null) {
            throw new IllegalArgumentException(
                "O tipo não pode ser nulo."
            );
        }

        List<ItemBiblioteca> resultados = new ArrayList<>();

        for (ItemBiblioteca item : itens) {
            if (tipo.isInstance(item)) {
                resultados.add(item);
            }
        }

        return resultados;
    }

    public List<ItemBiblioteca> buscarDisponiveis() {

        List<ItemBiblioteca> resultados = new ArrayList<>();

        for (ItemBiblioteca item : itens) {
            if (item.getStatus() == StatusItem.DISPONIVEL) {
                resultados.add(item);
            }
        }

        return resultados;
    }

    // EMPRÉSTIMOS

    public int contarEmprestimosAtivos(Usuario usuario) {

        int quantidade = 0;

        for (Emprestimo emprestimo : emprestimos) {

            if (emprestimo.getUsuario() == usuario
                    && emprestimo.estaAtivo()) {

                quantidade++;
            }
        }

        return quantidade;
    }

    public Emprestimo realizarEmprestimo(int idUsuario, int idItem)
            throws RegistroNaoEncontradoException,
                   ItemIndisponivelException,
                   LimiteExcedidoException {

        Usuario usuario = buscarUsuarioPorId(idUsuario);
        ItemBiblioteca item = buscarItemPorId(idItem);

        if (usuario == null) {
            throw new RegistroNaoEncontradoException(
                "Usuário não encontrado."
            );
        }

        if (item == null) {
            throw new RegistroNaoEncontradoException(
                "Item não encontrado."
            );
        }

        if (item.getStatus() != StatusItem.DISPONIVEL) {
            throw new ItemIndisponivelException(
                "Este item já está emprestado."
            );
        }

        int ativos = contarEmprestimosAtivos(usuario);

        if (!usuario.podeEmprestar(ativos)) {
            throw new LimiteExcedidoException(
                "O usuário atingiu seu limite de empréstimos."
            );
        }

        Emprestimo emprestimo = new Emprestimo(
            proximoIdEmprestimo,
            usuario,
            item,
            LocalDate.now()
        );

        item.setStatus(StatusItem.EMPRESTADO);
        emprestimos.add(emprestimo);
        proximoIdEmprestimo++;

        return emprestimo;
    }

    public Emprestimo buscarEmprestimoPorId(int id) {

        for (Emprestimo emprestimo : emprestimos) {
            if (emprestimo.getId() == id) {
                return emprestimo;
            }
        }

        return null;
    }

    public double devolverItem(int idEmprestimo)
            throws EmprestimoInvalidoException {

        Emprestimo emprestimo =
            buscarEmprestimoPorId(idEmprestimo);

        if (emprestimo == null) {
            throw new EmprestimoInvalidoException(
                "Empréstimo não encontrado."
            );
        }

        if (!emprestimo.estaAtivo()) {
            throw new EmprestimoInvalidoException(
                "Este empréstimo já foi devolvido."
            );
        }

        emprestimo.registrarDevolucao(LocalDate.now());

        double multa = emprestimo.calcularMulta();

        emprestimo.getItem().setStatus(StatusItem.DISPONIVEL);

        return multa;
    }

    public List<Emprestimo> listarEmprestimos() {
        return new ArrayList<>(emprestimos);
    }

    // RESTAURAÇÃO DOS DADOS

    public void restaurarEmprestimo(Emprestimo emprestimo) {

        if (emprestimo == null) {
            throw new IllegalArgumentException(
                "Empréstimo nulo."
            );
        }

        if (emprestimo.getId() <= 0) {
            throw new IllegalArgumentException(
                "ID de empréstimo inválido."
            );
        }

        if (buscarEmprestimoPorId(emprestimo.getId()) != null) {
            throw new IllegalArgumentException(
                "ID de empréstimo duplicado."
            );
        }

        Usuario usuario = buscarUsuarioPorId(
            emprestimo.getUsuario().getId()
        );

        ItemBiblioteca item = buscarItemPorId(
            emprestimo.getItem().getId()
        );

        if (usuario != emprestimo.getUsuario() ||
                item != emprestimo.getItem()) {

            throw new IllegalArgumentException(
                "Empréstimo com referência inválida."
            );
        }

        if (emprestimo.estaAtivo()) {
            for (Emprestimo anterior : emprestimos) {
                if (anterior.estaAtivo() &&
                        anterior.getItem() == item) {

                    throw new IllegalArgumentException(
                        "Item possui dois empréstimos ativos."
                    );
                }
            }
        }

        emprestimos.add(emprestimo);

        proximoIdEmprestimo = Math.max(
            proximoIdEmprestimo,
            emprestimo.getId() + 1
        );
    }

    public void validarEstadoRestaurado() {

        for (ItemBiblioteca item : itens) {

            int ativos = 0;

            for (Emprestimo emprestimo : emprestimos) {
                if (emprestimo.getItem() == item &&
                        emprestimo.estaAtivo()) {
                    ativos++;
                }
            }

            if (ativos > 1) {
                throw new IllegalStateException(
                    "Mais de um empréstimo ativo para o item "
                    + item.getId()
                );
            }

            StatusItem esperado = ativos == 1
                ? StatusItem.EMPRESTADO
                : StatusItem.DISPONIVEL;

            if (item.getStatus() != esperado) {
                throw new IllegalStateException(
                    "Status incompatível com o histórico do item "
                    + item.getId()
                );
            }
        }
    }
}
