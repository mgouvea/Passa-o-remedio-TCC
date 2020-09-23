package app.calcounterapplication.com.tcc.helper;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import app.calcounterapplication.com.tcc.R;

public class Marcadores {

    private GoogleMap mMap;
    private Marker marcadorEntregador;
    private Marker marcadorCliente;
    private Marker marcadorFarmacia;
    private Marker marcadorRealizada;
    private Context context;

    public Marcadores(Context current, GoogleMap mMap){
        this.context = current;
        this.mMap = mMap;
    }

    public Marcadores(GoogleMap mMap){
        this.mMap = mMap;
    }

    public Marker adicionaMarcadorEntregador(LatLng localizacao, String titulo){

        if( marcadorEntregador != null )
            marcadorEntregador.remove();

        if(marcadorFarmacia != null)
            marcadorFarmacia.remove();

        marcadorEntregador = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.scooter))
        );

        return marcadorEntregador;


    }

    public Marker adicionaMarcadorFarmacia(LatLng localizacao, String titulo){
        if( marcadorFarmacia != null )
            marcadorFarmacia.remove();

        marcadorFarmacia = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pharmacy))
        );

        return marcadorFarmacia;
    }

    public Marker adicionaMarcadorCliente(LatLng localizacao, String titulo){

        if( marcadorCliente != null )
            marcadorCliente.remove();

        if(marcadorFarmacia != null)
            marcadorFarmacia.remove();

        marcadorCliente = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
        );

        return marcadorCliente;
    }

    public Marker adicionaMarcadorEntregaFinalizada(LatLng localizacao, String titulo){

        if( marcadorFarmacia != null )
            marcadorFarmacia.remove();

        if(marcadorCliente != null)
            marcadorCliente.remove();

        if(marcadorEntregador != null)
            marcadorEntregador.remove();

        marcadorRealizada = mMap.addMarker(
                new MarkerOptions()
                        .position(localizacao)
                        .title(titulo)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.realizada))
        );

        return marcadorRealizada;
    }

    public void centralizarMarcador(LatLng local){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 20));
    }

    public void centralizarTresMarcadores(Marker marcador1, Marker marcador2, Marker marcador3){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(marcador1.getPosition());
        builder.include(marcador2.getPosition());
        builder.include(marcador3.getPosition());

        LatLngBounds bounds = builder.build();

        int largura =  context.getResources().getDisplayMetrics().widthPixels;
        int altura = context.getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.40);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds,largura,altura,espacoInterno)
        );
    }

    public void centralizarDoisMarcadores(Marker marcador1, Marker marcador2){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include( marcador1.getPosition() );
        builder.include( marcador2.getPosition() );

        LatLngBounds bounds = builder.build();

        int largura =  context.getResources().getDisplayMetrics().widthPixels;
        int altura = context.getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds,largura,altura,espacoInterno)
        );

    }

    public void centralizarDoisMarcadores(Marker marcador1, Marker marcador2, String classePedidosCliente){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include( marcador1.getPosition() );
        builder.include( marcador2.getPosition() );

        LatLngBounds bounds = builder.build();

        int largura =  context.getResources().getDisplayMetrics().widthPixels;
        int altura = context.getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.40);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds,largura,altura,espacoInterno)
        );

    }

}
