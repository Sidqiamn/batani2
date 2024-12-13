package com.example.batani.ui.feedback

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.batani.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backIcon.setOnClickListener {
            finish()
        }

        binding.sendFeedbackButton.setOnClickListener {
            val feedback = binding.feedbackEditText.text.toString().trim()
            if (feedback.isNotEmpty()) {
                sendEmail(feedback)
            } else {
                binding.feedbackEditText.error = "Feedback tidak boleh kosong"
            }
        }
    }

    private fun sendEmail(feedback: String) {
        val to = arrayOf("bangbatani@gmail.com")
        val subject = "Feedback dari pengguna"
        val body = "Feedback: \n\n$feedback"

        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)

        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        }
    }
}
