package modelo;

public class Revista extends ItemBiblioteca{
    private int numeroEdicao;
    private String issn;

    public Revista(int id, String titulo, int ano, int numeroEdicao, String issn){

        super(id, titulo, ano);
        this.numeroEdicao = numeroEdicao;
        this.issn = issn;
    }

    public void setNumeroEdicao(int numeroEdicao){
        this.numeroEdicao = numeroEdicao;
    }
    public int getNumeroEdicao(){
        return this.numeroEdicao;
    }

    public void setIssn(String issn){
        this.issn = issn;
    }
    public String getIssn(){
        return this.issn;
    }

    @Override
    public String toString() {
        return super.toString()
           + "\nNúmero da edição: " + getNumeroEdicao()
             + "\nISSN: " + getIssn();
    }

    @Override public int getPrazoEmprestimoDias(){
        return 10;
    }
}
