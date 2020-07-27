package com.example.ertificates

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.armdroid.filechooser.Content
import com.armdroid.filechooser.Error
import com.armdroid.filechooser.FileChooser
import com.armdroid.filechooser.OnContentSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity(), OnContentSelectedListener {

    val fileChooser: FileChooser = FileChooser(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnChoice.setOnClickListener {
            fileChooser.getFile(this)
        }
    }

    override fun onContentSelected(fileType: Int, content: Content?) {

        println(content?.fileName)

        content?.let {
            val file = File(it.path)
            val fileName = "${file.name.split(".")[0]}.0"
            var body = file.readLines(Charsets.UTF_8).toString()
            body = body
                .replace("[", "")
                .replace("]", "")
                .replace(",", "\r")
            writeFileOnInternalStorage(this, fileName, body)
        }

    }

    fun File.copyTo(file: File) {
        inputStream().use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    fun writeFileOnInternalStorage(
        mcoContext: Context,
        sFileName: String?,
        sBody: String?
    ) {
        val dir = File(mcoContext.getFilesDir(), "mydir")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val gpxfile = File(dir, sFileName)
            val writer = FileWriter(gpxfile)
            writer.append(sBody)
            writer.flush()
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onError(error: Error?) {
        if (error?.getType() == Error.NULL_PATH_ERROR) {
            Toast.makeText(this, "Could not locate path of the file", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, error?.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fileChooser.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileChooser.onActivityResult(requestCode, resultCode, data)

    }
}