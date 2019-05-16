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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Remittance extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remittance);

        //Прием данных
        final Intent intent = getIntent();
        final String password = intent.getStringExtra("password");

        //Объяваляю имена используемых в программе файлов
        final String fileName = "user";//Имя файла, в котором хранятся номера карт пользователя

        //Ищу в фале R.java необходимые элементы activity_main.xml
        final Button buttonRemittanceBeginning = (Button) findViewById(R.id.buttonRemittanceBeginnig);
        final Spinner spinnerOfBankCards = (Spinner) findViewById(R.id.spinnerOfBC);
        final Spinner spinnerOfMetropolitenCards = (Spinner) findViewById(R.id.spinnerOfMPC);
        final EditText editTextCardVerificationValue = (EditText) findViewById(R.id.editTextCardVerificationValue);
        final EditText editTextSum = (EditText) findViewById(R.id.editTextSum);

        //Чтение информации из файла
        //Инициирую необходимые в работе переменные
        Gson gson = new GsonBuilder().create();//Создаю объект распознования json файлов
        UserJSON bufferUserJSON = new UserJSON();//Инициирую объект с пользовательской информацией

        //Чтение файла
        try {
            String bufferLine;//Инициирую переменную в которой будут хранится прочитанные строки файла
            String bufferFile = new String();//Инициирую переменную в которой будет хранится все содержимое прочитанного файла
            ArrayAdapter<String> adapterBC = new ArrayAdapter<String>(Remittance.this, android.R.layout.simple_spinner_item);//Инициирую специальный объект для работы с содежимым раскрывающихся списков банковских карт
            ArrayAdapter<String> adapterMPC = new ArrayAdapter<String>(Remittance.this, android.R.layout.simple_spinner_item);//Инициирую специальный объект для работы с содежимым раскрывающихся списков карт метрополитена


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
                adapterBC.add(bufferUserJSON.getBankCardByIndex(password, i));
            }
            spinnerOfBankCards.setAdapter(adapterBC);//Вывожу сконструированный список банковских карт

            for(int i = 0; i < bufferUserJSON.getMetropolitenCardsLength(); i++){
                //Форматирую текст элементов списка
                adapterMPC.add(bufferUserJSON.getMetropolitenCardNumberByIndex(password, i)
                        + " Баланс:" + bufferUserJSON.getMetropolitenCardBalanceByIndex(password, i) + " руб");
            }
            spinnerOfMetropolitenCards.setAdapter(adapterMPC);//Вывожу сконструированный список карт метрополитена
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
        }
        final UserJSON userJSON = bufferUserJSON;//Фиксирую прочитанную из файла информацию

        buttonRemittanceBeginning.setOnClickListener(new View.OnClickListener() {//Действие по взаимодействию с кнопкой денжного переводи
            @Override
            public void onClick(View view) {//Действие по нажатию
                final int bankCardIndex = spinnerOfBankCards.getSelectedItemPosition();//Беру порядковый номер выбранного номера банковской карты
                final String bankCardVerificationValue = editTextCardVerificationValue.getText().toString();//Считываю CVV/CVC выбранной банковской карты
                final int metropolitenCardIndex = spinnerOfMetropolitenCards.getSelectedItemPosition();//Беру порядковый номер выбранного номера БСК
                final double sum = Double.parseDouble(editTextSum.getText().toString());//Считываю переводимую сумму

                //Выполнение перевода
                Response.Listener<String> responseListener = new Response.Listener<String>() {//Действия по ответу на запрос
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                Toast.makeText(getApplicationContext(), "Перевод в размере " + sum + " руб. осуществлен успешно", Toast.LENGTH_LONG).show();//Сообщение об ошибке
                                //Перезапись файла с учетом изменения в истории переводов
                                UserJSON bufferUserJSON = userJSON;
                                Calendar calendar = new GregorianCalendar();
                                calendar.getTime();
                                bufferUserJSON.addRemittanceStory(password
                                        ,bufferUserJSON.getBankCardByIndex(password, bankCardIndex)
                                        ,bufferUserJSON.getMetropolitenCardNumberByIndex(password, metropolitenCardIndex)
                                        ,sum
                                        ,calendar.getTime().toString());//Меняю значение баланса у выбранной БСК
                                Gson localGson = new GsonBuilder().create();
                                String userJSONtoString = localGson.toJson(bufferUserJSON);

                                //Перезаписываю файл с измененым балансом
                                try {
                                    OutputStream outputStream = openFileOutput(fileName, MODE_PRIVATE);//Открытие потока перезаписи файла
                                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);//Начинаю запись потока данных
                                    outputStreamWriter.write(userJSONtoString);//Записываю обновленные данные
                                    outputStreamWriter.close();//Завершаю запись потока данных
                                    outputStream.close();//Закртытие потока записи файла
                                } catch(Exception e)  {
                                    Toast.makeText(getApplicationContext(), "Exception: " + e.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Операция не была осуществлена", Toast.LENGTH_LONG).show();//Сообщение об ошибке
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Exception: " + e.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
                        }
                    }
                };
                RemittanceRequest remittanceRequest = new RemittanceRequest(userJSON.getBankCardByIndex(password, bankCardIndex)
                        ,bankCardVerificationValue
                        ,userJSON.getMetropolitenCardNumberByIndex(password, metropolitenCardIndex)
                        ,sum
                        ,responseListener);
                RequestQueue queue = Volley.newRequestQueue(Remittance.this);
                queue.add(remittanceRequest);
            }
        });

        buttonRemittanceBeginning.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Remittance.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для подтверждения начала денежного перевода")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        editTextCardVerificationValue.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Remittance.this);//Сообщ об ошибке
                builder.setMessage("В данную строку необходимо ввести 3-х значный код записанный на магнитной ленте выбранной банковской карты")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        editTextSum.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Remittance.this);//Сообщ об ошибке
                builder.setMessage("В данную строку необходимо ввести сумму денежного перевода")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });
    }
}
