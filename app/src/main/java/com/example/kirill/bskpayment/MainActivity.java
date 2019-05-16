package com.example.kirill.bskpayment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Прием данных
        Intent intent = getIntent();
        final String password = intent.getStringExtra("password");

        //Объяваляю имена используемых в программе файлов
        final String fileName = "user";//Имя файла, в котором хранятся номера карт пользователя

        //Ищу в фале R.java необходимые элементы activity_main.xml
        final Button buttonLogout = (Button) findViewById(R.id.buttonLogout);//Кнопка завершения сеанса
        final Button buttonUserBankCards = (Button) findViewById(R.id.buttonBC);//Кнопка перехода к меню банковских карт
        final Button buttonUserMetropolitenCards = (Button) findViewById(R.id.buttonMPC);//Кнопка перехода к меню БСК
        final Button buttonRefreshActivity = (Button) findViewById(R.id.buttonRefresh);//Кнопка обновления текущей страницы
        final Button buttonUserRemittance = (Button) findViewById(R.id.buttonRemittance);//Кнопка перехода к меню денежных переводов
        final Button buttonUserRemittanceStory = (Button) findViewById(R.id.buttonRemittanceStory);//Кнопка перехода к меню истории переводов
        final TextView textViewBCards = (TextView) findViewById(R.id.textViewBCards);//Список прикрепленных банковских карт
        final TextView textViewMTCards = (TextView) findViewById(R.id.textViewMTCards);//Список прикрепленных БСК

        //Инициирую необходимые в работе переменные
        Gson gson = new GsonBuilder().create();//Создаю объект распознования json файлов
        UserJSON bufferUserJSON = new UserJSON();//Инициирую объект с пользовательской информацией

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

            bufferUserJSON = gson.fromJson(bufferFile, UserJSON.class);//Создаю объект класса UserJSON из распознангого содержимого файла фротмата .json
            //Вывожу прочитанную информацию
            textViewBCards.setText(null);//Сбрасываю содержимое списка банковских карт
            for(int i = 0; i < bufferUserJSON.getBankCardsLength(); i++){
                //Заполняю список прочитанной информацией о банковских картах
                textViewBCards.setText(textViewBCards.getText() + bufferUserJSON.getBankCardByIndex(password, i) +"\n");
            }
            if (textViewBCards.getText() == null){//Проверяю наличие данных в списке
                textViewBCards.setText("Список пуст");
            }

            textViewMTCards.setText(null);//Сбрасываю содержимое списка карт метрополитена
            for(int i = 0; i < bufferUserJSON.getMetropolitenCardsLength(); i++){
                //Заполняю список прочитанной информацией о картах метрополитена
                textViewMTCards.setText(textViewMTCards.getText() + bufferUserJSON.getMetropolitenCardNumberByIndex(password, i)
                        + " Баланс:" + bufferUserJSON.getMetropolitenCardBalanceByIndex(password, i) + "\n");
            }
            if (textViewMTCards.getText() == null){//Проверяю наличие данных в списке
                textViewMTCards.setText("Список пуст");
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(), "Exception: " + t.toString(), Toast.LENGTH_LONG).show();//Сообщение об ошибке
        }
        final UserJSON userJSON = bufferUserJSON;//Фиксирую прочитанную из файла информацию

        //Обновляю состояние балансов карт метрополитена
        Response.Listener<String> responseListener = new Response.Listener<String>() {//Действия по ответу на запрос
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        final double requestedMetropolitenCardBalance = jsonResponse.getDouble("BALANCE");
                        final String requestedMetropolitenCardNumber = jsonResponse.getString("CARDNUM");

                        //Если запрошеные значения баланса не совпадают с сохраненными
                        if (!String.format("%.2f", requestedMetropolitenCardBalance).equals(userJSON.getMetropolitenCardBalanceByCardNumber(password, requestedMetropolitenCardNumber))){
                            UserJSON bufferUserJSON = userJSON;
                            bufferUserJSON.setMetropolitenCardBalanceByCardNumber(password, requestedMetropolitenCardNumber, requestedMetropolitenCardBalance);//Меняю значение баланса у выбранной БСК
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
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        for(int i = 0; i < userJSON.getMetropolitenCardsLength(); i++) {//Формирую запросы в колличестве равному числу прочитанных их файла БСК
            GettingMetropolitenCardsBalanceRequest gettingMetropolitenCardsBalanceRequestRequest =
                    new GettingMetropolitenCardsBalanceRequest(userJSON.getMetropolitenCardNumberByIndex(password, i), responseListener);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(gettingMetropolitenCardsBalanceRequestRequest);
        }

        buttonUserBankCards.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой перехода к меню банковских карт
            @Override
            public void onClick(View view) {//Действие по нажатию
                Intent intent = new Intent(MainActivity.this, UpdateBankCardList.class);
                intent.putExtra("password", password);
                MainActivity.this.startActivity(intent);
            }
        });

        buttonUserBankCards.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для перехода в меню управления списком банковских карт с которых пользователь может пополнять карты метрополитена")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        buttonUserMetropolitenCards.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой перехода к меню БСК
            @Override
            public void onClick(View view) {//Действие по нажатию
                Intent intent = new Intent(MainActivity.this, UpdateMetropolitenCardList.class);
                intent.putExtra("password", password);
                MainActivity.this.startActivity(intent);
            }
        });

        buttonUserMetropolitenCards.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для перехода в меню упраления списком карт метополитена на которые пользователь может переводить денежные средства с банковских карт")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        buttonRefreshActivity.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой обновления страницы
            @Override
            public void onClick(View view) {//Действие по нажатию
                recreate();//Обновление Activity
                recreate();//Обновление Activity
            }
        });

        buttonRefreshActivity.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для обновления текущей информации на странице")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        buttonUserRemittance.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой перехода к меню денежных переводов
            @Override
            public void onClick(View view) {//Действие по нажатию
                Intent intent = new Intent(MainActivity.this, Remittance.class);
                intent.putExtra("password", password);
                MainActivity.this.startActivity(intent);
            }
        });

        buttonUserRemittance.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для перехода в меню перевода денежных средств с банковских карт на карты метрополитена")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        buttonUserRemittanceStory.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой перехода к меню истории денежных переводов
            @Override
            public void onClick(View view) {//Действие по нажатию
                Intent intent = new Intent(MainActivity.this, RemittanceStory.class);
                intent.putExtra("password", password);
                MainActivity.this.startActivity(intent);
            }
        });

        buttonUserRemittanceStory.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для открытия истории денежных переводов")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {//Действия по взаимодействию с кнопкой завершения сеанса
            @Override
            public void onClick(View view) {//Действие по нажатию
                startActivity(new Intent(MainActivity.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                //Переход на страницу авторизации со сбросом стека страниц(Activity)
                finish();//Закрытие текущего Activity
            }
        });

        buttonLogout.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//Сообщ об ошибке
                builder.setMessage("Данная кнопка предназначена для выхода из главного меню")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        textViewBCards.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//Сообщ об ошибке
                builder.setMessage("В данном окне отображен список прикрепелнных банковскх карт. Чтобы изменить список банковских карт, перейдите в меню банковских карт, нажав на соотвествующую кнопку ниже")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });

        textViewMTCards.setOnLongClickListener(new View.OnLongClickListener() {//Действие по долгому нажатию кнопки
            public boolean onLongClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//Сообщ об ошибке
                builder.setMessage("В данном окне отображен список прикрепелнных карт метрополитена. Чтобы изменить список карт метрополитена, перейдите в меню карт метрополитена, нажав на соотвествующую кнопку ниже")
                        .setNegativeButton("Ok", null)
                        .create()
                        .show();
                return false;
            }
        });
    }
}
