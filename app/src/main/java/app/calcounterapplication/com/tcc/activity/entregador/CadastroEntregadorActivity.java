package app.calcounterapplication.com.tcc.activity.entregador;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import app.calcounterapplication.com.tcc.model.Entregador;
import app.calcounterapplication.com.tcc.model.Usuario;

public class CadastroEntregadorActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button buttonCadastro;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_entregador);

        inicializarComponentes();

    }

    public void validarCadastroUsuario(View view) {
        //Recuperar textos dos nomes
        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if (!textoNome.isEmpty() && !textoEmail.isEmpty() && !textoSenha.isEmpty()) {
            Usuario entregador = new Entregador();
            entregador.setNome(textoNome);
            entregador.setEmail(textoEmail);
            entregador.setSenha(textoSenha);
            entregador.setTipo("E");

            cadastrarUsuario(entregador);

        } else {
            Toast.makeText(CadastroEntregadorActivity.this,
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

                        startActivity(new Intent(CadastroEntregadorActivity.this, MenuEntregadorActivity.class));
                        finish();

                        Toast.makeText(CadastroEntregadorActivity.this,
                                "Sucesso ao cadastrar entregador!",
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

                    Toast.makeText(CadastroEntregadorActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    public void inicializarComponentes() {
        campoNome = findViewById(R.id.editEntregadorNome);
        campoEmail = findViewById(R.id.editEntregadorEmail);
        campoSenha = findViewById(R.id.editEntregadorSenha);
    }
}
