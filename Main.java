import excecao.EmprestimoInvalidoException;
import excecao.ItemIndisponivelException;
import excecao.LimiteExcedidoException;
import excecao.RegistroNaoEncontradoException;

import modelo.*;
import servico.Biblioteca;
import persistencia.GerenciadorArquivos;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class Main{

    private static final Scanner teclado = new Scanner(System.in);

    private static Biblioteca biblioteca = new Biblioteca();
    private static final GerenciadorArquivos arquivos = new GerenciadorArquivos(Path.of("dados"));
    public static void main(String[] args) {

        boolean rodando = true;

        try{
            biblioteca = arquivos.carregar();
            System.out.println("Dados carregados com sucesso!");
        }catch(Exception e){
            System.out.println("Não foi possível carregar os dados: "
                    + e.getMessage());
            System.out.println("Verifique os arquivos antes de salvar novos dados.");
            return;
        }
        while(rodando){
            exibirMenu();

            String opcao = ler("Escolha uma opção: ");

            try{
                switch (opcao) {

                    case "1" -> cadastrarItem();

                    case "2" -> editarItem();

                    case "3" -> {
                        int id = lerInt("ID do item: ");
                        biblioteca.removerItem(id);
                        System.out.println("Item removido com sucesso!");
                    }

                    case "4" -> cadastrarUsuario();

                    case "5" -> {
                        int id = lerInt("ID do usuário: ");
                        String nome = ler("Novo nome: ");

                        biblioteca.editarUsuario(id, nome);
                        System.out.println("Usuário editado com sucesso!");
                    }

                    case "6" -> {
                        int id = lerInt("ID do usuário: ");
                        biblioteca.removerUsuario(id);
                        System.out.println("Usuário removido com sucesso!");
                    }

                    case "7" -> consultarAcervo();

                    case "8" -> {
                        int idUsuario = lerInt("ID do usuário: ");
                        int idItem = lerInt("ID do item: ");

                        Emprestimo emprestimo =
                            biblioteca.realizarEmprestimo(idUsuario, idItem);

                        System.out.println("Empréstimo realizado!");
                        System.out.println(emprestimo);
                    }

                    case "9" -> {
                        int id = lerInt("ID do empréstimo: ");

                        double multa = biblioteca.devolverItem(id);

                        System.out.println("Devolução realizada!");
                        System.out.printf("Multa: R$ %.2f%n", multa);
                    }

                    case "10" -> {
                        for (Emprestimo e : biblioteca.listarEmprestimos()) {
                            System.out.println(e);
                        }
                    }

                    case "11" -> {
                        arquivos.salvar(biblioteca);
                        System.out.println("Dados salvos com sucesso!");
                    }

                    case "0" -> {
                        arquivos.salvar(biblioteca);
                        System.out.println("Dados salvos. Encerrando...");
                        rodando = false;
                    }

                    default -> System.out.println("Opção inválida!");
                }
            }catch(ItemIndisponivelException | LimiteExcedidoException | RegistroNaoEncontradoException | EmprestimoInvalidoException e){
                System.out.println("Erro: " + e.getMessage());
            }catch (IllegalArgumentException | IllegalStateException e){
                System.out.println("Operação inválida: " + e.getMessage());
            }catch(IOException e){
                System.out.println(
                    "Erro ao salvar os dados: " + e.getMessage()
                );
        }
    }
        teclado.close();
    }
    private static void exibirMenu() {

        System.out.println("\n========== BIBLIOTECA ==========");
        System.out.println("1  - Cadastrar item");
        System.out.println("2  - Editar item");
        System.out.println("3  - Remover item");
        System.out.println("4  - Cadastrar usuário");
        System.out.println("5  - Editar usuário");
        System.out.println("6  - Remover usuário");
        System.out.println("7  - Consultar acervo");
        System.out.println("8  - Realizar empréstimo");
        System.out.println("9  - Realizar devolução");
        System.out.println("10 - Listar empréstimos");
        System.out.println("11 - Salvar dados");
        System.out.println("0  - Salvar e sair");
        System.out.println("================================");
    }

    private static String ler(String pergunta){
        System.out.println(pergunta);
        return teclado.nextLine().trim();
    }

    private static int lerInt(String pergunta){
        try{
            return Integer.parseInt(ler(pergunta));
        }catch(NumberFormatException e){
            throw new IllegalArgumentException("Digite um número inteiro válido.");
        }
    }
private static void cadastrarItem(){
    System.out.println("\n=== CADASTRAR ITEM ===");
        System.out.println("1 - Livro");
        System.out.println("2 - Revista");
        System.out.println("3 - DVD");

        int tipo = lerInt("Tipo do item: ");

        if (tipo < 1 || tipo > 3) {
            throw new IllegalArgumentException(
                "Tipo de item inválido."
            );
        }

        int id = lerInt("ID: ");
        String titulo = ler("Título: ");
        int ano = lerInt("Ano: ");

        ItemBiblioteca item;

        switch(tipo){
            case 1 -> {
                String autor = ler("Autor: ");
                String isbn = ler("ISBN: ");

                item = new Livro(
                    id, titulo, ano, autor, isbn
                );
            }

            case 2 -> {
                int numeroEdicao = lerInt("Número da edição: ");
                String issn = ler("ISSN: ");

                item = new Revista(
                    id, titulo, ano, numeroEdicao, issn
                );
            }

            case 3 -> {
                int duracao = lerInt("Duração em minutos: ");
                String genero = ler("Gênero: ");

                item = new DVD(
                    id, titulo, ano, duracao, genero
                );
            }

                default -> throw new IllegalArgumentException(
                    "Tipo de item inválido."
                );
            }
            biblioteca.cadastrarItem(item);
            System.out.println(
            "Item cadastrado com sucesso!"
            );
}

private static void editarItem() throws RegistroNaoEncontradoException{
    System.out.println("\n=== EDITAR ITEM ===");

        int id = lerInt("ID do item: ");
        String novoTitulo = ler("Novo título: ");
        int novoAno = lerInt("Novo ano: ");

        biblioteca.editarItem(
            id, novoTitulo, novoAno
        );

        System.out.println(
            "Item editado com sucesso!"
        );
}

private static void cadastrarUsuario() {

        System.out.println("\n=== CADASTRAR USUÁRIO ===");
        System.out.println("1 - Aluno");
        System.out.println("2 - Professor");
        System.out.println("3 - Funcionário");

        int tipo = lerInt("Tipo do usuário: ");

        if (tipo < 1 || tipo > 3) {
            throw new IllegalArgumentException(
                "Tipo de usuário inválido."
            );
        }

        int id = lerInt("ID: ");
        String nome = ler("Nome: ");

        String resposta = ler(
            "Limite personalizado (Enter para padrão): "
        );

        Integer limite = null;

        if (!resposta.isBlank()) {
            limite = lerLimite(resposta);
        }

        Usuario usuario;

        switch (tipo) {

            case 1 -> {
                usuario = (limite == null)
                    ? new Aluno(id, nome)
                    : new Aluno(id, nome, limite);
            }

            case 2 -> {
                usuario = (limite == null)
                    ? new Professor(id, nome)
                    : new Professor(id, nome, limite);
            }

            case 3 -> {
                usuario = (limite == null)
                    ? new Funcionario(id, nome)
                    : new Funcionario(id, nome, limite);
            }

            default -> throw new IllegalArgumentException(
                "Tipo de usuário inválido."
            );
        }

        biblioteca.cadastrarUsuario(usuario);

        System.out.println(
            "Usuário cadastrado com sucesso!"
        );
}

private static int lerLimite(String texto) {

        try {
            int limite = Integer.parseInt(texto);

            if (limite <= 0) {
                throw new IllegalArgumentException(
                    "O limite deve ser maior que zero."
                );
            }

            return limite;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "O limite deve ser um número inteiro."
            );
        }
    }

