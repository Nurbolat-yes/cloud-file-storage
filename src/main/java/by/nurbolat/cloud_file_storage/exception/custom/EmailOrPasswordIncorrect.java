package by.nurbolat.cloud_file_storage.exception.custom;

public class EmailOrPasswordIncorrect extends Exception{
    @Override
    public String getMessage(){
        return "Email or Password incorrect";
    }
}
