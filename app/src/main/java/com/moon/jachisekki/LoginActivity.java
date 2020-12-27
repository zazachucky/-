package com.moon.jachisekki;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    
    //객체 선언
    private FirebaseAuth auth;
    EditText email_edittext;
    EditText textPassword;
    Button email_login_button;
    Button google_login_button;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    //로그인 액티비티 퍼블릭 클래스
    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //객체 초기화
        auth = FirebaseAuth.getInstance();
        email_edittext = findViewById(R.id.email_edittext);
        textPassword = findViewById(R.id.password_edittext);
        email_login_button = findViewById(R.id.email_login_button);
        google_login_button = findViewById(R.id.google_sign_in_button);
        //이메일 회원가입 및 로그인 버튼 처리 함수
        email_login_button.setOnClickListener((View.OnClickListener) (new View.OnClickListener() {
            public final void onClick(View it) {
                //로그인 처리 함수 호출
                //만약 둘중 하나라도 빈칸이면 로그인 불가.
                if(textPassword.getText().toString().equals("") || email_edittext.getText().toString().equals("")){
                    Toast.makeText(LoginActivity.this, "이메일, 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();

                }
                else {
                    //로그인 실행
                    signinAndSignup();

                }
            }
        }));

        //구글 로그인 객체 선언
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //로그인 정보가 존재할경우
        if (auth.getCurrentUser() != null) {
            //메인 액티비티 호출
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        //구글 사인 클라이언트 초기화
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //구글 로그인 버튼 온클릭 리스너
        google_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //구글 로그인 함수
                signIn();
            }
        });

    }
    //이메일 로그인 및 회원가입 함수
    private void signinAndSignup() {
            auth.createUserWithEmailAndPassword(email_edittext.getText().toString(), textPassword.getText().toString()).addOnCompleteListener((OnCompleteListener) (new OnCompleteListener() {
                public final void onComplete(Task task) {
                    if (task.isSuccessful()) {
                        moveMainPage(((AuthResult) Objects.requireNonNull(task.getResult())).getUser());
                    } else if (task.getException().getMessage().isEmpty() || task.getException().getMessage() == null) {
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        //이미 로그인 돼있으므로 바로 자동 로그인
                        signinEmail();
                    }
                }
            }));



    }
    //이메일 로그인 함수
    public final void signinEmail() {
        auth.signInWithEmailAndPassword(email_edittext.getText().toString(), textPassword.getText().toString()).addOnCompleteListener((OnCompleteListener) (new OnCompleteListener() {
            public final void onComplete(Task task) {
                if (task.isSuccessful()) {
                    moveMainPage(((AuthResult) Objects.requireNonNull(task.getResult())).getUser());
                } else {
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }));

    }
    //구글 로그인 함수
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Result 처리 함수
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result는GoogleSignInApi.getSignInIntent로 받은 인텐트를 처리;
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //만약 구글 로그인이 성공하면
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
            }
        }
    }
    //파이어베이스 구글 로그인 처리
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //로그인 성공 시 메인 액티비티로 이동
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Successed.", Snackbar.LENGTH_SHORT).show();
                            FirebaseUser user = auth.getCurrentUser();
                            moveMainPage(user);
                        } else {
                            //로그인 실패
                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            moveMainPage(null);
                        }
                    }
                });
    }
    //메인 액티비티로 이동할지 결정하는 함수
    private void moveMainPage(FirebaseUser user) {
        if (user != null) {
            //user가 not null 이면 이동
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
