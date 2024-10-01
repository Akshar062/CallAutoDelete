package com.akshar.callautodelete

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE = 100
    private var folderUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start folder selection
        val selectFolderButton = findViewById<Button>(R.id.selectFolderButton)
        selectFolderButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            folderUri = data?.data
            // Persist the folder URI for future access
            folderUri?.let { saveFolderUri(it.toString()) }

            // Schedule the file cleanup work
            scheduleFileCleanup()
        }
    }

    private fun saveFolderUri(uri: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("FOLDER_URI", uri).apply()
    }

    private fun scheduleFileCleanup() {
        scheduleFileCleanup(this)
    }
}