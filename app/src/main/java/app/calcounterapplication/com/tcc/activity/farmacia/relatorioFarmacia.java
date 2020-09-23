package app.calcounterapplication.com.tcc.activity.farmacia;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.calcounterapplication.com.tcc.Adapter.AdapterCarrinhoCompras;
import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.model.Cliente;
import app.calcounterapplication.com.tcc.model.Farmacia;
import app.calcounterapplication.com.tcc.model.Produto;
import dmax.dialog.SpotsDialog;

public class relatorioFarmacia extends AppCompatActivity {

    private TextView qtdItem;
    DatabaseReference produtoUsuarioRef;
    private AdapterCarrinhoCompras adapterCarrinhoCompras;
    private List<Produto> listaProduto = new ArrayList<>();
    private DatabaseReference pedidoRef;
    private RecyclerView recyclerPedido;
    private FirebaseAuth mAuth;
    private String produtoID, farmaciaID;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_farmacia);

        mAuth = ConfigFirebase.getFirebaseAuth();
        pedidoRef = ConfigFirebase.getFirebase()
                .child("historic_pedido")
                .child(ConfigFirebase.getIdUsuario());

        inicializarComponentes();
        quantidadeItens();
        recuperarHistorico();

        //configurar recyclerView
        recyclerPedido.setLayoutManager(new LinearLayoutManager(relatorioFarmacia.this));
        recyclerPedido.setHasFixedSize(true);
        adapterCarrinhoCompras = new AdapterCarrinhoCompras(listaProduto,
                relatorioFarmacia.this);
        recyclerPedido.setAdapter(adapterCarrinhoCompras);

    }

    public void quantidadeItens() {

        produtoUsuarioRef = ConfigFirebase.getFirebase()
                .child("historic_pedido")
                .child(ConfigFirebase.getIdUsuario());

        produtoUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Produto> lista = new ArrayList<>();
                List<Produto> valorLista = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lista.add(ds.getValue(Produto.class));
                }

                if (lista != null && lista.size() > 0) {
                    String qtd = String.valueOf(lista.size());
                    qtdItem.setText(qtd);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    public void valorTotal() {
//
//        Produto pedidoProduto = new Produto();
//        String produtoID = pedidoProduto.getIdProduto();
//        //configura nó por pedido
//        produtoUsuarioRef = ConfigFirebase.getFirebase()
//                .child("historic_pedido")
//                .child(ConfigFirebase.getIdUsuario())
//                .child(produtoID);
//
//        produtoUsuarioRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        })
//    }

    private void recuperarHistorico() {

        dialog = new SpotsDialog.Builder()
                .setContext(relatorioFarmacia.this)
                .setMessage("Carregando histórico !")
                .setCancelable(false)
                .build();
        dialog.show();

        listaProduto.clear();
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listaProduto.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    listaProduto.add(ds.getValue(Produto.class));
                }

                Collections.reverse(listaProduto);
                adapterCarrinhoCompras.notifyDataSetChanged();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void inicializarComponentes() {
        qtdItem = findViewById(R.id.qtdItem);
        recyclerPedido = findViewById(R.id.recyclerHistoric);

    }
}
