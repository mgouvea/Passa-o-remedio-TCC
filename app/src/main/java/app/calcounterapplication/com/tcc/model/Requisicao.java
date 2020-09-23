package app.calcounterapplication.com.tcc.model;


import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import app.calcounterapplication.com.tcc.config.ConfigFirebase;

public class Requisicao {

    private String id;
    private String status;
    private Usuario cliente;
    private Usuario entregador;
    private Usuario farmacia;
    private Destino destino;

    public static final String STATUS_AGUARDANDO = "aguardando";
    public static final String STATUS_A_CAMINHO = "acaminho";
    public static final String STATUS_VIAGEM = "viagem";
    public static final String STATUS_FINALIZADA = "finalizada";
    public static final String STATUS_ENCERRADA = "encerrada";

    public Requisicao() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        String idRequisicao = requisicoes.push().getKey();
        setId(idRequisicao);

        requisicoes.child(getId()).setValue(this);
    }

    public void remover(String idRequisicao){

        DatabaseReference referencia = ConfigFirebase.getFirebaseDatabase()
                .child("requisicoes")
                .child(idRequisicao);
        referencia.removeValue();
        System.out.println("porque nao " + getId());
        System.out.println("porque sim " + idRequisicao);

    }

    public void atualizar(){
        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");


        DatabaseReference requisicao = requisicoes.child(getId());

        Map objeto = new HashMap();
        objeto.put("entregador", getEntregador());
        objeto.put("status", getStatus());

        requisicao.updateChildren(objeto);
    }

    public void atualizarStatus(){
        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        DatabaseReference requisicao = requisicoes.child(getId());

        Map objeto = new HashMap();
        objeto.put("status", getStatus());

        requisicao.updateChildren(objeto);

    }

    public void atualizarLocalizacaoEntregador(){
        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        DatabaseReference requisicao = requisicoes
                .child(getId())
                .child("entregador");

        Map objeto = new HashMap();
        objeto.put("latitude", getEntregador().getLatitude());
        objeto.put("longitude", getEntregador().getLongitude());

        requisicao.updateChildren(objeto);
    }

    public void historicoRequisicao(String idEntregador){
        DatabaseReference firebaseRef = ConfigFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("historico_requisicoes").child(idEntregador);

        String idRequisicao = requisicoes.push().getKey();
        setId(idRequisicao);

        requisicoes.child(getId()).setValue(this);
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Usuario getEntregador() {
        return entregador;
    }

    public void setEntregador(Usuario entregador) {
        this.entregador = entregador;
    }

    public Usuario getFarmacia() {
        return farmacia;
    }

    public void setFarmacia(Usuario farmacia) {
        this.farmacia = farmacia;
    }

    public Destino getDestino() {
        return destino;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }
}

