package ishant.sharma.totalityassignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import ishant.sharma.totalityassignment.databinding.ActivityEditBinding
import java.io.File

class EditActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil. setContentView(this,R.layout.activity_edit)
        val myFile = File(intent.getStringExtra("MY_FILE"))
        Glide.with(this).load(myFile).into(binding.photoEditorView.source)


    }
}