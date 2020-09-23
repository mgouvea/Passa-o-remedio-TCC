package app.calcounterapplication.com.tcc.activity.farmacia;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.Permissoes;
import app.calcounterapplication.com.tcc.model.Farmacia;
import app.calcounterapplication.com.tcc.model.Produto;
import dmax.dialog.SpotsDialog;

public class CadastrarProdutosActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText campoMarca;
    private EditText campoProduto;
    private CurrencyEditText campoValor;
    private EditText campoDescricao;
    private ImageView imagem1;
    private Spinner campoCategorias, campoRegiao;
    private Produto produto;
    private AlertDialog dialog;
    private FirebaseUser firebaseUser;
    private TextView nomeFarmacia;
    private String farmacia;
    private TextView regiaoFarmacia;
    private String regiaoFarma;


    private StorageReference storage;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private List<String> listafotosRecuperadas = new ArrayList<>();
    private List<String> listaURLfotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produtos);

        Permissoes.validarPermissoes(permissoes, this, 1);
        storage = ConfigFirebase.getFirebaseStorage();
        firebaseUser = ConfigFirebase.getUsuarioAtual();

        //inicializando componentes
        inicializarComponentes();
        puxarDadosNome();
        puxarDadosRegiao();

        //carregar dados spinner
        carregarDadosSpinner();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circleCarrinhoProd:
                escolherImagem(1);
                break;
        }
    }

    //selecionar imagem a ser escolhida
    public void escolherImagem(int requestCode) {

        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);

    }

    //recuperar imagem escolhida
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            //recuperar a imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //configurar a imagem no imageView
            if (requestCode == 1) {
                imagem1.setImageURI(imagemSelecionada);
            }

            listafotosRecuperadas.add(caminhoImagem);

        }
    }

    public void salvarProduto() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Produto")
                .setCancelable(false)
                .build();
        dialog.show();


        //salvar imagens no storege
        for (int i = 0; i < listafotosRecuperadas.size(); i++) {

            String urlImagem = listafotosRecuperadas.get(i);
            int tamanhoLista = listafotosRecuperadas.size();
            salvarFotosStorage(urlImagem, tamanhoLista, i);
        }
    }

    private void salvarFotosStorage(final String urlString, final int totalFotos,
                                    int contador) {

        //criando nós no storage
        final StorageReference imagemProduto = storage.child("imagens")
                .child("produtos")
                .child(produto.getIdProduto())
                .child("imagem" + contador);

        //fazer upload do arquivo
        UploadTask uploadTask = imagemProduto.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                String urlConvertida = firebaseUrl.toString();
                Log.i("salvar", urlConvertida);
                listaURLfotos.add(urlConvertida);

                if (totalFotos == listaURLfotos.size()) {
                    produto.setFotos(listaURLfotos);
                    produto.salvar();

                    //fechando a dialog e encerrando o cadastro
                    dialog.dismiss();
                    startActivity(new Intent(CadastrarProdutosActivity.this,
                            MeusProdutosActivity.class));
                    finish();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mensagemErro("Erro ao fazer upload");
                Log.i("upload", "Erro ao fazer upload " + e.getMessage());
            }
        });

    }

    private Produto configurarProduto() {

        String nome = puxarDadosNome();
//        String regiao = campoRegiao.getSelectedItem().toString();
        String regiao = puxarDadosRegiao();
        String categoria = campoCategorias.getSelectedItem().toString();
        String marca = campoMarca.getText().toString();
        String prod = campoProduto.getText().toString().toUpperCase();
        String prodExibicao = campoProduto.getText().toString();
        String valor = String.valueOf(campoValor.getText().toString());
        String descricao = campoDescricao.getText().toString();
        String idUsuario = ConfigFirebase.getIdUsuario();

        Produto produto = new Produto();
        produto.setRegiao(regiao);
        produto.setCategoria(categoria);
        produto.setMarca(marca);
        produto.setProduto(prod);
        produto.setProdutoExibicao(prodExibicao);
        produto.setValor(valor);
        produto.setDescricao(descricao);
        produto.setNomeUsuario(nome);
        produto.setIdUsuario(idUsuario);

        return produto;
    }

    public String puxarDadosNome() {

        FirebaseUser user = firebaseUser;
        if (user != null) {
            DatabaseReference usuariosRef = ConfigFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(ConfigFirebase.getIdUsuario());
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Farmacia farma = dataSnapshot.getValue(Farmacia.class);

                    farmacia = farma.getNome();

                    nomeFarmacia.setText(farmacia);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return farmacia;
    }

    public String puxarDadosRegiao() {

        FirebaseUser user = firebaseUser;
        if (user != null) {
            DatabaseReference usuariosRef = ConfigFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(ConfigFirebase.getIdUsuario());
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Farmacia farma = dataSnapshot.getValue(Farmacia.class);

                    regiaoFarma = farma.getRegiao();

                    regiaoFarmacia.setText(regiaoFarma);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return regiaoFarma;
    }

    public void validarProduto(View view) {

        produto = configurarProduto();
        String valor = String.valueOf(campoValor.getRawValue());

        if (listafotosRecuperadas.size() != 0) {
            if (!produto.getCategoria().isEmpty()) {
                if (!produto.getMarca().isEmpty()) {
                    if (!produto.getProduto().isEmpty()) {
                        if (!valor.isEmpty() && !valor.equals("0")) {
                            if (!produto.getDescricao().isEmpty()) {

                                salvarProduto();

                            } else {
                                mensagemErro("Preencha o campo descrição");
                            }
                        } else {
                            mensagemErro("Preencha o campo valor");
                        }
                    } else {
                        mensagemErro("Preencha o campo produto");
                    }
                } else {
                    mensagemErro("Preencha o campo marca");
                }
            } else {
                mensagemErro("Preencha o campo categoria");
            }
        } else {
            mensagemErro("Selecione ao menos uma foto");
        }

    }

    public void mensagemErro(String mensagem) {
        Toast.makeText(getApplicationContext(),
                mensagem, Toast.LENGTH_SHORT).show();
    }


    //verificar se permissões foram negadas
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {

            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    //tratamento de permissões negadas
    private void alertaValidacaoPermissao() {

        //caso seja negado
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para cadastrar novos produtos " +
                "é necessário aceitar as permissões!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finaliza a interface
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void carregarDadosSpinner() {

//        //spinner Regiao
//        String[] regiao = getResources().getStringArray(R.array.regiao);
//        //adicionar valores do spinner
//        ArrayAdapter<String> adapterRegiao = new ArrayAdapter<String>(
//                getApplicationContext(), android.R.layout.simple_spinner_item,
//                regiao
//        );
//        adapterRegiao.setDropDownViewResource(android.R.layout
//                .simple_spinner_dropdown_item);
//
//        campoRegiao.setAdapter(adapterRegiao);


        //spinner Categorias
        String[] categorias = getResources().getStringArray(R.array.categorias);
        //adicionar valores do spinner
        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_item,
                categorias
        );
        adapterCategorias.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        campoCategorias.setAdapter(adapterCategorias);

    }

    public void inicializarComponentes() {
        campoMarca = findViewById(R.id.editMarca);
        campoProduto = findViewById(R.id.editProduto);
        campoValor = findViewById(R.id.editValor);
        campoDescricao = findViewById(R.id.editDescricao);
        campoCategorias = findViewById(R.id.spinnerCategorias);
//        campoRegiao = findViewById(R.id.spinnerRegiao);
        imagem1 = findViewById(R.id.circleCarrinhoProd);
        nomeFarmacia = findViewById(R.id.textViewFarmacia);
        regiaoFarmacia = findViewById(R.id.textViewRegiaoFarma);

        //gerenciando clique
        imagem1.setOnClickListener(this);

        //configurar localidade ptBR
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);
    }

}

