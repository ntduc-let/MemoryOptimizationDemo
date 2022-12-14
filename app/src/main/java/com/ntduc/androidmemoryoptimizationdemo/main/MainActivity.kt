package com.ntduc.androidmemoryoptimizationdemo.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.ntduc.androidmemoryoptimizationdemo.BuildConfig
import com.ntduc.androidmemoryoptimizationdemo.databinding.ActivityMainBinding
import com.ntduc.androidmemoryoptimizationdemo.get_all_file.activity.GetAllFileActivity
import com.ntduc.contextutils.inflater
import com.ntduc.contextutils.showConfirmationDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){
        initEvent()
    }

    private fun initEvent() {
        binding.btnDemo.setOnClickListener{
            if (checkPermissionReadAllFile()) {
                startActivity(Intent(this, GetAllFileActivity::class.java))
            } else {
                requestPermissionReadAllFile()
            }
        }
    }

    private fun checkPermissionReadAllFile(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val write =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissionReadAllFile() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            openDialogAccessAllFile()
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            requestPermissions(permissions, REQUEST_PERMISSION_READ_WRITE)
        }
    }

    private fun requestAccessAllFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                requestPermissionLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                requestPermissionLauncher.launch(intent)
            }
        } else {
            try {
                val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri)
                requestPermissionLauncher.launch(intent)
            } catch (_: Exception) {
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_READ_WRITE) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                startActivity(Intent(this, GetAllFileActivity::class.java))
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(permissions[0]!!)
                        && shouldShowRequestPermissionRationale(permissions[1]!!)
                    ) {
                        requestPermissions(
                            permissions,
                            REQUEST_PERMISSION_READ_WRITE
                        )
                    } else {
                        openDialogAccessAllFile()
                    }
                }
            }
        }
    }

    private fun openDialogAccessAllFile() {
        showConfirmationDialog(
            "Request Permission",
            "Access to read all file in your device",
            onResponse = {
                when (it) {
                    true -> requestAccessAllFile()
                    false -> finish()
                }
            })
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (checkPermissionReadAllFile()) {
                    startActivity(Intent(this, GetAllFileActivity::class.java))
                } else {
                    requestPermissionReadAllFile()
                }
            }
        }

    companion object {
        private const val REQUEST_PERMISSION_READ_WRITE = 100
    }
}