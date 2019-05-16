package com.example.kirill.bskpayment;

public class Encryption {
    public String encryption(String password, String encryptionString){
        StringBuilder builder = new StringBuilder();//Инициирую объект класса посторения объектов типа String
        int passwordCharIndex = 0;//Инициирую переменную указывающую на порядковый номер текущего символа из строки password
        for(int encryptionCharIndex = 0; encryptionCharIndex < encryptionString.length(); encryptionCharIndex++){//Обхожу всю строку encryptionString
            char xor = (char)(encryptionString.charAt(encryptionCharIndex) ^  password.charAt(passwordCharIndex));//Сложение по модулю 2 двух символов
            builder.append(xor);//Полученынй в результате сложения по модулю 2 символ в добавляю билдер
            if(passwordCharIndex < password.length() - 1){
                passwordCharIndex++;
            } else {
                passwordCharIndex = 0;
            }
        }
        return builder.toString();//Вывожу сконструированную строку
    }
}
