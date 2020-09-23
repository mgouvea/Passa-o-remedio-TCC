package app.calcounterapplication.com.tcc.model;

import java.util.Date;

public class PessoaFisica extends Usuario {

    private String rg;
    private String cpf;
    //TODO:
        //mudar o tipo String para date
    private String dtNascimento;

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDtNascimento() {
        return dtNascimento;
    }

    public void setDtNascimento(String dtNascimento) {
        this.dtNascimento = dtNascimento;
    }
}
