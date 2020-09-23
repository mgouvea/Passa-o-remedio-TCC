package app.calcounterapplication.com.tcc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.helper.Local;
import app.calcounterapplication.com.tcc.model.Requisicao;
import app.calcounterapplication.com.tcc.model.Usuario;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {

    private List<Requisicao> requisicoes;
    private Context context;
    private Usuario entregador;
    private LatLng localEntregador;

    public RequisicoesAdapter(List<Requisicao> requisicoes, Context context, Usuario entregador) {
        this.requisicoes = requisicoes;
        this.context = context;
        this.entregador = entregador;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_requisicoes, viewGroup, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Requisicao requisicao = requisicoes.get(i);
        Usuario cliente = requisicao.getCliente();
        String latitudeFarmacia = requisicao.getDestino().getLatitude();
        String longitudeFarmacia = requisicao.getDestino().getLongitude();



        myViewHolder.nome.setText(cliente.getNome());

        if(entregador != null) {

            LatLng localCliente = new LatLng(
                    Double.parseDouble(cliente.getLatitude()),
                    Double.parseDouble(cliente.getLongitude())
            );

            System.out.println("olha " + entregador.getNome());
            System.out.println("olha " + entregador.getLatitude());
            localEntregador = new LatLng(
                    Double.parseDouble(entregador.getLatitude()),
                    Double.parseDouble(entregador.getLongitude())
            );

            LatLng localFarmacia = new LatLng(
                    Double.parseDouble(latitudeFarmacia),
                    Double.parseDouble(longitudeFarmacia)
            );

            float distancia1 = Local.calcularDistancia(localEntregador, localFarmacia);
            float distancia2 = Local.calcularDistancia(localCliente, localFarmacia);

            String distanciaFormatada = Local.formatarDistancia(distancia1 + distancia2);
            myViewHolder.distancia.setText(distanciaFormatada + " - aproximadamente");

        }

    }

    @Override
    public int getItemCount() {
        return requisicoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, distancia;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.textRequisicaoNome);
            distancia = itemView.findViewById(R.id.textRequisicaoDistancia);

        }
    }

}