package app.calcounterapplication.com.tcc.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.model.Produto;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCarrinhoCompras extends
        RecyclerView.Adapter<AdapterCarrinhoCompras.MyViewHolder> {

    private List<Produto> produtos;
    private Context context;
    private FirebaseUser firebaseUser;

    public AdapterCarrinhoCompras(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        firebaseUser = ConfigFirebase.getUsuarioAtual();

        View item = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_carrinho_compras, viewGroup, false);

        return new AdapterCarrinhoCompras.MyViewHolder(item);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Produto produto = produtos.get(i);
        myViewHolder.titulo.setText(produto.getProdutoExibicao());
        myViewHolder.valor.setText(produto.getValor());


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
        CircleImageView foto;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTituloCarrinho);
            valor = itemView.findViewById(R.id.textValorCarrinho);
            foto = itemView.findViewById(R.id.circleCarrinhoProd);

        }
    }
}
