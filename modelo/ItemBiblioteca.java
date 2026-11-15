package modelo;

import interfaces.IPesquisavel;

public abstract class ItemBiblioteca implements IPesquisavel {
    private int id;
    private String titulo;
    private int ano;
    private StatusItem status;

    public ItemBiblioteca(int id, String titulo, int ano){
        this.id = id;
        this.titulo = titulo;
        this.ano = ano;
        this.status = StatusItem.DISPONIVEL;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }

    public void setTitulo(String titulo){
        this.titulo = titulo;
    }
    public String getTitulo(){
        return this.titulo;
    }

    public void setAno(int ano){
        this.ano = ano;
    }
    public int getAno(){
        return this.ano;
    }

    public void setStatus(StatusItem status){
        this.status = status;
    }
    public StatusItem getStatus(){
        return this.status;
    }

    @Override 
    public String toString(){
        return "ID: " + getId() + "\nTítulo: "+ getTitulo() + "\nAno: " + getAno() + "\nStatus: " + getStatus();
    }

    @Override 
    public String consultar(){
        return toString(); 
    }

    public abstract int getPrazoEmprestimoDias();
}
