package app.calcounterapplication.com.tcc.activity.farmacia;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Farmacia;
import app.calcounterapplication.com.tcc.model.Usuario;

public class CadastroFarmaciaActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha,
            campoCNPJ, campoCidade, campoCEP, campoRua, campoNumero;
    private EditText campoConfirmaSenha;
    private Spinner campoUF, campoRegiao;
    //private Button BTFarmaciaCadastro;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_farmacia);

        inicializarComponentes();
        carregarDadosSpinner();

    }

    public void validarCadastroUsuario(View view) {

        //Recuperar textos dos nomes
        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = validarSenha();
        String textoCNPJ = campoCNPJ.getText().toString();
        String textoCidade = campoCidade.getText().toString();
        String textoCEP = campoCEP.getText().toString();
        String textoRUA = campoRua.getText().toString();
        String textoUF = campoUF.getSelectedItem().toString();
        String textoRegiao = campoRegiao.getSelectedItem().toString();
        String textoNumero = campoNumero.getText().toString();

        if (!textoNome.isEmpty() && !textoEmail.isEmpty() && !textoSenha.isEmpty()
                && !textoCNPJ.isEmpty() && !textoCidade.isEmpty() && !textoCEP.isEmpty() && !textoRUA.isEmpty()
                && !textoNumero.isEmpty()) {

            Farmacia farmacia = new Farmacia();
            farmacia.setNome(textoNome);
            farmacia.setEmail(textoEmail);
            farmacia.setSenha(textoSenha);
            farmacia.setCnpj(textoCNPJ);
            farmacia.setCidade(textoCidade);
            farmacia.setCep(textoCEP);
            farmacia.setRua(textoRUA);
            farmacia.setUf(textoUF);
            farmacia.setRegiao(textoRegiao);
            farmacia.setNumero(textoNumero);
            farmacia.setTipo("F");

            cadastrarUsuario(farmacia);

        } else {
            Toast.makeText(CadastroFarmaciaActivity.this,
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void cadastrarUsuario(final Usuario usuario) {

        mAuth = ConfigFirebase.getFirebaseAuth();
        mAuth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    try {

                        String idUsuario = task.getResult().getUser().getUid();
                        usuario.setId(idUsuario);
                        usuario.salvar();

                        //Atualizar nome do Usuario no UserProfile
                        UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                        startActivity(new Intent(CadastroFarmaciaActivity.this, MenuFarmaciaActivity.class));
                        finish();

                        Toast.makeText(CadastroFarmaciaActivity.this,
                                "Sucesso ao cadastrar farmácia!",
                                Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    String excecao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Por favor, digite um e-mail válido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = "Esta conta já foi cadastrada!";
                    } catch (Exception e) {
                        excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroFarmaciaActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    public String validarSenha() {
        String textoSenha;
        String senha1 = campoSenha.getText().toString();
        String senha2 = campoConfirmaSenha.getText().toString();
        if (senha1.equals(senha2)) {
            textoSenha = campoSenha.getText().toString();

            return textoSenha;
        } else {
            Toast.makeText(CadastroFarmaciaActivity.this,
                    "Senha não batem!",
                    Toast.LENGTH_SHORT).show();
        }
        return textoSenha = "";
    }

    private void carregarDadosSpinner() {

        //spinner Regiao
        String[] regiao = getResources().getStringArray(R.array.regiao);
        //adicionar valores do spinner
        ArrayAdapter<String> adapterRegiao = new ArrayAdapter<String>(
                getApplicationContext(), android.R.layout.simple_spinner_item,
                regiao
        );
        adapterRegiao.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);

        campoRegiao.setAdapter(adapterRegiao);


        //spinner estados
        String[] estados = getResources().getStringArray(R.array.estados);

        //adicionar valores do spinner
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estados
        );

        adapterEstados.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        campoUF.setAdapter(adapterEstados);

    }

    public void inicializarComponentes() {
        campoNome = findViewById(R.id.editFarmaciaNome);
        campoEmail = findViewById(R.id.editFarmaciaEmail);
        campoSenha = findViewById(R.id.editFarmaciaSenha);
        campoCNPJ = findViewById(R.id.editFarmaciaCNPJ);
        campoCidade = findViewById(R.id.editFarmaciaCidade);
        campoCEP = findViewById(R.id.editFarmaciaCEP);
        campoRua = findViewById(R.id.editFarmaciaRua);
        campoUF = findViewById(R.id.spinnerFarmaciaUF);
        campoRegiao = findViewById(R.id.spinnerFarmaciaRegiao);
        campoNumero = findViewById(R.id.editFarmaciaNumero);
        campoConfirmaSenha = findViewById(R.id.editFarmaciaConfirmarSenha);
    }
}
