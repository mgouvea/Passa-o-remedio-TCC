package app.calcounterapplication.com.tcc.activity.cliente;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.activity.Fragments.CarrinhoDeCompra;
import app.calcounterapplication.com.tcc.activity.Fragments.ClienteMenu;
import app.calcounterapplication.com.tcc.activity.Fragments.DadosCliente;
import app.calcounterapplication.com.tcc.activity.Fragments.PedidosCliente;

public class ClienteNavigationDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //    private TextView textView;
    private FirebaseAuth mAuth;
    private String enderecoFarmacia = "";
    private String nomeFarmacia = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        textView = findViewById(R.id.textBoasVindas);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        try {
            enderecoFarmacia = getIntent().getExtras().getString("enderecoFarmacia");
            nomeFarmacia = getIntent().getExtras().getString("nomeFarmacia");
        } catch(Exception e) {
            enderecoFarmacia = "";
            nomeFarmacia = "";
        }


        if(enderecoFarmacia.isEmpty()){
            ClienteMenu clienteMenu = new ClienteMenu();
            FragmentTransaction clienteMenuTransaction =
                    getSupportFragmentManager().beginTransaction();
            clienteMenuTransaction.replace(R.id.content_frame, clienteMenu);
            clienteMenuTransaction.commit();
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            PedidosCliente pedidosCliente = new PedidosCliente();

            Bundle bundle = new Bundle();
            bundle.putString("enderecoFarmacia", enderecoFarmacia);
            bundle.putString("nomeFarmacia", nomeFarmacia);
            pedidosCliente.setArguments(bundle);
            fragmentTransaction.replace(R.id.content_frame, pedidosCliente);
            fragmentTransaction.commit();
        }


        getSupportActionBar().setTitle("Passa o Remédio");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cliente_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            ClienteMenu clienteMenu = new ClienteMenu();
            FragmentTransaction clienteMenuTransaction =
                    getSupportFragmentManager().beginTransaction();
            clienteMenuTransaction.replace(R.id.content_frame, clienteMenu);
            clienteMenuTransaction.commit();

        } else if (id == R.id.nav_pedidos) {

            PedidosCliente pedidosCliente = new PedidosCliente();
            FragmentTransaction pedidoTransaction =
                    getSupportFragmentManager().beginTransaction();
            pedidoTransaction.replace(R.id.content_frame, pedidosCliente);
            pedidoTransaction.commit();

        } else if (id == R.id.nav_perfil) {

            DadosCliente dadosCliente = new DadosCliente();
            FragmentTransaction dadosTransaction =
                    getSupportFragmentManager().beginTransaction();
            dadosTransaction.replace(R.id.content_frame, dadosCliente);
            dadosTransaction.commit();

        }else if(id == R.id.nav_carrinho){

            CarrinhoDeCompra addToCart = new CarrinhoDeCompra();
            FragmentTransaction dadosTransaction =
                    getSupportFragmentManager().beginTransaction();
            dadosTransaction.replace(R.id.content_frame, addToCart);
            dadosTransaction.commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
