package com.example.myapplication__1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.graphics.Color

class DiaryActivity : AppCompatActivity() {

    private lateinit var diaryImageView: ImageView
    private lateinit var diaryEditText: EditText
    private lateinit var saveDiaryButton: Button
    private var selectedDate: String? = null
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        diaryImageView = findViewById(R.id.diaryImageView)
        diaryEditText = findViewById(R.id.diaryEditText)
        saveDiaryButton = findViewById(R.id.saveDiaryButton)

        selectedDate = intent.getStringExtra("selectedDate")

        // 저장된 일기 로드
        selectedDate?.let {
            loadDiary(it)
        }

        diaryImageView.setOnClickListener {
            requestStoragePermission()
        }

        saveDiaryButton.setOnClickListener {
            saveDiary()
        }
        saveDiaryButton.setBackgroundColor(Color.LTGRAY) // 버튼 배경 색 회색
        saveDiaryButton.setTextColor(Color.BLACK) // 텍스트 색
    }

    private fun loadDiary(date: String) {
        val sharedPreferences = getSharedPreferences("DiaryPrefs", MODE_PRIVATE)
        val diaryContent = sharedPreferences.getString("${date}_text", "")
        val imagePath = sharedPreferences.getString("${date}_imagePath", null)

        // 텍스트 설정
        diaryEditText.setText(diaryContent)

        // 이미지 설정
        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                imageUri = Uri.fromFile(file)
                diaryImageView.setImageURI(imageUri)
                imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun requestStoragePermission() {
        // Android 13 이상에서는 READ_MEDIA_IMAGES 권한 필요
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
            )
        } else {
            openGallery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                diaryImageView.setImageURI(imageUri)
                imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            } else {
                Toast.makeText(this, "이미지를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        return try {
            val filename = "$selectedDate.jpg"
            val file = File(filesDir, filename)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun saveDiary() {
        val sharedPreferences = getSharedPreferences("DiaryPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val diaryContent = diaryEditText.text.toString()
        editor.putString("${selectedDate}_text", diaryContent)

        // 이미지 저장 후 경로 저장
        imageBitmap?.let {
            val imagePath = saveImageToInternalStorage(it)
            editor.putString("${selectedDate}_imagePath", imagePath)
        }

        editor.apply()

        Toast.makeText(this, "일기가 저장되었습니다", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "이미지를 선택하려면 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