private static void consultarAcervo() {

        System.out.println("\n=== CONSULTAR ACERVO ===");
        System.out.println("1 - Listar todos os itens");
        System.out.println("2 - Buscar por título");
        System.out.println("3 - Buscar por autor");
        System.out.println("4 - Buscar por tipo");
        System.out.println("5 - Listar itens disponíveis");
        System.out.println("6 - Listar usuários");

        int opcao = lerInt("Escolha uma opção: ");

        switch (opcao) {

            case 1 -> {
                for (ItemBiblioteca item :
                        biblioteca.listarItens()) {

                    System.out.println(item.consultar());
                }
            }

            case 2 -> {
                String titulo = ler("Título: ");

                for (ItemBiblioteca item :
                        biblioteca.buscarPorTitulo(titulo)) {

                    System.out.println(item.consultar());
                }
            }

            case 3 -> {
                String autor = ler("Autor: ");

                for (Livro livro :
                        biblioteca.buscarPorAutor(autor)) {

                    System.out.println(livro.consultar());
                }
            }

            case 4 -> {
                System.out.println("1 - Livro");
                System.out.println("2 - Revista");
                System.out.println("3 - DVD");

                int tipo = lerInt("Tipo: ");

                Class<? extends ItemBiblioteca> classe;

                switch (tipo) {

                    case 1 -> classe = Livro.class;

                    case 2 -> classe = Revista.class;

                    case 3 -> classe = DVD.class;

                    default -> throw new IllegalArgumentException(
                        "Tipo inválido."
                    );
                }

                for (ItemBiblioteca item :
                        biblioteca.buscarPorTipo(classe)) {

                    System.out.println(item.consultar());
                }
            }

            case 5 -> {
                for (ItemBiblioteca item :
                        biblioteca.buscarDisponiveis()) {

                    System.out.println(item.consultar());
                }
            }

            case 6 -> {
                for (Usuario usuario :
                        biblioteca.listarUsuarios()) {

                    System.out.println(usuario);
                }
            }

            default -> System.out.println(
                "Opção de consulta inválida."
            );
        }
    }

     private static void listarEmprestimos() {

        System.out.println("\n=== EMPRÉSTIMOS ===");

        for (Emprestimo emprestimo :
                biblioteca.listarEmprestimos()) {

            System.out.println(emprestimo);

            System.out.println(
                "----------------------------"
            );
        }
    }
}