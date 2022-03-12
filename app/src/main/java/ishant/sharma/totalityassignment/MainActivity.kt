package ishant.sharma.totalityassignment

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ishant.sharma.totalityassignment.databinding.ActivityMainBinding
import ishant.sharma.totalityassignment.utils.FileUtils
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    lateinit var file: File
    lateinit var imageBitmap: Bitmap

    private lateinit var requestStoragePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var cal = Calendar.getInstance()
    private lateinit var joinDateSetListener: DatePickerDialog.OnDateSetListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        registerCameraPermission()
        registerCameraLauncher()
        registerStoragePermission()
        registerGalleryLauncher()
        binding.camera.setOnClickListener {
            CallCameraPermission()

        }
        binding.storage.setOnClickListener {
            CallStoragePermission()
        }



    }

    private fun registerCameraPermission() {
        requestCameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                when {
                    granted -> openCamera()
                    else -> requestCameraPermission()
                }
            }
    }

    private fun registerStoragePermission() {
        requestStoragePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                when {
                    granted -> viewGallery()
                    else -> requestStoragePermission()
                }
            }
    }

    private fun registerCameraLauncher() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data == null) {
                        return@registerForActivityResult
                    }
                    val extras = data.extras
                    imageBitmap = extras!!["data"] as Bitmap
                    file = FileUtils.createFile(this,
                        getString(R.string.app_name),
                        "my_profile_image.png"
                    )
                    //FileUtils.saveBitmap(imageBitmap, file);
                    val imageLocalPath = FileUtils.saveImageToInternalStorage(file, imageBitmap)
                    startActivity(Intent(MainActivity@this,EditActivity::class.java).putExtra("MY_FILE", file))
                }
            }
    }

    private fun registerGalleryLauncher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    if (data == null) {
                        return@registerForActivityResult
                    }
                    val uri = data.data
                    var imageLocalPath = File(FileUtils.getPathReal(this, uri!!))
                    file = imageLocalPath.absoluteFile
                    startActivity(Intent(MainActivity@this,EditActivity::class.java).putExtra("MY_FILE", file.absolutePath))
                }
            }
    }



    fun CallStoragePermission() {

        if (!Status_checkReadExternalStoragePermission()) {
            requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            viewGallery()
        }
    }

    private fun Status_checkReadExternalStoragePermission(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> openCamera()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private fun requestStoragePermission() {

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> viewGallery()
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> { requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE) }
        }
    }



    fun viewGallery() {
        val intentDocument = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(
               "storage",
               "002")
        }

        galleryLauncher.launch(intentDocument)
    }

    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
           putExtra("camera","001")
        }

        cameraLauncher.launch(takePictureIntent)
    }

    fun CallCameraPermission() {
        if (!Status_checkCameraPermission()) {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    private fun Status_checkCameraPermission(): Boolean {
        val camera = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        )

        return camera == PackageManager.PERMISSION_GRANTED
    }

}