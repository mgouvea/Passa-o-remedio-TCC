package app.calcounterapplication.com.tcc.activity.Fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import app.calcounterapplication.com.tcc.Adapter.AdapterCarrinhoCompras;
import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.cliente.ClienteNavigationDrawer;
import app.calcounterapplication.com.tcc.activity.farmacia.MeusProdutosActivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.Local;
import app.calcounterapplication.com.tcc.helper.Marcadores;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Cliente;
import app.calcounterapplication.com.tcc.model.Destino;
import app.calcounterapplication.com.tcc.model.Produto;
import app.calcounterapplication.com.tcc.model.Requisicao;
import app.calcounterapplication.com.tcc.model.Usuario;
import dmax.dialog.SpotsDialog;

public class PedidosCliente extends Fragment
        implements OnMapReadyCallback {


    private AlertDialog dialog;
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private DatabaseReference firebaseRef;
    private LatLng localCliente;
    private LatLng localFarmacia;
    private LatLng localEntregador;
    private boolean entregadorChamado = false;
    private Requisicao requisicao;
    private String enderecoFarmacia, nomeFarmacia;
    private Button buttonCancelarEntrega;
    private TextView acompanharPedido, acompanharEntregador;
    private LinearLayout pedidosFeitos;
    private AdapterCarrinhoCompras adapterCarrinhoCompras;
    private List<Produto> listaProduto = new ArrayList<>();
    private DatabaseReference pedidoRef;
    private RecyclerView recyclerPedido;
    private String produtoID, farmaciaID;
    private Usuario cliente;
    private Usuario farmacia;
    private Usuario entregador;
    private String statusRequisicao;
    private Marcadores marcadores;
    private Marker marcadorEntregador;
    private Marker marcadorCliente;
    private Marker marcadorFarmacia;
    private Produto pedidoProduto;

    private View myView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.pedidos_cliente, container, false);

        mAuth = ConfigFirebase.getFirebaseAuth();
        inicializarComponentes();
        recuperarPedido();

        verificaStatusRequisicao();

        //configurar recyclerView
        recyclerPedido.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerPedido.setHasFixedSize(true);
        adapterCarrinhoCompras = new AdapterCarrinhoCompras(listaProduto, getActivity());
        recyclerPedido.setAdapter(adapterCarrinhoCompras);

        return myView;

    }

    private void verificaStatusRequisicao() {

        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("cliente/id")
                .equalTo(usuarioLogado.getId());

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List<Requisicao> lista = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lista.add(ds.getValue(Requisicao.class));
                }

                Collections.reverse(lista);
                if (lista != null && lista.size() > 0) {
                    requisicao = lista.get(0);

                    Log.d("resultado", "onDataChange: " + requisicao.getId());

                    if (requisicao != null) {
//                        if(!requisicao.getStatus().equals(Requisicao.STATUS_ENCERRADA)) {
                            cliente = requisicao.getCliente();
                            localCliente = new LatLng(
                                    Double.parseDouble(cliente.getLatitude()),
                                    Double.parseDouble(cliente.getLongitude())
                            );

                            Destino destino = requisicao.getDestino();
                            localFarmacia = new LatLng(
                                    Double.parseDouble(destino.getLatitude()),
                                    Double.parseDouble(destino.getLongitude())
                            );


                            if (requisicao.getEntregador() != null) {
                                entregador = requisicao.getEntregador();
                                localEntregador = new LatLng(
                                        Double.parseDouble(entregador.getLatitude()),
                                        Double.parseDouble(entregador.getLongitude())
                                );
                            }

                            statusRequisicao = requisicao.getStatus();
                            alteraInterfaceStatusRequisicao(statusRequisicao);
//                        }
                    }
                }
