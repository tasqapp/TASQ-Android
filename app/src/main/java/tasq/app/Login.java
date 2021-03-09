package tasq.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        log = (Button) findViewById(R.id.button_login);
        //set onclick listener for user login (call function to authorize login)
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    /**
     * Method for authorizing user login (checks to see whether entered credentials exist in the database)
     * Upon successful registration, user is navigated to task pages. If unsuccessful, error messages are
     * displayed.
     */
    public void login() {
        EditText emailAdd = findViewById(R.id.input_email);
        EditText pass = findViewById(R.id.input_pass);
        String email = emailAdd.getText().toString();
        String password = pass.getText().toString();
        //if user does not enter a username or password, display error message because authenticate
        //cannot be performed
        if (email.matches("")) {
            Toast.makeText(this, "You must enter an email to login.", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.matches("")) {
            Toast.makeText(this, "You must enter a password to login", Toast.LENGTH_LONG).show();
            return;
        }
        //use firebase database built-in to attempt to find user in database and compare credentials
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display error message to the user.
                            Toast.makeText(Login.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
