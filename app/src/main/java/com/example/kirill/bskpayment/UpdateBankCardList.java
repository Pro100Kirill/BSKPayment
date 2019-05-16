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

import static com.example.kirill.bskpayment.R.layout.activity_update_bank_card;

public class UpdateBankCardList extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_update_bank_card);

        //Прием данных
        Intent intent = getIntent();
        final String password = intent.getStringExtra("password");//Принимаю пароль

        //Объяваляю имена используемых в программе файлов
        final String fileName = "user";//Имя файла, в котором хранится информация пользователя

        //Ищу в файле R.java необходимые элементы activity_registration.xml
        final Button buttonNewBC = (Button) findViewById(R.id.buttonNewBC);//Кнопка добавления нового номера банковской карты
        final Button buttonDeleteBC = (Button) findViewById(R.id.buttonDeleteBC);//Кнопка удаления выбранного номера банковской карты
        final EditText editNewBC = (EditText) findViewById(R.id.editNewBC);//Строка ввода нового номера банкоской карты
        final Spinner spinnerOfBC = (Spinner) findViewById(R.id.spinnerOfBC);//Список сохраннеых номеров банковских карт

        //Инициирую необходимые в работе переменные
        final Gson gson = new GsonBuilder().create();//Создаю объект распознования json файлов
        UserJSON bufferUserJSON = new UserJSON();//Инициирую объект с пользовательской информацией

        //Чтение файла
        try {
            String bufferLine;//Инициирую переменную в которой будут хранится прочитанные строки файла
            String bufferFile = new String();//Инициирую переменную в которой будет хранится весь прочитанный файл
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateBankCardList.this, android.R.layout.simple_spinner_item);//Инициирую специальный объект для работы с содежимым раскрывающихся списков

            InputStream inputStream = openFileInput(fileName);//Открытие потока чтения файла
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//Начинаю чтение потока данных
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while ((bufferLine = bufferedReader.readLine()) != null){//Читать и копировать содержимое файла, пока следущая стока не станет пустой
                bufferFile += bufferLine;//Вношу в буффер прочитанную строку
            }
            bufferedReader.close();//Завершаю чтение потока данных
            inputStream.close();//Закртытие потока чтения файла

            bufferUserJSON = gson.fromJson(bufferFile, UserJSON.class);//Создаю объект класса UserJSON из распознангого содержимого файла фротмата .json
            for(int i = 0; i < bufferUserJSON.getBankCardsLength(); i++){
                //Форматирую текст элементов списка
                adapter.add(bufferUserJSON.getBankCardByIndex(password, i));
            }
            spinnerOfBC.setAdapter(adapter);//Вывожу сконструированный список
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
        }
        final UserJSON userJSON = bufferUserJSON;//Финальная версия класса, хранящего в себе информацию о пользователе

        buttonNewBC.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой добавления нового номера банковсвкой карты
            @Override
            public void onClick(View view) {//Действия по нажатию
                final String bankCardNum = editNewBC.getText().toString();//Получаю последнее введенное значение строки ввода нового номера банковской карты
                UserJSON localUserJSON = userJSON;
                if(!localUserJSON.bankCardIsExist(password, bankCardNum)) {
                    localUserJSON.addBankCard(password, bankCardNum);
                    final String userJSONtoString = gson.toJson(localUserJSON);

                    try {
                        OutputStream outputStream = openFileOutput(fileName, MODE_PRIVATE);//Открытие потока перезаписи файла
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);//Начинаю запись потока данных
                        outputStreamWriter.write(userJSONtoString);
                        outputStreamWriter.close();//Завершаю запись потока данных
                        outputStream.close();//Закртытие потока записи файла
                        Toast.makeText(getApplicationContext(), "Банковская карта с номером " + bankCardNum + " была добавлена в список карт", Toast.LENGTH_LONG).show();//Сообщение о добавлении БСК
                    } catch(Exception e)  {
                        Toast.makeText(getApplicationContext(), "Exception: " + e.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
                    }
                    editNewBC.setText(null);//Сбрасываю текстовое поле
                    recreate();//Обновление Activity
                } else {
                    Toast.makeText(getApplicationContext(), "Карта с таким номером уже добавлена в список карт", Toast.LENGTH_LONG).show();//Сообщение об ошибке
                }
            }
        });

        buttonNewBC.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateBankCardList.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для подтверждения дополнения вписаного выше номера банковской карты в список банковских карт пользователя")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        buttonDeleteBC.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой удаления выбранного номера банковсвкой карты
            @Override
            public void onClick(View view) {//Действия по нажатию
                final int bankCardNumPosition = spinnerOfBC.getSelectedItemPosition();//Беру порядковый номер выбранного номера банковской
                UserJSON localUserJSON = userJSON;
                localUserJSON.deleteBankCardByIndex(bankCardNumPosition);//Удаляю выбранную карту
                final String userJSONtoString = gson.toJson(localUserJSON);

                //Перезаписываю список банковских карт без выбранной карты
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

        buttonDeleteBC.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateBankCardList.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для подтверждения удаления выбранногов выше номера банковской карты из списка банковских карт пользователя")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });
    }
}
