package br.com.mbcustom.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private Button btnSignOut;
    private ImageView ivFoto;
    private TextView txtEmail, txtID;
    private Button btnSingOut;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnSignOut = (Button) findViewById(R.id.btnSingOut);
        ivFoto = (ImageView) findViewById(R.id.ivFoto);
        txtEmail = (TextView) findViewById(R.id.tvEmail);
        txtID = (TextView) findViewById(R.id.tvID);
        btnSingOut = (Button) findViewById(R.id.btnSignIn);

        inicializarFirebase();
        conectarGoogleApi();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PerfilActivity.this, "clicado", Toast.LENGTH_SHORT).show();
                signOut();

            }
        });


    }

    private void signOut() {
        mFirebaseAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                alert("Conta desconectada");
                finish();
            }
        });
    }

    private void conectarGoogleApi() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }

    private void inicializarFirebase() {
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if(mFirebaseUser != null){
                    exebirDados(mFirebaseUser);
                }else{
                    finish();
                }
            }
        };
    }

    private void exebirDados(FirebaseUser mFirebaseUser) {
        txtEmail.setText(mFirebaseUser.getEmail());
        txtID.setText(mFirebaseUser.getUid());

        Glide.with(PerfilActivity.this).load(mFirebaseUser.getPhotoUrl()).into(ivFoto);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        alert("Falha na Conexão");
    }

    private void alert(String msg) {
        Toast.makeText(PerfilActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

}