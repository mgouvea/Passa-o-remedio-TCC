package app.calcounterapplication.com.tcc.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import app.calcounterapplication.com.tcc.config.ConfigFirebase;

public class Farmacia extends Usuario implements Serializable {

    private String cnpj;
    private String cep;
    private String rua;
    private String uf;
    private String regiao;
    private String numero;
    private String cidade;
    private String idFarma;


    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getIdFarma() {
        return idFarma;
    }

    public void setIdFarma(String idFarma) {
        this.idFarma = idFarma;
    }

    //    public void salvar() {
//        //super.salvar();
//        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
//        DatabaseReference usuarios = firebaseRef.child("usuarios").child("farmacias")
//                .child(getId());
//        usuarios.setValue(this);
//    }
}
