package com.example.kirill.bskpayment;

public class UserJSON {
    private String passwordHash;
    private String[] bankCards;
    private MetropolitenCard[] metropolitenCards;
    private RemittanceStory[] remittanceStories;

    private Encryption encryption = new Encryption();

    private class MetropolitenCard{
        private String cardNum;
        private String balance;

        public MetropolitenCard(String cardNum, String balance){
            this.cardNum = cardNum;
            this.balance = balance;
        }
    }

    private class RemittanceStory{
        private String senderNumber;
        private String receiverNumber;
        private String sum;
        private String date;

        public RemittanceStory(String senderNumber, String receiverNumber, String sum, String date){
            this.senderNumber = senderNumber;
            this.receiverNumber = receiverNumber;
            this.sum = sum;
            this.date = date;
        }
    }

    public UserJSON(){
        this.passwordHash = null;
        this.bankCards = new String[0];
        this.metropolitenCards = new MetropolitenCard[0];
        this.remittanceStories = new RemittanceStory[0];
    }

    public String getPasswordHash(){
        return passwordHash;
    }

    public void setPasswordHash(String newPasswordHash){
        passwordHash = newPasswordHash;
    }

    public int getBankCardsLength(){
        return bankCards.length;
    }

    public String getBankCardByIndex(String password, int index){
        return encryption.encryption(password, bankCards[index]);
    }

    public void addBankCard(String password,String newBankCard){
        //Добавляю новую карту в конец
        String[] buffer = new String[bankCards.length+1];//Создаю промежуточный массив на 1 элемент больший, чем размер bankCards
        for (int i = 0; i < bankCards.length; i++){
            buffer[i] = bankCards[i];//Заполняю промежуточный массив
        }
        buffer[buffer.length-1] = encryption.encryption(password, newBankCard);//Последний элемент массива промежуточного массива заменяю на newBankCard
        bankCards = buffer;//Промежуточным массивом строк заменяю старое значение bankCards
    }

    public boolean bankCardIsExist(String password,String bankCard){
        String encryptionBankCard = encryption.encryption(password, bankCard);
        for (int i = 0; i < bankCards.length; i++){
            if (bankCards[i].equals(encryptionBankCard)){
                return(true);
            }
        }
        return (false);
    }

    public void deleteBankCardByIndex(int index){
        String[] buffer = new String[bankCards.length - 1];//Создаю промежуточный массив на 1 элемент меньший, чем размер bankCards
        for (int i = 0; i < bankCards.length - 1; i++){
            if (i < index){
                buffer[i] = bankCards[i];
            } else {
                buffer[i] = bankCards[i+1];
            }
        }
        bankCards = buffer;//Промежуточным массивом строк заменяю старое значение bankCards
    }

    public int getMetropolitenCardsLength(){
        return metropolitenCards.length;
    }

    public void addMetropolitenCard(String password, String cardNumber, double balance){
        String encryptionCardNumber = encryption.encryption(password, cardNumber);
        String encryptionBalance = encryption.encryption(password, String.format("%.2f", balance));
        MetropolitenCard newMetropolitenCard = new MetropolitenCard(encryptionCardNumber, encryptionBalance);
        //Добавляю новую карту в конец
        MetropolitenCard[] buffer = new MetropolitenCard[metropolitenCards.length+1];//Создаю промежуточный массив на 1 элемент больший, чем размер metropolitenCards
        for (int i = 0; i < metropolitenCards.length; i++){
            buffer[i] = metropolitenCards[i];//Заполняю промежуточный массив
        }
        buffer[buffer.length-1] = newMetropolitenCard;//Последний элемент массива промежуточного массива заменяю на newMetropolitenCard
        metropolitenCards = buffer;//Промежуточным массивом заменяю старое значение metropolitenCards
    }

    public boolean metropolitenCardIsExist(String password, String cardNumber){
        String encryptionCardNumber = encryption.encryption(password, cardNumber);
        for (int i = 0; i < metropolitenCards.length; i++){
            if (metropolitenCards[i].cardNum.equals(encryptionCardNumber)){
                return (true);
            }
        }
        return (false);
    }

