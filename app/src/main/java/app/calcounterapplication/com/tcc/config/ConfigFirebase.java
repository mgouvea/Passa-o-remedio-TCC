package app.calcounterapplication.com.tcc.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfigFirebase {

    private static DatabaseReference databaseReference;
    private static FirebaseAuth mAuth;
    private static StorageReference referenciaStorage;


    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = getFirebaseAuth();
        return usuario.getCurrentUser();
    }

    public static String getIdUsuario(){

        FirebaseAuth autenticacao = getFirebaseAuth();
        return autenticacao.getCurrentUser().getUid();

    }

    //Criando a referência para o banco de dados
    //com  essa referencia conseguiremos adicionar e remover itens do firebase
    public static DatabaseReference getFirebaseDatabase(){
        if(databaseReference == null){
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    //Esse metodo foi criado com o intuito de conseguir pegar a autorização para
    //alterar, incluir e excluir informações do banco.
    public static FirebaseAuth getFirebaseAuth(){

        if(mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

    //retorna a referencia do firebase
    public static DatabaseReference getFirebase() {
        if (databaseReference == null) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }
        return databaseReference;
    }

    //retorna referencia do Storage
    public static StorageReference getFirebaseStorage() {
        if (referenciaStorage == null) {
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }

}
