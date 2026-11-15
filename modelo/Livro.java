package modelo;

public class Livro extends ItemBiblioteca{
    private String autor;
    private String isbn;

    public Livro(int id, String titulo, int ano, String autor, String isbn){

        super(id, titulo, ano);

        this.autor = autor;
        this.isbn = isbn;
    }

    public void setAutor(String autor){
        this.autor = autor;
    }
    public String getAutor(){
        return this.autor;
    }

    public void setIsbn(String isbn){
        this.isbn = isbn;
    }
    public String getIsbn(){
        return this.isbn;
    }

    @Override
    public String toString() {
        return super.toString()
           + "\nAutor: " + getAutor()
             + "\nISBN: " + getIsbn();
    }

    @Override public int getPrazoEmprestimoDias(){
        return 15;
    }
}