//                else {
//
//                    acompanharPedido.setText("Não foi realizado nenhum pedido :(");
//                    acompanharEntregador.setVisibility(View.INVISIBLE);
//                    pedidosFeitos.setVisibility(View.INVISIBLE);
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void alteraInterfaceStatusRequisicao(String status) {
        if (status != null && !status.isEmpty()) {
            switch (status) {
                case Requisicao.STATUS_AGUARDANDO:
                    requisicaoAguardando();
                    break;
                case Requisicao.STATUS_A_CAMINHO:
                    requisicaoAcaminho();
                    break;
                case Requisicao.STATUS_VIAGEM:
                    requisicaoViagem();
                    break;
                case Requisicao.STATUS_FINALIZADA:
                    requisicaoFinalizada();
                    break;
                case Requisicao.STATUS_ENCERRADA:
                    requisicaoEncerrada();
                    break;
            }
        } else {
            //Adiciona marcador passageiro
            marcadores.adicionaMarcadorCliente(localCliente, "Meu Local");
            marcadores.centralizarMarcador(localCliente);
        }
    }

    private void requisicaoAguardando() {
        buttonCancelarEntrega.setVisibility(View.VISIBLE);
        entregadorChamado = true;

        //Adiciona marcador cliente
        marcadores.adicionaMarcadorCliente(localCliente, cliente.getNome());
        marcadores.centralizarMarcador(localCliente);
    }

    private void requisicaoAcaminho() {
        buttonCancelarEntrega.setVisibility(View.VISIBLE);
        entregadorChamado = true;

        //Adicionar marcador cliente
        marcadorCliente = marcadores.adicionaMarcadorCliente(localCliente, cliente.getNome());

        //Adiciona marcador motorista
        marcadorEntregador = marcadores.adicionaMarcadorEntregador(localEntregador, entregador.getNome());

        //Adiciona marcador farmacia
        marcadorFarmacia = marcadores.adicionaMarcadorFarmacia(localFarmacia, nomeFarmacia);

        //Centralizar passageiro / motorista
        marcadores.centralizarTresMarcadores(marcadorEntregador, marcadorCliente, marcadorFarmacia);
    }

    private void requisicaoViagem() {
        buttonCancelarEntrega.setVisibility(View.VISIBLE);
        entregadorChamado = true;

        //Adiciona marcador motorista
        marcadores.adicionaMarcadorEntregador(localEntregador, entregador.getNome());

        //Adiciona marcador cliente
        marcadores.adicionaMarcadorCliente(localCliente, cliente.getNome());

        //Centralizar marcadores motorista / destino
        marcadores.centralizarDoisMarcadores(marcadorEntregador, marcadorCliente);
    }

    private void requisicaoFinalizada() {

        buttonCancelarEntrega.setVisibility(View.VISIBLE);

        //Adiciona marcador de entrega finalizada
        marcadores.adicionaMarcadorEntregaFinalizada(localCliente, "Local de Entrega");
        marcadores.centralizarMarcador(localCliente);


    }

    private void requisicaoEncerrada(){
        //Calcular a distancia
        float distancia = Local.calcularDistancia(localCliente, localFarmacia);
        float valor = distancia * 12;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String resultado = decimalFormat.format(valor);


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setTitle("Total da viagem")
                .setMessage("Sua taxa de entrega ficou: R$ " + resultado)
                .setCancelable(false)
                .setNegativeButton("Confirmar Entrega", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
                        String idRequisicao = requisicao.getId();
                        requisicao.historicoRequisicao(entregador.getId());
                        requisicao.remover(idRequisicao);
                        pedidoProduto.removerPedido();

                        Intent intent = new Intent(getActivity(), ClienteNavigationDrawer.class);
                        getActivity().finish();
                        startActivity(intent);

                    }
                }).setPositiveButton("Denunciar Erro", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        marcadores = new Marcadores(getActivity(), mMap);

        //Recuperar localizacao do usuario
        recuperarLocalizacaoCliente();

    }

    private void salvarRequisicao(Destino destino) {
        Requisicao requisicao = new Requisicao();
        requisicao.setDestino(destino);

        Usuario usuarioCliente = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioCliente.setLatitude(String.valueOf(localCliente.latitude));
        usuarioCliente.setLongitude(String.valueOf(localCliente.longitude));

        requisicao.setCliente(usuarioCliente);
        requisicao.setStatus(Requisicao.STATUS_AGUARDANDO);
        requisicao.salvar();

        buttonCancelarEntrega.setVisibility(View.VISIBLE);
//        recuperarPedido();
    }

    private void recuperaEnderecoFarmacia(String enderecoFarmacia) {

        if (enderecoFarmacia != null) {
            if (!enderecoFarmacia.equals("") || enderecoFarmacia != null) {
                Address addressDestino = recuperarEndereco(enderecoFarmacia);
                if (addressDestino != null) {
                    final Destino destino = new Destino();
                    destino.setCidade(addressDestino.getAdminArea());
                    destino.setCep(addressDestino.getPostalCode());
                    destino.setBairro(addressDestino.getSubLocality());
                    destino.setRua(addressDestino.getThoroughfare());
                    destino.setNumero(addressDestino.getFeatureName());
                    destino.setLatitude(String.valueOf(addressDestino.getLatitude()));
                    destino.setLongitude(String.valueOf(addressDestino.getLongitude()));
                    destino.setNomeDestino(nomeFarmacia);

                    salvarRequisicao(destino);
                    entregadorChamado = true;

                } else {
                    Toast.makeText(getActivity(),
                            "Por algum motivo a farmacia não possui endereço!",
                            Toast.LENGTH_SHORT).show();
                }
                //Fim
            }
        }
    }

    private void recuperarLocalizacaoCliente() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localCliente = new LatLng(latitude, longitude);

                //Atualizar Geofire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);

                mMap.clear();
                //Altera interface de acordo com o status
                alteraInterfaceStatusRequisicao(statusRequisicao);


                if (entregadorChamado != true) {
                    Bundle bundle = getArguments();
                    if (bundle != null) {
                        nomeFarmacia = bundle.getString("nomeFarmacia");
                        enderecoFarmacia = bundle.getString("enderecoFarmacia");
                        recuperaEnderecoFarmacia(enderecoFarmacia);
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Solicitar atualizacoes de localizacao
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10,
                    10,
                    locationListener
            );
        } else {

        }

    }

    private Address recuperarEndereco(String endereco) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

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

    private void recuperarPedido() {

//        FragmentTransaction ftr = getFragmentManager().beginTransaction();
//        ftr.detach(PedidosCliente.this).attach(PedidosCliente.this).commit();

        pedidoProduto = new Produto();
        Cliente id = new Cliente();
        produtoID = pedidoProduto.getIdProduto();
        farmaciaID = id.getIdCliente();

        dialog = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setMessage("Carregando pedidos !")
                .setCancelable(false)
                .build();
        dialog.show();

        //configura nó por pedido
        pedidoRef = ConfigFirebase.getFirebase()
                .child("pedido_cliente");

        listaProduto.clear();
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot clienteID : dataSnapshot.getChildren()) {
                    for (DataSnapshot produtoID : clienteID.getChildren()) {

                        Produto produto = produtoID.getValue(Produto.class);
                        listaProduto.add(produto);

                    }
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


    private void inicializarComponentes() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (MapFragment) getFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseRef = ConfigFirebase.getFirebaseDatabase();

        buttonCancelarEntrega = myView.findViewById(R.id.cancelarEntrega);
        acompanharEntregador = myView.findViewById(R.id.acompanharEntregadorID);
        acompanharPedido = myView.findViewById(R.id.acompanharPedidoID);
        pedidosFeitos = myView.findViewById(R.id.linearLayoutPedidos);
        recyclerPedido = myView.findViewById(R.id.recyclerPedido);
    }


}
