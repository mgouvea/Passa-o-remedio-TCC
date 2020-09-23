package app.calcounterapplication.com.tcc.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

import app.calcounterapplication.com.tcc.config.ConfigFirebase;


public class Produto implements Serializable {

    private String idProduto;
    private String regiao;
    private String categoria;
    private String marca;
    private String produto;
    private String produtoExibicao;
    private String valor;
    private String descricao;
    private String idUsuario;
    private String nomeUsuario;
    private List<String> Fotos;

    public Produto() {
        DatabaseReference produtoRef = ConfigFirebase.getFirebase()
                .child("meus_produtos");
        setIdProduto(produtoRef.push().getKey());

    }

    public void salvar() {

        String idUsuario = ConfigFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfigFirebase.getFirebase()
                .child("meus_produtos");

        anuncioRef.child(idUsuario)
                .child(getIdProduto())
                .setValue(this);

        salvarProdutoPublico();

    }

    public void salvarProdutoPublico() {

        String idUsuario = ConfigFirebase.getIdUsuario();
        DatabaseReference produtoRef = ConfigFirebase.getFirebase()
                .child("produtos");

        produtoRef.child(getIdProduto())
                .setValue(this);

//        produtoRef.child(getRegiao())
//                .child(getCategoria())
//                .child(getIdProduto())
//                .setValue(this);
    }

    public void salvarCarrinho() {

        String idUsuario = ConfigFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfigFirebase.getFirebase()
                .child("carrinho_compras");

        anuncioRef.child(idUsuario)
                .child(getIdProduto())
                .setValue(this);

    }

    public void pedidoRealizado() {

        String idUsuario = ConfigFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfigFirebase.getFirebase()
                .child("pedido_cliente");

        anuncioRef.child(idUsuario)
                .child(getIdProduto())
                .setValue(this);



    }

    public void historicoPedido(String idFarmacia) {

        String idUsuario = ConfigFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfigFirebase.getFirebase()
                .child("historic_pedido");

        anuncioRef.child(idFarmacia)
                .child(getIdProduto())
                .setValue(this);

    }

    public void remover() {

        String idUsuario = ConfigFirebase.getIdUsuario();
        final DatabaseReference anuncioRef = ConfigFirebase.getFirebase()
                .child("meus_produtos")
                .child(idUsuario)
                .child(getIdProduto());

        anuncioRef.removeValue();
        removerProdutoPublico();

    }

    public void removerProdutoPublico() {

        DatabaseReference produtoRef = ConfigFirebase.getFirebase()
                .child("produtos").child(getIdProduto());
//                .child(getRegiao())
//                .child(getCategoria());


        produtoRef.removeValue();
    }

    public void removerPedido() {

        String idUsuario = ConfigFirebase.getIdUsuario();
        final DatabaseReference anuncioRef = ConfigFirebase.getFirebase()
                .child("pedido_cliente")
                .child(idUsuario);
//                .child(getIdProduto());

        anuncioRef.removeValue();

    }


    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto.toUpperCase();
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return Fotos;
    }

    public void setFotos(List<String> fotos) {
        Fotos = fotos;
    }

    public String getRegiao() {
        return regiao;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getProdutoExibicao() {
        return produtoExibicao;
    }

    public void setProdutoExibicao(String produtoExibicao) {
        this.produtoExibicao = produtoExibicao;
    }
}
