package app.calcounterapplication.com.tcc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.model.Cliente;
import app.calcounterapplication.com.tcc.model.Farmacia;
import app.calcounterapplication.com.tcc.model.Produto;

public class AdapterProdutoPublico extends RecyclerView.Adapter<AdapterProdutoPublico.MyViewHolder> {

    private List<Produto> produtos;
    private Context context;
    private FirebaseUser firebaseUser;


    public AdapterProdutoPublico(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        firebaseUser = ConfigFirebase.getUsuarioAtual();

        View item = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_produto_publico, viewGroup, false);

        return new MyViewHolder(item);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {


        Produto produto = produtos.get(i);
        myViewHolder.titulo.setText(produto.getProdutoExibicao());
        myViewHolder.valor.setText(produto.getValor());
        myViewHolder.farmacia.setText(produto.getNomeUsuario());
        myViewHolder.regiao.setText(produto.getRegiao());

        //configurar imagem
        List<String> urlFotos = produto.getFotos();
        String urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(myViewHolder.foto);
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titulo;
        TextView valor;
        TextView farmacia;
        TextView regiao;
        ImageView foto;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTituloPublico);
            valor = itemView.findViewById(R.id.textPrecoPublico);
            farmacia = itemView.findViewById(R.id.textFarmacia);
            foto = itemView.findViewById(R.id.imageProdutoPublico);
            regiao = itemView.findViewById(R.id.regiaoProdutoFarma);

        }
    }
}
