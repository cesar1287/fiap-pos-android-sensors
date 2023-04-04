package com.github.cesar1287.sensores

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.github.cesar1287.sensores.databinding.ActivityLoginBinding
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        doLoginFingerPrint()

        binding.btLogin.setOnClickListener {
            doLoginFingerPrint()
        }
    }

    private fun doLoginFingerPrint() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                authUser(executor)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Toast.makeText(
                    this,
                    "Hardware de biometria nao encontrado",
                    Toast.LENGTH_LONG
                ).show()
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Toast.makeText(
                    this,
                    "Hardware de biometria nao disponivel",
                    Toast.LENGTH_LONG
                ).show()
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                Toast.makeText(
                    this,
                    "Hardware de biometria não foi configurado",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    private fun authUser(executor: Executor) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Sensores")
            .setSubtitle("Autenticação biométrica")
            .setDescription("Posicione o seu dedo no sensor para realizar a autenticação")
            .setDeviceCredentialAllowed(true)
            .build()

        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

                override fun onAuthenticationError(
                    errorCode: Int, errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Usuário sem permissão de acesso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext,
                        "Falha na autenticação",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }
}