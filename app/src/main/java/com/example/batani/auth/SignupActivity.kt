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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.batani.R
import com.example.batani.databinding.ActivitySignupBinding
import com.example.batani.network.ApiConfig
import com.example.batani.network.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        binding.signupButton.setOnClickListener {
            val name = binding.registerName.text.toString()
            val email = binding.registerEmail.text.toString()
            val password = binding.registerPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(name, email, password)
            } else {
//                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                MotionToast.darkToast(this@SignupActivity,
                    "Warning !!",
                    "Please fill all fields",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this@SignupActivity, R.font.poppins_medium))



            }


        }

        binding.Masuk.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun playAnimation() {

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }

    private fun registerUser(name: String, email: String, password: String) {
        binding.progressIndicator.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = ApiConfig.getApiServiceRegister()
                val response = apiService.register(name, email, password)

                withContext(Dispatchers.Main) {
                    binding.progressIndicator.visibility = View.GONE

                    if (response.error) {

//                        Toast.makeText(this@SignupActivity, "Registration Failed: ${response.message}", Toast.LENGTH_SHORT).show()
                        MotionToast.darkToast(this@SignupActivity,
                            "Register Failed !!",
                            response.message,
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@SignupActivity, R.font.poppins_medium))


                    } else {

                        val dialog = AlertDialog.Builder(this@SignupActivity).apply {
                            setTitle("Registrasi Berhasil!")
                            setMessage("Silahkan Login $email")
                            setCancelable(false)
                        }.create()

                        dialog.show()


                        Handler().postDelayed({
                            dialog.dismiss()
                            finish()
                        }, 3000)

                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    binding.progressIndicator.visibility = View.GONE

                    val errorBody = e.response()?.errorBody()?.string()
                    if (errorBody != null) {

                        val errorResponse = Gson().fromJson(errorBody, RegisterResponse::class.java)
//                        Toast.makeText(this@SignupActivity, "Error: ${errorResponse.message}", Toast.LENGTH_SHORT).show()
                        MotionToast.darkToast(this@SignupActivity,
                            "Error !!",
                            errorResponse.message,
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@SignupActivity, R.font.poppins_medium))



                    } else {

//                        Toast.makeText(this@SignupActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        MotionToast.darkToast(this@SignupActivity,
                            "Error !!",
                            e.message.toString(),
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@SignupActivity, R.font.poppins_medium))


                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressIndicator.visibility = View.GONE
                    Toast.makeText(this@SignupActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
