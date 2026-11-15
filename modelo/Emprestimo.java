package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprestimo {
    private int id;
    private Usuario usuario;
    private ItemBiblioteca item;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;

    public Emprestimo(int id, Usuario usuario, ItemBiblioteca item, LocalDate dataEmprestimo){

        this.id = id;
        this.usuario = usuario;
        this.item = item;
        this.dataEmprestimo = dataEmprestimo;

        int prazo = Math.min(item.getPrazoEmprestimoDias(), usuario.getPrazoMaximoEmprestimoDias());
        
        this.dataPrevistaDevolucao = dataEmprestimo.plusDays(prazo);

        this.dataDevolucao = null;
    }

    public int getId(){
        return this.id;
    }
    public Usuario getUsuario(){
        return this.usuario;
    }
    public ItemBiblioteca getItem(){
        return this.item;
    }
    public LocalDate getEmprestimo(){
        return this.dataEmprestimo;
    }
    public LocalDate getDataPrevistaDevolucao(){
        return this.dataPrevistaDevolucao;
    }
    public LocalDate getDataDevolucao(){
        return this.dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao){
        this.dataDevolucao = dataDevolucao;
    }

    public boolean estaAtivo(){
        if(dataDevolucao == null){
            return true;
        }else{
            return false;
        }
    }

    public long calcularDiasAtraso(){
        LocalDate dataReferencia;

        if(dataDevolucao != null){
            dataReferencia = dataDevolucao;
        }else{
            dataReferencia = LocalDate.now();
        }

        long diasAtraso = ChronoUnit.DAYS.between(dataPrevistaDevolucao, dataReferencia);
        
        return Math.max(0, diasAtraso);
    }

    public double calcularMulta(){
        long multa = calcularDiasAtraso();
        return multa * 0.5;
    }

    public void registrarDevolucao(LocalDate data){
        if(!estaAtivo()){
            throw new IllegalStateException(
                "Este empréstimo já foi devolvido"
            );
        }

        if(dataDevolucao == null){
            throw new IllegalArgumentException("A data de devolução não pode ser nula.");
        }

        if(dataDevolucao.isBefore(dataEmprestimo)){
            throw new IllegalArgumentException("A data de devolução não pode ser anterior à data do empréstimo.");
        }

        this.dataDevolucao = dataDevolucao;
    }
    
    @Override
    public String toString() {

        String resultado = "ID do empréstimo: " + getId()
            + "\nUsuário: " + usuario.getNome()
            + "\nItem: " + item.getTitulo()
            + "\nData do empréstimo: " + dataEmprestimo
            + "\nData prevista de devolução: " + dataPrevistaDevolucao
            + "\nSituação: " + (estaAtivo() ? "Ativo" : "Devolvido");

        if (dataDevolucao != null) {
            resultado += "\nData de devolução: " + dataDevolucao;
        }

        double multa = calcularMulta();

        if (multa > 0) {
            resultado += "\nMulta: R$ " + String.format("%.2f", multa);
        }

        return resultado;
    }
}
