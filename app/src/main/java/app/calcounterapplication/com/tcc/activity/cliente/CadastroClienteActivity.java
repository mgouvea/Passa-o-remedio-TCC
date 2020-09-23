package app.calcounterapplication.com.tcc.activity.cliente;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.text.DateFormat;
import java.util.Calendar;

import app.calcounterapplication.com.tcc.R;
import app.calcounterapplication.com.tcc.config.ConfigFirebase;
import app.calcounterapplication.com.tcc.helper.DatePickerFragment;
import app.calcounterapplication.com.tcc.helper.UsuarioFirebase;
import app.calcounterapplication.com.tcc.model.Cliente;
import app.calcounterapplication.com.tcc.model.Usuario;

public class CadastroClienteActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText campoNome, campoEmail, campoSenha, campoRg, campoDtNascimento, campoCPF, campoCidade, campoCEP, campoRua, campoUF, campoNumero;
    private EditText campoConfirmaSenha;
    private TextView textData;

    private static String dataNascimento = "";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_cliente);

        inicializarComponentes();

    }

    public String validarSenha(){
        String textoSenha;
        String senha1 = campoSenha.getText().toString();
        String senha2 = campoConfirmaSenha.getText().toString();
        if(senha1.equals(senha2)){
            textoSenha = campoSenha.getText().toString();

            return textoSenha;
        } else {
            Toast.makeText(CadastroClienteActivity.this,
                    "Senha não batem!",
                    Toast.LENGTH_SHORT).show();
        }
        return textoSenha = "";
    }

    public void validarCadastroUsuario(View view) {
        //Recuperar textos dos nomes
        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = validarSenha();
        String textoRg = campoRg.getText().toString();

        //converter Date para String
        String textoDtNascimento = this.dataNascimento;
        if(textoDtNascimento == null){
            textoDtNascimento = "";
        }

        String textoCPF  = campoCPF.getText().toString();
        String textoCidade = campoCidade.getText().toString();
        String textoCEP = campoCEP.getText().toString();
        String textoRUA = campoRua.getText().toString();
        String textoUF = campoUF.getText().toString();
        String textoNumero = campoNumero.getText().toString();

        if (!textoNome.isEmpty() && !textoEmail.isEmpty() && !textoSenha.isEmpty()
                && !textoRg.isEmpty() && !textoDtNascimento.isEmpty() && !textoCPF.isEmpty()
                && !textoCidade.isEmpty() && !textoCEP.isEmpty() && !textoRUA.isEmpty() && !textoUF.isEmpty() && !textoNumero.isEmpty()) {
            Cliente cliente = new Cliente();
            cliente.setNome(textoNome);
            cliente.setEmail(textoEmail);
            cliente.setSenha(textoSenha);
            cliente.setRg(textoRg);

            cliente.setDtNascimento(textoDtNascimento);

            cliente.setCpf(textoCPF);
            cliente.setCidade(textoCidade);
            cliente.setCep(textoCEP);
            cliente.setRua(textoRUA);
            cliente.setUf(textoUF);
            cliente.setNumero(textoNumero);
            cliente.setTipo("C");

            cadastrarUsuario(cliente);

        } else {
            Toast.makeText(CadastroClienteActivity.this,
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

                        startActivity(new Intent(CadastroClienteActivity.this, ClienteNavigationDrawer.class));
                        finish();

                        Toast.makeText(CadastroClienteActivity.this,
                                "Sucesso ao cadastrar cliente!",
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

                    Toast.makeText(CadastroClienteActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

//    public void setarData(String data){
//        if(!data.isEmpty()) {
//            this.dataNascimento = data;
//            System.out.println("Ta funcionando: " + dataNascimento);
//        }
//    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        String currentDate = DateFormat.getDateInstance().format(c.getTime());

        this.dataNascimento = currentDate;
//        System.out.println("Ta funcionando: " + dataNascimento);
        textData.setText(currentDate);
    }

    public void inicializarComponentes() {
        campoNome = findViewById(R.id.editClienteNome);
        campoEmail = findViewById(R.id.editClienteEmail);
        campoSenha = findViewById(R.id.editClienteSenha);
        campoRg = findViewById(R.id.editClienteRG);
        //campoDtNascimento = findViewById(R.id.editClienteDataNascimento);
        campoCPF = findViewById(R.id.editClienteCPF);
        campoCidade = findViewById(R.id.editClienteCidade);
        campoCEP = findViewById(R.id.editClienteCEP);
        campoRua = findViewById(R.id.editClienteRua);
        campoUF = findViewById(R.id.editClienteUF);
        campoNumero = findViewById(R.id.editClienteNumero);
        campoConfirmaSenha = findViewById(R.id.editClienteConfirmarSenha);
        textData = findViewById(R.id.textDataID);

    }
}
