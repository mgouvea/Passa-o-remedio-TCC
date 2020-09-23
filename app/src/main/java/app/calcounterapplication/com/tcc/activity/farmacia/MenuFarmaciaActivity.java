package app.calcounterapplication.com.tcc.activity.farmacia;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.MainActivity;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.model.Farmacia;
import app.calcounterapplication.com.tcc.model.Usuario;

public class MenuFarmaciaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ImageView logoutButton;
    private ImageView meusProdutos;
    private ImageView cadastrarProdutos;
    private ImageView relatorio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_farmacia);

        mAuth = ConfigFirebase.getFirebaseAuth();

        inicializarComponentes();

        //meusProduto
        meusProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuFarmaciaActivity.this,
                        MeusProdutosActivity.class));
            }
        });

        //cadastrar Produtos
        cadastrarProdutos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuFarmaciaActivity.this,
                        CadastrarProdutosActivity.class));
            }
        });

        //relatorios
        relatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuFarmaciaActivity.this,
                        relatorioFarmacia.class));
            }
        });

        //sair
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutEntregador();
            }
        });

        //titulo
        getSupportActionBar().setTitle("Farmácia");
    }


    public void logoutEntregador() {
        mAuth.signOut();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    public void inicializarComponentes() {
        logoutButton = findViewById(R.id.logoutFarmacia);
        meusProdutos = findViewById(R.id.meusProdutosFarmacia);
        cadastrarProdutos = findViewById(R.id.cadastrarProdutosFarmacia);
        relatorio = findViewById(R.id.relatorioFarmacia);
    }

}
