package modelo;

public class Aluno extends Usuario{
   public Aluno(int id, String nome){
        super(id, nome, TipoUsuario.ALUNO, 3);
    }

    public Aluno(int id, String nome, int limiteEmprestimo){
        super(id, nome, TipoUsuario.ALUNO, validarLimite(limiteEmprestimo));
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
