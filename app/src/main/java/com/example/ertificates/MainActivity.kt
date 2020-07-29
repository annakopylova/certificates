package com.example.ertificates

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cc.duduhuo.util.digest.Digest
import com.armdroid.filechooser.Content
import com.armdroid.filechooser.Error
import com.armdroid.filechooser.FileChooser
import com.armdroid.filechooser.OnContentSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.lang.StringBuilder
import java.nio.charset.Charset
import kotlin.experimental.and
import kotlin.experimental.or


class MainActivity : AppCompatActivity(), OnContentSelectedListener {

    val fileChooser: FileChooser = FileChooser(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnChoice.setOnClickListener {
            fileChooser.getFile(this)
        }
    }

    /**
     * Функция, в которой получаем результат выбора файла.
     * @param fileType - тип файла.
     * @param content - содержание файла.
     */
    override fun onContentSelected(fileType: Int, content: Content?) {

        // Если файл существует
        content?.let {
            // То создаем файл
            val file = File(it.path)
            // Читаем этот файл
            var body = file.readLines(Charsets.UTF_8).toString()
            // Убираем лишние знаки, которые получились в результате чтения строк списком.
            body = body
                .replace("[", "")
                .replace("]", "")
                .replace(",", "\r")
            // Получаем хэш сертификата. Он должен быть 8 символов, но минимум можем получить 20.
            // Поэтому выполняем sha1 и оттуда возьмем первые 8.
            val hash = Digest.sha1(body)
            // Создаем хэш в строке из байтов
            val stringBuilder = StringBuilder()
            for (b in hash) {
                val st = String.format("%02X", b)
                stringBuilder.append(st)
            }

            // Название файла - 8 символов хэша с расширением .0
            val fileName = "${stringBuilder.toString().substring(0, 8).toLowerCase()}.0"
            // Записываем сертификат в файл для перемещения
            writeFileOnInternalStorage(this, fileName, body)


        }

    }

    /**
     * Функция записи файла в хранилище приложения
     * @param mcoContext - контекст для записи,
     * @param sFileName - имя файла для записи,
     * @param sBody - содержание файла для записи.
     */
    fun writeFileOnInternalStorage(
        mcoContext: Context,
        sFileName: String?,
        sBody: String?
    ) {
        // Определяем директорию для записи файла
        val dir = File(mcoContext.getFilesDir(), "mydir")
        // Если не существует директории, то создаем ее
        if (!dir.exists()) {
            dir.mkdir()
        }
        // Записываем в файл сертификат
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

    // Срабатывает в случае ошибки библиотеки.
    override fun onError(error: Error?) {
        if (error?.getType() == Error.NULL_PATH_ERROR) {
            Toast.makeText(this, "Could not locate path of the file", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, error?.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Управление разрешением для доступа в файловую систему
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        fileChooser.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // Получаем в результат файл, который мы выбрали
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileChooser.onActivityResult(requestCode, resultCode, data)

    }
}