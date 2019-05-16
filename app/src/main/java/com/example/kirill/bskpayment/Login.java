package com.example.kirill.bskpayment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.example.kirill.bskpayment.R.layout.activity_login;

public class Login extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_login);

        //Объяваляю имена используемых в программе файлов
        final String fileName = "user";//Имя файла, в котором хранятся данные о пользователе

        //Проверяю наличие учетной записи
        if(!new File(getFilesDir() + "/" + fileName).exists()){//Если файл не существует, то переход к окну регистрации
            Intent intent = new Intent(Login.this, Registration.class);//Переход к странице регистрации
            Login.this.startActivity(intent);
            finish();//Закрытие текущего Activity во избежание возвращения к нему из окна регистрации без регистрации
        }

        //Ищу в фале R.java необходимые элементы activity_login.xml
        final Button buttonLogin = (Button) findViewById(R.id.buttonLogin);//Кнопка запроса проверки правильноси ввода
        final Button buttonReset = (Button) findViewById(R.id.buttonReset);//Кнопка сброса содержимого *passwordFileName*
        final EditText textPassword = (EditText) findViewById(R.id.editPassword);//Строка ввода пароля

        //Инициирую необходимые для работы переменные
        final Gson gson = new GsonBuilder().create();//Создаю объект распознования json файлов

        buttonLogin.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой авторизации
            @Override
            public void onClick(View v) {//Действия по нажатию
                final String password = textPassword.getText().toString();//Получение пароля в момент нажатия кнопки
                final UserJSON userJSON;//Инициирую объект с пользовательской информацией

                try {//Читаю файл
                    String bufferLine;//Инициирую переменную в которой будут хранится прочитанные строки файла
                    String bufferFile = new String();//Инициирую переменную в которой будет хранится все содержимое прочитанного файла

                    InputStream inputStream = openFileInput(fileName);//Открытие потока чтения файла
                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        while ((bufferLine = bufferedReader.readLine()) != null){//Читать и копировать содержимое файла, пока следущая стока не станет пустой
                            bufferFile += bufferLine;//Вношу в буффер прочитанную строку
                        }
                        bufferedReader.close();
                        inputStream.close();//Закртытие потока чтения файла

                        userJSON = gson.fromJson(bufferFile, UserJSON.class);//Создаю объект класса UserJSON из распознангого содержимого файла фротмата .json
                        if (userJSON.getPasswordHash().equals(Integer.toString(password.hashCode()))) {//Сравнение хэшей введенного пароля и эталонного пароля
                            Intent intent = new Intent(Login.this, MainActivity.class);//Переход к главной странице
                            intent.putExtra("password", password);//Передаю пароль в новое activity
                            Login.this.startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Введен неверный пароль", Toast.LENGTH_LONG).show();//Сообщение об ошибке
                        }

                    }
                } catch (Throwable t) {
                    Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
                }
            }
        });

        buttonLogin.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);//Сообщ об ошибке
                builder.setMessage("Для дальнейшего использования приложения введите пароль или сбросьте параметры пользователя нажав на соотвествуюущую кнопку")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {//Действия по нажатию кнопки сброса содержимого *filename*
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);//Инициирую диалоговое окно
                builder.setTitle("Предупреждение");
                builder.setMessage("После подтверждения данного действия будут сброшены следующие настройки пользователя: пароль, список прикрепленных банковских карт, списк прикрепленных карт метрополитена");
                builder.setCancelable(false);
                builder.setPositiveButton("Ок",//Позитивный ответ
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();//Завершения диалога
                                Intent intent = new Intent(Login.this, Registration.class);//Переход к странице регистрации
                                Login.this.startActivity(intent);
                            }
                });
                builder.setNegativeButton("Отмена",//Негативный ответ
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();//Завершения диалога
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();//Вызов диалога
            }
        });

        buttonReset.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для перехода в меню сбрасывания параметров пользователя")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        textPassword.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);//Сообщ об ошибке
                builder.setMessage("В данную строку необходимо ввести пароль доступа к главному меню приложения")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });
    }
}
