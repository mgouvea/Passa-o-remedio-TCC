package app.calcounterapplication.com.tcc.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Usuario;


public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;

    //permitir acesso
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializar componentes
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);

    }

    public void validarLoginUsuario(View view) {

        //Recuperar textos dos campos
        String textEmail = campoEmail.getText().toString();
        String textSenha = campoSenha.getText().toString();

        if (!textEmail.isEmpty() && !textSenha.isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setEmail(textEmail);
            usuario.setSenha(textSenha);

            logarUsuario(usuario);


        } else {
            Toast.makeText(LoginActivity.this,
                    "Preencha todos os campos!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void logarUsuario(final Usuario usuario) {

        mAuth = ConfigFirebase.getFirebaseAuth();
        mAuth.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //Verificar o tipo de usuário que está logando
                    //Motorista // Passageiro // Farmacia
                    UsuarioFirebase.redirecionaUsuarioLogado(LoginActivity.this);

                } else {
                    String excecao = "";

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        excecao = "Usuario não está cadastrado!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = "Email e senha não correspondem.";
                    } catch (Exception e) {
                        excecao = "Erro ao entrar: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}


