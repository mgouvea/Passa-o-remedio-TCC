package app.calcounterapplication.com.tcc.model;

import com.google.firebase.database.DatabaseReference;

import app.calcounterapplication.com.tcc.config.ConfigFirebase;

public class Entregador extends PessoaFisica{

    private String contaBancaria;

    public String getContaBancaria() {
        return contaBancaria;
    }

    public void setContaBancaria(String contaBancaria) {
        this.contaBancaria = contaBancaria;
    }

//    public void salvar() {
//        //super.salvar();
//        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
//        DatabaseReference usuarios = firebaseRef.child("usuarios").child("entregadores")
//                .child(getId());
//        usuarios.setValue(this);
//    }

}
