package com.example.trainingapp.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.trainingapp.ui.theme.TrainingAppTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            TrainingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(
                        text = "Hello, Compose is ready!",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrainingAppTheme {
        Text(
            text = "Hello, Compose is ready!",
            color = MaterialTheme.colorScheme.onBackground
        )

    }

}