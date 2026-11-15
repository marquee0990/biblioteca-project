
package persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import modelo.*;
import servico.Biblioteca;

public class GerenciadorArquivos {

    private final Path pastaDados;

    public GerenciadorArquivos(Path pastaDados) {
        this.pastaDados = pastaDados;
    }

    public void salvar(Biblioteca biblioteca) throws IOException {

        biblioteca.validarEstadoRestaurado();

        Files.createDirectories(pastaDados);

        salvarItens(biblioteca);
        salvarUsuarios(biblioteca);
        salvarEmprestimos(biblioteca);
    }

    private void salvarItens(Biblioteca biblioteca)
            throws IOException {

        List<String> linhas = new ArrayList<>();

        for (ItemBiblioteca item : biblioteca.listarItens()) {

            String linha;

            if (item instanceof Livro livro) {

                linha = csv(
                    "LIVRO",
                    String.valueOf(livro.getId()),
                    livro.getTitulo(),
                    String.valueOf(livro.getAno()),
                    livro.getStatus().name(),
                    livro.getAutor(),
                    livro.getIsbn()
                );

            } else if (item instanceof Revista revista) {

                linha = csv(
                    "REVISTA",
                    String.valueOf(revista.getId()),
                    revista.getTitulo(),
                    String.valueOf(revista.getAno()),
                    revista.getStatus().name(),
                    String.valueOf(revista.getNumeroEdicao()),
                    revista.getIssn()
                );

            } else if (item instanceof DVD dvd) {

                linha = csv(
                    "DVD",
                    String.valueOf(dvd.getId()),
                    dvd.getTitulo(),
                    String.valueOf(dvd.getAno()),
                    dvd.getStatus().name(),
                    String.valueOf(dvd.getDuracaoMinutos()),
                    dvd.getGenero()
                );

            } else {
                throw new IOException(
                    "Tipo de item desconhecido."
                );
            }

            linhas.add(linha);
        }

        Files.write(
            pastaDados.resolve("itens.csv"),
            linhas,
            StandardCharsets.UTF_8
        );
    }

    private void salvarUsuarios(Biblioteca biblioteca)
            throws IOException {

        List<String> linhas = new ArrayList<>();

        for (Usuario usuario : biblioteca.listarUsuarios()) {

            linhas.add(csv(
                usuario.getTipo().name(),
                String.valueOf(usuario.getId()),
                usuario.getNome(),
                String.valueOf(usuario.getLimiteEmprestimo())
            ));
        }

        Files.write(
            pastaDados.resolve("usuarios.csv"),
            linhas,
            StandardCharsets.UTF_8
        );
    }

    private void salvarEmprestimos(Biblioteca biblioteca)
            throws IOException {

        List<String> linhas = new ArrayList<>();

        for (Emprestimo emprestimo :
                biblioteca.listarEmprestimos()) {

            linhas.add(csv(
                String.valueOf(emprestimo.getId()),
                String.valueOf(emprestimo.getUsuario().getId()),
                String.valueOf(emprestimo.getItem().getId()),
                emprestimo.getEmprestimo().toString(),
                emprestimo.getDataPrevistaDevolucao().toString(),
                emprestimo.getDataDevolucao() == null
                    ? ""
                    : emprestimo.getDataDevolucao().toString()
            ));
        }

        Files.write(
            pastaDados.resolve("emprestimos.csv"),
            linhas,
            StandardCharsets.UTF_8
        );
    }

    public Biblioteca carregar() throws IOException {

        Path arquivoItens = pastaDados.resolve("itens.csv");
        Path arquivoUsuarios = pastaDados.resolve("usuarios.csv");
        Path arquivoEmprestimos =
            pastaDados.resolve("emprestimos.csv");

        boolean existeItens = Files.exists(arquivoItens);
        boolean existeUsuarios = Files.exists(arquivoUsuarios);
        boolean existeEmprestimos = Files.exists(arquivoEmprestimos);

        Biblioteca biblioteca = new Biblioteca();

        if (!existeItens && !existeUsuarios && !existeEmprestimos) {
            return biblioteca;
        }

        if (!existeItens || !existeUsuarios || !existeEmprestimos) {
            throw new IOException(
                "Arquivos de dados incompletos."
            );
        }

        try {
            carregarItens(biblioteca, arquivoItens);
            carregarUsuarios(biblioteca, arquivoUsuarios);
            carregarEmprestimos(biblioteca, arquivoEmprestimos);

            biblioteca.validarEstadoRestaurado();

            return biblioteca;

        } catch (RuntimeException e) {
            throw new IOException(
                "Dados inválidos nos arquivos: " + e.getMessage(),
                e
            );
        }
    }

