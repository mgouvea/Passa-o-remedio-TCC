package app.calcounterapplication.com.tcc.activity.cliente;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.Fragments.PedidosCliente;
import app.calcounterapplication.com.tcc.activity.LoginActivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Destino;
import app.calcounterapplication.com.tcc.model.Farmacia;
import app.calcounterapplication.com.tcc.model.Produto;

public class DetalheActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView titulo;
    private TextView valor;
    private TextView regiao;
    private TextView descricao;
    private TextView nomeFarmacia, enderecoFarmacia;
    private Produto produtoSelecionado;
    private String produtoID, farmacia, farmaciaID;
    private String enderecoDestino;
    private String cep, cidade, numero, rua, uf, bairro, nome;

    //permitir acesso
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        inicializarComponentes();

        //Recupera anuncio selecionado
        produtoSelecionado = (Produto) getIntent()
                .getSerializableExtra("produtoSelecionado");

        if (produtoSelecionado != null) {

            titulo.setText(produtoSelecionado.getProduto());
            descricao.setText(produtoSelecionado.getDescricao());
            regiao.setText(produtoSelecionado.getRegiao());
            valor.setText(produtoSelecionado.getValor());
            produtoID = produtoSelecionado.getIdProduto();
            farmacia = produtoSelecionado.getNomeUsuario();
            farmaciaID = produtoSelecionado.getIdUsuario();
            recuperarLocalizacaoDaFarmacia(farmaciaID);

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {

                    String urlString = produtoSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);

                }
            };

            carouselView.setPageCount(produtoSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);

        }


        //Mostrar o botão
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Ativar o botão
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Detalhe Produto");
    }

    //    Address addressDestino = recuperarEndereco(enderecoDestino);
//    final Destino destino = new Destino();
    public void comprarDireto(View view) {

        enderecoDestino = enderecoFarmacia.getText().toString();

        if (!enderecoDestino.equals("") || enderecoDestino != null) {
            Address addressDestino = recuperarEndereco(enderecoDestino);
            if (addressDestino != null) {
                final Destino destino = new Destino();
                destino.setCidade(addressDestino.getAdminArea());
                destino.setCep(addressDestino.getPostalCode());
                destino.setBairro(addressDestino.getSubLocality());
                destino.setRua(addressDestino.getThoroughfare());
                destino.setNumero(addressDestino.getFeatureName());
                destino.setLatitude(String.valueOf(addressDestino.getLatitude()));
                destino.setLongitude(String.valueOf(addressDestino.getLongitude()));
                destino.setNomeDestino(nomeFarmacia.getText().toString());

                //Atualizar Geofire
                UsuarioFirebase.atualizarDadosLocalizacao(addressDestino.getLatitude(), addressDestino.getLongitude(), farmaciaID);

                StringBuilder mensagem = new StringBuilder();
                mensagem.append("Deseja confirmar a sua compra em: ");
                mensagem.append("\n" + nomeFarmacia.getText().toString());
                mensagem.append("\n" + enderecoFarmacia.getText().toString());

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("Confirme sua compra")
                        .setMessage(mensagem)
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //salvar nó pedido e histórico
                                produtoSelecionado.pedidoRealizado();
                                produtoSelecionado.historicoPedido(farmaciaID);

                                //salvar requisicao
                                Intent intent = new Intent(DetalheActivity.this, ClienteNavigationDrawer.class);
                                intent.putExtra("nomeFarmacia", nomeFarmacia.getText().toString());
                                intent.putExtra("enderecoFarmacia", enderecoFarmacia.getText().toString()); // getText() SHOULD NOT be static!!!
                                startActivity(intent);


                                //TODO: fragment / activity connection
//                                    FragmentManager fragmentManager = getSupportFragmentManager();
//                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                    PedidosCliente pedidosCliente = new PedidosCliente();
//
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("enderecoFarmacia", enderecoFarmacia.getText().toString());
//                                    pedidosCliente.setArguments(bundle);
//                                    fragmentTransaction.replace(R.id.frameID, pedidosCliente);
//                                    fragmentTransaction.commit();;


                            }
                        }).setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancelar requisicao
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        } else {
            Toast.makeText(this,
                    "Não foi possível localizar o endereço da farmácia!",
                    Toast.LENGTH_SHORT).show();
        }
        //Fim
    }

    private void recuperarLocalizacaoDaFarmacia(String id) {

        //acessar endereco farmacia
        DatabaseReference usuariosRef = ConfigFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(id);
        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Farmacia farmacia = dataSnapshot.getValue(Farmacia.class);

                cep = farmacia.getCep();
                cidade = farmacia.getCidade();
                numero = farmacia.getNumero();
                rua = farmacia.getRua();
                uf = farmacia.getUf();
                bairro = farmacia.getRegiao();

                String enderecoFarmaciaString = cidade + ", " + bairro + ", " + rua + ", " + numero + ", " + cep;

                nomeFarmacia.setText(farmacia.getNome());
                enderecoFarmacia.setText(enderecoFarmaciaString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void salvarRequisicao(Destino destino){
//        Requisicao requisicao = new Requisicao();
//        requisicao.setDestino(destino);
//
//        Usuario usuarioPassageiro = UsuarioFirebase.getDadosUsuarioLogado();
//        usuarioPassageiro.setLatitude( String.valueOf(localPassageiro.latitude)  );
//        usuarioPassageiro.setLongitude( String.valueOf(localPassageiro.longitude) );
//
//        requisicao.setPassageiro(usuarioPassageiro);
//        requisicao.setStatus(Requisicao.STATUS_AGUARDANDO);
//        requisicao.salvar();
//
////        linearLayoutDestino.setVisibility(View.GONE);
////        buttonChamarUber.setText("Cancelar Uber");
//        //esse codigo nao estava comentado!!!!
//
//    }

    public static FirebaseUser getUsuarioAtual() {
        FirebaseAuth usuario = ConfigFirebase.getFirebaseAuth();
        return usuario.getCurrentUser();
    }

    private Address recuperarEndereco(String endereco) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> listaEndereco = geocoder.getFromLocationName(endereco, 1);

            if (listaEndereco != null && listaEndereco.size() > 0) {
                Address address = listaEndereco.get(0);

                return address;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void inicializarComponentes() {
        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.textTituloDetalhe);
        valor = findViewById(R.id.textValorDetalhe);
        regiao = findViewById(R.id.textVRegiaoDetalhe);
        descricao = findViewById(R.id.textDescricaoDetalhe);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        nomeFarmacia = findViewById(R.id.nomeFarmacia);
        enderecoFarmacia = findViewById(R.id.enderecoFarmacia);
    }

}
