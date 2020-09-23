package app.calcounterapplication.com.tcc.model;

import com.google.firebase.database.DatabaseReference;

import app.calcounterapplication.com.tcc.config.ConfigFirebase;

public class Cliente extends PessoaFisica {
    private String cartaoDeCredido;
    public static final String PAIS = "Brazil";
    private String cidade;
    private String cep;
    private String bairro;
    private String rua;
    private String numero;
    private String uf;
    private String idCliente;

//   campoRg, campoDtNascimento, campoCPF, campoCidade, campoCEP, campoRua, campoUF;

    public String getCartaoDeCredido() {
        return cartaoDeCredido;
    }

    public void setCartaoDeCredido(String cartaoDeCredido) {
        cartaoDeCredido = cartaoDeCredido;
    }

//    public void salvar() {
//        //super.salvar();
//        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
//        DatabaseReference usuarios = firebaseRef.child("usuarios").child("clientes")
//                .child(getId());
//        usuarios.setValue(this);
//    }


    public static String getPAIS() {
        return PAIS;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }
}

