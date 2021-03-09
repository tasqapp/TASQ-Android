package tasq.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Task;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Button back;
    Button reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        back = (Button) findViewById(R.id.loginPage);
        reg = (Button) findViewById(R.id.button_reg);
        //if login button pressed, navigate to user login page
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
        //if register button is clicked, call function to authorize/perform user registration
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    /**
     * Method for verifying user registration is done correctly
     * and navigation to task pages on successful registration
     */
    public void register() {
        EditText emailAdd = findViewById(R.id.input_email);
        EditText pass = findViewById(R.id.input_pass);
        String email = emailAdd.getText().toString();
        String password = pass.getText().toString();
        //if user leaves email or password blank, display error message
        if (email.matches("")) {
            Toast.makeText(this, "You must enter an email to register.", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.matches("")) {
            Toast.makeText(this, "You must enter a password to register", Toast.LENGTH_LONG).show();
            return;
        }
        //use firebase database built-in to attempt to add user to database (register)
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display error message to the user.
                            Toast.makeText(Register.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
