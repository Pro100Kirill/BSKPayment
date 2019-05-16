package com.example.kirill.bskpayment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.example.kirill.bskpayment.R.layout.activity_registration;

public class Registration extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_registration);

        //Объяваляю имена используемых в программе файлов
        final String fileName = "user";//Имя файла, в котором хранится данные о пользователе пользователя

        //Ищу в файле R.java необходимые элементы activity_registration.xml
        final Button buttonNewPassword = (Button) findViewById(R.id.buttonNewPassword);//Кнопка подтверждения нового пароля
        final EditText textNewPassword = (EditText) findViewById(R.id.editNewPassword);//Строка ввода нового пароля

        buttonNewPassword.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой подтверждения нового пароля
            @Override
            public void onClick(View v) {//Действие по нажатию кнопки
                final String password = Integer.toString(textNewPassword.getText().toString().hashCode());//Получаю последнее введенное значение строки ввода нового пароля
                UserJSON userJSON = new UserJSON();//Инициирую объект с пользовательской информацией
                userJSON.setPasswordHash(password);//Добавляю хеш пароля в объект с пользовательской информацией

                Gson gson = new GsonBuilder().create();//Создаю объект распознования json файлов
                final String userJSONtoString = gson.toJson(userJSON);//Преобразую класс userJSON в строку

                if(password.length() != 0) {//Если строка ввода пароля не пуста
                    try {
                        OutputStream outputStream = openFileOutput(fileName, MODE_PRIVATE);//Открываю файл с паролем на перезапись
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                        outputStreamWriter.write(userJSONtoString);//Перезаписываю файл
                        outputStreamWriter.close();
                        outputStream.close();//Завершаю поток записи

                        Intent intent = new Intent(Registration.this, Login.class);//Переход к странице регистрации
                        Registration.this.startActivity(intent);
                        finish();//Закрыть текущее Activity во избежание возвращения к нему из окна авторизации
                    } catch (Throwable t) {
                        Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Заполните поле ввода пароля", Toast.LENGTH_LONG).show();//Сообщение об ошибке
                }
            }
        });

        buttonNewPassword.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для подтверждения сброса старых параметров пользователя и для сохранения нового пароля")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });
    }
}