    public void deleteMetropolitenCardByIndex(int index){
        MetropolitenCard[] buffer = new MetropolitenCard[metropolitenCards.length - 1];//Создаю промежуточный массив на 1 элемент меньший, чем размер metropolitenCards
        for (int i = 0; i < metropolitenCards.length - 1; i++){
            if (i < index){
                buffer[i] = metropolitenCards[i];
            } else {
                buffer[i] = metropolitenCards[i+1];
            }
        }
        metropolitenCards = buffer;//Промежуточным массивом строк заменяю старое значение bankCards
    }

    public String getMetropolitenCardNumberByIndex(String password, int index){
        return encryption.encryption(password, metropolitenCards[index].cardNum);
    }

    public String getMetropolitenCardBalanceByIndex(String password, int index){
        return encryption.encryption(password, metropolitenCards[index].balance);
    }

    public String getMetropolitenCardBalanceByCardNumber(String password, String cardNumber){
        String encryptionCardNumber = encryption.encryption(password, cardNumber);
        for(int i = 0; i < getMetropolitenCardsLength(); i++){
            if(metropolitenCards[i].cardNum.equals(encryptionCardNumber)){
                return (encryption.encryption(password, metropolitenCards[i].balance));
            }
        }
        return null;
    }

    public void setMetropolitenCardNumberByIndex(String password, int index, String newMetropolitenCardNumber){
        String encryptionNewMetropolitenCardNumber = encryption.encryption(password, newMetropolitenCardNumber);
        metropolitenCards[index].cardNum = encryptionNewMetropolitenCardNumber;
    }

    public void setMetropolitenCardBalanceByIndex(String password, int index, double newMetropolitenCardBalance){
        String encryptionNewMetropolitenCardBalance = encryption.encryption(password, String.format("%.2f", newMetropolitenCardBalance));
                metropolitenCards[index].balance = String.format("%.2f", encryptionNewMetropolitenCardBalance);
    }

    public void setMetropolitenCardBalanceByCardNumber(String password, String cardNumber, double newCardBalance){
        String encryptionCardNumber = encryption.encryption(password, cardNumber);
        String encryptionNewCardBalance = encryption.encryption(password, String.format("%.2f", newCardBalance));
        for (int i = 0; i < getMetropolitenCardsLength(); i++){
            if (metropolitenCards[i].cardNum.equals(encryptionCardNumber)){
                metropolitenCards[i].balance = encryptionNewCardBalance;
            }
        }
    }

    public int getRemittanceStoriesLength(){
        return remittanceStories.length;
    }

    public String getSenderNumberByIndex(String password, int index){
        return encryption.encryption(password, remittanceStories[index].senderNumber);
    }

    public String getReceiverNumberByIndex(String password, int index){
        return encryption.encryption(password, remittanceStories[index].receiverNumber);
    }

    public String getRemittanceSumByIndex(String password, int index){
        return encryption.encryption(password, remittanceStories[index].sum);
    }

    public String getRemmittanceDateByIndex(String password, int index){
        return encryption.encryption(password, remittanceStories[index].date);
    }

    public void addRemittanceStory(String password, String newSenderNumber, String newReceiverNumber, double newSum, String newDate) {
        String encryptionNewSenderNumber = encryption.encryption(password, newSenderNumber);
        String encryptionNewReceiverNumber = encryption.encryption(password, newReceiverNumber);
        String encryptionNewSum = encryption.encryption(password, String.format("%.2f", newSum));
        String encryptionNewDate = encryption.encryption(password, newDate);
        RemittanceStory newRemittanceStory = new RemittanceStory(encryptionNewSenderNumber, encryptionNewReceiverNumber, encryptionNewSum, encryptionNewDate);
        //Добавляю перевод в историю переводов
        RemittanceStory[] buffer = new RemittanceStory[remittanceStories.length+1];//Создаю промежуточный массив на 1 элемент больший, чем размер remittanceStories
        for (int i = 0; i < remittanceStories.length; i++){
            buffer[i] = remittanceStories[i];//Заполняю промежуточный массив
        }
        buffer[buffer.length-1] = newRemittanceStory;//Последний элемент массива промежуточного массива заменяю на newRemittanceStory
        remittanceStories = buffer;//Промежуточным массивом заменяю старое значение metropolitenCards
    }
}