    private void carregarItens(
            Biblioteca biblioteca, Path arquivo)
            throws IOException {

        List<String> linhas = Files.readAllLines(
            arquivo, StandardCharsets.UTF_8
        );

        for (String linha : linhas) {

            List<String> c = separarCsv(linha);

            if (c.size() != 7) {
                throw new IOException(
                    "Registro de item com número incorreto de campos."
                );
            }

            String tipo = c.get(0);
            int id = Integer.parseInt(c.get(1));
            String titulo = c.get(2);
            int ano = Integer.parseInt(c.get(3));
            StatusItem status = StatusItem.valueOf(c.get(4));

            ItemBiblioteca item;

            switch (tipo) {

                case "LIVRO" -> item = new Livro(
                    id, titulo, ano, c.get(5), c.get(6)
                );

                case "REVISTA" -> item = new Revista(
                    id, titulo, ano,
                    Integer.parseInt(c.get(5)),
                    c.get(6)
                );

                case "DVD" -> item = new DVD(
                    id, titulo, ano,
                    Integer.parseInt(c.get(5)),
                    c.get(6)
                );

                default -> throw new IOException(
                    "Tipo de item desconhecido: " + tipo
                );
            }

            item.setStatus(status);
            biblioteca.cadastrarItem(item);
        }
    }

    private void carregarUsuarios(
            Biblioteca biblioteca, Path arquivo)
            throws IOException {

        List<String> linhas = Files.readAllLines(
            arquivo, StandardCharsets.UTF_8
        );

        for (String linha : linhas) {

            List<String> c = separarCsv(linha);

            if (c.size() != 4) {
                throw new IOException(
                    "Registro de usuário com número incorreto de campos."
                );
            }

            String tipo = c.get(0);
            int id = Integer.parseInt(c.get(1));
            String nome = c.get(2);
            int limite = Integer.parseInt(c.get(3));

            Usuario usuario;

            switch (tipo) {

                case "ALUNO" -> usuario =
                    new Aluno(id, nome, limite);

                case "PROFESSOR" -> usuario =
                    new Professor(id, nome, limite);

                case "FUNCIONARIO" -> usuario =
                    new Funcionario(id, nome, limite);

                default -> throw new IOException(
                    "Tipo de usuário desconhecido: " + tipo
                );
            }

            biblioteca.cadastrarUsuario(usuario);
        }
    }

    private void carregarEmprestimos(
            Biblioteca biblioteca, Path arquivo)
            throws IOException {

        List<String> linhas = Files.readAllLines(
            arquivo, StandardCharsets.UTF_8
        );

        for (String linha : linhas) {

            List<String> c = separarCsv(linha);

            if (c.size() != 6) {
                throw new IOException(
                    "Registro de empréstimo com número incorreto de campos."
                );
            }

            int id = Integer.parseInt(c.get(0));
            int idUsuario = Integer.parseInt(c.get(1));
            int idItem = Integer.parseInt(c.get(2));

            LocalDate dataEmprestimo =
                LocalDate.parse(c.get(3));

            LocalDate dataPrevista =
                LocalDate.parse(c.get(4));

            LocalDate dataDevolucao = c.get(5).isBlank()
                ? null
                : LocalDate.parse(c.get(5));

            Usuario usuario =
                biblioteca.buscarUsuarioPorId(idUsuario);

            ItemBiblioteca item =
                biblioteca.buscarItemPorId(idItem);

            if (usuario == null || item == null) {
                throw new IOException(
                    "Empréstimo referencia usuário ou item inexistente."
                );
            }

            Emprestimo emprestimo = new Emprestimo(
                id,
                usuario,
                item,
                dataEmprestimo,
                dataPrevista,
                dataDevolucao
            );

            biblioteca.restaurarEmprestimo(emprestimo);
        }
    }

    // MÉTODOS AUXILIARES DO CSV

    private String csv(String... campos) {

        List<String> valores = new ArrayList<>();

        for (String campo : campos) {

            if (campo == null) {
                throw new IllegalArgumentException(
                    "Campo CSV não pode ser nulo."
                );
            }

            if (campo.contains("\n") || campo.contains("\r")) {
                throw new IllegalArgumentException(
                    "Campos CSV não podem conter quebras de linha."
                );
            }

            String escapado = campo.replace("\"", "\"\"");

            if (campo.contains(";") || campo.contains("\"")) {
                escapado = "\"" + escapado + "\"";
            }

            valores.add(escapado);
        }

        return String.join(";", valores);
    }

    private List<String> separarCsv(String linha) throws IOException {

        List<String> campos = new ArrayList<>();
        StringBuilder atual = new StringBuilder();

        boolean entreAspas = false;
        boolean fechouAspas = false;

        for (int i = 0; i < linha.length(); i++) {

            char ch = linha.charAt(i);

            if (entreAspas) {

                if (ch == '"') {

                    if (i + 1 < linha.length()
                            && linha.charAt(i + 1) == '"') {

                        atual.append('"');
                        i++;

                    } else {
                        entreAspas = false;
                        fechouAspas = true;
                    }

                } else {
                    atual.append(ch);
                }

            } else if (ch == ';') {

                campos.add(atual.toString());
                atual.setLength(0);
                fechouAspas = false;

            } else if (ch == '"' && atual.length() == 0
                    && !fechouAspas) {

                entreAspas = true;

            } else if (ch == '"' || fechouAspas) {

                throw new IOException(
                    "Aspas inválidas no arquivo CSV."
                );

            } else {
                atual.append(ch);
            }
        }

        if (entreAspas) {
            throw new IOException(
                "Campo CSV com aspas não fechadas."
            );
        }

        campos.add(atual.toString());
        return campos;
    }
}
