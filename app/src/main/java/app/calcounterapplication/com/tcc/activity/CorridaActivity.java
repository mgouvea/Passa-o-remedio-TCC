package app.calcounterapplication.com.tcc.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.cliente.ClienteNavigationDrawer;
import app.calcounterapplication.com.tcc.activity.entregador.MenuEntregadorActivity;
import app.calcounterapplication.com.tcc.activity.entregador.RequisicoesEntregadorAcitivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.Local;
import app.calcounterapplication.com.tcc.helper.Marcadores;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Cliente;
import app.calcounterapplication.com.tcc.model.Destino;
import app.calcounterapplication.com.tcc.model.Requisicao;
import app.calcounterapplication.com.tcc.model.Usuario;

public class CorridaActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    /*
    *Localizaçao
    *   entregador: Latitude -> -15.811 / Longitude -> -47.8896714
    *   farmacia: Latitude -> -15.8149985 / Longitude -> -47.8896714
    *   cliente: Latitude -> -15.813 / Longitude -> -47.8896714
     */

    //Componente
    private Button buttonAceitarCorrida;
    private FloatingActionButton fabRota;
    private GoogleMap mMap;
    private GoogleMap mMapDois;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localEntregador;
    private LatLng localCliente;
    private LatLng localFarmacia;
    private Usuario entregador;
    private Usuario cliente;
    private Usuario farmacia;
    private String idRequisicao;
    private Requisicao requisicao;
    private DatabaseReference firebaseRef;
    private TextView nomeCliente, enderecoCliente;
    private Marker marcadorEntregador;
    private Marker marcadorCliente;
    private Marker marcadorFarmacia;
    private Marker marcadorRealizada;
    private String nomeFarmacia;
    private String statusRequisicao;
    private Boolean requisicaoAtiva;
    private Marcadores marcadores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corrida);

        inicializarComponentes();

        //Recuperar dados do usuário
        if(getIntent().getExtras().containsKey("idRequisicao")
                && getIntent().getExtras().containsKey("entregador")){
            Bundle extras = getIntent().getExtras();
            entregador = (Usuario) extras.getSerializable("entregador");
            localEntregador = new LatLng(
                    Double.parseDouble(entregador.getLatitude()),
                    Double.parseDouble(entregador.getLongitude())
            );
            idRequisicao = extras.getString("idRequisicao");
            requisicaoAtiva = extras.getBoolean("requisicaoAtiva");
            verificaStatusRequisicao();
        }

    }

    private void verificaStatusRequisicao(){

        final DatabaseReference requisicoes = firebaseRef.child("requisicoes")
                .child(idRequisicao);
        requisicoes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Recupera a requisicao
                requisicao = dataSnapshot.getValue(Requisicao.class);
                if(requisicao != null){
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

                    statusRequisicao = requisicao.getStatus();
                    alteraInterfaceStatusRequisicao(statusRequisicao);

                    //dados dos campos de texto
                    nomeCliente.setText("Cliente: " + requisicao.getCliente().getNome());
                    nomeFarmacia = requisicao.getDestino().getNomeDestino();
                    enderecoCliente.setText("Nome farmácia: " + nomeFarmacia);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void alteraInterfaceStatusRequisicao(String status){
        switch ( status ){
            case Requisicao.STATUS_AGUARDANDO :
                requisicaoAguardando();
                break;
            case Requisicao.STATUS_A_CAMINHO :
                requisicaoAcaminho();
                break;
            case Requisicao.STATUS_VIAGEM:
                requisicaoViagem();
                break;
            case Requisicao.STATUS_FINALIZADA:
                requisicaoFinalizada();
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    private void requisicaoFinalizada(){

        fabRota.setVisibility(View.GONE);
        requisicaoAtiva = false;

        if(marcadorEntregador != null){
            marcadorEntregador.remove();
        }

        if(marcadorCliente != null){
            marcadorCliente.remove();
//            marcadorCliente = null;
        }

        //Exibe marcador de destino
        LatLng localFinal = new LatLng(
                localCliente.latitude,
                localCliente.longitude
        );

        marcadores.adicionaMarcadorEntregaFinalizada(localFinal, "Entrega feita a(o): " + cliente.getNome());
        marcadores.centralizarMarcador(localFinal);


        //Calcular a distancia
        float distancia = Local.calcularDistancia(localCliente, localFarmacia);
        float valor = distancia * 12;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String resultado = decimalFormat.format(valor);

        buttonAceitarCorrida.setText("Confirmar Entregar - R$ " + resultado);
        buttonAceitarCorrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verificar o status da requisicao para encerrar
                if(statusRequisicao != null && !statusRequisicao.isEmpty()){
                    requisicao.setStatus(Requisicao.STATUS_ENCERRADA);
                    requisicao.atualizarStatus();
                    Intent intent = new Intent(CorridaActivity.this, MenuEntregadorActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

    }

    private void requisicaoAguardando(){
        buttonAceitarCorrida.setText("Realizar Entrega");

        //Exibe marcador do entregador
        marcadorEntregador = marcadores.adicionaMarcadorEntregador(localEntregador, entregador.getNome() );

        marcadores.centralizarMarcador(localEntregador);
    }

    @SuppressLint("RestrictedApi")
    private void requisicaoAcaminho(){
        buttonAceitarCorrida.setText("A caminho da farmácia");
        fabRota.setVisibility(View.VISIBLE);

        //Exibe marcador do entregador
        marcadorEntregador = marcadores.adicionaMarcadorEntregador(localEntregador, entregador.getNome() );

        //Exibe marcador farmacia
        marcadorFarmacia = marcadores.adicionaMarcadorFarmacia(localFarmacia, nomeFarmacia);

        //Centralizar dois marcadores
        marcadores.centralizarDoisMarcadores(marcadorEntregador, marcadorFarmacia);

        //Inicia monitoramento do entregador / farmácia
        iniciarMonitoramento(entregador, localFarmacia, Requisicao.STATUS_VIAGEM);
    }

    @SuppressLint("RestrictedApi")
    private void requisicaoViagem(){

        //Alterar interface
        fabRota.setVisibility(View.VISIBLE);
        buttonAceitarCorrida.setText("A caminho do cliente");

        //Exibe marcador do motorista
        marcadorEntregador = marcadores.adicionaMarcadorEntregador(localEntregador, entregador.getNome());

        LatLng localCliente = new LatLng(
                Double.parseDouble(cliente.getLatitude()),
                Double.parseDouble(cliente.getLongitude())
        );
        marcadorCliente = marcadores.adicionaMarcadorCliente(localCliente, "Cliente");

        //Centraliza marcadores motorista / cliente
        marcadores.centralizarDoisMarcadores(marcadorEntregador, marcadorCliente);
        iniciarMonitoramento(entregador, localCliente, Requisicao.STATUS_FINALIZADA);


    }

    private void iniciarMonitoramento(final Usuario usuarioOrigem, LatLng localDestino, final String status) {

        //Inicializar Geofire
        DatabaseReference localUsuario = ConfigFirebase.getFirebaseDatabase()
                .child("local_usuario");
        GeoFire geoFire = new GeoFire(localUsuario);

        //Adiciona círculo no destino
        final Circle circulo = mMap.addCircle(
                new CircleOptions()
                        .center( localDestino )
                        .radius( 50 ) //em metros
                        .fillColor(Color.argb(90, 255, 153, 0))
                        .strokeColor(Color.argb(190, 255, 152, 0))
        );

        final GeoQuery geoQuery = geoFire.queryAtLocation(
                new GeoLocation(localDestino.latitude, localDestino.longitude),
                0.05 //em km (0.05 50 metros)
        );
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(key.equals(usuarioOrigem.getId())){
                    //Log.d("onKeyEntered", "onKeyEntered: motorista esta dentro da area");


                    //Altera status da requisicao
                    requisicao.setStatus(status);
                    requisicao.atualizarStatus();

                    //Remove listener
                    geoQuery.removeAllListeners();
                    circulo.remove();

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMapDois = mMap;

        marcadores = new Marcadores(CorridaActivity.this, mMap);
        //Recuperar localizacao do usuario
        recuperarLocalizacaoUsuario();
    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localEntregador = new LatLng(latitude, longitude);

                //Atualizar Geofire
                UsuarioFirebase.atualizarDadosLocalizacao(latitude, longitude);

                //Atualizar localizacao entregador no Firebase
                entregador.setLatitude(String.valueOf(latitude));
                entregador.setLongitude(String.valueOf(longitude));
                requisicao.setEntregador(entregador);
                requisicao.atualizarLocalizacaoEntregador();

                alteraInterfaceStatusRequisicao(statusRequisicao);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
        }


    }

    public void realizarEntrega(View view) {

        //Configurar Requisicao
        requisicao = new Requisicao();
        requisicao.setId(idRequisicao);
        requisicao.setEntregador(entregador);
        requisicao.setStatus(Requisicao.STATUS_A_CAMINHO);

        requisicao.atualizar();
    }

    private void inicializarComponentes(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Iniciar Entrega");

        //Configuracoes iniciais
        buttonAceitarCorrida = findViewById(R.id.buttonAceitarCorrida);
        firebaseRef = ConfigFirebase.getFirebaseDatabase();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        nomeCliente = findViewById(R.id.nomeCliente);
        enderecoCliente = findViewById(R.id.enderecoCliente);

        //Adiciona evento de clique no FabRota
        fabRota = findViewById(R.id.fabRota);
        fabRota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = statusRequisicao;

                if(status != null && !status.isEmpty()) {
                    String lat = "";
                    String lon = "";

                    switch (status) {
                        case Requisicao.STATUS_A_CAMINHO:
                            lat = String.valueOf(localFarmacia.latitude);
                            lon = String.valueOf(localFarmacia.longitude);
                            break;
                        case Requisicao.STATUS_VIAGEM:
                            lat = String.valueOf(localCliente.latitude);
                            lon = String.valueOf(localCliente.longitude);
                            break;
                    }

                    //Abrir rota
                    String latLong = lat + "," + lon;
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLong + "&mode=d");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        System.out.println("Olha aqui: " + requisicaoAtiva);
        if(requisicaoAtiva){
            System.out.println("Olha aqui: " + requisicaoAtiva);
            Toast.makeText(CorridaActivity.this,
                    "Necessário encerrar a requisição atual!",
                    Toast.LENGTH_SHORT).show();
        } else{
            Intent i = new Intent(CorridaActivity.this, RequisicoesEntregadorAcitivity.class);
            startActivity(i);
        }

        return false;
    }

}
