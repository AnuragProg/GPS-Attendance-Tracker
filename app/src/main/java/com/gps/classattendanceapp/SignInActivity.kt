package com.gps.classattendanceapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.gps.classattendanceapp.ui.theme.ClassAttendanceAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignInActivity : ComponentActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClassAttendanceAppTheme {
                SignInUI(signInIntent = googleSignInClient.signInIntent){
                    handler(it)
                }
            }
        }

        if(auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        val gos = GoogleSignInOptions.Builder()
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gos)
    }

    private fun handler(task : Task<GoogleSignInAccount>){
        if(task.isSuccessful){
            updateUi(task.result)
        }else{
            Toast.makeText(this, task.exception?.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUi(result: GoogleSignInAccount?) {
        if(result!=null){
            val credentials = GoogleAuthProvider.getCredential(result.idToken, null)
            auth.signInWithCredential(credentials).addOnCompleteListener { result->
                if(result.isSuccessful){
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, result.exception?.message ?: "Unknown error!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


@Composable
fun SignInUI(
    signInIntent: Intent,
    handler: (Task<GoogleSignInAccount>) -> Unit
) {

    val uiState = rememberSignInActivityUIState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ){result ->
        uiState.showProgressBar.value = true
        if(result.resultCode == Activity.RESULT_OK){
            val googleAccountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handler(googleAccountTask)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if(uiState.showProgressBar.value){
            Column {
                CircularProgressIndicator()
                Text(
                    text = "Loading...",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }else{
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                factory = { context ->
                    SignInButton(context)
                }
            ) {
                it.setOnClickListener {
                    launcher.launch(signInIntent)
                }
            }
        }
    }
}