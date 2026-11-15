package modelo;

public class DVD extends ItemBiblioteca{
    private int duracaoMinutos;
    private String genero;

    public DVD(int id, String titulo, int ano, int duracaoMinutos, String genero){

        super(id, titulo, ano);
        this.duracaoMinutos = duracaoMinutos;
        this.genero = genero;
    }

    public void setDuracaoMinutos(int duracaoMinutos){
        this.duracaoMinutos = duracaoMinutos;
    }
    public int getDuracaoMinutos(){
        return this.duracaoMinutos;
    }

    public void setGenero(String genero){
        this.genero = genero;
    }

    public String getGenero(){
        return this.genero;
    }

    @Override
    public String toString() {
        return super.toString()
           + "\nDuração em minutos: " + getDuracaoMinutos()
             + "\nGênero: " + getGenero();
    }

    @Override public int getPrazoEmprestimoDias(){
        return 5;
    }
}
