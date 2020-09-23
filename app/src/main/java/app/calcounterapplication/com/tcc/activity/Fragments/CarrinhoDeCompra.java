package app.calcounterapplication.com.tcc.activity.Fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.calcounterapplication.com.tcc.Adapter.AdapterProdutoPublico;
import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.model.Produto;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarrinhoDeCompra extends Fragment {

    View myView;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerCarrinho;
    private AdapterProdutoPublico adapterCarrinhoCompras;
    private List<Produto> listaProduto = new ArrayList<>();
    private DatabaseReference produtoCarrinhoRef;
    private FirebaseUser usuarioLogado;
    private AlertDialog dialog;


    public CarrinhoDeCompra() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_carrinho_de_compra, container, false);

        mAuth = ConfigFirebase.getFirebaseAuth();
        usuarioLogado = ConfigFirebase.getUsuarioAtual();
        produtoCarrinhoRef = ConfigFirebase.getFirebase()
                .child("carrinho_compras");



        inicializarComponentes();

        //configurar recyclerView
        recyclerCarrinho.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerCarrinho.setHasFixedSize(true);
        adapterCarrinhoCompras = new AdapterProdutoPublico(listaProduto, getActivity());
        recyclerCarrinho.setAdapter(adapterCarrinhoCompras);

        recuperarCarrinho();


        return myView;
    }

    private void recuperarCarrinho() {

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Carregando Carrinho de Compras !")
                .setCancelable(false)
                .build();
        dialog.show();

        listaProduto.clear();
        produtoCarrinhoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    listaProduto.add(ds.getValue(Produto.class));
                }

//                for (DataSnapshot regiao : dataSnapshot.getChildren()) {
//                    for (DataSnapshot categorias : regiao.getChildren()) {
//                        for (DataSnapshot produtos : categorias.getChildren()) {
//
//                            Produto produto = produtos.getValue(Produto.class);
//                            listaProduto.add(produto);
//
//                        }
//                    }
//                }


                Collections.reverse(listaProduto);
                adapterCarrinhoCompras.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarComponentes() {
        recyclerCarrinho = myView.findViewById(R.id.recyclerCarrinho);
    }

}
