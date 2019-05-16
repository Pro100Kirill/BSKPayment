package com.example.kirill.bskpayment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RemittanceStory extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remittance_story);

        final TextView textViewStory = (TextView) findViewById(R.id.textViewStory);

        //Прием данных
        final Intent intent = getIntent();
        final String password = intent.getStringExtra("password");

        //Объяваляю имена используемых в программе файлов
        final String fileName = "user";//Имя файла, в котором хранятся номера карт пользователя

        //Инициирую необходимые в работе переменные
        Gson gson = new GsonBuilder().create();//Создаю объект распознования json файлов
        UserJSON userJSON = new UserJSON();//Инициирую объект с пользовательской информацией

        //Чтение файла
        try {
            String bufferLine;//Инициирую переменную в которой будут хранится прочитанные строки файла
            String bufferFile = new String();//Инициирую переменную в которой будет хранится все содержимое прочитанного файла


            InputStream inputStream = openFileInput(fileName);//Открытие потока чтения файла
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//Начинаю чтение потока данных
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((bufferLine = bufferedReader.readLine()) != null){//Читать и копировать содержимое файла, пока следущая стока не станет пустой
                bufferFile += bufferLine;//Вношу в буффер прочитанную строку
            }
            bufferedReader.close();//Завершаю чтение потока данных
            inputStream.close();//Закртытие потока чтения файла

            userJSON = gson.fromJson(bufferFile, UserJSON.class);//Создаю объект класса UserJSON из распознангого содержимого файла фротмата .json
            //Вывожу прочитанную информацию
            if(userJSON.getRemittanceStoriesLength() != 0){//Проверяю наличие истории переводов
                textViewStory.setText(null);
                for(int i = 0; i < userJSON.getRemittanceStoriesLength(); i++){
                    textViewStory.setText(userJSON.getRemmittanceDateByIndex(password, i)
                            + " c карты:" + userJSON.getSenderNumberByIndex(password, i)
                            + " на карту:" + userJSON.getReceiverNumberByIndex(password, i)
                            + " переведено:" + userJSON.getRemittanceSumByIndex(password, i)
                            + " руб.\n"
                            + textViewStory.getText());
                }
            } else {
                textViewStory.setText("История переводов отсутствует");
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
        }

        textViewStory.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemittanceStory.this);//Сообщ об ошибке
                builder.setMessage("В данном окне изображен список всех успешно выполненых денежных переводов")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });
    }
}
