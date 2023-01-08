package io.hex01e.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    private ImageView avatar;
    private TextView name;
    private TextView email;
    private Button btn;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        avatar = findViewById(R.id.imageView);
        name   = findViewById(R.id.name);
        email  = findViewById(R.id.email);
        btn    = findViewById(R.id.logout);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInButton = findViewById(R.id.sign_in_button);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null)updateUi(account);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUi(account);
        } catch (ApiException e) {
            Log.w("login", "signInResult:failed code=" + e.getStatusCode());
        }
    }
    private void updateUi(GoogleSignInAccount account) {
        avatar.setVisibility(View.VISIBLE);
        Picasso.get().load(account.getPhotoUrl()).error(R.drawable.ic_launcher_foreground).into(avatar);
        name.setVisibility(View.VISIBLE);
        name.setText(account.getDisplayName());
        email.setVisibility(View.VISIBLE);
        email.setText(account.getEmail());
        btn.setVisibility(View.VISIBLE);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut();
                avatar.setVisibility(View.GONE);
                name.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                btn.setVisibility(View.GONE);
                signInButton.setVisibility(View.VISIBLE);
            }
        });
        signInButton.setVisibility(View.GONE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}