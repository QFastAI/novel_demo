package com.small.world.fiction.activity

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.viewModelScope
import com.aiso.qfast.base.BaseActivity
import com.aiso.qfast.base.R
import com.aiso.qfast.base.ext.doOnApplyWindowInsets
import com.aiso.qfast.base.ext.showSystemBars
import com.blankj.utilcode.util.ActivityUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.small.world.fiction.databinding.ActivityMainBinding
import com.small.world.fiction.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//sk-8ef953b8e6b9480680bce4438ef91c53
@Suppress("DEPRECATION")
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel: UserViewModel by viewModels()

    private val RC_SIGN_IN = 64206

    private val TAG = "MainActivity==="
    override fun createBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        initWindows()
        initViewClick()
        initViewModels()
    }

    private fun initViewModels() {
        viewModel.viewModelScope.launch(Dispatchers.Main) {
            viewModel.addUserResult.collect {
                if(it == null) return@collect
                if(it.isSuccess == true){
                    Toast.makeText(this@MainActivity, "添加成功", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this@MainActivity, "添加失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initViewClick() {
        binding.loginWithGoogleBtn.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("427707861909-8hk2nk1k4sar4i4ablbn2psokkfkhmd6.apps.googleusercontent.com") // 替换为实际的
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    private fun initWindows(){
        window.showSystemBars(
            navigationBarColor = resources.getColor(R.color.main_color),
        )
        binding.root.doOnApplyWindowInsets { view, windowInsetsCompat ->
            val navigationBarHeight = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).bottom
            view.updatePadding(bottom = navigationBarHeight+20)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            task.addOnCompleteListener { completedTask ->
                if (completedTask.isSuccessful) {
                    val account = completedTask.result
                    firebaseAuthWithGoogle(account.idToken!!)
                } else {
                    Log.e("GoogleLogin", "登录失败", completedTask.exception)
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
//                    viewModel.addUser(user!!)

                    //登录成功了之后跳转页面并结束当前页面
                    ActivityUtils.startActivity(BookShellActivity::class.java)
                    finish()
                } else {
                    // 登录失败
                }
            }
    }
}