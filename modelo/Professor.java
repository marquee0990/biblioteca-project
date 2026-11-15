package modelo;

public class Professor extends Usuario{
   public Professor(int id, String nome){
        super(id, nome, TipoUsuario.PROFESSOR, 5);
    }

    public Professor(int id, String nome, int limiteEmprestimo){
        super(id, nome, TipoUsuario.PROFESSOR, validarLimite(limiteEmprestimo));
    }

    private static int validarLimite(int limite){
        if(limite <= 0){
            throw new IllegalArgumentException("Limite de empréstimos deve ser maior que zero.");
        }
        return limite;
    }

    @Override 
    public int getPrazoMaximoEmprestimoDias(){
        return 20;
    }
    
    public String toString(){
        return super.toString() + "\nPrazo Máximo: " + getPrazoMaximoEmprestimoDias() + " dias";
    }
}
