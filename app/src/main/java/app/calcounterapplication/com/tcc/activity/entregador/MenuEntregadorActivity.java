package app.calcounterapplication.com.tcc.activity.entregador;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.CorridaActivity;
import app.calcounterapplication.com.tcc.activity.MainActivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Usuario;

public class MenuEntregadorActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private ImageView logoutButton;
    private ImageButton minhasRequisicoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_entregador);

        mAuth = ConfigFirebase.getFirebaseAuth();

//        recuperarLocalizacaoCliente();

        logoutButton = findViewById(R.id.logoutEntregador);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutEntregador();
            }
        });

        minhasRequisicoes = findViewById(R.id.requisicoesEntregador);
        minhasRequisicoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRequisicoes();
            }
        });

        getSupportActionBar().setTitle("Entregador");
    }

    public void logoutEntregador(){
        mAuth.signOut();
        startActivity(new Intent( this, MainActivity.class));
        finish();
    }

    public void abrirTelaRequisicoes() {
        startActivity(new Intent(this, RequisicoesEntregadorAcitivity.class));
    }

    public void abrirMinhasEntregas(View view){
        startActivity(new Intent(this, EntregasRealizadasActivity.class));
    }

}
