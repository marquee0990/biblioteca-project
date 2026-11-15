package modelo;

public abstract class Usuario {
    private int id;
    private String nome;
    private TipoUsuario tipo;
    private int limiteEmprestimo;

    public abstract int getPrazoMaximoEmprestimoDias();

    public Usuario(int id, String nome, TipoUsuario tipo, int limiteEmprestimo){
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.limiteEmprestimo = limiteEmprestimo;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }

    public void setNome(String nome){
        this.nome = nome;
    }
    public String getNome(){
        return this.nome;
    }

    public void setTipo(TipoUsuario tipo){
        this.tipo = tipo;
    }
    public TipoUsuario getTipo(){
        return this.tipo;
    }

    public void setLimiteEmprestimo(int limiteEmprestimo){
        this.limiteEmprestimo = limiteEmprestimo;
    }
    public int getLimiteEmprestimo(){
        return this.limiteEmprestimo;
    }

    public boolean podeEmprestar(int emprestimosAtivos){
        return emprestimosAtivos < limiteEmprestimo;
    }

    @Override 
    public String toString(){
        return "ID: " + getId() + "\nNome: "+ getNome() + "\nTipo: " + getTipo() + "\nLimite de Empréstimos: " + getLimiteEmprestimo();
    }
}
