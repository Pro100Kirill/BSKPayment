package com.example.kirill.bskpayment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static com.example.kirill.bskpayment.R.layout.activity_update_metropoliten_card;

public class UpdateMetropolitenCardList extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_update_metropoliten_card);

        //Прием данных
        Intent intent = getIntent();
        final String password = intent.getStringExtra("password");//Принимаю пароль

        //Объяваляю имена используемых в программе файлов
        final String fileName = "user";//Имя файла, в котором хранятся данные пользователя

        //Ищу в файле R.java необходимые элементы activity_registration.xml
        final Button buttonNewMPC = (Button) findViewById(R.id.buttonNewMPC);//Кнопка добавления нового номера БСК
        final Button buttonDeleteMPC = (Button) findViewById(R.id.buttonDeleteMPC);//Кнопка удаления выбранного номера БСК
        final EditText editNewMPC = (EditText) findViewById(R.id.editNewMPC);//Строка ввода нового номера БСК
        final Spinner spinnerOfMPC = (Spinner) findViewById(R.id.spinnerOfMPC);//Список сохраннеых номеров БСК

        //Инициирую необходимые в работе переменные
        final Gson gson = new GsonBuilder().create();//Создаю объект распознования json файлов
        UserJSON bufferUserJSON = new UserJSON();//Инициирую объект с пользовательской информацией

        //Чтение файла
        try {
            String bufferLine;//Инициирую переменную в которой будут хранится прочитанные строки файла
            String bufferFile = new String();//Инициирую переменную в которой будет хранится весь прочитанный файл
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateMetropolitenCardList.this, android.R.layout.simple_spinner_item);//Инициирую специальный объект для работы с содежимым раскрывающихся списков

            InputStream inputStream = openFileInput(fileName);//Открытие потока чтения файла
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//Начинаю чтение потока данных
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((bufferLine = bufferedReader.readLine()) != null){//Читать и копировать содержимое файла, пока следущая стока не станет пустой
                bufferFile += bufferLine;//Вношу в буффер прочитанную строку
            }
            bufferedReader.close();//Завершаю чтение потока данных
            inputStream.close();//Закртытие потока чтения файла

            bufferUserJSON = gson.fromJson(bufferFile, UserJSON.class);//Создаю объект класса UserJSON из распознангого содержимого файла фротмата .json
            for(int i = 0; i < bufferUserJSON.getMetropolitenCardsLength(); i++){
                //Форматирую текст элементов списка
                adapter.add(bufferUserJSON.getMetropolitenCardNumberByIndex(password, i)
                        + " Баланс:" + bufferUserJSON.getMetropolitenCardBalanceByIndex(password, i) + " руб");
            }
            spinnerOfMPC.setAdapter(adapter);//Вывожу сконструированный список
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
        }
        final UserJSON userJSON = bufferUserJSON;//Финальная версия класса, хранящего в себе информацию о пользователе

        buttonNewMPC.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой добавления нового номера БСК
            @Override
            public void onClick(View view) {//Действия по нажатию
                final String metropolitenCardNum = editNewMPC.getText().toString();//Получаю последнее введенное значение строки ввода нового номера БСК
                UserJSON localUserJSON = userJSON;
                if(!localUserJSON.metropolitenCardIsExist(password, metropolitenCardNum)){
                    localUserJSON.addMetropolitenCard(password, metropolitenCardNum, 0);
                    final String userJSONtoString = gson.toJson(localUserJSON);

                    try {
                        OutputStream outputStream = openFileOutput(fileName, MODE_PRIVATE);//Открытие потока перезаписи файла
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);//Начинаю запись потока данных
                        outputStreamWriter.write(userJSONtoString);
                        outputStreamWriter.close();//Завершаю запись потока данных
                        outputStream.close();//Закртытие потока записи файла
                        Toast.makeText(getApplicationContext(), "БСК с номером " + metropolitenCardNum + " была добавлена в список БСК", Toast.LENGTH_LONG).show();//Сообщение о добавлении БСК
                    } catch(Exception e)  {
                        Toast.makeText(getApplicationContext(), "Exception: " + e.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
                    }
                    editNewMPC.setText(null);//Сбрасываю текстовое поле
                    recreate();//Обновление Activity
                } else {
                    Toast.makeText(getApplicationContext(), "Карта с таким номером уже добавлена в список карт", Toast.LENGTH_LONG).show();//Сообщение об ошибке
                }
            }
        });

        buttonNewMPC.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateMetropolitenCardList.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для подтверждения дополнения вписаного выше номера карты метрополитена в список карт метрополитена пользователя")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        buttonDeleteMPC.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой удаления выбранного номера БСК
            @Override
            public void onClick(View view) {//Действия по нажатию
                final int metropolitenCardNumPosition = spinnerOfMPC.getSelectedItemPosition();//Беру порядковый номер выбранного номера БСК
                UserJSON localUserJSON = userJSON;
                localUserJSON.deleteMetropolitenCardByIndex(metropolitenCardNumPosition);//Удаляю выбранную карту
                final String userJSONtoString = gson.toJson(localUserJSON);

                //Перезаписываю файл без выбранной карты
                try {
                    OutputStream outputStream = openFileOutput(fileName, MODE_PRIVATE);//Открытие потока перезаписи файла
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);//Начинаю запись потока данных
                    outputStreamWriter.write(userJSONtoString);//Записываю обновленные данные
                    outputStreamWriter.close();//Завершаю запись потока данных
                    outputStream.close();//Закртытие потока записи файла
                } catch(Exception e)  {
                    Toast.makeText(getApplicationContext(), "Exception: " + e.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
                }
                recreate();//Обновление Activity
            }
        });

        buttonDeleteMPC.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateMetropolitenCardList.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для подтверждения удаления выбранногов выше номера карты метрополитена из списка карт метрополитена пользователя")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });
    }
}