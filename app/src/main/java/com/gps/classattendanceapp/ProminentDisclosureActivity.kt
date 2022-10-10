package com.gps.classattendanceapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gps.classattendanceapp.components.UserPreferences
import com.gps.classattendanceapp.ui.theme.ClassAttendanceAppTheme

class ProminentDisclosureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(this)
        setContent {
            ClassAttendanceAppTheme {
                val intent = Intent(this, MainActivity::class.java)
                ProminentDisclosure{
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}

@Composable
fun ProminentDisclosure(
    navigate: ()->Unit
) {
    val disclosure = stringResource(id = R.string.prominent_disclosure)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = stringResource(id = R.string.disclaimer),
            fontSize = 30.sp,
            fontFamily = FontFamily(
                listOf(
                    Font(
                        R.font.notoserif_bold
                    )
                )
            )
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = disclosure,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            fontFamily = FontFamily(
                listOf(
                    Font(R.font.notoserif_regular)
                )
            )
        )

        Image(
            painter = painterResource(id = R.drawable.map),
            contentDescription = null
        )

        OutlinedButton(
            onClick = {
                navigate()
            }
        ) {
            Text("Got it!")
        }

    }

}


//@Preview(showBackground = true)
//@Composable
//fun ProminentDisclosurePreview() = ProminentDisclosure()

