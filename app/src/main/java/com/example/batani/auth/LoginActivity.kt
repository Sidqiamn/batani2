package com.example.batani.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.batani.R
import com.example.batani.databinding.ActivityLoginBinding
import com.example.batani.pref.UserModel
import com.example.batani.ui.lokasiIzin.IzinLokasiActivity
import com.example.batani.ui.rekomendasi.ViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi Firebase
        FirebaseApp.initializeApp(this)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()
            mediaPlayer = MediaPlayer.create(this, R.raw.pop).apply {

                start()
            }
            if (password.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {

                MotionToast.darkToast(this@LoginActivity,
                    "Warning !!",
                    "Please fill all fields",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this@LoginActivity, R.font.poppins_medium))

            }

        }

        binding.daftar.setOnClickListener {

            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            mediaPlayer = MediaPlayer.create(this, R.raw.pop).apply {

                start()
            }
        }

    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(300)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }


    private fun loginUser(email: String, password: String) {
        binding.progressIndicator.visibility = View.VISIBLE
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressIndicator.visibility = View.GONE
                if (task.isSuccessful) {

                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val userModel = UserModel(
                            email = email,
                            token = currentUser.uid
                        )
                        viewModel.saveSession(userModel)

                        MotionToast.darkToast(
                            this@LoginActivity,
                            "Berhasil Login 😍",
                            "Selamat Datang $email !",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@LoginActivity, R.font.poppins_medium)
                        )

                        Handler().postDelayed({
                            // Setelah login berhasil, tambahkan log
                            Log.d("LoginActivity", "Login berhasil, berpindah ke IzinLokasiActivity")

                            val intent = Intent(this@LoginActivity, IzinLokasiActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }, 2000)
                    }
                } else {

                    val errorMessage = "Password atau email tidak ditemukan"
                    MotionToast.darkToast(
                        this@LoginActivity,
                        "Login Failed !!",
                        errorMessage,
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@LoginActivity, R.font.poppins_medium)
                    )
                }
            }
            .addOnFailureListener { exception ->
                binding.progressIndicator.visibility = View.GONE

            }
    }
    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()
        mediaPlayer = null
    }


}