package com.example.batani.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.batani.MainActivity
import com.example.batani.R
import com.example.batani.databinding.ActivityLoginBinding
import com.example.batani.network.ApiConfig
import com.example.batani.network.RegisterResponse
import com.example.batani.pref.UserModel
import com.example.batani.ui.rekomendasi.ViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
//#cek aja
@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            if (password.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = ApiConfig.getApiServiceLogin()
                val response = apiService.login(email, password)

                withContext(Dispatchers.Main) {

                    if (response.error) {
                        binding.progressIndicator.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "Login Failed: ${response.message}", Toast.LENGTH_SHORT).show()
                        MotionToast.darkToast(this@LoginActivity,
                            "Login Failed !!",
                            response.message,
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@LoginActivity, R.font.poppins_medium))


                    } else {

                        val loginResult = response.loginResult
                        if (loginResult != null) {

                            val userModel = UserModel(
                                email = email,
                                token = loginResult.token,

                                )
                            viewModel.saveSession(userModel)

                            binding.progressIndicator.visibility = View.GONE

                            MotionToast.darkToast(this@LoginActivity,
                                "Berhasil Login 😍",
                                "Selamat Datang $email !",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this@LoginActivity, R.font.poppins_medium))


                            Handler().postDelayed({

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }, 2000)
                        } else {
                            binding.progressIndicator.visibility = View.GONE
                            Toast.makeText(this@LoginActivity, "Login failed: No login result", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    binding.progressIndicator.visibility = View.GONE
                    val errorBody = e.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
//                        Toast.makeText(this@LoginActivity, "Error: ${errorResponse.message}", Toast.LENGTH_SHORT).show()
                        MotionToast.darkToast(this@LoginActivity,
                            "Error !!",
                            errorResponse.message,
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@LoginActivity, R.font.poppins_medium))

                    } else {
//                        Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        MotionToast.darkToast(this@LoginActivity,
                            "Error !!",
                            "${e.message}",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@LoginActivity, R.font.poppins_medium))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}