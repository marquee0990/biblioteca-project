package modelo;

public class Funcionario extends Usuario{
   public Funcionario(int id, String nome){
        super(id, nome, TipoUsuario.FUNCIONARIO, 4);
    }

    public Funcionario(int id, String nome, int limiteEmprestimo){
        super(id, nome, TipoUsuario.FUNCIONARIO, validarLimite(limiteEmprestimo));
    }

    private static int validarLimite(int limite){
        if(limite <= 0){
            throw new IllegalArgumentException("Limite de empréstimos deve ser maior que zero.");
        }
        return limite;
    }

    @Override 
    public int getPrazoMaximoEmprestimoDias(){
        return 15;
    }

    public String toString(){
        return super.toString() + "\nPrazo Máximo: " + getPrazoMaximoEmprestimoDias() + " dias";
    }
}
